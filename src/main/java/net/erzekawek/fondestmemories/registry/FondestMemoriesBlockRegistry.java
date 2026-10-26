package net.erzekawek.fondestmemories.registry;

import net.erzekawek.fondestmemories.blocks.*;
import net.erzekawek.veyl.utils.registry.CBlock;
import net.erzekawek.veyl.utils.registry.UnifiedRegistry;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.erzekawek.fondestmemories.FondestMemories.MOD_ID;

public class FondestMemoriesBlockRegistry {

    // liminal unbreakable blocks
    public static Block REAL_GATE = CBlock.makeUnbreakable(MOD_ID, "real_gate", 15);
    public static Block FALSE_GATE = CBlock.makeUnbreakable(MOD_ID, "false_gate", 15);
    public static Block POOL_TILING = CBlock.makeUnbreakable(MOD_ID, "pool_tiling", 0);

    // poolrooms blocks
    public static final Tiles POOL_TILE = new Tiles("pool_tile", MapColor.TERRACOTTA_WHITE);

    public static Block SKY_WINDOW = registerWindow("sky_window", 15);
    public static Block SKY_PANEL_NOON = registerPanel("sky_panel_noon", 15);
    public static Block SKY_PANEL_NOON_LIGHTLESS = registerPanel("sky_panel_noon_lightless", 0);

    // utility blocks

    public static Block LOCKABLE_METAL_DOOR = registerKeyedDoor("lockable_metal_door");

    // rift blocks

    public static Block POOL_RIFT = CBlock.makeRift(MOD_ID, "poolrooms_rift", 15, FondestMemoriesDimensionRegistry.FRONTIERS, 60, 90, 60, 0f);
    public static Block HIGHWAY_RIFT = CBlock.makeRift(MOD_ID, "highway_rift", 15, FondestMemoriesDimensionRegistry.ENDLESS_HIGHWAY, 5, 3, 0, 0f);
    public static Block LIGHTS_OUT_RIFT = CBlock.makeRift(MOD_ID, "lights_out_rift", 0, FondestMemoriesDimensionRegistry.LIGHTS_OUT, (int) 47.5, 75, (int) 47.5, 0f);
    public static Block LIMINAL_GLASS = CBlock.makeLight(MOD_ID, "liminal_glass", 15);

    public static Block CORN_PLANT = CBlock.makeDoublePlant(MOD_ID,"corn_stalk", FondestMemoriesTagsRegistry.CORN_PLACEABLE_ON);

    private static Block registerPanel(String name, int light) {
        return UnifiedRegistry.registerBlock(MOD_ID, name, settings -> new SkyPanelBlock(BlockBehaviour.Properties.of()
                .strength(-1, 3600000)
                .sound(SoundType.GLASS)
                .lightLevel(blockState -> light)
                .noLootTable()
                .noOcclusion()
                .setId(settings.blockId())));
    }

    private static Block registerWindow(String name, int light) {
        return UnifiedRegistry.registerBlock(MOD_ID, name, settings -> new SkyWindowBlock(BlockBehaviour.Properties.of()
                .strength(-1, 3600000)
                .sound(SoundType.GLASS)
                .lightLevel(blockState -> light)
                .noLootTable()
                .noOcclusion()
                .setId(settings.blockId())));
    }

    private static Block registerKeyedDoor(String name) {
        return UnifiedRegistry.registerBlock(MOD_ID, name, settings -> new LockableDoorBlock(BlockBehaviour.Properties.of()
                .strength(50, 1200)
                .sound(SoundType.NETHERITE_BLOCK)
                .pushReaction(PushReaction.IMMOVEABLE)
                .noOcclusion()
                .setId(settings.blockId())));
    }

    public static class Tiles {

        public final String baseName;
        public final Block tiles;
        public final Block tileStairs;
        public final Block tileSlab;
        public final Block tileWall;
        public final List<Block> blocks;
        private final MapColor mapColor;


        public Tiles(String name, MapColor mapColor) {
            this.baseName = name;
            this.mapColor = mapColor;

            tiles = UnifiedRegistry.registerBlock(MOD_ID, baseName, settings -> new Block(applySettings(settings)));
            tileStairs = UnifiedRegistry.registerBlock(MOD_ID, baseName + "_stairs",
                    settings -> new StairBlock(tiles.defaultBlockState(), applySettings(settings)));
            tileSlab = UnifiedRegistry.registerBlock(MOD_ID, baseName + "_slab",
                    settings -> new SlabBlock(applySettings(settings)));
            tileWall = UnifiedRegistry.registerBlock(MOD_ID, baseName + "_wall",
                    settings -> new WallBlock(applySettings(settings)));

            blocks = Arrays.asList(tiles, tileStairs, tileSlab, tileWall);
        }

        public BlockBehaviour.Properties applySettings(BlockBehaviour.Properties settings) {
            return settings
                    .instrument(NoteBlockInstrument.HAT)
                    .strength(-1, 3600000)
                    .sound(SoundType.GLASS)
                    .noLootTable()
                    .mapColor(this.mapColor);
        }
    }

    public static void initialize() {
    }
}
