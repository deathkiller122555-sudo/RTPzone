package me.yourname.rtpzone;

import org.bukkit.*;
import org.bukkit.boss.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class RTPManager {

    private final JavaPlugin plugin;
    private Location pos1, pos2;
    private final Set<UUID> queuedPlayers = new HashSet<>();
    private boolean teleportScheduled = false;
    private BossBar bossBar;
    private int timeLeft;

    public RTPManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setPos1(Location l) { pos1 = l; }
    public void setPos2(Location l) { pos2 = l; }

    public void createZone(String name, World world) {
        plugin.getConfig().set("zones." + name + ".world", world.getName());
        plugin.getConfig().set("zones." + name + ".minX", Math.min(pos1.getBlockX(), pos2.getBlockX()));
        plugin.getConfig().set("zones." + name + ".maxX", Math.max(pos1.getBlockX(), pos2.getBlockX()));
        plugin.getConfig().set("zones." + name + ".minZ", Math.min(pos1.getBlockZ(), pos2.getBlockZ()));
        plugin.getConfig().set("zones." + name + ".maxZ", Math.max(pos1.getBlockZ(), pos2.getBlockZ()));
        plugin.saveConfig();
    }

    public void checkZone(Player player, Location to) {
        if (!plugin.getConfig().isConfigurationSection("zones")) return;

        for (String zone : plugin.getConfig().getConfigurationSection("zones").getKeys(false)) {
            String w = plugin.getConfig().getString("zones." + zone + ".world");
            if (!player.getWorld().getName().equals(w)) continue;

            int x = to.getBlockX();
            int z = to.getBlockZ();

            int minX = plugin.getConfig().getInt("zones." + zone + ".minX");
            int maxX = plugin.getConfig().getInt("zones." + zone + ".maxX");
            int minZ = plugin.getConfig().getInt("zones." + zone + ".minZ");
            int maxZ = plugin.getConfig().getInt("zones." + zone + ".maxZ");

            if (x >= minX && x <= maxX && z >= minZ && z <= maxZ) {
                queuedPlayers.add(player.getUniqueId());
                if (!teleportScheduled) startCountdown(player.getWorld());
                if (bossBar != null) bossBar.addPlayer(player);
            }
        }
    }

    private void startCountdown(World world) {
        teleportScheduled = true;
        timeLeft = plugin.getConfig().getInt("delay", 30);

        bossBar = Bukkit.createBossBar(
                ChatColor.DARK_RED + "RTP starting in " + timeLeft + " seconds",
                BarColor.RED,
                BarStyle.SEGMENTED_10
        );

        new BukkitRunnable() {
            @Override
            public void run() {
                timeLeft--;
                bossBar.setTitle(ChatColor.DARK_RED + "RTP starting in " + timeLeft + " seconds");
                bossBar.setProgress(timeLeft / (double) plugin.getConfig().getInt("delay"));

                if (timeLeft <= 0) {
                    Location loc = randomLocation(world);
                    for (UUID id : queuedPlayers) {
                        Player p = Bukkit.getPlayer(id);
                        if (p != null) p.teleport(loc);
                    }
                    bossBar.removeAll();
                    queuedPlayers.clear();
                    teleportScheduled = false;
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20, 20);
    }

    private Location randomLocation(World world) {
        Random r = new Random();
        int x = r.nextInt(5000) - 2500;
        int z = r.nextInt(5000) - 2500;
        int y = world.getHighestBlockYAt(x, z) + 1;
        return new Location(world, x + 0.5, y, z + 0.5);
    }
}