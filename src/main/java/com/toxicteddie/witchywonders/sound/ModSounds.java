package com.toxicteddie.witchywonders.sound;

import com.toxicteddie.witchywonders.WitchyWonders;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, WitchyWonders.MODID);

    public static final RegistryObject<SoundEvent> MAGIC_SOUND = registerSoundEvent("magic_sound");

    public static final RegistryObject<SoundEvent> MAGIC_SOUND_END = registerSoundEvent("magic_sound_end");

    public static final ForgeSoundType MAGIC_POWER_SOUNDS = new ForgeSoundType(1f, 1f,
            ModSounds.MAGIC_SOUND, ModSounds.MAGIC_SOUND_END, null, null, null);

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(WitchyWonders.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
