package net.erzekawek.fondestmemories;

import net.erzekawek.fondestmemories.levelgen.chkgen.gen.MazeChunkGenerator;
import net.erzekawek.fondestmemories.levelgen.chkgen.gen.MazeRegion;
import net.erzekawek.fondestmemories.registry.FondestMemoriesRoomRegistry;
import net.erzekawek.fondestmemories.levelgen.chkgen.rooms.RoomGroup;
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
            ServerLevel level = server.getLevel(FondestMemoriesDimensionRegistry.ROAD_DIM);
            if (level != null) generator.tick(level);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> generator.reset());
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, id("maze"), MazeChunkGenerator.CODEC);

        ServerLifecycleEvents.SERVER_STARTING.register(s -> server = s);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((s, rm, ok) -> clearCaches());
        ServerLifecycleEvents.SERVER_STOPPED.register(s -> { server = null; clearCaches(); });

        FondestMemoriesRoomRegistry.registerPoolrooms();
        // FondestMemoriesRoomRegistry.registerHotel();
        FondestMemoriesBlockRegistry.initialize();
        FondestMemoriesItemRegistry.initialize();
        FondestMemoriesItemGroup.initialize();
        FondestMemoriesBlockEntityRegistry.initialize();
    }

    private static void clearCaches() {
        RoomGroup.clearRuntimes();
        MazeRegion.clearCache();
    }
}
