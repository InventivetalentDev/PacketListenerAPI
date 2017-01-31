package org.inventivetalent.packetlistener.handler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.inventivetalent.reflection.minecraft.Minecraft;
import org.inventivetalent.reflection.resolver.FieldResolver;
import org.inventivetalent.reflection.resolver.MethodResolver;
import org.inventivetalent.reflection.resolver.minecraft.NMSClassResolver;
import org.inventivetalent.reflection.util.AccessUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class PacketHandler {

	private static final List<PacketHandler> handlers = new ArrayList<>();

	private boolean hasSendOptions;
	private boolean forcePlayerSend;
	private boolean forceServerSend;

	private boolean hasReceiveOptions;
	private boolean forcePlayerReceive;
	private boolean forceServerReceive;

	public static boolean addHandler(PacketHandler handler) {
		boolean b = handlers.contains(handler);
		if (!b) {
			try {
				PacketOptions options = handler.getClass().getMethod("onSend", SentPacket.class).getAnnotation(PacketOptions.class);
				if (options != null) {
					handler.hasSendOptions = true;
					if (options.forcePlayer() && options.forceServer()) { throw new IllegalArgumentException("Cannot force player and server packets at the same time!"); }
					if (options.forcePlayer()) {
						handler.forcePlayerSend = true;
					} else if (options.forceServer()) {
						handler.forceServerSend = true;
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Failed to register handler (onSend)", e);
			}
			try {
				PacketOptions options = handler.getClass().getMethod("onReceive", ReceivedPacket.class).getAnnotation(PacketOptions.class);
				if (options != null) {
					handler.hasReceiveOptions = true;
					if (options.forcePlayer() && options.forceServer()) { throw new IllegalArgumentException("Cannot force player and server packets at the same time!"); }
					if (options.forcePlayer()) {
						handler.forcePlayerReceive = true;
					} else if (options.forceServer()) {
						handler.forceServerReceive = true;
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Failed to register handler (onReceive)", e);
			}
		}
		handlers.add(handler);
		return !b;
	}

	public static boolean removeHandler(PacketHandler handler) {
		return handlers.remove(handler);
	}

	public static void notifyHandlers(SentPacket packet) {
		for (PacketHandler handler : getHandlers()) {
			try {
				if (handler.hasSendOptions) {
					if (handler.forcePlayerSend) {
						if (!packet.hasPlayer()) {
							continue;
						}
					} else if (handler.forceServerSend) {
						if (!packet.hasChannel()) {
							continue;
						}
					}
				}
				handler.onSend(packet);
			} catch (Exception e) {
				System.err.println("[PacketListenerAPI] An exception occured while trying to execute 'onSend'" + (handler.plugin != null ? " in plugin " + handler.plugin.getName() : "") + ": " + e.getMessage());
				e.printStackTrace(System.err);
			}
		}
	}

	public static void notifyHandlers(ReceivedPacket packet) {
		for (PacketHandler handler : getHandlers()) {
			try {
				if (handler.hasReceiveOptions) {
					if (handler.forcePlayerReceive) {
						if (!packet.hasPlayer()) {
							continue;
						}
					} else if (handler.forceServerReceive) {
						if (!packet.hasChannel()) {
							continue;
						}
					}
				}
				handler.onReceive(packet);
			} catch (Exception e) {
				System.err.println("[PacketListenerAPI] An exception occured while trying to execute 'onReceive'" + (handler.plugin != null ? " in plugin " + handler.plugin.getName() : "") + ": " + e.getMessage());
				e.printStackTrace(System.err);
			}
		}
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) { return true; }
		if (object == null || getClass() != object.getClass()) { return false; }

		PacketHandler that = (PacketHandler) object;

		if (hasSendOptions != that.hasSendOptions) { return false; }
		if (forcePlayerSend != that.forcePlayerSend) { return false; }
		if (forceServerSend != that.forceServerSend) { return false; }
		if (hasReceiveOptions != that.hasReceiveOptions) { return false; }
		if (forcePlayerReceive != that.forcePlayerReceive) { return false; }
		if (forceServerReceive != that.forceServerReceive) { return false; }
		return !(plugin != null ? !plugin.equals(that.plugin) : that.plugin != null);

	}

	@Override
	public int hashCode() {
		int result = (hasSendOptions ? 1 : 0);
		result = 31 * result + (forcePlayerSend ? 1 : 0);
		result = 31 * result + (forceServerSend ? 1 : 0);
		result = 31 * result + (hasReceiveOptions ? 1 : 0);
		result = 31 * result + (forcePlayerReceive ? 1 : 0);
		result = 31 * result + (forceServerReceive ? 1 : 0);
		result = 31 * result + (plugin != null ? plugin.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "PacketHandler{" +
				"hasSendOptions=" + hasSendOptions +
				", forcePlayerSend=" + forcePlayerSend +
				", forceServerSend=" + forceServerSend +
				", hasReceiveOptions=" + hasReceiveOptions +
				", forcePlayerReceive=" + forcePlayerReceive +
				", forceServerReceive=" + forceServerReceive +
				", plugin=" + plugin +
				'}';
	}

	public static List<PacketHandler> getHandlers() {
		return new ArrayList<>(handlers);
	}

	public static List<PacketHandler> getForPlugin(Plugin plugin) {
		List<PacketHandler> handlers = new ArrayList<>();
		if (plugin == null) { return handlers; }
		for (PacketHandler h : getHandlers())
			if (plugin.equals(h.getPlugin())) {
				handlers.add(h);
			}
		return handlers;
	}

	static NMSClassResolver nmsClassResolver               = new NMSClassResolver();
	static FieldResolver    EntityPlayerFieldResolver      = new FieldResolver(nmsClassResolver.resolveSilent("EntityPlayer"));
	static MethodResolver   PlayerConnectionMethodResolver = new MethodResolver(nmsClassResolver.resolveSilent("PlayerConnection"));

	// Sending methods
	public void sendPacket(Player p, Object packet) {
		if (p == null || packet == null) { throw new NullPointerException(); }
		try {
			Object handle = Minecraft.getHandle(p);
			Object connection = EntityPlayerFieldResolver.resolve("playerConnection").get(handle);
			PlayerConnectionMethodResolver.resolve("sendPacket").invoke(connection, new Object[] { packet });
		} catch (Exception e) {
			System.err.println("[PacketListenerAPI] Exception while sending " + packet + " to " + p);
			e.printStackTrace();
		}
	}

	public Object cloneObject(Object obj) throws Exception {
		if (obj == null) { return obj; }
		Object clone = obj.getClass().newInstance();
		for (Field f : obj.getClass().getDeclaredFields()) {
			f = AccessUtil.setAccessible(f);
			f.set(clone, f.get(obj));
		}
		return clone;
	}

	// //////////////////////////////////////////////////

	private Plugin plugin;

	@Deprecated
	public PacketHandler() {
	}

	public PacketHandler(Plugin plugin) {
		this.plugin = plugin;
	}

	public Plugin getPlugin() {
		return this.plugin;
	}

	public abstract void onSend(SentPacket packet);

	public abstract void onReceive(ReceivedPacket packet);

}
