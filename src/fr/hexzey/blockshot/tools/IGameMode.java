package fr.hexzey.blockshot.tools;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import fr.hexzey.blockshot.Arena;

public interface IGameMode {
	// informations sur le mode de jeu
	public String GetName();
	
	// statut de la partie
	public void SetState(GameState state);
	public GameState GetState();
	
	// arène dans laquelle se déroule la partie
	public void SetArena(Arena arena);
	public Arena GetArena();
	
	// récupérer le score de chaque équipe
	public int GetBlueScore();
	public int GetRedScore();
	
	// gestion du début & fin de partie
	public void StartGame(ArrayList<Player> bluePlayers, ArrayList<Player> redPlayers);
	public void EndGame(Team winner);
	
	// début & fin de round
	public void StartRound();
	public void EndRound(Team winner);
	
	// mort d'un joueur
	public void KillPlayer(Player shooter,Player target);
	// apparition d'un joueur
	public void SpawnPlayer(Player player, Location location);
	
	// liste des joueurs (équipe rouge)
	public ArrayList<Player> GetRedPlayers();
	public ArrayList<Player> GetRedPlayersAlive();
	// liste des joueurs (équipe bleue)
	public ArrayList<Player> GetBluePlayers();
	public ArrayList<Player> GetBluePlayersAlive();
	// récupérer l'équipe d'un joueur
	public Team getPlayerTeam(Player player);
	
	// événements en jeu
	public void onPlayerHit(Player shooter, Player target); // lorsqu'un joueur se fait toucher
	public void onPlayerDisconnect(Player player);			// lorsqu'un joueur se déconnecte
	public void onPlayerShoot(Player shooter);				// lorsqu'un joueur tire
	public void onPlayerMove(Player player);				// lorsqu'un joueur se déplace
	
	// gestion du scoreboard
	public void UpdateScoreboard();
	public Scoreboard GetScoreboard();
}