package org.inventivetalent.packetlistener;

public class Cancellable implements org.bukkit.event.Cancellable {

	private boolean cancelled;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
	}
}
