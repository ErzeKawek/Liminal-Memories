package net.erzekawek.fondestmemories.levelgen.chkgen.rooms;

import net.erzekawek.fondestmemories.FondestMemories;
import net.erzekawek.fondestmemories.levelgen.chkgen.grid.Side;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/** loaded */
public final class GroupRuntime {

    public record Candidate(LoadedRoom room, int rot, float weight) {}

    private final Map<Integer, List<Candidate>> singleByMask = new HashMap<>();
    public final List<LoadedRoom> multis = new ArrayList<>();
    public final LoadedRoom start;

    GroupRuntime(RoomGroup g, int cellSize) {
        for (RoomGroup.RoomDef def : g.singles) {
            LoadedRoom lr = LoadedRoom.load(def, cellSize, g.singleGuide);
            if (lr == null || lr.openMask == 0) continue;
            FondestMemories.LOGGER.info("[Fondest Memories] single {} -> {}", def.nbt(), shapeName(lr.openMask));

            Map<Integer, List<Integer>> rotsPerMask = new HashMap<>();
            for (int k = 0; k < 4; k++)
                rotsPerMask.computeIfAbsent(Side.rotateMask(lr.openMask, k), m -> new ArrayList<>()).add(k);
            rotsPerMask.forEach((mask, rots) -> {
                for (int k : rots)
                    singleByMask.computeIfAbsent(mask, m -> new ArrayList<>())
                            .add(new Candidate(lr, k, (float) def.weight() / rots.size()));
            });
        }
        for (RoomGroup.RoomDef def : g.multis) {
            LoadedRoom lr = LoadedRoom.load(def, cellSize, g.multiGuide);
            if (lr != null) multis.add(lr);
        }
        start = g.start == null ? null : LoadedRoom.load(g.start, cellSize, g.multiGuide);
    }

    public Candidate pickSingle(int mask, RandomSource rng) {
        List<Candidate> list = singleByMask.get(mask);
        if (list == null || list.isEmpty()) return null;
        float total = 0; for (Candidate c : list) total += c.weight();
        float r = rng.nextFloat() * total;
        for (Candidate c : list) { r -= c.weight(); if (r <= 0) return c; }
        return list.get(list.size() - 1);
    }

    public LoadedRoom pickMulti(RandomSource rng) {
        if (multis.isEmpty()) return null;
        float total = 0; for (LoadedRoom r : multis) total += r.def.weight();
        float v = rng.nextFloat() * total;
        for (LoadedRoom r : multis) { v -= r.def.weight(); if (v <= 0) return r; }
        return multis.get(multis.size() - 1);
    }

    public boolean hasMask(int mask) { return singleByMask.containsKey(mask); }

    private static String shapeName(int mask) {
        int n = Integer.bitCount(mask);
        return switch (n) {
            case 1 -> "nub";
            case 2 -> (mask == 5 || mask == 10) ? "I" : "L";
            case 3 -> "F";
            case 4 -> "T";
            default -> "?";
        };
    }
}
