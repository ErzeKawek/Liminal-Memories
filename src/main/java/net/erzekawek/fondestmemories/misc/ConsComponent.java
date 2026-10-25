package net.erzekawek.fondestmemories.misc;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

import java.util.List;

public class ConsComponent {

    public static final Consumable DELUSIONARY_EFFECT = Consumables.defaultFood()
            .consumeSeconds(0.8F)
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(List.of(
                            new MobEffectInstance(MobEffects.NIGHT_VISION, 6, 1),
                            new MobEffectInstance(MobEffects.NAUSEA, 6, 1)
                    ))
            ).build();

    public static final Consumable BENEFICIAL = Consumables.defaultFood()
            .consumeSeconds(0.8F)
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(List.of(
                            new MobEffectInstance(MobEffects.REGENERATION, 10, 1),
                            new MobEffectInstance(MobEffects.STRENGTH, 6, 2)
                    ))
            ).build();

    public static final Consumable PAIN = Consumables.defaultFood()
            .consumeSeconds(0.8F)
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(List.of(
                            new MobEffectInstance(MobEffects.WITHER, 10, 1),
                            new MobEffectInstance(MobEffects.SLOWNESS, 7, 1)
                    ))
            ).build();

}
