package com.toxicteddie.witchywonders.entity;

import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.entity.custom.CustomPlayerEntityThick;
import com.toxicteddie.witchywonders.entity.custom.CustomPlayerEntityThin;
import com.toxicteddie.witchywonders.entity.custom.MandrakeEntity;
import com.toxicteddie.witchywonders.entity.custom.SeatEntity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, WitchyWonders.MODID);

    public static final RegistryObject<EntityType<MandrakeEntity>> MANDRAKE =
        ENTITY_TYPES.register("mandrake", () -> EntityType.Builder.of(MandrakeEntity::new, MobCategory.MONSTER)
            .sized(0.6f, 1.0f).build("mandrake"));
    
    public static final RegistryObject<EntityType<CustomPlayerEntityThick>> CUSTOM_PLAYER_THICK =
            ENTITY_TYPES.register("custom_player_thick", () -> EntityType.Builder.of(CustomPlayerEntityThick::new, MobCategory.MISC)
                .sized(0.6f, 1.0f).build("custom_player_thick"));
    
    public static final RegistryObject<EntityType<CustomPlayerEntityThin>> CUSTOM_PLAYER_THIN =
            ENTITY_TYPES.register("custom_player_thin", () -> EntityType.Builder.of(CustomPlayerEntityThin::new, MobCategory.MISC)
                .sized(0.6f, 1.0f).build("custom_player_thin"));
    public static final RegistryObject<EntityType<SeatEntity>> SEAT =
    		ENTITY_TYPES.register("seat",
            () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                .sized(0.001F, 0.001F)
                .build("seat"));
    

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
