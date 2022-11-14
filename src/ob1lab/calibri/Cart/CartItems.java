package ob1lab.calibri.Cart;

import net.minecraft.util.com.google.gson.Gson;
import ob1lab.calibri.Main;
import ob1lab.calibri.Requests.Requests;

import java.util.*;

public class CartItems {
    private static final Gson gson = new Gson();
    public static List<CartItem> get(String player) {
        Map<String, String> body = new HashMap<>();
        body.put("key", Main.getInstance().getConfig().getString("cart.serverCode"));
        body.put("player", player);
        String response = Requests.post(Main.getInstance().getConfig().getString("cart.urlServer"), gson.toJson(body));
        return Arrays.asList(gson.fromJson(response, CartItem[].class));
    }
}
