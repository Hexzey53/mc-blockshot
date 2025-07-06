package fr.hexzey.blockshot.tools;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;
import org.bukkit.generator.ChunkGenerator.ChunkData;

public class EmptyWorldGenerator extends ChunkGenerator {
	public EmptyWorldGenerator() {}
	
	@Override
	public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome)
	{
		return createChunkData(world);
	}
}
