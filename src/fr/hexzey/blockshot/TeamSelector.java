package fr.hexzey.blockshot;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.hexzey.blockshot.tools.GameState;
import fr.hexzey.blockshot.tools.InventoryManager;
import fr.hexzey.blockshot.tools.Team;

public class TeamSelector
{
	private ArrayList<Player> spectators;
	private ArrayList<Player> blueTeam;
	private ArrayList<Player> redTeam;
	
	private static TeamSelector instance = null;
	public static TeamSelector getInstance() {
		if(TeamSelector.instance == null) { TeamSelector.instance = new TeamSelector(); }
		return TeamSelector.instance;
	}
	
	private TeamSelector() {
		this.spectators = new ArrayList<Player>();
		this.blueTeam = new ArrayList<Player>();
		this.redTeam = new ArrayList<Player>();
	}
	
	public ArrayList<Player> getSpectatorTeam() { return this.spectators; }
	public ArrayList<Player> getBlueTeam() { return this.blueTeam; }
	public ArrayList<Player> getRedTeam() { return this.redTeam; }
	
	public void setPlayerTeam(Player player, Team team) {
		// 0 = spectator, 1 = blue, 2 = red
		this.spectators.remove(player);
		this.blueTeam.remove(player);
		this.redTeam.remove(player);
		if(team == Team.Spectator) { this.spectators.add(player); }
		else if(team == Team.Blue) { this.blueTeam.add(player); }
		else { this.redTeam.add(player); }
		
		// afficher un message seulement si on est en attente
		if(Main.getCurrentGameMode() == null || Main.getCurrentGameMode().GetState() != GameState.Running) {
			String msg = ChatColor.WHITE + player.getName() + " a rejoint l'équipe ";
			switch(team) {
				case Spectator:
					msg += ChatColor.LIGHT_PURPLE + "Spectateur";
					break;
				case Blue:
					msg += ChatColor.BLUE + "Bleue";
					break;
				case Red:
					msg += ChatColor.RED + "Rouge";
					break;
				default:
					break;
			}
			for(Player p : Bukkit.getOnlinePlayers()) { p.sendMessage(msg); }
		}
		
		InventoryManager.SetInventoryForLobby(player);
	}
	
	public void unregisterPlayer(Player player) {
		this.spectators.remove(player);
		this.blueTeam.remove(player);
		this.redTeam.remove(player);
	}
	
	public Team getTeamFor(Player player) {
		if(this.spectators.contains(player)) { return Team.Spectator; }
		else if(this.blueTeam.contains(player)) { return Team.Blue; }
		else if(this.redTeam.contains(player)) { return Team.Red; }
		else { return null; }
	}
}
