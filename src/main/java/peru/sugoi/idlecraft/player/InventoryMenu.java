package peru.sugoi.idlecraft.player;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class InventoryMenu {
    private static final ItemStack BLANK_ITEM;
    private static final ItemStack PREV_ITEM;
    private static final ItemStack REFRESH_ITEM;
    private static final ItemStack NEXT_ITEM;

    static {
        BLANK_ITEM = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        PREV_ITEM = new ItemStack(Material.COMPARATOR);
        REFRESH_ITEM = new ItemStack(Material.NETHER_STAR);
        NEXT_ITEM = new ItemStack(Material.REPEATER);

        ItemMeta blankMeta = BLANK_ITEM.getItemMeta();
        ItemMeta prevMeta = PREV_ITEM.getItemMeta();
        ItemMeta refreshMeta = REFRESH_ITEM.getItemMeta();
        ItemMeta nextMeta = NEXT_ITEM.getItemMeta();

        blankMeta.setDisplayName(ChatColor.RESET + "");
        prevMeta.setDisplayName(ChatColor.GREEN + "← Previous page");
        refreshMeta.setDisplayName(ChatColor.GREEN + "⇆ Refresh");
        nextMeta.setDisplayName(ChatColor.GREEN + "→ Next page");

        BLANK_ITEM.setItemMeta(blankMeta);
        PREV_ITEM.setItemMeta(prevMeta);
        REFRESH_ITEM.setItemMeta(refreshMeta);
        NEXT_ITEM.setItemMeta(nextMeta);
    }

    private final UUID uid;
    private int page;

    InventoryMenu(OfflinePlayer player) {
        this.uid = player.getUniqueId();
        page = 0;
    }

    public int getPage() {
    	return page;
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getPlayer(uid);
    }

    public void setPage(int page) {
        PlayerData playerData = PlayerData.get(getPlayer());
        VInventory vInventory = playerData.getVirtualInventory();

        this.page = page;

        int maxPage = (vInventory.getCommonItemMap().size() + vInventory.getToolList().size() - 1) / 18;
        if (this.page > maxPage) this.page = maxPage;

        if (this.page < 0) this.page = 0;

        update();
    }

    public void update() {
        if (!getPlayer().isOnline()) return;

        Player player = getPlayer().getPlayer();

        PlayerInventory inv = player.getInventory();

        for (int i = 10; i < 17; i++) {
            inv.setItem(i, BLANK_ITEM);
        }

        inv.setItem(9, PREV_ITEM);
        inv.setItem(13, REFRESH_ITEM);
        inv.setItem(17, NEXT_ITEM);

        PlayerData playerData = PlayerData.get(getPlayer());
        VInventory vInventory = playerData.getVirtualInventory();

        ImmutableMap<Material, Long> commonItemMap = vInventory.getCommonItemMap();
        Material[] commonItemKeyArray = commonItemMap.keySet().toArray(new Material[0]);

        ImmutableList<ItemStack> toolList = vInventory.getToolList();

        for (int i = 18; i < 36; i++) {
            int index = i - 18 + 18 * page;

            if (commonItemKeyArray.length > index) {
                Material material = commonItemKeyArray[index];
                long inventoryAmount = commonItemMap.get(material);

                int maxStackSize = material.getMaxStackSize();

                int amount = inventoryAmount > maxStackSize ? maxStackSize : (int)inventoryAmount;

                ItemStack item = new ItemStack(material, amount);

                ItemMeta itemMeta = item.getItemMeta();

                itemMeta.setDisplayName(ChatColor.WHITE + "Common Item: " + inventoryAmount + "x " + material);

                item.setItemMeta(itemMeta);

                inv.setItem(i, item);

            }else if (toolList.size() + commonItemKeyArray.length > index) {
                ItemStack item = toolList.get(index - commonItemKeyArray.length);

                ItemMeta itemMeta = item.getItemMeta();

                itemMeta.setDisplayName(ChatColor.WHITE + "Tool: " + item.getAmount() + "x " + item.getType());

                item.setItemMeta(itemMeta);

                inv.setItem(i, item);

            }else {
                inv.setItem(i, null);

            }
        }

        player.sendMessage("======================");
        commonItemMap.forEach((key, value) -> {
            player.sendMessage(value + "x " + key);
        });
        player.sendMessage("======================");
    }
}
