package ob1lab.calibri.command;

import com.google.common.collect.Lists;
import ob1lab.calibri.Main;
import ob1lab.calibri.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class StaffList {
    public static void add(CommandSender sender, String player) {
        ConfigurationSection players = Main.getData().getConfig().getConfigurationSection("staffList");
        if (players != null && players.getKeys(false).contains(player)) {
            Message.playerInStaffList.replace("{player}", player).send(sender);
            return;
        }
        Main.getData().getConfig().set("staffList." + player, Lists.newArrayList());
        Main.getData().save();
        Message.successAddStaffList.replace("{player}", player).send(sender);
    }
    public static void rm(CommandSender sender, String player) {
        ConfigurationSection players = Main.getData().getConfig().getConfigurationSection("staffList");
        if (players != null && !players.getKeys(false).contains(player)) {
            Message.playerNotInStaffList.replace("{player}", player).send(sender);
            return;
        }
        Main.getData().getConfig().set("staffList." + player, null);
        Main.getData().save();
        Message.successRmStaffList.replace("{player}", player).send(sender);
    }
    public static void list(CommandSender sender) {
        List<String> players = new ArrayList<>();
        ConfigurationSection config = Main.getData().getConfig().getConfigurationSection("staffList");
        if (config == null) {
            Message.listEmpty.send(sender);
            return;
        }
        for(String player: config.getKeys(false)) {
            players.add(Message.player.replace("{index}", String.valueOf(players.size()+1)).replace("{player}", player).string());
        }
        Message.playerList.replace("{players}", String.join("\n ", players)).send(sender);
    }
    public static void wipe(CommandSender sender) {
        ConfigurationSection config = Main.getData().getConfig().getConfigurationSection("staffList");
        if (config == null) {
            Message.listEmpty.send(sender);
            return;
        }
        for(String player: config.getKeys(false)) {
            Main.getData().getConfig().set("staffList." + player, Lists.newArrayList());
        }
        Main.getData().save();
        Message.successfulWipeCart.send(sender);
    }
}
