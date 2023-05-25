package peru.sugoi.idlecraft.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

/**
 * Contains player's data like vInventory, Stats
 */
public class PlayerData {
    private static final HashMap<UUID, PlayerData> instanceMap = new HashMap<>();

    /**
     * Returns instance of given player.
     * Creates new instance if there is no PlayerData for player.
     * @param player who you want PlayerData for
     * @return PlayerData instance for param player
     */
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

    /**
     * Returns player of this instance
     * @return OfflinePlayer of this instance
     */
    public OfflinePlayer getPlayer() {
        return Bukkit.getPlayer(uid);
    }

    /**
     * Returns player's stats of this instance
     * @return PlayerStats of this instance
     */
    public PlayerStats getPlayerStats() {
        return playerStats;
    }

    /**
     * Returns Virtual Inventory of this instance
     * @return VInventory of this instance
     */
    public VInventory getVirtualInventory() {
        return vInventory;
    }

    /**
     * Puts item in Virtual Inventory.
     * @param item ItemStack picked up
     */
    public void pickUpItem(ItemStack item) {
        getVirtualInventory().addItem(item);
    }
}
