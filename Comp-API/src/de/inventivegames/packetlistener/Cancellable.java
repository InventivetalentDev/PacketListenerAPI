package de.inventivegames.packetlistener;

@Deprecated public class Cancellable {

	private boolean cancelled = false;

	/**
	 * @return <code>true</code> if the packet has been cancelled
	 */
	public boolean isCancelled() {
		return this.cancelled;
	}

	/**
	 * @param paramBoolean if set to <code>true</code> the packet will be cancelled
	 */
	public void setCancelled(boolean paramBoolean) {
		this.cancelled = paramBoolean;
	}

}
