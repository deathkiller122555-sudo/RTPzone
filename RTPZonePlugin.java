package me.yourname.rtpzone;

import org.bukkit.plugin.java.JavaPlugin;

public class RTPZonePlugin extends JavaPlugin {

    private RTPManager rtpManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        rtpManager = new RTPManager(this);

        getCommand("rtpzone").setExecutor(new RTPZoneCommand(this, rtpManager));
        getServer().getPluginManager().registerEvents(new RTPListener(rtpManager), this);
    }
}