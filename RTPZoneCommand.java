package me.yourname.rtpzone;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class RTPZoneCommand implements CommandExecutor {

    private final RTPManager manager;

    public RTPZoneCommand(RTPZonePlugin plugin, RTPManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (args.length == 0) return false;

        if (args[0].equalsIgnoreCase("pos1")) manager.setPos1(p.getLocation());
        if (args[0].equalsIgnoreCase("pos2")) manager.setPos2(p.getLocation());
        if (args[0].equalsIgnoreCase("create")) manager.createZone(args[1], p.getWorld());

        return true;
    }
}