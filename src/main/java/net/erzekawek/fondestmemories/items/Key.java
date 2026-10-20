package net.erzekawek.fondestmemories.items;

import net.minecraft.world.item.Item;

public class Key extends Item {
    private final boolean consume;

    public Key(Properties properties, boolean consume) {
        super(properties);
        this.consume = consume;
    }

    public boolean isConsume() {
        return consume;
    }
}
