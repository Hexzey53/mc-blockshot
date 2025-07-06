package fr.hexzey.blockshot;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import fr.hexzey.blockshot.gamemodes.Elimination;
import fr.hexzey.blockshot.tools.EmptyWorldGenerator;
import fr.hexzey.blockshot.tools.IGameMode;
import fr.hexzey.blockshot.tools.Team;

public class Main extends JavaPlugin {
	public static Plugin plugin;
	public static Plugin getPlugin() { return Main.plugin; }
	
	private static Location lobbyLoc = null;
	
	private static ArrayList<IGameMode> availableGameModes = new ArrayList<IGameMode>();
	private static IGameMode currentGameMode = null;
	
	private static ArrayList<Arena> availableArenas = new ArrayList<Arena>();
	
	public void onEnable() {
		plugin = this;
		Bukkit.getConsoleSender().sendMessage("Blockshot is enabled !");
		
		// enregistrement des events
		Bukkit.getServer().getPluginManager().registerEvents(new Events(), this);
		
		// enregistrement des commandes
		getCommand("blockshot").setExecutor(new Commands());
		getCommand("bs").setExecutor(new Commands());
		
		// charger la map world2
		WorldCreator wc = new WorldCreator("world2");
		wc.environment(Environment.NORMAL);
		wc.generator(new EmptyWorldGenerator());
		Bukkit.createWorld(wc);
		
		// enregistrer les modes de jeu
		Main.registerNewGameMode(new Elimination()); // mode "Elimination"
		Main.setCurrentGameMode(Main.availableGameModes.get(0)); // mode elimination actif par défaut
		
		// enregistrer le lobby
		// COORDONNES DANS LE CODE SOURCE -> PREFERER CHARGER DEPUIS UN FICHIER YAML
		World w = Bukkit.getWorld("world");
		Location lobby = new Location(w, 35.5d, -51.75d, -23.5d);
		setLobbyLocation(lobby);
		
		// enregistrer les cartes
		ArenaLoader.LoadArenas();
		
		// si des joueurs sont déjà en ligne, on les téléporte au lobby
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.setGameMode(GameMode.SURVIVAL);
			TeamSelector.getInstance().setPlayerTeam(player, Team.Spectator);
			player.teleport(Main.getLobbyLocation());
			player.setLevel(0); player.setExp(0);
		}
	}
	
	public static void setCurrentGameMode(IGameMode gameMode) { Main.currentGameMode = gameMode; }
	public static IGameMode getCurrentGameMode() { return Main.currentGameMode; }
	
	public static void registerNewGameMode(IGameMode gameModeInstance) { Main.availableGameModes.add(gameModeInstance); }
	public static ArrayList<IGameMode> getAvailableGameModes() { return Main.availableGameModes; }
	
	public static void setLobbyLocation(Location loc) { Main.lobbyLoc = loc; }
	public static Location getLobbyLocation() { return Main.lobbyLoc; }
	
	public static void registerNewArena(Arena arena) { Main.availableArenas.add(arena); }
	public static ArrayList<Arena> getAvailableArenas() { return Main.availableArenas; }
	public static void unregisterArenas() { Main.availableArenas = new ArrayList<Arena>(); }
}
