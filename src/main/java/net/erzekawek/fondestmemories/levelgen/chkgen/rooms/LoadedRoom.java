package net.erzekawek.fondestmemories.levelgen.chkgen.rooms;

import com.mojang.serialization.DataResult;
import net.erzekawek.fondestmemories.FondestMemories;
import net.erzekawek.fondestmemories.levelgen.chkgen.grid.Port;
import net.erzekawek.fondestmemories.levelgen.chkgen.grid.Side;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/** A room NBT parsed into flat arrays, guide blocks stripped and converted to {@link Port}s. */
public final class LoadedRoom {

    public final RoomGroup.RoomDef def;
    public final int sx, sy, sz, cellsW, cellsD;
    public final List<Port> ports;
    /** 4-bit opening mask (rotation 0). Only meaningful for 1x1 pieces. */
    public final int openMask;

    private final Rotated[] rotated = new Rotated[4];
    @SuppressWarnings("unchecked")
    private final List<Port>[] portsRot = new List[4];

    public static final class Rotated {
        public final int[] xs, ys, zs;
        public final BlockState[] states;
        public final CompoundTag[] nbts; // block entity data per block, null when none
        public final int sx, sz;
        Rotated(int[] xs, int[] ys, int[] zs, BlockState[] st, CompoundTag[] nbts, int sx, int sz) {
            this.xs = xs; this.ys = ys; this.zs = zs; this.states = st; this.nbts = nbts;
            this.sx = sx; this.sz = sz;
        }
    }

    private final int[] xs, ys, zs;
    private final BlockState[] states;
    private final CompoundTag[] nbts;

    private LoadedRoom(RoomGroup.RoomDef def, int sx, int sy, int sz, int[] xs, int[] ys, int[] zs,
                       BlockState[] st, CompoundTag[] nbts, List<Port> ports) {
        this.def = def; this.sx = sx; this.sy = sy; this.sz = sz;
        this.xs = xs; this.ys = ys; this.zs = zs; this.states = st; this.nbts = nbts;
        this.cellsW = def.cellsX(); this.cellsD = def.cellsZ();
        this.ports = ports;
        int m = 0; for (Port p : ports) m |= p.side().bit;
        this.openMask = m;
    }

    public int footW(int rot) { return (rot & 1) == 0 ? cellsW : cellsD; }
    public int footD(int rot) { return (rot & 1) == 0 ? cellsD : cellsW; }

    /** Ports for a given clockwise rotation, in footprint-local cell coords. */
    public synchronized List<Port> portsRotated(int rot) {
        if (portsRot[rot] == null) {
            List<Port> cur = ports; int w = cellsW, d = cellsD;
            for (int r = 0; r < rot; r++) {
                List<Port> next = new ArrayList<>();
                for (Port p : cur) next.add(p.rotateCw(w, d));
                cur = next; int t = w; w = d; d = t;
            }
            portsRot[rot] = cur;
        }
        return portsRot[rot];
    }

    public synchronized Rotated rotated(int rot) {
        if (rotated[rot] == null) {
            int n = xs.length;
            int[] rx = new int[n], rz = new int[n]; BlockState[] rs = new BlockState[n];
            int outW = (rot & 1) == 0 ? sx : sz, outD = (rot & 1) == 0 ? sz : sx;
            for (int i = 0; i < n; i++) {
                int x = xs[i], z = zs[i], w = sx, d = sz; BlockState s = states[i];
                for (int r = 0; r < rot; r++) {
                    int nx = d - 1 - z, nz = x; x = nx; z = nz;
                    int t = w; w = d; d = t; s = s.rotate(Rotation.CLOCKWISE_90);
                }
                rx[i] = x; rz[i] = z; rs[i] = s;
            }
            rotated[rot] = new Rotated(rx, ys, rz, rs, nbts, outW, outD); // same index order -> same nbts array
        }
        return rotated[rot];
    }

