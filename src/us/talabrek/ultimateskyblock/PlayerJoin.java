package us.talabrek.ultimateskyblock;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerJoin implements Listener {
    private Player hungerman = null;
    private SkullMeta meta = null;
    /*  47: 37 */ int randomNum = 0;
    /*  48: 39 */ Player p = null;

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerFoodChange(final FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            hungerman = (Player) event.getEntity();
            if (hungerman.getWorld().getName().equalsIgnoreCase(Settings.general_worldName)) {
                if (hungerman.getFoodLevel() > event.getFoodLevel()) {
                    if (uSkyBlock.getInstance().playerIsOnIsland(hungerman)) {
                        if (VaultHandler.checkPerk(hungerman.getName(), "usb.extra.hunger", hungerman.getWorld())) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (Settings.extras_obsidianToLava && uSkyBlock.getInstance().playerIsOnIsland(event.getPlayer())) {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                    && event.getPlayer().getItemInHand().getType() == Material.BUCKET
                    && event.getClickedBlock().getType() == Material.OBSIDIAN
                    && event.getClickedBlock().getRelative(event.getBlockFace()).getType() == Material.AIR) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Changing your obsidian back into lava. Be careful!");
                event.getClickedBlock().setType(Material.AIR);
                event.getPlayer().getInventory().removeItem(new ItemStack[]{new ItemStack(Material.BUCKET, 1)});
                event.getPlayer().getInventory().addItem(new ItemStack[]{new ItemStack(Material.LAVA_BUCKET, 1)});
                event.getPlayer().updateInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (!uSkyBlock.isSkyBlockWorld(event.getPlayer().getWorld()))
            return;

        uSkyBlock.getInstance().onEnterSkyBlock(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        if (!uSkyBlock.isSkyBlockWorld(event.getPlayer().getWorld()))
            uSkyBlock.getInstance().onLeaveSkyBlock(event.getPlayer().getUniqueId());
        else
            uSkyBlock.getInstance().onEnterSkyBlock(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        uSkyBlock.getInstance().onLeaveSkyBlock(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void guiClick(InventoryClickEvent event) {
        if (event.getInventory().getName().contains("Challenge Menu")) {
            event.setCancelled(true);
            if ((event.getSlot() < 0) || (event.getSlot() > 35)) {
                return;
            }
            if ((event.getCurrentItem().getType() != Material.DIRT) && (event.getCurrentItem().getType() != Material.IRON_BLOCK) && (event.getCurrentItem().getType() != Material.GOLD_BLOCK) && (event.getCurrentItem().getType() != Material.DIAMOND_BLOCK)) {
                this.p = ((Player) event.getWhoClicked());

                this.p.closeInventory();
                if (event.getCurrentItem().getItemMeta() != null) {
                    this.p.performCommand("c c " + event.getCurrentItem().getItemMeta().getDisplayName().replace("§e", "").replace("§8", "").replace("§a", "").replace("§2", "").replace("§l", ""));
                    this.p.closeInventory();
                }
                this.p.openInventory(uSkyBlock.getInstance().displayChallengeGUI(this.p));
            } else {
                this.p.closeInventory();
            }
        }
    }
}