package net.erzekawek.fondestmemories.datagen;

import net.erzekawek.fondestmemories.registry.FondestMemoriesBlockRegistry;
import net.erzekawek.fondestmemories.registry.FondestMemoriesBlockRegistry.Tiles;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;

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

        generateTileModels(blockModelGenerator, FondestMemoriesBlockRegistry.POOL_TILE);
    }

    public static void generateTileModels(BlockModelGenerators blockModelGenerator,
                                              Tiles tiles) {
        blockModelGenerator.family(tiles.tiles).stairs(tiles.tileStairs)
                .slab(tiles.tileSlab).wall(tiles.tileWall);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
    }
}
