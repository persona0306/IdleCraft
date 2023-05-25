package peru.sugoi.idlecraft.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class VInventory {
    private final UUID uid;

    private HashMap<Material, Long> commonItemMap = new HashMap<>();
    private ArrayList<ItemStack> toolList = new ArrayList<>();

    private InventoryMenu inventoryMenu;

    VInventory(OfflinePlayer player) {
        this.uid = player.getUniqueId();
        this.inventoryMenu = new InventoryMenu(player);
    }

    public void addItem(ItemStack item) {
        if (item.getType().equals(Material.AIR)) return;

        if (item.getMaxStackSize() == 1) {
            toolList.add(item);
            return;
        }

        Material itemType = item.getType();

        long inventoryAmount = commonItemMap.getOrDefault(itemType, 0L);

        commonItemMap.put(itemType, inventoryAmount + item.getAmount());

        inventoryMenu.update();
    }

    public void removeItem(ItemStack item) {
        toolList.remove(item);

        Material material = item.getType();

        if (commonItemMap.containsKey(material)) {
            long amount = commonItemMap.get(material) - item.getAmount();

            commonItemMap.put(material, amount);

            if (amount <= 0) commonItemMap.remove(material);
        }

        inventoryMenu.update();
    }

    public ImmutableMap<Material, Long> getCommonItemMap() {
        return ImmutableMap.copyOf(commonItemMap);
    }

    public int getPage() {
    	return inventoryMenu.getPage();
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getPlayer(uid);
    }

    public ImmutableList<ItemStack> getToolList() {
        return ImmutableList.copyOf(toolList);
    }

    public void setPage(int page) {
    	inventoryMenu.setPage(page);
    }
}
