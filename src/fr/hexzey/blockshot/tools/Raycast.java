package fr.hexzey.blockshot.tools;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class Raycast
{
	private Location source;	// origine du ray cast
	private Vector direction;	// direction du ray cast
	private Double distance;	// distance à parcourir
	private Double step;		// distance entre chaque pas/itération
	private Particle particle;	// particule à afficherf à chaque pas/itération
	private Object particleData;
	
	public Raycast(Location _source, Vector _direction, Double _distance, Double _step, Particle _particle, Object _particleData)
	{
		this.source = _source;
		this.step = _step;
		this.direction = _direction.normalize().multiply(_step);
		this.distance = _distance;
		this.particle = _particle;
		this.particleData = _particleData;
	}
	
	public ArrayList<Object> getResult() {
		/**
		 * Retourne une ArrayList contenant les Block ou Entity touchées
		 */
		ArrayList<Object> hits = new ArrayList<Object>();
		Location currentLoc = this.source.clone();
		// on commence au pas 1 pour ignorer l'origine
		currentLoc.add(this.direction);
		Double currentDist = this.step;
		// boucle pour exécuter tous les pas jusqu'à la distance maximale
		while(currentDist < this.distance) {
			// générer le pas suivant
			currentLoc = currentLoc.add(this.direction);
			currentDist += this.step;
			Double x1 = currentLoc.getX() - 0.065d;
			Double x2 = currentLoc.getX() + 0.065d;
			Double y1 = currentLoc.getY() - 0.065d;
			Double y2 = currentLoc.getY() + 0.065d;
			Double z1 = currentLoc.getZ() - 0.065d;
			Double z2 = currentLoc.getZ() + 0.065d;
			BoundingBox box = new BoundingBox(x1, y1, z1, x2, y2, z2);
			// récupérer le potientiel bloc touché
			Block blockHit = currentLoc.getWorld().getBlockAt(currentLoc);
			if(blockHit.getType() != Material.AIR && blockHit.getType() != Material.BARRIER) {
				/*
				if(hits.contains(blockHit) == false) { hits.add(blockHit); }
				break; // s'arrêter si on touche un bloc
				*/
				BoundingBox blockBox = blockHit.getBoundingBox();
				if(box.overlaps(blockBox)) {
					hits.add(blockHit);
					break; // s'arrêter si on touche un bloc
				}
			}
			// récupérer les potentiels joueurs touchés
			for(Entity entity : currentLoc.getWorld().getNearbyEntities(box)) {
				if(hits.contains(entity) == false) { hits.add(entity); }
			}
			// afficher une particle s'il le faut
			currentLoc.getWorld().spawnParticle(this.particle, currentLoc.getX(), currentLoc.getY(), currentLoc.getZ(), 2, this.particleData);
		}
		return hits;
	}
}
