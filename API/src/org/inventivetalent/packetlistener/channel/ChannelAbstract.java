package org.inventivetalent.packetlistener.channel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.inventivetalent.packetlistener.Cancellable;
import org.inventivetalent.packetlistener.IPacketListener;
import org.inventivetalent.reflection.accessor.FieldAccessor;
import org.inventivetalent.reflection.resolver.FieldResolver;
import org.inventivetalent.reflection.resolver.MethodResolver;
import org.inventivetalent.reflection.resolver.minecraft.NMSClassResolver;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class ChannelAbstract {

    protected static final NMSClassResolver nmsClassResolver = new NMSClassResolver();

    static final Class<?> EntityPlayer = nmsClassResolver.resolveSilent("EntityPlayer", "server.level.EntityPlayer");
    static final Class<?> PlayerConnection = nmsClassResolver.resolveSilent("PlayerConnection", "server.network.PlayerConnection");
    static final Class<?> NetworkManager = nmsClassResolver.resolveSilent("NetworkManager", "network.NetworkManager");
    static final Class<?> Packet = nmsClassResolver.resolveSilent("Packet", "network.protocol.Packet");
    static final Class<?> ServerConnection = nmsClassResolver.resolveSilent("ServerConnection", "server.network.ServerConnection");
    static final Class<?> MinecraftServer = nmsClassResolver.resolveSilent("MinecraftServer", "server.MinecraftServer");

    protected static final FieldResolver entityPlayerFieldResolver = new FieldResolver(EntityPlayer);
    protected static final FieldResolver playerConnectionFieldResolver = new FieldResolver(PlayerConnection);
    protected static final FieldResolver networkManagerFieldResolver = new FieldResolver(NetworkManager);
    protected static final FieldResolver minecraftServerFieldResolver = new FieldResolver(MinecraftServer);
    protected static final FieldResolver serverConnectionFieldResolver = new FieldResolver(ServerConnection);

    static final FieldAccessor networkManager = playerConnectionFieldResolver.resolveByFirstTypeAccessor(NetworkManager);
    static final FieldAccessor playerConnection = entityPlayerFieldResolver.resolveByFirstTypeAccessor(PlayerConnection);
    static final FieldAccessor serverConnection = minecraftServerFieldResolver.resolveByFirstTypeAccessor(ServerConnection);
    static final FieldAccessor connectionList = serverConnectionFieldResolver.resolveByLastTypeAccessor(List.class);

    protected static final MethodResolver craftServerFieldResolver = new MethodResolver(Bukkit.getServer().getClass());

    static final Method getServer = craftServerFieldResolver.resolveSilent("getServer");

    final Executor addChannelExecutor = Executors.newSingleThreadExecutor();
    final Executor removeChannelExecutor = Executors.newSingleThreadExecutor();

    static final String KEY_HANDLER = "packet_handler";
    static final String KEY_PLAYER = "packet_listener_player";
    static final String KEY_SERVER = "packet_listener_server";

    private IPacketListener iPacketListener;

    public ChannelAbstract(IPacketListener iPacketListener) {
        this.iPacketListener = iPacketListener;
    }

    public abstract void addChannel(Player player);

    public abstract void removeChannel(Player player);

    public void addServerChannel() {
        try {
            Object dedicatedServer = getServer.invoke(Bukkit.getServer());
            if (dedicatedServer == null) { return; }
            Object serverConnection = ChannelAbstract.serverConnection.get(dedicatedServer);
            if (serverConnection == null) { return; }
            List currentList = (List<?>) connectionList.get(serverConnection);
            if (!currentList.isEmpty()) {
                // Try to check if our list is already set
                try {
                    FieldAccessor superListField = new FieldAccessor(currentList.getClass().getSuperclass().getDeclaredField("list"));
                    Object list = superListField.get(currentList);
                    if (IListenerList.class.isAssignableFrom(list.getClass())) { return; }
                } catch (Exception e) {
                    // Newer Java versions will prevent access to the SynchronizedCollection classes completely, so just override it :shrug:
                    System.err.println("[PacketListenerAPI] Failed to determine existing server channel, overriding non-empty one! This will break things!");
                    e.printStackTrace();
                }
            }
            List newList = Collections.synchronizedList(newListenerList());
            for (Object o : currentList) {
                newList.add(o);
            }
            connectionList.set(serverConnection, newList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract IListenerList newListenerList();

    protected final Object onPacketSend(Object receiver, Object packet, Cancellable cancellable) {
        return iPacketListener.onPacketSend(receiver, packet, cancellable);
    }

    protected final Object onPacketReceive(Object sender, Object packet, Cancellable cancellable) {
        return iPacketListener.onPacketReceive(sender, packet, cancellable);
    }

    interface IListenerList<E> extends List<E> {
    }

    interface IChannelHandler {
    }

    interface IChannelWrapper {
    }

}
