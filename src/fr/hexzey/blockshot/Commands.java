package fr.hexzey.blockshot;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.hexzey.blockshot.tools.GameState;
import fr.hexzey.blockshot.tools.IGameMode;

public class Commands implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args)
	{
		if(sender instanceof Player)
		{
			Player player = (Player)sender;
			
			if (cmd.getName().equalsIgnoreCase("blockshot") || cmd.getName().equalsIgnoreCase("bs"))
			{
				if(args.length < 1) {
					player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
					player.sendMessage(ChatColor.RED + "Commande incomplète.");
					return false;
				}
				
				// COMMANDE POUR CHANGER LE MODE DE JEU
				if(args[0].equalsIgnoreCase("setmode")) {
					if(Main.getCurrentGameMode() != null && Main.getCurrentGameMode().GetState() == GameState.Running) {
						player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
						player.sendMessage(ChatColor.RED + "Action impossible : une partie est en cours.");
						return false;
					}
					if(args.length < 2) {
						player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
						player.sendMessage(ChatColor.RED + "Merci de préciser un mode de jeu.");
						return false;
					}
					String name = args[1].toLowerCase();
					boolean found = false;
					for(IGameMode gm : Main.getAvailableGameModes()) {
						if(gm.GetName().equalsIgnoreCase(name)) {
							found = true;
							Main.setCurrentGameMode(gm);
							break;
						}
					}
					
					if(found == false) {
						player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
						player.sendMessage(ChatColor.RED + "Ce mode de jeu n'existe pas.");
					} else {
						player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
						player.sendMessage(ChatColor.GREEN + "Mode de jeu changé pour " + Main.getCurrentGameMode().GetName() + ".");
					}
					return false;
					
				// COMMANDE POUR DEMARRER UNE NOUVELLE PARTIE	
				} else if(args[0].equalsIgnoreCase("start")) {
					IGameMode gameMode = Main.getCurrentGameMode();
					if(gameMode == null) {
						player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
						player.sendMessage(ChatColor.RED + "Action impossible. Aucun mode de jeu défini.");
					} else if(gameMode.GetState() == GameState.Running) {
						player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
						player.sendMessage(ChatColor.RED + "Action impossible. Une partie est déjà en cours.");
					} else {
						// vérifier s'il y a des arènes qui acceptent le nombre de joueurs
						ArrayList<Arena> arenas = Main.getAvailableArenas();
						int players = TeamSelector.getInstance().getBlueTeam().size() + TeamSelector.getInstance().getRedTeam().size();
						ArrayList<Arena> eligibleArenas = new ArrayList<Arena>();
						for(Arena arena : arenas) {
							if(arena.getMinPlayers() <= players && arena.getMaxPlayers() >= players) {
								eligibleArenas.add(arena);
							}
						}
						if(eligibleArenas.size() < 1) {
							player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
							player.sendMessage(ChatColor.RED + "Action impossible. Aucune arène n'est prévue pour accepter " + String.valueOf(players) + " joueurs.");
						} else {
							player.sendMessage(ChatColor.GREEN + "Démarrage...");
							Random rand = new Random();
							gameMode.SetArena(eligibleArenas.get(rand.nextInt(eligibleArenas.size()))); // sélection d'une arène au hasard parmi celles disponibles
							gameMode.StartGame(TeamSelector.getInstance().getBlueTeam(), TeamSelector.getInstance().getRedTeam());
						}
					}
				}
			}
		}
		return false;
	}
}
