package net.erzekawek.fondestmemories.mixins;

import net.erzekawek.fondestmemories.misc.RestrictedDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void fondestmemories$blockF3(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (!RestrictedDimensions.noDebug(mc.level)) return;

        if (mc.debugEntries.isOverlayVisible()) {
            graphics.text(mc.font, "No F3 for you brochacho.", 2, 2, 0xFFFFFFFF, true);
        }
        ci.cancel();
    }
}
