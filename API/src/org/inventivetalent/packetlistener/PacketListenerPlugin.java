package org.inventivetalent.packetlistener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.apihelper.APIManager;

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

		//Initialize this API if the plugin got enabled
		APIManager.initAPI(PacketListenerAPI.class);
	}

	@Override
	public void onDisable() {
		//Disable this API if the plugin was enabled
		APIManager.disableAPI(PacketListenerAPI.class);
	}

}
