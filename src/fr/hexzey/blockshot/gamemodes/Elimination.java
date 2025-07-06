package fr.hexzey.blockshot.gamemodes;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import fr.hexzey.blockshot.Arena;
import fr.hexzey.blockshot.Main;
import fr.hexzey.blockshot.TeamSelector;
import fr.hexzey.blockshot.WeaponReloader;
import fr.hexzey.blockshot.tools.Freezer;
import fr.hexzey.blockshot.tools.GameState;
import fr.hexzey.blockshot.tools.IGameMode;
import fr.hexzey.blockshot.tools.InventoryManager;
import fr.hexzey.blockshot.tools.ScoreboardBuilder;
import fr.hexzey.blockshot.tools.ScoreboardDisplayer;
import fr.hexzey.blockshot.tools.Team;

public class Elimination implements IGameMode
{
	private GameState gameState;
	private ArrayList<Player> bluePlayers;
	private ArrayList<Player> redPlayers;
	private ArrayList<Player> bluePlayersAlive;
	private ArrayList<Player> redPlayersAlive;
	private Scoreboard scoreboard;
	
	private Arena arena;
	
	private int blueScore; private int redScore; private int maxScore;
	
	public Elimination() {
		this.gameState = GameState.Ready;
		this.bluePlayers = new ArrayList<Player>();
		this.redPlayers = new ArrayList<Player>();
		this.blueScore = 0; this.redScore = 0; this.maxScore = 3;
		this.arena = null;
	}
	
	@Override
	public String GetName() { return "Elimination"; }
	
	@Override
	public void SetState(GameState state) { this.gameState = state; }

	@Override
	public GameState GetState() { return this.gameState; }
	
	@Override
	public void SetArena(Arena arena) { this.arena = arena; }
	
	@Override
	public Arena GetArena() { return this.arena; }
	
	@Override
	public int GetBlueScore() { return this.blueScore; }
	
	@Override
	public int GetRedScore() { return this.redScore; }

	@Override
	public void StartGame(ArrayList<Player> _bluePlayers, ArrayList<Player> _redPlayers) {
		// gestion des spectateurs
		for(Player spec : TeamSelector.getInstance().getSpectatorTeam()) {
			spec.getInventory().clear();
			spec.setGameMode(GameMode.SPECTATOR);
			spec.teleport(this.arena.getBlueSpawnpoints().get(0));
		}
		/// gestion des joueurs de chaque équipe
		this.gameState = GameState.Running;
		this.bluePlayers = _bluePlayers; this.redPlayers = _redPlayers;
		this.blueScore = 0; this.redScore = 0;
		ArrayList<Player> allPlayers = new ArrayList<Player>(bluePlayers);
		allPlayers.addAll(redPlayers);
		this.UpdateScoreboard(); // mettre à jour le scoreboard affiché
		this.StartRound(); // démarrer la manche
	}

