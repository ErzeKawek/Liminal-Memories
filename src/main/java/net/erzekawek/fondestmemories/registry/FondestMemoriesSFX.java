package net.erzekawek.fondestmemories.registry;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

import net.erzekawek.veyl.utils.registry.UnifiedRegistry;

import static net.erzekawek.fondestmemories.FondestMemories.MOD_ID;

public class FondestMemoriesSFX {

    public static final Holder<SoundEvent> SPACES_AMBIENCE = UnifiedRegistry.registerReference(MOD_ID, "ambient.spaces.loop");
    public static final Holder<SoundEvent> LIM_MUSIC = UnifiedRegistry.registerReference(MOD_ID, "music.liminal");

    public static void initialize() {
    }

}
