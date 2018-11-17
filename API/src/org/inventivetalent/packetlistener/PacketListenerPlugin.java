package org.inventivetalent.packetlistener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.apihelper.APIManager;
import org.inventivetalent.packetlistener.metrics.Metrics;
import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.UpdateCallback;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

public class PacketListenerPlugin extends JavaPlugin {

	private PacketListenerAPI packetListenerAPI = new PacketListenerAPI();

	@Override
	public void onLoad() {
		//Register this API if the plugin gets loaded
		APIManager.registerAPI(packetListenerAPI, this);
	}

	@Override
	public void onEnable() {
		if (!packetListenerAPI.injected) {
			getLogger().warning("Injection failed. Disabling...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

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

		new Metrics(this);

		//Initialize this API if the plugin got enabled
		APIManager.initAPI(PacketListenerAPI.class);
	}

	@Override
	public void onDisable() {
		//Disable this API if the plugin was enabled
		APIManager.disableAPI(PacketListenerAPI.class);
	}

}
