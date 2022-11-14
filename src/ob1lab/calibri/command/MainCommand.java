package ob1lab.calibri.command;

import ob1lab.calibri.Main;
import ob1lab.calibri.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainCommand extends AbstractCommand {
    private static final Map<String, Long> time = new HashMap<>();
    public MainCommand() {
        super("calibri");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("OP")
                && ( Main.getData().getConfig().getConfigurationSection("staffList") == null || !Main.getData().getConfig().getConfigurationSection("staffList").getKeys(false).contains(sender.getName()))) {
            Message.noPermission.send(sender);
            return;
        }
        if (args.length == 0) {
            Message.info.send(sender);
            return;
        }
        if (args[0].equalsIgnoreCase("staff")) {
            if (!sender.hasPermission("OP")) {
                Message.noPermission.send(sender);
                return;
            }
            if (args.length < 2) {
                Message.notEnoughArguments.send(sender);
                return;
            }
            if (args[1].equalsIgnoreCase("list")) {
                StaffList.list(sender);
                return;
            }
            if (args[1].equalsIgnoreCase("wipe")) {
                StaffList.wipe(sender);
                return;
            }
            if (args.length < 3) {
                Message.notEnoughArguments.send(sender);
                return;
            }
            String act = args[1];
            String playerName = args[2];
            if (!Arrays.asList("add", "rm", "list").contains(act.toLowerCase())) {
                Message.argumentNotFound.send(sender);
                return;
            }
            if (args[1].equalsIgnoreCase("add")) {
                StaffList.add(sender, playerName);
                return;
            }
            StaffList.rm(sender, playerName);
            return;
        }
        if (args[0].equalsIgnoreCase("cart")) {
            if (Main.getData().getConfig().getConfigurationSection("staffList") == null
                || !Main.getData().getConfig().getConfigurationSection("staffList").getKeys(false).contains(sender.getName())) {
                Message.mustBeInList.send(sender);
                return;
            }
            if (args.length == 2 && args[1].equalsIgnoreCase("get")) {
                Message.notEnoughArguments.send(sender);
                return;
            }
            if (args.length == 1) {
                Cart.get(sender);
                return;
            }
            if (args.length == 2 && args[1].equalsIgnoreCase("update")) {
                if (time.containsKey(sender.getName()) && System.currentTimeMillis()/1000 - time.get(sender.getName()) < 15) {
                    Message.timeout.send(sender);
                    return;
                }
                Cart.update(sender);
                time.put(sender.getName(), System.currentTimeMillis()/1000);
                return;
            }
            Player player = (Player) sender;
            if (args[1].equalsIgnoreCase("get")) {
                if (args[2].equalsIgnoreCase("all")) {
                    Cart.getAll(player);
                    return;
                }
                int itemId;
                try {
                    itemId = Integer.parseInt(args[2]);
                } catch(NumberFormatException e) {
                    Message.indexItemMustBeInteger.send(sender);
                    return;
                }
                if (args.length == 3) {
                    Cart.getOneAll(player, itemId);
                    return;
                }
                int amount;
                try {
                    amount = Integer.parseInt(args[3]);
                    if (amount < 1) {
                        Message.numberMustBePositive.send(sender);
                        return;
                    }
                } catch(NumberFormatException e) {
                    Message.indexItemMustBeInteger.send(sender);
                    return;
                }
                Cart.getOneAmount(player, itemId, amount);
                return;
            }
        }
        Message.argumentNotFound.send(sender);
    }
}
