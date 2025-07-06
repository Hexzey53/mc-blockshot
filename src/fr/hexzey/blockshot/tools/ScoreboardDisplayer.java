package fr.hexzey.blockshot.tools;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardDisplayer
{
	///////////////
	// SINGLETON //
	///////////////
	private static ScoreboardDisplayer instance = null;
	public static ScoreboardDisplayer getInstance() {
		if(ScoreboardDisplayer.instance == null) { ScoreboardDisplayer.instance = new ScoreboardDisplayer(); }
		return ScoreboardDisplayer.instance;
	}
	
	//////////////////////
	// ATTRIBUTS PRIVES //
	//////////////////////
	private Scoreboard activeScoreboard;
	
	private ScoreboardDisplayer() {
		this.activeScoreboard = null;
	}
	
	public void SetActiveScoreboard(Scoreboard sb) {
		if(this.activeScoreboard != null) { for(Player p : Bukkit.getOnlinePlayers()) { this.RemovePlayer(p); } }
		this.activeScoreboard = sb;
		if(sb != null) { for(Player p : Bukkit.getOnlinePlayers()) { this.AddPlayer(p); } }
	}
	
	public void AddPlayer(Player player) {
		if(this.activeScoreboard != null) { player.setScoreboard(this.activeScoreboard); }
	}
	
	public void RemovePlayer(Player player) { player.setScoreboard(null); }	
}