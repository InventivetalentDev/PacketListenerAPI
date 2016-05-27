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

package org.inventivetalent.packetlistener;

import org.bukkit.entity.Player;
import org.inventivetalent.packetlistener.channel.ChannelAbstract;
import org.inventivetalent.reflection.resolver.ClassResolver;
import org.inventivetalent.reflection.resolver.ConstructorResolver;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ChannelInjector {

	private static final ClassResolver CLASS_RESOLVER = new ClassResolver();

	private ChannelAbstract channel;

	public boolean inject(IPacketListener iPacketListener) {
		List<Exception> exceptions = new ArrayList<>();
		try {
			Class.forName("net.minecraft.util.io.netty.channel.Channel");
			channel = newChannelInstance(iPacketListener, "org.inventivetalent.packetlistener.channel.NMUChannel");
			System.out.println("[PacketListenerAPI] Using NMUChannel");
			return true;
		} catch (Exception e) {
			exceptions.add(e);
		}
		try {
			Class.forName("io.netty.channel.Channel");
			channel = newChannelInstance(iPacketListener, "org.inventivetalent.packetlistener.channel.INCChannel");
			System.out.println("[PacketListenerAPI] Using INChannel");
			return true;
		} catch (Exception e1) {
			exceptions.add(e1);
		}
		for (Exception e : exceptions) {
			e.printStackTrace();
		}
		return false;
	}

	protected ChannelAbstract newChannelInstance(IPacketListener iPacketListener, String clazzName) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		return (ChannelAbstract) new ConstructorResolver(CLASS_RESOLVER.resolve(clazzName)).resolve(new Class[] { IPacketListener.class }).newInstance(iPacketListener);
	}

	public void addChannel(Player p) {
		this.channel.addChannel(p);
	}

	public void removeChannel(Player p) {
		this.channel.removeChannel(p);
	}

	public void addServerChannel() {
		this.channel.addServerChannel();
	}

}
