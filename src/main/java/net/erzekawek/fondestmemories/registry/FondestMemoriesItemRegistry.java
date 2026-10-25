package net.erzekawek.fondestmemories.registry;

import net.erzekawek.fondestmemories.FondestMemories;
import net.erzekawek.veyl.items.AbstractUsableItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FondestMemoriesItemRegistry {

    public static Map<Item, ResourceKey<Item>> idLookup = new HashMap<>();

    public static Item REGULAR_KEY = register("regular_key", settings -> new AbstractUsableItem(settings, true), new Item.Properties().stacksTo(16));

    public static Item MASTER_KEY = register("master_key", settings -> new AbstractUsableItem(settings, false), new Item.Properties().stacksTo(16));

    public static Item register(String name) {
        return register(name, new Item.Properties());
    }

    public static Item register(String name, Item.Properties settings) {
        return register(name, Item::new, settings);
    }

    public static Item register(
            String name,
            Function<Item.Properties, Item> factory,
            Item.Properties settings
    ) {
        ResourceKey<Item> id = ResourceKey.create(Registries.ITEM, FondestMemories.id(name));
        Item item = factory.apply(settings.setId(id));
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }
        return register(id, item);
    }

    public static Item register(ResourceKey<Item> id, Item item) {
        idLookup.put(item, id);
        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    public static void initialize() {
    }

}
