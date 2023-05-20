package peru.sugoi.idlecraft;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import peru.sugoi.idlecraft.listener.EventListener;

@SuppressWarnings("unused")
public class IdleCraft extends JavaPlugin {
    private static IdleCraft instance;

    @SuppressWarnings("unused")
    public static IdleCraft getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
    }
}
