package net.erzekawek.fondestmemories.misc;

import net.erzekawek.fondestmemories.registry.FondestMemoriesDimensionRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Set;

public class RestrictedDimensions {
    public static final Set<ResourceKey<Level>> NO_DEBUG = Set.of(
            FondestMemoriesDimensionRegistry.FRONTIERS,
            FondestMemoriesDimensionRegistry.LIGHTS_OUT
    );

    public static boolean noDebug(Level level) {
        return level != null && NO_DEBUG.contains(level.dimension());
    }
}
