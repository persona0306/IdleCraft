package peru.sugoi.idlecraft.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData {
    private static final HashMap<UUID, PlayerData> instanceMap = new HashMap<>();

    public static PlayerData get(OfflinePlayer player) {
        PlayerData instance = instanceMap.get(player.getUniqueId());

        if (instance == null) {
            return new PlayerData(player);
        }

        return instance;
    }

    private final UUID uid;
    private final VInventory vInventory;
    private final PlayerStats playerStats;

    private PlayerData(OfflinePlayer player) {
        this.uid = player.getUniqueId();
        this.vInventory = new VInventory(player);
        this.playerStats = new PlayerStats(player);

        instanceMap.put(player.getUniqueId(), this);
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getPlayer(uid);
    }

    public PlayerStats getPlayerStats() {
        return playerStats;
    }

    public VInventory getVirtualInventory() {
        return vInventory;
    }

    public void pickUpItem(ItemStack item) {
        getVirtualInventory().addItem(item);
    }
}
