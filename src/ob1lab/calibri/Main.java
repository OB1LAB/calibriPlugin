package ob1lab.calibri;
import ob1lab.calibri.command.MainCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main instance;
    private Storage data;
    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        data = new Storage("staff.yml");

        new MainCommand();
        Message.load(getConfig());
    }
    public static Main getInstance() {
        return instance;
    }
    public static Storage getData() {
        return instance.data;
    }
}