	@Override
	public void EndGame(Team winner) {
		String msg = ChatColor.GREEN + "Victoire de l'équipe ";
		if(winner == Team.Blue) {
			msg += ChatColor.BLUE + "bleue";
		} else {
			msg += ChatColor.RED + " rouge";
		}
		msg += ChatColor.GREEN + " !";
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(msg);
			player.setHealth(20);
		}
		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> {
			ScoreboardDisplayer.getInstance().SetActiveScoreboard(null); // ne plus afficher de scoreboard
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.setGameMode(GameMode.SURVIVAL);
				player.teleport(Main.getLobbyLocation());
				InventoryManager.SetInventoryForLobby(player);
			}
		}, 3*20L); // retour au lobby après 3 secondes
		
		this.bluePlayers = new ArrayList<Player>(); this.redPlayers = new ArrayList<Player>();
		this.gameState = GameState.Ready;
	}

	@Override
	public void StartRound() {
		ArrayList<Player> allPlayers = new ArrayList<Player>(this.bluePlayers);
		allPlayers.addAll(this.redPlayers);
		for(Player player : allPlayers) {
			player.getInventory().clear();
			player.setHealth(4); // 4 hp = 2 coeurs
		}
		// créer les listes des joueurs en vie dans la manche
		this.bluePlayersAlive = new ArrayList<Player>(this.bluePlayers);
		this.redPlayersAlive = new ArrayList<Player>(this.redPlayers);
		// jouer l'animation de spawn aux joueurs de chaque équipe
		for(Player player : this.bluePlayers) { this.SpawnPlayer(player, this.arena.getBlueSpawnpoints().get(0)); }
		for(Player player : this.redPlayers) { this.SpawnPlayer(player, this.arena.getRedSpawnpoints().get(0)); }
	}

	@Override
	public void EndRound(Team winner) {
		// vider la liste des joueurs en vie
		this.bluePlayersAlive = new ArrayList<Player>();
		this.redPlayersAlive = new ArrayList<Player>();
		
		// actualiser le score
		if(winner == Team.Blue) { this.blueScore++; }
		else { this.redScore++; }
		
		// mettre fin à la partie si le score maximal est atteint
		if(this.blueScore >= this.maxScore) { this.EndGame(Team.Blue); return; }
		else if(this.redScore >= this.maxScore) { this.EndGame(Team.Red); return; }
		
		// sinon démarrer une nouvelle manche
		String msg = "";
		if(winner == Team.Blue) {
			msg += ChatColor.BLUE + "Bleu ";
		} else {
			msg += ChatColor.RED + "Rouge ";
		}
		msg += ChatColor.WHITE + "remporte la manche !";
		String msgScore = ChatColor.GOLD + "Score : " + ChatColor.BLUE + String.valueOf(this.blueScore) + ChatColor.GOLD + " - " + ChatColor.RED +  String.valueOf(this.redScore);
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(msg);
			p.sendTitle(msg, null, 1, 45, 1);
			p.sendMessage(msgScore);
		}
		this.UpdateScoreboard(); // mettre à jour le scoreboard affiché
		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> this.StartRound(), 3*20L);
	}

	@Override
	public void KillPlayer(Player shooter, Player target) {
		// récupérer l'équipe du joueur éliminé
		Team targetTeam = this.getPlayerTeam(target);
		if(targetTeam == null) { return; }
		
		Team shooterTeam = this.getPlayerTeam(shooter);
		if(shooterTeam == null) { return; }
		
		target.setGameMode(GameMode.SPECTATOR);
		
		// envoyer un message pour informer de la mort du joueur
		String msg = "";
		if(targetTeam == Team.Blue) { msg += ChatColor.BLUE + target.getName(); }
		else {msg += ChatColor.RED + target.getName(); }
		msg += ChatColor.LIGHT_PURPLE + " a été éliminé par ";
		if(shooterTeam == Team.Blue) { msg += ChatColor.BLUE + shooter.getName(); }
		else {msg += ChatColor.RED + shooter.getName(); }
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(msg);
		}
		
		// eliminer le joueur de la manche en cours
		if(targetTeam == Team.Blue) {
			this.GetBluePlayersAlive().remove(target); // retirer le joueur de la liste des joueurs en vie de son équipe
			if(this.GetBluePlayersAlive().size() < 1) { this.EndRound(Team.Red); } // mettre fin au round si l'équipe n'a plus aucun joueur en vie
		} else {
			this.GetRedPlayersAlive().remove(target); // retirer le joueur de la liste des joueurs en vie de son équipe
			if(this.GetRedPlayersAlive().size() < 1) { this.EndRound(Team.Blue); } // mettre fin au round si l'équipe n'a plus aucun joueur en vie
		}
	}

	@Override
	public void SpawnPlayer(Player player, Location location) {
		// téléporter et geler le joueur
		player.removePotionEffect(PotionEffectType.SPEED);
		player.setGameMode(GameMode.SURVIVAL);
		player.teleport(location);
		Freezer.getInstance().addPlayer(player);
		WeaponReloader.getInstance().RemovePlayer(player);
		player.setLevel(0);
		// donner l'armure colorée au joueur
		this.GiveColoredArmor(player);
		// afficher le décompte puis libérer le joueur
		int delay = 1;
		if(this.blueScore == this.maxScore-1 && this.redScore == this.maxScore-1) {
			Bukkit.getScheduler().runTaskLater(Main.plugin, () -> player.sendTitle(ChatColor.RED + "DERNIERE MANCHE", ChatColor.WHITE + "de la partie", 1, 45, 10), delay*20L);
			Bukkit.getScheduler().runTaskLater(Main.plugin, () -> player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 2.0f), delay*20L);
			delay += 3;
		}
		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f), delay*20L);
		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> player.sendTitle(ChatColor.GREEN + "3", null, 1, 15, 1), delay*20L);
		delay++;
		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f), delay*20L);
		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> player.sendTitle(ChatColor.GREEN + "2", null, 1, 15, 1), delay*20L);
		delay++;
		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f), delay*20L);
		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> player.sendTitle(ChatColor.GREEN + "1", null, 1, 15, 1), delay*20L);
		delay++;
		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.0f, 1.0f), delay*20L);
		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> Freezer.getInstance().removePlayer(player), delay*20L);
		// décharger les armes en début de partie, actualiser l'inventaire et activer le reloader
		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> player.setLevel(0), delay*20L);
		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> player.setExp(0), delay*20L);
		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> InventoryManager.SetGunInventoryFor(player), delay*20L);
		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> WeaponReloader.getInstance().AddPlayer(player), delay*20L);
		// ajouter l'effet speed 1
		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0)), delay*20L);
		
	}

	@Override
	public ArrayList<Player> GetRedPlayers() { return this.redPlayers; }

	@Override
	public ArrayList<Player> GetRedPlayersAlive() { return this.redPlayersAlive; }

	@Override
	public ArrayList<Player> GetBluePlayers() { return this.bluePlayers; }

	@Override
	public ArrayList<Player> GetBluePlayersAlive() { return this.bluePlayersAlive; }
	
	@Override
	public Team getPlayerTeam(Player player) {
		if(this.bluePlayers.contains(player)) { return Team.Blue; }
		else if(this.redPlayers.contains(player)) { return Team.Red; }
		else { return null; }
	}

	@Override
	public void onPlayerHit(Player shooter, Player target) {
		if(this.getPlayerTeam(shooter) == this.getPlayerTeam(target)) { return; } // annuler si les deux joueurs sont dans la même équipe
		Double targetHp = target.getHealth();
		Double newTargetHp = targetHp -2;
		
		// récupérer les couleurs associées aux équipes des joueurs pour préparer les messages à afficher
		ChatColor shooterColor = null; ChatColor targetColor = null;
		if(this.getPlayerTeam(shooter) == Team.Blue) {
			shooterColor = ChatColor.BLUE; targetColor = ChatColor.RED;
		} else {
			shooterColor = ChatColor.RED; targetColor = ChatColor.BLUE;
		}
		
		shooter.setLevel(0); shooter.setExp(0);
		if(newTargetHp <= 0) { // tuer le joueur s'il n'a plus assez de vie pour encaisser le tir
			this.KillPlayer(shooter, target);
		} else {
			target.setHealth(newTargetHp);
			shooter.sendMessage("Vous avez touché " + targetColor + target.getName());
			shooter.playSound(shooter.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
			target.sendMessage("Vous avez été touché par " + shooterColor + shooter.getName());
			target.playSound(target.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
			String message = targetColor + target.getName() + ChatColor.GOLD + " a été touché par " + shooterColor + shooter.getName();
			for(Player player : Bukkit.getOnlinePlayers()) { // envoyer un message aux autres joueurs
				if(player == shooter || player == target) { continue; }
				player.sendMessage(message);
			}
		}
	}

	@Override
	public void onPlayerDisconnect(Player player) {
		// TODO Auto-generated method stub
		Team team = this.getPlayerTeam(player);
		ChatColor color = null;
		if(team == Team.Blue) {
			this.bluePlayersAlive.remove(player);
			this.bluePlayers.remove(player);
			color = ChatColor.BLUE;
		} else if(team == Team.Red) {
			this.redPlayersAlive.remove(player);
			this.redPlayers.remove(player);
			color = ChatColor.RED;
		} else { return; } // aucune team ou spectateur = ne rien faire
		
		
		String msg = color + player.getName() + ChatColor.LIGHT_PURPLE + " a abandonné la partie.";
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(msg);
			p.playSound(p.getLocation(), Sound.ENTITY_CAT_DEATH, 1.0f, 1.0f);
		}
		if(this.bluePlayers.size() <= 0) { this.EndGame(Team.Red); }
		else if(this.redPlayers.size() <= 0) { this.EndGame(Team.Blue); }
		else if(this.bluePlayersAlive.size() <= 0) { this.EndRound(Team.Red); }
		else if(this.redPlayersAlive.size() <= 0) { this.EndRound(Team.Blue); }
		else { return; }
	}

	@Override
	public void onPlayerShoot(Player shooter) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onPlayerMove(Player shooter) {
		// TODO Auto-generated method stub
	}
	
	public void GiveColoredArmor(Player player) {
		// récupérer l'équipe du joueur pour créer la couleur
		Team team = this.getPlayerTeam(player);
		Color color = Color.fromRGB(0, 255, 0);
		if(team == Team.Blue) { color = Color.fromRGB(0, 0, 255); }
		else if(team == Team.Red) { color = Color.fromRGB(255, 0, 0); }
		// créer les items
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		// récupérer la meta de chaque item
		LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
		LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
		LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
		LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
		// changer la couleur
		helmetMeta.setColor(color); chestplateMeta.setColor(color); leggingsMeta.setColor(color); bootsMeta.setColor(color);
		// appliquer la meta
		helmet.setItemMeta(helmetMeta); chestplate.setItemMeta(chestplateMeta); leggings.setItemMeta(leggingsMeta); boots.setItemMeta(bootsMeta);
		// donner l'armure au joueur
		player.getInventory().setHelmet(helmet);
		player.getInventory().setChestplate(chestplate);
		player.getInventory().setLeggings(leggings);
		player.getInventory().setBoots(boots);
	}
	
	@Override
	public void UpdateScoreboard() {
		ScoreboardBuilder builder = new ScoreboardBuilder();
		builder.AddLine(ChatColor.WHITE + "Mode: Elimitation");
		builder.AddLine("");
		builder.AddLine(ChatColor.BLUE + "Bleu" + ChatColor.WHITE + ": " + String.valueOf(this.blueScore));
		builder.AddLine(ChatColor.RED + "Rouge" + ChatColor.WHITE + ": " + String.valueOf(this.redScore));
		this.scoreboard = builder.build();
		ScoreboardDisplayer.getInstance().SetActiveScoreboard(this.scoreboard);
	}
	
	@Override
	public Scoreboard GetScoreboard() { return this.scoreboard; }
}
