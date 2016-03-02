/*
 * Copyright 2015-2016 inventivetalent. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and contributors and should not be interpreted as representing official policies,
 *  either expressed or implied, of anybody else.
 */

package org.inventivetalent.packetlistener.channel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.inventivetalent.packetlistener.Cancellable;
import org.inventivetalent.packetlistener.IPacketListener;
import org.inventivetalent.reflection.resolver.FieldResolver;
import org.inventivetalent.reflection.resolver.MethodResolver;
import org.inventivetalent.reflection.resolver.minecraft.NMSClassResolver;
import org.inventivetalent.reflection.util.AccessUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class ChannelAbstract {

	protected static final NMSClassResolver nmsClassResolver = new NMSClassResolver();

	static final Class<?> EntityPlayer     = nmsClassResolver.resolveSilent("EntityPlayer");
	static final Class<?> PlayerConnection = nmsClassResolver.resolveSilent("PlayerConnection");
	static final Class<?> NetworkManager   = nmsClassResolver.resolveSilent("NetworkManager");
	static final Class<?> Packet           = nmsClassResolver.resolveSilent("Packet");
	static final Class<?> ServerConnection = nmsClassResolver.resolveSilent("ServerConnection");
	static final Class<?> MinecraftServer  = nmsClassResolver.resolveSilent("MinecraftServer");

	protected static final FieldResolver entityPlayerFieldResolver     = new FieldResolver(EntityPlayer);
	protected static final FieldResolver playerConnectionFieldResolver = new FieldResolver(PlayerConnection);
	protected static final FieldResolver networkManagerFieldResolver   = new FieldResolver(NetworkManager);
	protected static final FieldResolver minecraftServerFieldResolver  = new FieldResolver(MinecraftServer);
	protected static final FieldResolver serverConnectionFieldResolver = new FieldResolver(ServerConnection);

	static final Field networkManager   = playerConnectionFieldResolver.resolveSilent("networkManager");
	static final Field playerConnection = entityPlayerFieldResolver.resolveSilent("playerConnection");
	static final Field serverConnection = minecraftServerFieldResolver.resolveByFirstTypeSilent(ServerConnection);
	static final Field connectionList   = serverConnectionFieldResolver.resolveByLastTypeSilent(List.class);

	protected static final MethodResolver craftServerFieldResolver = new MethodResolver(Bukkit.getServer().getClass());

	static final Method getServer = craftServerFieldResolver.resolveSilent("getServer");

	final Executor addChannelExecutor    = Executors.newSingleThreadExecutor();
	final Executor removeChannelExecutor = Executors.newSingleThreadExecutor();

	static final String KEY_HANDLER = "packet_handler";
	static final String KEY_PLAYER  = "packet_listener_player";
	static final String KEY_SERVER  = "packet_listener_server";

	private IPacketListener iPacketListener;

	public ChannelAbstract(IPacketListener iPacketListener) {
		this.iPacketListener = iPacketListener;
	}

	public abstract void addChannel(Player player);

	public abstract void removeChannel(Player player);

	public void addServerChannel() {
		try {
			Object dedicatedServer = getServer.invoke(Bukkit.getServer());
			Object serverConnection = ChannelAbstract.serverConnection.get(dedicatedServer);
			System.out.println(ServerConnection);
			System.out.println(serverConnection);
			System.out.println(connectionList);
			List currentList = (List<?>) connectionList.get(serverConnection);
			Field superListField = AccessUtil.setAccessible(currentList.getClass().getSuperclass().getDeclaredField("list"));
			Object list = superListField.get(currentList);
			if (IListenerList.class.isAssignableFrom(list.getClass())) { return; }
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

	protected final Object onPacketSend(Player player, Object packet, Cancellable cancellable) {
		return iPacketListener.onPacketSend(player, packet, cancellable);
	}

	protected final Object onPacketReceive(Player player, Object packet, Cancellable cancellable) {
		return iPacketListener.onPacketReceive(player, packet, cancellable);
	}

	interface IListenerList<E> extends List<E> {
	}

	interface IChannelHandler {
	}

}
