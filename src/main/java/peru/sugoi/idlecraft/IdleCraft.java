package peru.sugoi.idlecraft;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import peru.sugoi.idlecraft.command.Command_Stats;
import peru.sugoi.idlecraft.listener.EventListener;

public class IdleCraft extends JavaPlugin {
    private static IdleCraft instance;

    public static IdleCraft getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        getCommand("stats").setExecutor(new Command_Stats());
    }
}
