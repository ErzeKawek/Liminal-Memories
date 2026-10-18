package net.erzekawek.fondestmemories.registry;

import net.erzekawek.fondestmemories.FondestMemories;
import net.erzekawek.fondestmemories.levelgen.chkgen.rooms.RoomGroup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;

/**
 * basically where all the rooms are registered
 */
public final class FondestMemoriesRoomRegistry {
    private FondestMemoriesRoomRegistry() {}

    public static void registerPoolrooms() {
        RoomGroup.builder(FondestMemories.id("poolrooms"))
                .guides(Blocks.PRISMARINE, Blocks.DARK_PRISMARINE)

                // le singlet rooms
                .single(FondestMemories.id("pools/pool_8x8_nub"), 0, 2)
                .single(FondestMemories.id("pools/pool_8x8_i"), 0, 4)
                .single(FondestMemories.id("pools/pool_8x8_l"), 0, 4)
                .single(FondestMemories.id("pools/pool_8x8_f"), 0, 2)
                .single(FondestMemories.id("pools/pool_8x8_t"), 0, 1)

                // dis are big rooms
                .multi(FondestMemories.id("pools/pool_8x16_small"),   1, 2, 0, 3)
                .multi(FondestMemories.id("pools/pool_16x24_skyview"), 2, 3, 0, 2)
                .multi(FondestMemories.id("pools/pool_24x24_lush"), 3, 3, 0, 1)
                .multi(FondestMemories.id("pools/pool_24x32_pillars"), 3, 4, 0, 1)
                .multi(FondestMemories.id("pools/pool_24x32_tunneling"), 3, 4, 0, 1)

                // de starting room
                .start(FondestMemories.id("pools/pool_24x24_entrance"),3, 3, -8)
                .register();
    }

    public static void registerHotel() {

    }

    public static Identifier findPool(String roomId) {
        return FondestMemories.findRoom("pools", roomId);
    }
}
