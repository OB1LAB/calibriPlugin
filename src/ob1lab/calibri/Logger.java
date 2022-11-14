package ob1lab.calibri;

import ob1lab.calibri.Requests.Requests;

import java.util.List;

public class Logger {
    public static void send(String player, String action, List<String> items) {
        String body = Message.webhook.replace("{player}", player).replace("{action}", action).replace("{items}", String.join("\\n", items)).replace("§", "&").string();
        System.out.println(body);
        Thread request = new Thread(() -> Requests.post(Main.getInstance().getConfig().getString("cart.urlWebHook"), body));
        request.start();
    }
}
