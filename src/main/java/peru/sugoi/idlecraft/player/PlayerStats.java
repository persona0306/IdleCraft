package peru.sugoi.idlecraft.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class PlayerStats {
    private final UUID uid;
    private int atk;
    private double health;
    private double maxHealth;

    PlayerStats(OfflinePlayer player) {
        this.uid = player.getUniqueId();

        this.atk = 100;
        this.maxHealth = 100;
        this.health = maxHealth;
    }

    public int getAtk() {
        return atk;
    }

    public double getHealth() {
        return health;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getPlayer(uid);
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }
}
