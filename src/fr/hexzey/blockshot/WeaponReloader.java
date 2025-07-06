package fr.hexzey.blockshot;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WeaponReloader
{
	// static pour singleton
	private static WeaponReloader instance = null;
	public static WeaponReloader getInstance() {
		if(WeaponReloader.instance == null) { WeaponReloader.instance = new WeaponReloader(); }
		return WeaponReloader.instance;
	}
	
	// attributs privés
	private ArrayList<Player> playersToReload;
	private int task;
	
	private WeaponReloader() {
		this.playersToReload = new ArrayList<Player>();
		this.task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
		    public void run() {
		    	ReloadWeapons();
		    }}, 1, 1);
	}
	
	public void AddPlayer(Player player) {
		if(this.playersToReload.contains(player) == false) { this.playersToReload.add(player); }
	}
	
	public void RemovePlayer(Player player) { this.playersToReload.remove(player); }
	
	private void ReloadWeapons() {
		if(this.playersToReload.size() > 0) {
			for(Player player : new ArrayList<Player>(this.playersToReload)) {
				if(player.getLevel() < 3) {
					float expToSet = player.getExp() + 0.05f;
					if(expToSet >= 1.0f) {
						player.setExp(0);
						player.setLevel(player.getLevel() + 1);
					} else {
						player.setExp(expToSet);
					}
					if(player.getLevel() >= 3) {
						player.setLevel(3);
						//this.RemovePlayer(player);
					}
				}
			}
		}
	}
	
}
