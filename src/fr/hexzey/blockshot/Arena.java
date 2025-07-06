package fr.hexzey.blockshot;

import java.util.ArrayList;

import org.bukkit.Location;

public class Arena
{
	private String name;
	private int minPlayers;
	private int maxPlayers;
	
	private ArrayList<Location> redSpawnpoints;
	private ArrayList<Location> blueSpawnpoints;
	
	public Arena(String _name, int _minPlayers, int _maxPlayers, ArrayList<Location> _redSpawns, ArrayList<Location> _blueSpawns) {
		this.name = _name;
		this.minPlayers = _minPlayers;
		this.maxPlayers = _maxPlayers;
		this.redSpawnpoints = _redSpawns;
		this.blueSpawnpoints = _blueSpawns;
	}
	
	public int getMinPlayers() { return this.minPlayers; }
	public int getMaxPlayers() { return this.maxPlayers; }
	public ArrayList<Location> getRedSpawnpoints() { return this.redSpawnpoints; }
	public ArrayList<Location> getBlueSpawnpoints() { return this.blueSpawnpoints; }
}
