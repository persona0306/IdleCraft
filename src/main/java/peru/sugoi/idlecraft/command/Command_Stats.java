package peru.sugoi.idlecraft.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import peru.sugoi.idlecraft.player.PlayerData;
import peru.sugoi.idlecraft.player.PlayerStats;

public class Command_Stats implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String index, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("ムリ");
            return true;
        }

        PlayerData playerData = PlayerData.get(player);

        PlayerStats playerStats = playerData.getPlayerStats();

        int health = (int)playerStats.getHealth();
        int maxHealth = (int)playerStats.getMaxHealth();
        int str = playerStats.getStr();
        int def = playerStats.getDef();

        sender.sendMessage(ChatColor.GREEN + "Health: " + ChatColor.AQUA + health + " / " + maxHealth);
        sender.sendMessage(ChatColor.RED + "STR: " + ChatColor.AQUA + str);
        sender.sendMessage(ChatColor.BLUE + "DEF: " + ChatColor.AQUA + def);

        return true;
    }
}