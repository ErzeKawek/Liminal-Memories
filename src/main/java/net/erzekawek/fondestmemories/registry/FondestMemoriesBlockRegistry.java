package net.erzekawek.fondestmemories.registry;

import net.erzekawek.fondestmemories.FondestMemories;
import net.erzekawek.fondestmemories.blocks.DimensionalRiftBlock;
import net.erzekawek.fondestmemories.blocks.SkyPanelBlock;
import net.erzekawek.fondestmemories.blocks.SkyWindowBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FondestMemoriesBlockRegistry {

    // liminal unbreakable blocks
    public static Block REAL_GATE = registerLimBlock("real_gate", 15);
    public static Block FALSE_GATE = registerLimBlock("false_gate", 15);
    public static Block POOL_TILING = registerLimBlock("pool_tiling", 0);

    // poolrooms blocks
    public static final Tiles POOL_TILE = new Tiles("pool_tile", MapColor.TERRACOTTA_WHITE);

    public static Block SKY_WINDOW = registerWindow("sky_window", 15);
    public static Block SKY_PANEL_NOON = registerPanel("sky_panel_noon", 15);
    public static Block SKY_PANEL_NOON_LIGHTLESS = registerPanel("sky_panel_noon_lightless", 0);

    // rift blocks & util

    static int num1 = 5;
    static int num2 = -5;
    static Random rand = new Random();
    static int randX = rand.nextBoolean() ? num1 : num2;

    public static final Block POOL_RIFT = register("poolrooms_rift",
            settings -> new DimensionalRiftBlock(BlockBehaviour.Properties.of()
                    .noCollision()
                    .strength(-1f)
                    .lightLevel(bs -> 15)
                    .setId(settings.blockId()),
                    FondestMemoriesDimensionRegistry.FRONTIERS,
                    new Vec3(60, 70, 60), 0f));

    public static final Block HIGHWAY_RIFT = register("highway_rift",
            settings -> new DimensionalRiftBlock(BlockBehaviour.Properties.of()
                    .strength(-1f)
                    .lightLevel(bs -> 15)
                    .setId(settings.blockId()),
                    FondestMemoriesDimensionRegistry.ENDLESS_HIGHWAY,
                    new Vec3(randX, 3, 0), 0f));

    private static Block registerPanel(String name, int light) {
        return register(name, settings -> new SkyPanelBlock(BlockBehaviour.Properties.of()
                .strength(-1, 3600000)
                .sound(SoundType.GLASS)
                .lightLevel(blockState -> light)
                .noLootTable()
                .noOcclusion()
                .setId(settings.blockId())));
    }

    private static Block registerWindow(String name, int light) {
        return register(name, settings -> new SkyWindowBlock(BlockBehaviour.Properties.of()
                .strength(-1, 3600000)
                .sound(SoundType.GLASS)
                .lightLevel(blockState -> light)
                .noLootTable()
                .noOcclusion()
                .setId(settings.blockId())));
    }

    private static Block registerLimBlock(String name, int light) {
        return register(name, settings -> new Block(BlockBehaviour.Properties.of()
                .strength(-1, 3600000)
                .sound(SoundType.GLASS)
                .lightLevel(blockState -> light)
                .noLootTable()
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

            tiles = FondestMemoriesBlockRegistry.register(baseName, settings -> new Block(applySettings(settings)));
            tileStairs = FondestMemoriesBlockRegistry.register(baseName + "_stairs",
                    settings -> new StairBlock(tiles.defaultBlockState(), applySettings(settings)));
            tileSlab = FondestMemoriesBlockRegistry.register(baseName + "_slab",
                    settings -> new SlabBlock(applySettings(settings)));
            tileWall = FondestMemoriesBlockRegistry.register(baseName + "_wall",
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

    public static Block register(String name, Function<BlockBehaviour.Properties, Block> factory) {
        return register(name, factory, true);
    }

    public static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, boolean hasItem) {
        return register(name, factory, BlockBehaviour.Properties.of(), hasItem);
    }

    private static Block register(
            String name,
            Function<BlockBehaviour.Properties, Block> factory,
            BlockBehaviour.Properties settings,
            boolean hasItem
    ) {
        if (hasItem) {
            BlockItemId id = BlockItemId.create(FondestMemories.id(name), FondestMemories.id(name));
            Block block = factory.apply(settings.setId(id.block()));
            FondestMemoriesItemRegistry.register(
                    id.item(),
                    new BlockItem(
                            block,
                            new Item.Properties()
                                    .setId(id.item())
                                    .useBlockDescriptionPrefix()
                                    .requiredFeatures(block.requiredFeatures())
                    )
            );
            return Registry.register(BuiltInRegistries.BLOCK, id.block(), block);
        }
        ResourceKey<Block> id = ResourceKey.create(Registries.BLOCK, FondestMemories.id(name));
        Block block = factory.apply(settings.setId(id));
        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    public static Block registerSpecialBlockItem(
            String name,
            Function<BlockBehaviour.Properties, Block> factory,
            BiFunction<Block, BlockItemId, BlockItem> itemFactory
    ) {
        return registerSpecialBlockItem(name, name, factory, itemFactory);
    }

    public static Block registerSpecialBlockItem(
            String blockName,
            String itemName,
            Function<BlockBehaviour.Properties, Block> factory,
            BiFunction<Block, BlockItemId, BlockItem> itemFactory
    ) {
        BlockItemId id = BlockItemId.create(FondestMemories.id(blockName), FondestMemories.id(itemName));
        Block block = factory.apply(BlockBehaviour.Properties.of().setId(id.block()));
        FondestMemoriesItemRegistry.register(id.item(), itemFactory.apply(block, id));
        return Registry.register(BuiltInRegistries.BLOCK, id.block(), block);
    }

    public static void initialize() {
    }
}
