package peru.sugoi.idlecraft.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class VInventory {
    private final UUID uid;
    HashMap<Material, Long> commonItemMap = new HashMap<>();
    ArrayList<ItemStack> toolList = new ArrayList<>();

    VInventory(OfflinePlayer player) {
        this.uid = player.getUniqueId();
    }

    public void addItem(ItemStack item) {
        if (item.getMaxStackSize() == 1) {
            toolList.add(item);
            return;
        }

        Material itemType = item.getType();

        long inventoryAmount = commonItemMap.getOrDefault(itemType, 0L);

        commonItemMap.put(itemType, inventoryAmount + item.getAmount());
    }

    public ImmutableMap<Material, Long> getCommonItemMap() {
        return ImmutableMap.copyOf(commonItemMap);
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getPlayer(uid);
    }

    public ImmutableList<ItemStack> getToolList() {
        return ImmutableList.copyOf(toolList);
    }
}
