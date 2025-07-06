package fr.hexzey.blockshot.tools;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class Freezer
{
	///////////////
	// SINGLETON //
	///////////////
	private static Freezer instance = null;
	public static Freezer getInstance() {
		if(Freezer.instance == null) { Freezer.instance = new Freezer(); }
		return Freezer.instance;
	}
	
	//////////////////////
	// ATTRIBUTS PRIVES //
	//////////////////////
	private ArrayList<Player> freezedPlayers;
	
	//////////////////
	// CONSTRUCTEUR //
	//////////////////
	private Freezer() {
		this.freezedPlayers = new ArrayList<Player>();
	}
	
	////////////////////////
	// METHODES PUBLIQUES //
	////////////////////////
	public void addPlayer(Player player) {
		if(this.freezedPlayers.contains(player)) { return; }
		else { this.freezedPlayers.add(player); }
	}
	
	public void removePlayer(Player player) {
		this.freezedPlayers.remove(player);
	}
	
	public boolean isFreezed(Player player) {
		return this.freezedPlayers.contains(player);
	}
}
