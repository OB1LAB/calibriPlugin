package ob1lab.calibri.command;

import com.google.common.collect.Lists;
import net.minecraft.util.com.google.gson.Gson;
import ob1lab.calibri.Cart.CartItem;
import ob1lab.calibri.Cart.CartItems;
import ob1lab.calibri.Logger;
import ob1lab.calibri.Main;
import ob1lab.calibri.Message;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.IntStream;

public class Cart {
    private static final Gson gson = new Gson();
    public static void get(CommandSender sender) {
        List<String> itemsOutput = Lists.newArrayList();
        List<String> jsonItems =  Main.getData().getConfig().getStringList("staffList." + sender.getName());
        if (jsonItems.size() == 0) {
            Message.itemsIfItemsIsNone.send(sender);
            return;
        }
        for (String itemString: jsonItems) {
            CartItem item = gson.fromJson(itemString, CartItem.class);
            itemsOutput.add(Message.item.replace("{index}", String.valueOf(itemsOutput.size()+1)).replace("{item}", item.itemViewName).replace("{amount}", String.valueOf(item.amount)).string());
        }
        Message.items.replace("{items}", String.join("\n ", itemsOutput)).send(sender);
    }
    public static void getOneAll(Player player, int index) {
        List<String> jsonItems = Main.getData().getConfig().getStringList("staffList." + player.getName());
        if (jsonItems.size() == 0) {
            Message.listEmpty.send(player);
            return;
        }
        if (!(0 <= index && index <= jsonItems.size())) {
            Message.indexItemNotFound.send(player);
            return;
        }
        CartItem item = gson.fromJson(jsonItems.get(index-1), CartItem.class);
        List<String> logItems = new ArrayList<>();
        logItems.add("1. " + item.itemViewName + " - " + item.amount + " шт.");
        if (Objects.equals(item.itemType, "case")) {
            Server server = player.getServer();
            server.dispatchCommand(server.getConsoleSender(), "cases add " + player.getName() + " " + item.itemName + " " + item.amount);
            Message.successfulGiveItems.send(player);
            item.amount = 0;
            setConfigItems(jsonItems, index-1, item, player.getName());
            return;
        }
        int freeSlots = 0;
        for (ItemStack slot: player.getInventory().getContents()) {
            if (slot == null) {
                freeSlots += 1;
            }
        }
        int expectedSlots = (int) Math.ceil(item.amount / (float) item.stackSize);
        if (expectedSlots > freeSlots) {
            Message.NotEnoughSlots.send(player);
            return;
        }
        Server server = player.getServer();
        IntStream.range(1, expectedSlots).forEach(n -> server.dispatchCommand(server.getConsoleSender(), "give " + player.getName() + " " + item.itemName + " " + item.stackSize + " " + item.itemId));
        int range = item.amount - (expectedSlots-1)*(item.stackSize);
        if (range > 0) {
            server.dispatchCommand(server.getConsoleSender(), "give " + player.getName() + " " + item.itemName + " " + range + " " + item.itemId);
        }
        Message.successfulGiveItems.send(player);
        Logger.send(player.getName(), "получил в игре с корзины", logItems);
        item.amount = 0;
        setConfigItems(jsonItems, index-1, item, player.getName());
    }
    public static void getOneAmount(Player player, int index, int amount) {
        List<String> jsonItems =  Main.getData().getConfig().getStringList("staffList." + player.getName());
        if (jsonItems.size() == 0) {
            Message.listEmpty.send(player);
            return;
        }
        if (!(0 <= index && index <= jsonItems.size())) {
            Message.indexItemNotFound.send(player);
            return;
        }
        CartItem item = gson.fromJson(jsonItems.get(index-1), CartItem.class);
        if (Objects.equals(item.itemType, "case")) {
            Server server = player.getServer();
            server.dispatchCommand(server.getConsoleSender(), "cases add " + player.getName() + " " + item.itemName + " " + amount);
            Message.successfulGiveItems.send(player);
            item.amount -= amount;
            setConfigItems(jsonItems, index-1, item, player.getName());
            return;
        }
        int freeSlots = 0;
        for (ItemStack slot: player.getInventory().getContents()) {
            if (slot == null) {
                freeSlots += 1;
            }
        }
        if (amount > item.amount) {
            Message.NotEnoughAmountItems.send(player);
            return;
        }
        int expectedSlots = (int) Math.ceil(amount / (float) item.stackSize);
        if (expectedSlots > freeSlots) {
            Message.NotEnoughSlots.send(player);
            return;
        }
        Server server = player.getServer();
        IntStream.range(1, expectedSlots).forEach(n -> server.dispatchCommand(server.getConsoleSender(), "give " + player.getName() + " " + item.itemName + " " + item.stackSize + " " + item.itemId));
        int range = amount - (expectedSlots-1)*(item.stackSize);
        if (range > 0) {
            server.dispatchCommand(server.getConsoleSender(), "give " + player.getName() + " " + item.itemName + " " + range + " " + item.itemId);
        }
        item.amount -= amount;
        setConfigItems(jsonItems, index-1, item, player.getName());
        Message.successfulGiveItems.send(player);
        List<String> logItems = new ArrayList<>();
        logItems.add("1. " + item.itemViewName + " - " + amount + " шт.");
        Logger.send(player.getName(), "получил в игре с корзины", logItems);
    }
    public static void getAll(Player player) {
        List<String> jsonItems = Main.getData().getConfig().getStringList("staffList." + player.getName());
        if (jsonItems.size() == 0) {
            Message.listEmpty.send(player);
            return;
        }
        int freeSlots = 0;
        for (ItemStack slot: player.getInventory().getContents()) {
            if (slot == null) {
                freeSlots += 1;
            }
        }
        int totalExpectedSlots = 0;
        for (String itemString: jsonItems) {
            CartItem item = gson.fromJson(itemString, CartItem.class);
            if (Objects.equals(item.itemType, "item")) totalExpectedSlots += (int) Math.ceil(item.amount / (float) item.stackSize);
        }
        if (totalExpectedSlots > freeSlots) {
            Message.NotEnoughSlots.send(player);
            return;
        }
        Server server = player.getServer();
        List<String> logItems = new ArrayList<>();
        for (String itemString: jsonItems) {
            CartItem item = gson.fromJson(itemString, CartItem.class);
            logItems.add(logItems.size() + 1 + ". " + item.itemViewName + " - " + item.amount + " шт.");
            if (Objects.equals(item.itemType, "case")) {
                server.dispatchCommand(server.getConsoleSender(), "cases add " + player.getName() + " " + item.itemName + " " + item.amount);
            } else if (Objects.equals(item.itemType, "item")) {
                int expectedSlots = (int) Math.ceil(item.amount / (float) item.stackSize);
                IntStream.range(1, expectedSlots).forEach(n -> server.dispatchCommand(server.getConsoleSender(), "give " + player.getName() + " " + item.itemName + " " + item.stackSize + " " + item.itemId)
                );
                int range = item.amount - (expectedSlots-1)*(item.stackSize);
                if (range > 0) {
                    server.dispatchCommand(server.getConsoleSender(), "give " + player.getName() + " " + item.itemName + " " + range + " " + item.itemId);
                }
            }
        }
        Message.successfulGiveItems.send(player);
        Main.getData().getConfig().set("staffList." + player.getName(), Lists.newArrayList());
        Main.getData().save();
        Logger.send(player.getName(), "получил в игре с корзины", logItems);
    }
    public static void update(CommandSender sender) {
        Thread thread = new Thread(() -> {
            List<String> jsonItems = Main.getData().getConfig().getStringList("staffList." + sender.getName());
            List<CartItem> items = CartItems.get(sender.getName());
            if (items.size() == 0) {
                Message.cartEmpty.send(sender);
                return;
            }
            jsonItems.addAll(getStringItems(items));
            Main.getData().getConfig().set("staffList." + sender.getName(), jsonItems);
            Main.getData().save();
            Message.successfulUpdateCart.send(sender);
            List<String> logItems = new ArrayList<>();
            for (CartItem item: items) {
                logItems.add(logItems.size() + 1 + ". " + item.itemViewName + " - " + item.amount + " шт.");
            }
            Logger.send(sender.getName(), "получил предметы с сайта в корзину", logItems);
        });
        thread.start();
    }
    private static List<String> getStringItems(List<CartItem> items) {
        List<String> itemsString = new ArrayList<>();
        for (CartItem item: items) {
            itemsString.add(gson.toJson(item));
        }
        return itemsString;
    }
    private static void setConfigItems(List<String> jsonItems, int index, CartItem item, String player) {
        if (item.amount == 0) {
            jsonItems.remove(index);
            Main.getData().getConfig().set("staffList." + player, jsonItems);
        } else {
            jsonItems.set(index, gson.toJson(item));
            Main.getData().getConfig().set("staffList." + player, jsonItems);
        }
        Main.getData().save();
    }
}
