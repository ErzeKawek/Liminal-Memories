package net.erzekawek.fondestmemories.levelgen.endlesshighway;

import net.erzekawek.fondestmemories.FondestMemories;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoadGenerator {

    private static final int CHUNKS_PER_TICK = 4;

    private final List<StructureTemplate> templates = new ArrayList<>();
    private final List<Integer> weights = new ArrayList<>();
    private StructureTemplate startTemplate;
    private int totalWeight;
    private long seed;
    private boolean loaded;

    private final Set<Long> checked = new HashSet<>();

    public synchronized void reset() {
        templates.clear();
        weights.clear();
        startTemplate = null;
        totalWeight = 0;
        loaded = false;
        checked.clear();
    }

    public void tick(ServerLevel level) {
        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) return;

        loadTemplates(level);
        if (templates.isEmpty()) return;

        Vec3i size = templates.get(0).getSize();
        int lenX0 = widthX(size);
        int originX0 = -(lenX0 / 2);
        int minChunkX = Math.floorDiv(originX0, 16);
        int maxChunkX = Math.floorDiv(originX0 + lenX0 - 1, 16);

        List<ChunkPos> pos1 = new ArrayList<>();
        int[] nearestZ = new int[players.size()];
        for (int i = 0; i < players.size(); i++) {
            BlockPos pos = players.get(i).blockPosition();
            int pcz = pos.getZ() >> 4;
            nearestZ[i] = pcz;
            for (int cx = minChunkX; cx <= maxChunkX; cx++) {
                int fromZ = pcz - RoadConfig.RADIUS_CHUNKS;
                if (!RoadConfig.BOTH_DIRECTIONS) fromZ = Math.max(0, fromZ);
                for (int cz = fromZ; cz <= pcz + RoadConfig.RADIUS_CHUNKS; cz++) {
                    if (!checked.contains(key(cx, cz))) pos1.add(new ChunkPos(cx, cz));
                }
            }
        }
        if (pos1.isEmpty()) return;

        pos1.sort(Comparator.comparingInt(c -> {
            int best = Integer.MAX_VALUE;
            for (int z : nearestZ) best = Math.min(best, Math.abs(c.z() - z));
            return best;
        }));

        int budget = CHUNKS_PER_TICK;
        for (ChunkPos cp : pos1) {
            if (budget <= 0) break;
            LevelChunk chunk = level.getChunkSource().getChunkNow(cp.x(), cp.z());
            if (chunk == null) continue;

            checked.add(key(cp.x(), cp.z()));
            BlockPos marker = new BlockPos(cp.getMinBlockX(), RoadConfig.MARKER_Y, cp.getMinBlockZ());
            if (level.getBlockState(marker).is(Blocks.OBSIDIAN)) continue;

            placeChunk(level, chunk);
            level.setBlock(marker, Blocks.OBSIDIAN.defaultBlockState(), 2 | 16);
            budget--;
        }
    }

    private static boolean swapped() {
        return RoadConfig.ROTATION == Rotation.CLOCKWISE_90 || RoadConfig.ROTATION == Rotation.COUNTERCLOCKWISE_90;
    }

    private static int widthX(Vec3i size) {
        return swapped() ? size.getZ() : size.getX();
    }

    private static int lengthZ(Vec3i size) {
        return swapped() ? size.getX() : size.getZ();
    }

    private static BlockPos pivotFor(int minX, int y, int minZ, Vec3i size) {
        return switch (RoadConfig.ROTATION) {
            case CLOCKWISE_90 -> new BlockPos(minX + size.getZ() - 1, y, minZ);
            case COUNTERCLOCKWISE_90 -> new BlockPos(minX, y, minZ + size.getX() - 1);
            case CLOCKWISE_180 -> new BlockPos(minX + size.getX() - 1, y, minZ + size.getZ() - 1);
            default -> new BlockPos(minX, y, minZ);
        };
    }

    private static long key(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }

    private void placeChunk(ServerLevel level, LevelChunk chunk) {
        Vec3i size = templates.get(0).getSize();
        int lenX = widthX(size);
        int lenZ = lengthZ(size);
        int originX = -(lenX / 2);
        int originY = RoadConfig.ORIGIN_Y;

        ChunkPos cp = chunk.getPos();
        int minZ = cp.getMinBlockZ();
        int maxZ = cp.getMaxBlockZ();
        if (!RoadConfig.BOTH_DIRECTIONS && maxZ < 0) return;
        if (originX + lenX - 1 < cp.getMinBlockX() || originX > cp.getMaxBlockX()) return;

        BoundingBox clip = new BoundingBox(
                cp.getMinBlockX(), originY, minZ,
                cp.getMaxBlockX(), originY + size.getY() - 1, maxZ);

        int first = Math.floorDiv(minZ, lenZ);
        if (!RoadConfig.BOTH_DIRECTIONS) first = Math.max(0, first);
        int last = Math.floorDiv(maxZ, lenZ);

        for (int piece = first; piece <= last; piece++) {
            StructurePlaceSettings settings = new StructurePlaceSettings()
                    .setRotation(RoadConfig.ROTATION)
                    .setBoundingBox(clip)
                    .setIgnoreEntities(true);
            BlockPos origin = pivotFor(originX, originY, piece * lenZ, size);
            pick(piece).placeInWorld(level, origin, origin, settings, level.getRandom(), 2 | 16);
        }
    }

    private synchronized void loadTemplates(ServerLevel level) {
        if (loaded) return;
        seed = level.getSeed();
        var manager = level.getStructureTemplateManager();
        for (RoadLoader p : RoadConfig.PIECES) {
            if (p.weight() <= 0) continue;
            var opt = manager.get(FondestMemories.id(p.id()));
            if (opt.isEmpty()) {
                System.err.println("yo ts missing: " + p.id());
                continue;
            }
            templates.add(opt.get());
            weights.add(p.weight());
            totalWeight += p.weight();
            if (p.id().equals(RoadConfig.START_PIECE)) startTemplate = opt.get();
        }
        loaded = true;
    }

    private StructureTemplate pick(int piece) {
        if (piece == 0 && startTemplate != null) return startTemplate;
        int roll = (int) Math.floorMod(mix(piece ^ seed), (long) totalWeight);
        for (int i = 0; i < templates.size(); i++) {
            roll -= weights.get(i);
            if (roll < 0) return templates.get(i);
        }
        return templates.get(0);
    }

    private static long mix(long z) {
        z += 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }
}
