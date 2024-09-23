package com.toxicteddie.witchywonders.effect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class CallToTheBeastEffect extends MobEffect {
    public CallToTheBeastEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }
    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if(!pLivingEntity.level().isClientSide()) {
            if(true) //acceptableBeastOffer
            {
                //make naseause
                //scary sound
                
                //spawn The Beast in front of player
                
            }
        }
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }
    @Override
    public boolean isDurationEffectTick(int p_19455_, int p_19456_) {
        return true;
    }
    
}
