package net.erzekawek.fondestmemories.registry;

import net.erzekawek.fondestmemories.items.Key;
import net.erzekawek.veyl.utils.registry.UnifiedRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

import static net.erzekawek.fondestmemories.FondestMemories.MOD_ID;

public class FondestMemoriesItemRegistry {

    public static Map<Item, ResourceKey<Item>> idLookup = new HashMap<>();

    public static Item REGULAR_KEY = UnifiedRegistry.registerItem(MOD_ID, "regular_key", settings -> new Key(settings, true), new Item.Properties().stacksTo(16));

    public static Item MASTER_KEY = UnifiedRegistry.registerItem(MOD_ID, "master_key", settings -> new Key(settings, false), new Item.Properties().stacksTo(16));

    public static void initialize() {
    }

}
