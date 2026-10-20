package net.erzekawek.fondestmemories.datagen;

import net.erzekawek.fondestmemories.registry.FondestMemoriesBlockRegistry;
import net.erzekawek.fondestmemories.registry.FondestMemoriesBlockRegistry.Tiles;
import net.erzekawek.fondestmemories.registry.FondestMemoriesItemRegistry;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.world.item.Item;

import java.util.Arrays;

public class ModelProv extends FabricModelProvider {

    protected ModelProv(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerator) {
        blockModelGenerator.createTrivialCube(FondestMemoriesBlockRegistry.REAL_GATE);
        blockModelGenerator.createTrivialCube(FondestMemoriesBlockRegistry.FALSE_GATE);
        blockModelGenerator.createTrivialCube(FondestMemoriesBlockRegistry.POOL_TILING);
        blockModelGenerator.createTrivialCube(FondestMemoriesBlockRegistry.HIGHWAY_RIFT);
        blockModelGenerator.createTrivialCube(FondestMemoriesBlockRegistry.POOL_RIFT);

        blockModelGenerator.createDoor(FondestMemoriesBlockRegistry.LOCKABLE_METAL_DOOR);

        generateTileModels(blockModelGenerator, FondestMemoriesBlockRegistry.POOL_TILE);
    }

    public static void generateTileModels(BlockModelGenerators blockModelGenerator,
                                              Tiles tiles) {
        blockModelGenerator.family(tiles.tiles).stairs(tiles.tileStairs)
                .slab(tiles.tileSlab).wall(tiles.tileWall);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        for (Item item : Arrays.asList(
                FondestMemoriesItemRegistry.MASTER_KEY,
                FondestMemoriesItemRegistry.REGULAR_KEY
        )) {
            itemModelGenerator.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
        }
    }
}
