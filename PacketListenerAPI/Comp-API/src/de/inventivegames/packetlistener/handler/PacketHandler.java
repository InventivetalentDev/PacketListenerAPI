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

package de.inventivegames.packetlistener.handler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecated Please use {@link org.inventivetalent.packetlistener.handler.PacketHandler}
 */
@Deprecated public abstract class PacketHandler {

	static Map<PacketHandler, org.inventivetalent.packetlistener.handler.PacketHandler> compatibilityMap = new HashMap<>();

	@Deprecated
	public static boolean addHandler(final PacketHandler handler) {
		if (handler.getPlugin() != null) {
			System.out.println("[PacketListenerAPI] " + handler.getPlugin().getName() + " by " + handler.getPlugin().getDescription().getAuthors() + " uses a deprecated PacketHandler!");
		}
		org.inventivetalent.packetlistener.handler.PacketHandler compatibilityHandler = new org.inventivetalent.packetlistener.handler.PacketHandler() {
			@Override
			public void onSend(final org.inventivetalent.packetlistener.handler.SentPacket packet) {
				handler.onSend(new SentPacket(packet.getPacket(), new de.inventivegames.packetlistener.Cancellable() {
					@Override
					public boolean isCancelled() {
						return packet.isCancelled();
					}

					@Override
					public void setCancelled(boolean paramBoolean) {
						packet.setCancelled(paramBoolean);
					}
				}, packet.getPlayer()));
			}

			@Override
			public void onReceive(final org.inventivetalent.packetlistener.handler.ReceivedPacket packet) {
				handler.onReceive(new ReceivedPacket(packet.getPacket(), new de.inventivegames.packetlistener.Cancellable() {
					@Override
					public boolean isCancelled() {
						return packet.isCancelled();
					}

					@Override
					public void setCancelled(boolean paramBoolean) {
						packet.setCancelled(paramBoolean);
					}
				}, packet.getPlayer()));
			}
		};
		compatibilityMap.put(handler, compatibilityHandler);
		return org.inventivetalent.packetlistener.handler.PacketHandler.addHandler(compatibilityHandler);
	}

	@Deprecated
	public static boolean removeHandler(PacketHandler handler) {
		boolean b = org.inventivetalent.packetlistener.handler.PacketHandler.removeHandler(compatibilityMap.get(handler));
		compatibilityMap.remove(handler);
		return b;
	}

	@Deprecated
	public static List<PacketHandler> getHandlers() {
		return new ArrayList<>(compatibilityMap.keySet());
	}

	@Deprecated
	public void sendPacket(Player p, Object packet) {
		compatibilityMap.get(this).sendPacket(p, packet);
	}

	// //////////////////////////////////////////////////

	private Plugin plugin;

	@Deprecated
	public PacketHandler() {
	}

	@Deprecated
	public PacketHandler(Plugin plugin) {
		this.plugin = plugin;
	}

	public Plugin getPlugin() {
		return this.plugin;
	}

	@Deprecated
	public abstract void onSend(SentPacket packet);

	@Deprecated
	public abstract void onReceive(ReceivedPacket packet);

}
