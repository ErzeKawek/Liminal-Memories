package net.erzekawek.fondestmemories.registry;

import net.erzekawek.fondestmemories.FondestMemories;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class FondestMemoriesDimensionRegistry {
    public static final ResourceKey<Level> ENDLESS_HIGHWAY = ResourceKey.create(
            Registries.DIMENSION, FondestMemories.id("endless_highway"));

    /*public static final ResourceKey<Level> THALASSOPHOBIA = ResourceKey.create(
            Registries.DIMENSION, FondestMemories.id("thalassophobia"));*/

    public static final ResourceKey<Level> FRONTIERS = ResourceKey.create(
            Registries.DIMENSION, FondestMemories.id("frontiers"));
}
