package peru.sugoi.idlecraft.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import peru.sugoi.idlecraft.player.PlayerData;
import peru.sugoi.idlecraft.player.PlayerStats;
import peru.sugoi.idlecraft.player.VInventory;

public class EventListener implements Listener {

	@EventHandler
	public void onClickInventory(InventoryClickEvent e) {

		if (e.getClickedInventory() == null) return;

		Player player = (Player) e.getWhoClicked();

		if (!e.getInventory().getType().equals(InventoryType.CRAFTING)) {
			e.setCancelled(true);
			return;
		}

		if (e.getSlot() < 9 &&
				!e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) return;

		e.setCancelled(true);

		PlayerData playerData = PlayerData.get(player);

		VInventory vInventory = playerData.getVirtualInventory();

		if (e.getSlot() == 9) vInventory.setPage(vInventory.getPage() - 1);
		if (e.getSlot() == 17) vInventory.setPage(vInventory.getPage() + 1);

		if (8 < e.getSlot() && e.getSlot() < 18) return;

		if (e.getCurrentItem() == null && e.getCursor() == null) return;
		if (e.getCurrentItem().getType().equals(Material.AIR) && e.getCursor().getType().equals(Material.AIR)) return;

		ItemStack item = e.getCursor().getType().equals(Material.AIR) ? e.getCurrentItem().clone() : e.getCursor().clone();

		player.setCooldown(Material.AIR, 60);

		ItemStack itemAdd = null;
		ItemStack itemRemove = null;

		if (e.getAction().equals(InventoryAction.DROP_ALL_SLOT)) {
			itemRemove = e.getCurrentItem().clone();

			Item itemEntity = player.getWorld().dropItem(player.getEyeLocation(), itemRemove);

			itemEntity.setPickupDelay(40);
			itemEntity.setVelocity(player.getLocation().getDirection().multiply(0.35));
		}

		if (e.getAction().equals(InventoryAction.DROP_ONE_SLOT)) {
			itemRemove = e.getCurrentItem().clone();
			itemRemove.setAmount(1);

			Item itemEntity = player.getWorld().dropItem(player.getEyeLocation(), itemRemove);

			itemEntity.setPickupDelay(40);
			itemEntity.setVelocity(player.getLocation().getDirection().multiply(0.35));
		}

		if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
			itemRemove = e.getCurrentItem().clone();

			e.getWhoClicked().setItemOnCursor(itemRemove);
		}

		if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
			int amount = (int) Math.ceil((double)item.getAmount() / 2);

			itemRemove = e.getCurrentItem().clone();
			itemRemove.setAmount(amount);

			e.getWhoClicked().setItemOnCursor(itemRemove);
		}

		if (e.getAction().equals(InventoryAction.PLACE_ALL)) {
			itemAdd = e.getCursor().clone();

			e.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
		}

		if (e.getAction().equals(InventoryAction.PLACE_ONE)) {
			itemAdd = e.getCursor().clone();
			itemAdd.setAmount(1);

			ItemStack itemCursor = e.getWhoClicked().getItemOnCursor();

			int amount = itemCursor.getAmount() - 1;

			itemCursor.setAmount(amount);
		}

		if (e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
			if (e.getSlot() < 9) {
				itemAdd = e.getCurrentItem().clone();

				e.getClickedInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));

			}else {
				int slot = -1;

				for (int i = 0; i < 9; i++) {
					ItemStack inventoryItem = e.getClickedInventory().getItem(i);

					if (inventoryItem == null
							|| inventoryItem.getType().equals(Material.AIR)) {
						slot = i;
						break;
					}
				}

				if (slot == -1) return;

				itemRemove = e.getCurrentItem().clone();

				e.getClickedInventory().setItem(slot, itemRemove);
				e.getClickedInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));

			}
		}

		if (itemAdd != null) vInventory.addItem(itemAdd);
		if (itemRemove != null) vInventory.removeItem(itemRemove);

	}

	@EventHandler
	public void onDragInventory(InventoryDragEvent e) {
		if (e.getInventory() == null) return;

		if (!e.getInventory().getType().equals(InventoryType.CRAFTING)) {
			e.setCancelled(true);
			return;
		}

		boolean isHotbarOnly = true;
		for (int slot : e.getInventorySlots()) {
			if (slot > 8) isHotbarOnly = false;
		}

		if (isHotbarOnly) return;

		e.setCancelled(true);
	}

	@EventHandler
	public void onKillEntity(EntityDeathEvent e) {
		if (e.getEntity().getKiller() == null) return;

		Player player = e.getEntity().getKiller();

		PlayerData playerData = PlayerData.get(player);

		PlayerStats playerStats = playerData.getPlayerStats();

		int entityMaxHealth = (int)e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

		playerStats.setStr(playerStats.getStr() + entityMaxHealth);

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
