package net.erzekawek.fondestmemories.levelgen.chkgen.rooms;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An NBT Group: every room NBT + its size in cells + its Y offset + spawn weight.
 * Register with {@link #builder(Identifier)}.
 */
public final class RoomGroup {
    /** cellsX/cellsZ = footprint in cells. yOffset is added to the generator's base_y. */
    public record RoomDef(Identifier nbt, int cellsX, int cellsZ, int yOffset, int weight) {}

    private static final Map<Identifier, RoomGroup> REGISTRY = new ConcurrentHashMap<>();

    public final Identifier id;
    public final Block singleGuide, multiGuide;
    public final List<RoomDef> singles, multis;
    public final RoomDef start;
    private final Map<Integer, GroupRuntime> runtimes = new ConcurrentHashMap<>();

    private RoomGroup(Builder b) {
        id = b.id; singleGuide = b.singleGuide; multiGuide = b.multiGuide;
        singles = List.copyOf(b.singles); multis = List.copyOf(b.multis); start = b.start;
    }

    public static RoomGroup get(Identifier id) { return REGISTRY.get(id); }
    public static void clearRuntimes() {
        REGISTRY.values().forEach(g -> g.runtimes.clear());
    }

    public GroupRuntime runtime(int cellSize) {
        return runtimes.computeIfAbsent(cellSize, cs -> new GroupRuntime(this, cs));
    }

    public static Builder builder(Identifier id) { return new Builder(id); }

    public static final class Builder {
        private final Identifier id;
        private Block singleGuide = Blocks.PRISMARINE;        // cyan guide blocks (1.png)
        private Block multiGuide = Blocks.COBBLED_DEEPSLATE;  // dark guide blocks (2.png / 3.png)
        private final List<RoomDef> singles = new ArrayList<>(), multis = new ArrayList<>();
        private RoomDef start;

        private Builder(Identifier id) { this.id = id; }

        /** guide block */
        public Builder guides(Block single, Block multi) {
            singleGuide = single; multiGuide = multi; return this;
        }

        /** normal 1 cell room */
        public Builder single(Identifier nbt, int yOffset, int weight) {
            singles.add(new RoomDef(nbt, 1, 1, yOffset, weight)); return this;
        }

        /** for multi celled rooms */
        public Builder multi(Identifier nbt, int cellsX, int cellsZ, int yOffset, int weight) {
            multis.add(new RoomDef(nbt, cellsX, cellsZ, yOffset, weight)); return this;
        }

        /** starting room */
        public Builder start(Identifier nbt, int cellsX, int cellsZ, int yOffset) {
            start = new RoomDef(nbt, cellsX, cellsZ, yOffset, 1); return this;
        }

        public RoomGroup register() {
            RoomGroup g = new RoomGroup(this);
            REGISTRY.put(id, g);
            return g;
        }
    }
}
