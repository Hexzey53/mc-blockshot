package fr.hexzey.blockshot;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ArenaLoader {
	public static File arenaConfigFile = new File("plugins/Blockshot/arenas.yml");
    public static FileConfiguration arenaConfig = YamlConfiguration.loadConfiguration(arenaConfigFile);
	
	public static void LoadArenas() {
		Main.unregisterArenas();
		try {
			World world = Bukkit.getWorld("world");
			for(String idArene : arenaConfig.getKeys(false))
			{
				// informations générales de l'arène
				String nom = arenaConfig.getString(idArene + ".name");
				int minPlayers = arenaConfig.getInt(idArene + ".minPlayers");
				int maxPlayers = arenaConfig.getInt(idArene + ".maxPlayers");
				
				// points d'apparition de l'équipe rouge
				ArrayList<Location> redSpawnPoints = new ArrayList<Location>();
	    		ConfigurationSection redSpawnsConfig = arenaConfig.getConfigurationSection(idArene + ".redSpawnPoints");
	    		for(String point : redSpawnsConfig.getKeys(false)) {
	    			double x = redSpawnsConfig.getDouble(point + ".x");
	    			double y = redSpawnsConfig.getDouble(point + ".y");
	    			double z = redSpawnsConfig.getDouble(point + ".z");
	    			float yaw = (float)redSpawnsConfig.getDouble(point + ".yaw");
	    			redSpawnPoints.add(new Location(world, x, y, z, yaw, 0f));
	    		}
	    		
	    		// points d'apparition de l'équipe bleue
	    		ArrayList<Location> blueSpawnPoints = new ArrayList<Location>();
	    		ConfigurationSection blueSpawnsConfig = arenaConfig.getConfigurationSection(idArene + ".blueSpawnPoints");
	    		for(String point : blueSpawnsConfig.getKeys(false)) {
	    			double x = blueSpawnsConfig.getDouble(point + ".x");
	    			double y = blueSpawnsConfig.getDouble(point + ".y");
	    			double z = blueSpawnsConfig.getDouble(point + ".z");
	    			float yaw = (float)blueSpawnsConfig.getDouble(point + ".yaw");
	    			blueSpawnPoints.add(new Location(world, x, y, z, yaw, 0f));
	    		}
	    		
	    		Arena arena = new Arena(nom, minPlayers, maxPlayers, redSpawnPoints, blueSpawnPoints);
	    		Main.registerNewArena(arena);
	    		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "BLOCKSHOT : arena " + idArene + " " + nom + " loaded.");
	    	}
	    	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "BLOCKSHOT : arenas loaded successfully.");
		}
		catch(Exception e) {
			Bukkit.getLogger().warning("BLOCKSHOT : Error while loading arenas : " + e.getMessage());
		}
	}
}
