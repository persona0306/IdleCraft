package peru.sugoi.idlecraft;

import org.bukkit.plugin.java.JavaPlugin;

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
    }
}
