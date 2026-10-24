package net.erzekawek.fondestmemories.levelgen.chkgen.gen;

import net.minecraft.resources.Identifier;

/** ts to make everything configurable from dimension JSON. */
public record MazeSettings(int cellSize, int regionSize, int baseY, long seed, float multiRoomChance, float gateChance, Identifier group) {

}
