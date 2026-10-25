package net.erzekawek.fondestmemories.registry;

import net.erzekawek.fondestmemories.FondestMemories;
import net.erzekawek.veyl.levelgen.chunkgen.rooms.RoomGroup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;

/**
 * basically where all the rooms are registered
 */
public final class FondestMemoriesRoomRegistry {
    private FondestMemoriesRoomRegistry() {
    }

    public static void registerPoolrooms() {
        RoomGroup.builder(FondestMemories.id("poolrooms"))
                .guides(Blocks.PRISMARINE, Blocks.DARK_PRISMARINE)

                // le singlet rooms
                .single(FondestMemories.id("pools/pool_8x8_nub"), 0, 2)
                .single(FondestMemories.id("pools/pool_8x8_i"), 0, 4)
                .single(FondestMemories.id("pools/pool_8x8_l"), 0, 4)
                .single(FondestMemories.id("pools/pool_8x8_f"), 0, 2)
                .single(FondestMemories.id("pools/pool_8x8_t"), 0, 1)
                .single(FondestMemories.id("pools/pool_8x8_deadendtall"), -8, 2)

                // dis are big rooms
                .multi(FondestMemories.id("pools/pool_8x16_small"), 1, 2, 0, 3)
                .multi(FondestMemories.id("pools/pool_8x16_tallhall"), 1, 2, 0, 3)
                .multi(FondestMemories.id("pools/pool_16x24_skyview"),2, 3, 0, 2)
                .multi(FondestMemories.id("pools/pool_24x24_lush"), 3, 3, 0, 1)
                .multi(FondestMemories.id("pools/pool_24x32_arches"), 3, 4, 0, 1)
                .multi(FondestMemories.id("pools/pool_24x32_archwalkway"), 3, 4, 0, 1)
                .multi(FondestMemories.id("pools/pool_24x32_tunneling"), 3, 4, 0, 1)

                // de starting room
                .start(FondestMemories.id("pools/pool_24x24_entrance"),3, 3, -8)
                .register();
    }

    public static void registerLightsOut() {
        RoomGroup.builder(FondestMemories.id("lights_out"))
                .guides(Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE)
                .single(findHalls("hall_i"), 0, 30)
                .single(findHalls("hall_l"), 0, 35)
                .single(findHalls("hall_f"), 0, 20)
                .single(findHalls("hall_t"), 0, 20)
                .single(findHalls("hall_dead"), 0, 30)
                .single(findHalls("hall_i_door"), 0, 7)
                .single(findHalls("hall_l_light"), 0, 1)
                .single(findHalls("hall_f_light"), 0, 1)
                .single(findHalls("hall_t_light"), 0, 1)
                .single(findHalls("hall_dead_light"), 0, 1)
                .single(findHalls("hall_i_lightdoor"), 0, 1)

                .multi(findHalls("large_room_chair"), 3, 2, 0, 8)
                .multi(findHalls("large_room_chair_intersect"), 3, 2, 0, 8)
                .multi(findHalls("large_room_chests"), 3, 2, 0, 8)
                .multi(findHalls("large_room_office"), 3, 2, 0, 8)

                .multi(findHalls("large_room_chair_light"), 3, 2, 0, 1)
                .multi(findHalls("large_room_chair_intersect_light"), 3, 2, 0, 1)
                .multi(findHalls("large_room_chests_light"), 3, 2, 0, 1)
                .multi(findHalls("large_room_office_light"), 3, 2, 0, 1)

                .start(findHalls("start_hall"), 2, 2, 0)
                .register();
    }

    public static Identifier findHalls(String roomId) {
        return FondestMemories.findRoom("lights_out", roomId);
    }
}
