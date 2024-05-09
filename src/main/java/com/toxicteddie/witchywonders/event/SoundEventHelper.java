package com.toxicteddie.witchywonders.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;

public class SoundEventHelper {
    private static SimpleSoundInstance soundInstance = null;

    public static void playContinuousSound(Minecraft mc, SoundEvent sound) {
        if (soundInstance == null || !mc.getSoundManager().isActive(soundInstance)) {
            soundInstance = SimpleSoundInstance.forMusic(sound);
            mc.getSoundManager().play(soundInstance);
        }
    }

    public static void stopContinuousSound() {
        if (soundInstance != null) {
            Minecraft.getInstance().getSoundManager().stop(soundInstance);
            soundInstance = null;
        }
    }

    public static void playOneShotSound(Minecraft mc, SoundEvent sound) {
        SimpleSoundInstance soundInstance = SimpleSoundInstance.forUI(sound, 1.0f);
        mc.getSoundManager().play(soundInstance);
    }
}
