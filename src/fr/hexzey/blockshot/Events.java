package fr.hexzey.blockshot;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import fr.hexzey.blockshot.tools.Freezer;
import fr.hexzey.blockshot.tools.GameState;
import fr.hexzey.blockshot.tools.InventoryManager;
import fr.hexzey.blockshot.tools.Raycast;
import fr.hexzey.blockshot.tools.Team;

public class Events implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.setHealth(20);
		if(Main.getCurrentGameMode() != null && Main.getCurrentGameMode().GetState() == GameState.Running) {
			player.setGameMode(GameMode.SPECTATOR);
		} else {
			player.setGameMode(GameMode.SURVIVAL);
			TeamSelector.getInstance().setPlayerTeam(player, Team.Spectator);
		}
		player.teleport(Main.getLobbyLocation());
		player.setLevel(0);player.setExp(0);
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event)
	{
		if(event.getPlayer().getGameMode() != GameMode.CREATIVE) { event.setCancelled(true); }
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player)event.getWhoClicked();
		
		if(player.getGameMode() != GameMode.CREATIVE) {
			ItemStack item = event.getCurrentItem();
			if(item == null) { return; }
			
			if(event.getView().getTitle().equalsIgnoreCase("Team select")) {
				if(item.getType() == Material.BARRIER) {
					TeamSelector.getInstance().setPlayerTeam(player, Team.Spectator);
					player.closeInventory();
					event.setCancelled(true);
				} else if(item.getType() == Material.BLUE_WOOL) {
					TeamSelector.getInstance().setPlayerTeam(player, Team.Blue);
					player.closeInventory();
					event.setCancelled(true);
				} else if(item.getType() == Material.RED_WOOL) {
					TeamSelector.getInstance().setPlayerTeam(player, Team.Red);
					player.closeInventory();
					event.setCancelled(true);
				}
			}
			event.setCancelled(true);
		}
		
		
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(player.getGameMode() != GameMode.CREATIVE) {
			if(Main.getCurrentGameMode() != null && Main.getCurrentGameMode().GetState() == GameState.Ready) {
				if(event.getItem() != null) {
					if(event.getItem().getType() == Material.BLUE_WOOL || event.getItem().getType() == Material.RED_WOOL ||
						event.getItem().getType() == Material.BARRIER) {
						InventoryManager.OpenTeamSelectInventoryFor(player);
						event.setCancelled(true);
					}
				}
			}
			
			if(event.getItem() != null && event.getItem().getType() == Material.IRON_HOE && player.getLevel() >= 1) {
				player.setLevel(player.getLevel() -1);
				
				Color rayColor = Color.fromRGB(255,0,0);
				Team playerTeam = Main.getCurrentGameMode().getPlayerTeam(player);
				if(playerTeam == Team.Blue) { rayColor = Color.fromRGB(0,0,255); }
				else if(playerTeam == Team.Red) { rayColor = Color.fromRGB(255,0,0); }
				for(Player p : Bukkit.getOnlinePlayers()) {
					p.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.7f, 1.9f);
				};
				DustOptions data = new DustOptions(rayColor, 1);
				Raycast rc = new Raycast(player.getEyeLocation(), player.getEyeLocation().getDirection(), 100d, 0.12d, Particle.REDSTONE, data);
				ArrayList<Object> hits = rc.getResult();
				
				for(Object obj : hits) {
					if(obj instanceof Block) {
						Block b = (Block)obj;
					} else if(obj instanceof Entity) {
						Entity e = (Entity)obj;
						if(e instanceof Player) {
							Player p = (Player) e;
							if(Main.getCurrentGameMode().GetBluePlayersAlive().contains(p) || Main.getCurrentGameMode().GetRedPlayersAlive().contains(p)) {
								Main.getCurrentGameMode().onPlayerHit(player, p);
							}
						}
					}
				}
				
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.getPlayer().getGameMode() != GameMode.CREATIVE) { event.setCancelled(true); }
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.getPlayer().getGameMode() != GameMode.CREATIVE) { event.setCancelled(true); }
	}
	@EventHandler
	public void onHungerDeplete(FoodLevelChangeEvent event) {
		event.setFoodLevel(20);
	}
	@EventHandler
	public void onDamageEvent(EntityDamageEvent event) {
		event.setCancelled(true);
	}
	@EventHandler
	public void onRegainHealth(EntityRegainHealthEvent event) {
		event.setCancelled(true);
	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if(Freezer.getInstance().isFreezed(event.getPlayer())) {
			Location from = event.getFrom();
			Location to = event.getTo();
			Location newLoc = new Location(from.getWorld(), from.getX(), to.getY(), from.getZ(), to.getYaw(), to.getPitch());
			event.setTo(newLoc);
		}
		else if(Main.getCurrentGameMode() != null && Main.getCurrentGameMode().GetState() == GameState.Running) {
			Main.getCurrentGameMode().onPlayerMove(event.getPlayer()); // déclencher l'event dans le mode de jeu
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(Main.getCurrentGameMode() != null && Main.getCurrentGameMode().GetState() == GameState.Running) {
			Main.getCurrentGameMode().onPlayerDisconnect(event.getPlayer()); // déclencher l'event dans le mode de jeu
		}
		TeamSelector.getInstance().unregisterPlayer(event.getPlayer());
		Freezer.getInstance().removePlayer(event.getPlayer());
		WeaponReloader.getInstance().RemovePlayer(event.getPlayer());
	}
}