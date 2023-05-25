package peru.sugoi.idlecraft.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

/**
 * Contains player's stats like STR, Health
 */
public class PlayerStats {
    private final UUID uid;
    private int str;
    private int def;
    private double health;
    private double maxHealth;

    PlayerStats(OfflinePlayer player) {
        this.uid = player.getUniqueId();

        this.str = 100;
        this.def = 100;
        this.maxHealth = 10;
        this.health = maxHealth;
    }

    /**
     * Returns STR that mainly affects damage dealt
     * @return STR
     */
    public int getStr() {
        return str;
    }

    /**
     * Returns DEF that mainly affects damage taken
     * @return DEF
     */
    public int getDef() {
        return def;
    }

    /**
     * Returns Health that affects how much damage can take
     * @return Health
     */
    public double getHealth() {
        return health;
    }

    /**
     * Returns Maximum health that affects how much health can have
     * @return Maximum health
     */
    public double getMaxHealth() {
        return maxHealth;
    }

    /**
     * Returns player of this instance
     * @return OfflinePlayer of this instance
     */
    public OfflinePlayer getPlayer() {
        return Bukkit.getPlayer(uid);
    }

    /**
     * Sets player's STR that mainly affects damage dealt
     * @param str new value
     */
    public void setStr(int str) {
        this.str = str;
    }

    /**
     * Sets DEF that mainly affects damage taken
     * @param def new value
     */
    public void setDef(int def) {
        this.def = def;
    }

    /**
     * Sets Health that affects how much damage can take.
     * Health above max health will be voided.
     * If health is 0 or less, player dies.
     * @param health new value
     */
    public void setHealth(double health) {
        if (health < 0) {
            //TODO code on death
            this.health = maxHealth;

            update();

            return;
        }

        this.health = health;

        if (this.health > maxHealth) this.health = maxHealth;

        update();
    }

    /**
     * Sets Maximum health that affects how much health can have
     * @param maxHealth new value
     */
    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    /**
     * Updates player's default health hearts.
     */
    public void update() {
        if (!getPlayer().isOnline()) return;

        Player player = getPlayer().getPlayer();

        double healthRatio = health / maxHealth;
        double maxHearts = 20 * Math.log10(maxHealth);
        double hearts = healthRatio * maxHearts;

        AttributeInstance attributeMaxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        Collection<AttributeModifier> attributeModifierCollection = attributeMaxHealth.getModifiers();

        attributeModifierCollection.stream().forEach(attributeMaxHealth::removeModifier);

        attributeMaxHealth.addModifier(new AttributeModifier("", maxHearts - 20, AttributeModifier.Operation.ADD_NUMBER));

        player.setHealth(hearts);
    }
}
