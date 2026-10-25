package net.erzekawek.fondestmemories;

import net.erzekawek.fondestmemories.registry.FondestMemoriesRoomRegistry;
import net.erzekawek.fondestmemories.levelgen.endlesshighway.RoadGenerator;
import net.erzekawek.fondestmemories.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FondestMemories implements ModInitializer {

    public static final String MOD_ID = "fondest_memories";

    public static final String MOD_NAME = "Fondest Memories";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }

    public static Identifier findRoom(String space, String name) {
        return id(space + "/" + name);
    }

    private static volatile MinecraftServer server;
    public static MinecraftServer server() {
        return server;
    }
    private final RoadGenerator generator = new RoadGenerator();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ServerLevel level = server.getLevel(FondestMemoriesDimensionRegistry.ENDLESS_HIGHWAY);
            if (level != null) generator.tick(level);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> generator.reset());

        FondestMemoriesRoomRegistry.registerPoolrooms();
        FondestMemoriesRoomRegistry.registerLightsOut();
        FondestMemoriesBlockRegistry.initialize();
        FondestMemoriesItemRegistry.initialize();
        FondestMemoriesItemGroup.initialize();
        FondestMemoriesBlockEntityRegistry.initialize();
    }
}
