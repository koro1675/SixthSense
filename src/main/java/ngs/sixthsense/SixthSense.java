package ngs.sixthsense;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.List;

public final class SixthSense extends JavaPlugin implements Listener {

    ActionBarAPI actionBarAPI;

    Player player;
    LivingEntity target;

    List<Player> cooldown = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        actionBarAPI = new ActionBarAPI();
        getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        player = e.getPlayer();
        if (cooldown.contains(target)) {
            return;
        }
        getTarget();
        if (target == null) {
            return;
        }

        if (!(target instanceof Player)) {
            return;
        }

        Player target = (Player) this.target;

        if (Bukkit.getServer().getOnlinePlayers().contains(Bukkit.getServer().getPlayer(target.getName()))) {
            actionBarAPI.sendActionBar(target, "§3§lMech§r: §b§lマスター、§e§l" + player.getName() + "§b§lに視認されています");
            cooldown.add(target);
            new BukkitRunnable() {
                @Override
                public void run() {
                    cooldown.remove(target);
                }
            }.runTaskTimer(this, 20, 0);
        }
    }

    void getTarget() {
        List<Entity> nearbyE = player.getNearbyEntities(50, 50, 50);
        ArrayList<LivingEntity> livingE = new ArrayList<LivingEntity>();

        for (Entity e : nearbyE) {
            if (e instanceof LivingEntity) {
                livingE.add((LivingEntity) e);
            }
        }

        this.target = null;
        BlockIterator bItr = new BlockIterator(this.player, 50);
        Block block;
        Location loc;
        int bx, by, bz;
        double ex, ey, ez;
        // loop through player's line of sight
        while (bItr.hasNext()) {
            block = bItr.next();
            bx = block.getX();
            by = block.getY();
            bz = block.getZ();
            // check for entities near this block in the line of sight
            for (LivingEntity e : livingE) {
                loc = e.getLocation();
                ex = loc.getX();
                ey = loc.getY();
                ez = loc.getZ();
                if ((bx-.75 <= ex && ex <= bx+1.75) && (bz-.75 <= ez && ez <= bz+1.75) && (by-1 <= ey && ey <= by+2.5)) {
                    // entity is close enough, set target and stop
                    this.target = e;
                    break;
                }
            }
        }

    }
}
