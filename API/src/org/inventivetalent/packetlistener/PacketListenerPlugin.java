package org.inventivetalent.packetlistener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.packetlistener.metrics.Metrics;
import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.UpdateCallback;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

public class PacketListenerPlugin extends JavaPlugin {

	private final PacketListenerAPI packetListenerAPI = new PacketListenerAPI();

	@Override
	public void onLoad() {
		packetListenerAPI.load();
	}

	@Override
	public void onEnable() {
		if (!packetListenerAPI.injected) {
			getLogger().warning("Injection failed. Disabling...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		try {
			SpigetUpdate updater = new SpigetUpdate(this, 2930);
			updater.setUserAgent("PacketListenerAPI/" + getDescription().getVersion()).setVersionComparator(VersionComparator.SEM_VER_SNAPSHOT);
			updater.checkForUpdate(new UpdateCallback() {
				@Override
				public void updateAvailable(String s, String s1, boolean b) {
					getLogger().info("There is a new version available: https://r.spiget.org/2930");
				}

				@Override
				public void upToDate() {
					getLogger().info("Plugin is up-to-date");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();	
		}

		new Metrics(this, 225);

		packetListenerAPI.init(this);
	}

	@Override
	public void onDisable() {
		packetListenerAPI.disable(this);
	}

}
