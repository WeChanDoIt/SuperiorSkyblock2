package com.bgsoftware.superiorskyblock.commands.command.admin;

import com.bgsoftware.superiorskyblock.Locale;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.ICommand;
import com.bgsoftware.superiorskyblock.utils.StringUtil;
import com.bgsoftware.superiorskyblock.wrappers.SSuperiorPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CmdAdminSetBlockLimit implements ICommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("setblocklimit");
    }

    @Override
    public String getPermission() {
        return "superior.admin.setblocklimit";
    }

    @Override
    public String getUsage() {
        return "island admin setblocklimit <player-name/island-name> <block> <limit>";
    }

    @Override
    public String getDescription() {
        return Locale.COMMAND_DESCRIPTION_ADMIN_SET_BLOCK_LIMIT.getMessage();
    }

    @Override
    public int getMinArgs() {
        return 5;
    }

    @Override
    public int getMaxArgs() {
        return 5;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return true;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        SuperiorPlayer targetPlayer = SSuperiorPlayer.of(args[2]);
        Island island = targetPlayer == null ? plugin.getGrid().getIsland(args[2]) : targetPlayer.getIsland();

        if(island == null){
            if(args[2].equalsIgnoreCase(sender.getName()))
                Locale.INVALID_ISLAND.send(sender);
            else if(targetPlayer == null)
                Locale.INVALID_ISLAND_OTHER_NAME.send(sender, args[2]);
            else
                Locale.INVALID_ISLAND_OTHER.send(sender, targetPlayer.getName());
            return;
        }

        Key key = Key.of(args[3].toUpperCase());

        int limit;

        try{
            limit = Integer.parseInt(args[4]);
        }catch(IllegalArgumentException ex){
            Locale.INVALID_LIMIT.send(sender, args[4]);
            return;
        }

        island.setBlockLimit(key, limit);

        if(targetPlayer == null)
            Locale.CHANGED_BLOCK_LIMIT_NAME.send(sender, StringUtil.format(key.toString().split(":")[0]), island.getName());
        else
            Locale.CHANGED_BLOCK_LIMIT.send(sender, StringUtil.format(key.toString().split(":")[0]), targetPlayer.getName());
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        if(args.length == 3){
            for(Player player : Bukkit.getOnlinePlayers()){
                SuperiorPlayer onlinePlayer = SSuperiorPlayer.of(player);
                Island onlineIsland = onlinePlayer.getIsland();
                if (onlineIsland != null) {
                    if (player.getName().toLowerCase().startsWith(args[2].toLowerCase()))
                        list.add(player.getName());
                    if (onlineIsland.getName().toLowerCase().startsWith(args[2].toLowerCase()))
                        list.add(onlineIsland.getName());
                }
            }
        }
        else if(args.length == 4){
            SuperiorPlayer targetPlayer = SSuperiorPlayer.of(args[2]);
            Island island = targetPlayer == null ? plugin.getGrid().getIsland(args[2]) : targetPlayer.getIsland();

            if(island != null){
                for(Material material : Material.values()){
                    if(material.isBlock() && !material.name().startsWith("LEGACY_") && material.name().toLowerCase().startsWith(args[3].toLowerCase()))
                        list.add(material.name().toLowerCase());
                }
            }
        }

        return list;
    }
}