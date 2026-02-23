package me.yourname.rtpzone;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class RTPListener implements Listener {

    private final RTPManager manager;

    public RTPListener(RTPManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().getBlock().equals(e.getTo().getBlock())) return;
        manager.checkZone(e.getPlayer(), e.getTo());
    }
}