package peru.sugoi.idlecraft.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import peru.sugoi.idlecraft.IdleCraft;
import peru.sugoi.idlecraft.player.PlayerData;
import peru.sugoi.idlecraft.player.PlayerStats;
import peru.sugoi.idlecraft.player.VInventory;

public class EventListener implements Listener {

    @EventHandler
    public void onClickInventory(InventoryClickEvent e) {
        e.getWhoClicked().sendMessage("click");
        e.getWhoClicked().sendMessage("cursor: " + (e.getCursor() == null ? "null" : "" + e.getCursor().getType() + e.getCursor().getAmount()));
        e.getWhoClicked().sendMessage("current: " + (e.getCursor() == null ? "null" : "" + e.getCurrentItem().getType() + e.getCurrentItem().getAmount()));

        if (e.getClickedInventory() == null) return;

        Player player = (Player) e.getWhoClicked();

        Inventory inv = player.getInventory();

        if (!e.getClickedInventory().equals(inv)) {
            e.setCancelled(true);
            return;
        }

        if (e.getSlot() < 9) return;

        if (8 < e.getSlot() && e.getSlot() < 18) {
            e.setCancelled(true);
            return;
        }

        if (e.getCurrentItem() == null && e.getCursor() == null) return;
        if (e.getCurrentItem().getType().equals(Material.AIR) && e.getCursor().getType().equals(Material.AIR)) return;

        PlayerData playerData = PlayerData.get(player);

        VInventory vInventory = playerData.getVirtualInventory();

        ItemStack item = e.getCursor().getType().equals(Material.AIR) ? e.getCurrentItem().clone() : e.getCursor().clone();

        if (e.getCursor().getType().equals(Material.AIR)) {
            vInventory.removeItem(item);
            e.setCurrentItem(item);

        }else {
            e.getCursor().setAmount(0);
            e.setCancelled(true);

            vInventory.addItem(item);
        }

        e.getWhoClicked().sendMessage("cursor: " + (e.getCursor() == null ? "null" : "" + e.getCursor().getType() + e.getCursor().getAmount()));
        e.getWhoClicked().sendMessage("current: " + (e.getCursor() == null ? "null" : "" + e.getCurrentItem().getType() + e.getCurrentItem().getAmount()));
    }

    @EventHandler
    public void onDragInventory(InventoryDragEvent e) {
        Inventory inv = e.getWhoClicked().getInventory();

        if (!e.getInventory().equals(inv)) {
            return;
        }

        boolean isHotBar = true;

        for (Integer slot : e.getInventorySlots()) {
            if (slot > 8) {
                isHotBar = false;
                break;
            }
        }

        if (isHotBar) return;

        Player player = (Player) e.getWhoClicked();

        PlayerData playerData = PlayerData.get(player);

        VInventory vInventory = playerData.getVirtualInventory();

        e.getCursor().setAmount(0);
        e.setCancelled(true);

        vInventory.addItem(e.getCursor());
    }

    @EventHandler
    public void onKillEntity(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) return;

        Player player = e.getEntity().getKiller();

        PlayerData playerData = PlayerData.get(player);

        PlayerStats playerStats = playerData.getPlayerStats();

        VInventory vInventory = playerData.getVirtualInventory();

        int entityMaxHealth = (int)e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

        playerStats.setStr(playerStats.getStr() + entityMaxHealth);

        e.getDrops().stream().forEach(item -> {

        });
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        PlayerData playerData = PlayerData.get(player);

        playerData.pickUpItem(e.getItem().getItemStack());

        int amount = e.getItem().getItemStack().getAmount();
        Material type = e.getItem().getItemStack().getType();

        player.sendTitle(
                "",
                ChatColor.GREEN + "+ "
                        + ChatColor.AQUA + amount
                        + ChatColor.RESET + "x " + type,
                0, 0, 15);

        e.getItem().getWorld().playSound(
                e.getItem().getLocation(),
                Sound.ENTITY_ITEM_PICKUP,
                1,
                1 + (float)Math.random());

        PlayerStats playerStats = playerData.getPlayerStats();

        if (type.getMaxStackSize() == 1) {
            playerStats.setDef(playerStats.getDef() + 5 * amount);
        }else {
            playerStats.setMaxHealth(playerStats.getMaxHealth() + amount);
        }

        playerStats.setHealth(playerStats.getHealth() + amount * playerStats.getMaxHealth() / 100);
        player.setFoodLevel(player.getFoodLevel() + 1);

        e.setCancelled(true);
        e.getItem().remove();
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) return;

        PlayerData playerData = PlayerData.get(player);

        PlayerStats playerStats = playerData.getPlayerStats();

        double strMultiplier = (double)playerStats.getStr() / 100D;

        double lastDamage = e.getDamage() * strMultiplier;

        e.setDamage(lastDamage);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        PlayerData playerData = PlayerData.get(player);

        PlayerStats playerStats = playerData.getPlayerStats();

        double defMultiplier = (double)playerStats.getDef() / 100D;

        double lastDamage = e.getDamage() / defMultiplier;

        double healthAfterDamage = playerStats.getHealth() - lastDamage;

        playerStats.setHealth(healthAfterDamage);

        e.setDamage(0);
    }

}
