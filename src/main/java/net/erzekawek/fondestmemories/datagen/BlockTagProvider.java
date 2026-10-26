package net.erzekawek.fondestmemories.datagen;

import net.erzekawek.fondestmemories.registry.FondestMemoriesTagsRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.references.BlockItemId;
import net.minecraft.references.BlockItemIds;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends FabricTagsProvider.BlockTagsProvider {

    public BlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        builder(FondestMemoriesTagsRegistry.CORN_PLACEABLE_ON).add(
                BlockItemIds.GRASS_BLOCK.block(),
                BlockItemIds.DIRT.block()
        );
    }
}