    /** Loads + parses the NBT. Returns null when it can't be found. */
    static LoadedRoom load(RoomGroup.RoomDef def, int cellSize, Block guide) {
        MinecraftServer server = FondestMemories.server();
        if (server == null) {
            FondestMemories.LOGGER.error("[Fondest Memories] server not ready while loading {}", def.nbt()); return null;
        }

        Optional<StructureTemplate> opt = server.getStructureTemplateManager().get(def.nbt());
        if (opt.isEmpty()) {
            FondestMemories.LOGGER.error("[Fondest Memories] missing structure NBT: {}", def.nbt()); return null;
        }

        CompoundTag tag = opt.get().save(new CompoundTag());
        ListTag size = tag.getListOrEmpty("size");
        int sx = size.getIntOr(0, 0), sy = size.getIntOr(1, 0), sz = size.getIntOr(2, 0);

        if (sx != def.cellsX() * cellSize || sz != def.cellsZ() * cellSize) {
            FondestMemories.LOGGER.warn("[Fondest Memories] {} is {}x{} blocks but {}x{} cells of {} = {}x{}; check the NBT or cell_size",
                    def.nbt(), sx, sz, def.cellsX(), def.cellsZ(), cellSize,
                    def.cellsX() * cellSize, def.cellsZ() * cellSize);
        }

        ListTag paletteTag = tag.getListOrEmpty("palette");
        BlockState[] palette = new BlockState[paletteTag.size()];
        for (int i = 0; i < palette.length; i++) {
            palette[i] = BlockState.CODEC.parse(NbtOps.INSTANCE, paletteTag.getCompoundOrEmpty(i))
                    .result().orElse(Blocks.AIR.defaultBlockState());
        }

        ListTag blocks = tag.getListOrEmpty("blocks");
        int cap = blocks.size();
        int[] xs = new int[cap], ys = new int[cap], zs = new int[cap];
        BlockState[] st = new BlockState[cap];
        CompoundTag[] nb = new CompoundTag[cap];
        int count = 0;
        Set<Port> portSet = new LinkedHashSet<>();

        for (int i = 0; i < cap; i++) {
            CompoundTag b = blocks.getCompoundOrEmpty(i);
            ListTag pos = b.getListOrEmpty("pos");
            int x = pos.getIntOr(0, 0), y = pos.getIntOr(1, 0), z = pos.getIntOr(2, 0);
            BlockState s = palette[Math.max(0, Math.min(palette.length - 1, b.getIntOr("state", 0)))];

            if (s.is(guide)) {
                Side side = Side.nearestEdge(x, z, sx, sz);
                int cx, cz;
                switch (side) {
                    case NORTH -> { cx = Math.min(x / cellSize, def.cellsX() - 1); cz = 0; }
                    case SOUTH -> { cx = Math.min(x / cellSize, def.cellsX() - 1); cz = def.cellsZ() - 1; }
                    case WEST  -> { cx = 0; cz = Math.min(z / cellSize, def.cellsZ() - 1); }
                    default    -> { cx = def.cellsX() - 1; cz = Math.min(z / cellSize, def.cellsZ() - 1); }
                }
                portSet.add(new Port(cx, cz, side));
                continue;
            }
            if (s.isAir() || s.is(Blocks.STRUCTURE_VOID)) continue;
            xs[count] = x; ys[count] = y; zs[count] = z; st[count] = s;
            nb[count] = b.getCompound("nbt").orElse(null);   // block entity data (null if none)
            count++;
        }

        LoadedRoom room = new LoadedRoom(def, sx, sy, sz,
                Arrays.copyOf(xs, count), Arrays.copyOf(ys, count), Arrays.copyOf(zs, count),
                Arrays.copyOf(st, count), Arrays.copyOf(nb, count), List.copyOf(portSet));
        if (portSet.isEmpty()) FondestMemories.LOGGER.warn("[Fondest Memories] {} has no guide blocks ({}), it will never connect", def.nbt(), guide);
        return room;
    }
}
