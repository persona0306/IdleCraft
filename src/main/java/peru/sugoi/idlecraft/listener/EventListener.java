package peru.sugoi.idlecraft.listener;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import peru.sugoi.idlecraft.player.PlayerData;

public class EventListener implements Listener {
    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        PlayerData playerData = PlayerData.get(player);

        playerData.pickUpItem(e.getItem().getItemStack());

        player.sendTitle(
                "",
                ChatColor.GREEN + "+ "
                        + ChatColor.AQUA + e.getItem().getItemStack().getAmount()
                        + ChatColor.RESET + "x " + e.getItem().getItemStack().getType(),
                0, 0, 15);

        e.getItem().getWorld().playSound(
                e.getItem().getLocation(),
                Sound.ENTITY_ITEM_PICKUP,
                1,
                1 + (float)Math.random());

        e.setCancelled(true);
        e.getItem().remove();
    }
}
