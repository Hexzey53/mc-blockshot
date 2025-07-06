package fr.hexzey.blockshot.tools;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.hexzey.blockshot.TeamSelector;

public class InventoryManager
{
	public static void SetInventoryForLobby(Player player) {
		player.getInventory().clear();
		
		Team playerTeam = TeamSelector.getInstance().getTeamFor(player);
		
		ItemStack is = null;
		if(playerTeam == Team.Blue) { is = new ItemStack(Material.BLUE_WOOL); }
		else if(playerTeam == Team.Red) { is = new ItemStack(Material.RED_WOOL); }
		else { is = new ItemStack(Material.BARRIER); }
		
		player.getInventory().setItem(0, is);
	}
	
	public static void SetGunInventoryFor(Player player) {
		ItemStack gun = new ItemStack(Material.IRON_HOE);
		player.getInventory().setItem(0, gun);
	}
	
	public static void OpenTeamSelectInventoryFor(Player player) {
		//player.closeInventory(); // fermer l'inventaire si le joueur en a déjà un d'ouvert
		
		Inventory inv = Bukkit.createInventory(null, 9, "Team select");
		ItemBuilder ib0 = new ItemBuilder(Material.BARRIER).displayName("Spectateurs");
		ItemBuilder ib1 = new ItemBuilder(Material.BLUE_WOOL).displayName("Equipe bleue");
		ItemBuilder ib2 = new ItemBuilder(Material.RED_WOOL).displayName("Equipe rouge");
		
		ArrayList<Player> spectators = new ArrayList<Player>(TeamSelector.getInstance().getSpectatorTeam());
		ArrayList<Player> blue = new ArrayList<Player>(TeamSelector.getInstance().getBlueTeam());
		ArrayList<Player> red = new ArrayList<Player>(TeamSelector.getInstance().getRedTeam());
		
		if(spectators.size() > 0) {for(Player p : spectators) ib0.addLine(p.getName()); }
		if(blue.size() > 0) {for(Player p : blue) ib1.addLine(p.getName()); }
		if(red.size() > 0) {for(Player p : red) ib2.addLine(p.getName()); }
		
		if(TeamSelector.getInstance().getTeamFor(player) == Team.Spectator) { // equipe spectateur
			inv.setItem(0, ib0.buildGlow());
			inv.setItem(1, ib1.build());
			inv.setItem(2, ib2.build());
		} else if(TeamSelector.getInstance().getTeamFor(player) == Team.Blue) { // equipe bleue
			inv.setItem(0, ib0.build());
			inv.setItem(1, ib1.buildGlow());
			inv.setItem(2, ib2.build());
		} else {							// equipe rouge
			inv.setItem(0, ib0.build());
			inv.setItem(1, ib1.build());
			inv.setItem(2, ib2.buildGlow());
		}
		
		player.openInventory(inv);
	}
	
}
