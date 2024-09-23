package com.toxicteddie.witchywonders;

import com.mojang.logging.LogUtils;
import com.toxicteddie.witchywonders.block.custom.AltarBlock;
import com.toxicteddie.witchywonders.block.custom.BelladonnaCropBlock;
import com.toxicteddie.witchywonders.block.custom.ClothColor;
import com.toxicteddie.witchywonders.block.custom.HemlockCropBlock;
import com.toxicteddie.witchywonders.block.custom.MandrakeCropBlock;
import com.toxicteddie.witchywonders.block.custom.MeditationBedBlock;
import com.toxicteddie.witchywonders.block.custom.MeditationStoolBlock;
import com.toxicteddie.witchywonders.block.custom.MortarAndPestleBlock;
import com.toxicteddie.witchywonders.block.custom.PestleBlock;
import com.toxicteddie.witchywonders.block.custom.WitchsCauldronBlock;
import com.toxicteddie.witchywonders.block.custom.WitchsOvenBlock;
import com.toxicteddie.witchywonders.block.custom.WoodType;
import com.toxicteddie.witchywonders.block.entity.AltarBlockEntity;
import com.toxicteddie.witchywonders.block.entity.MeditationBedBlockEntity;
import com.toxicteddie.witchywonders.block.entity.MeditationStoolBlockEntity;
import com.toxicteddie.witchywonders.block.entity.MortarAndPestleBlockEntity;
import com.toxicteddie.witchywonders.block.entity.WitchsCauldronBlockEntity;
import com.toxicteddie.witchywonders.block.entity.WitchsOvenBlockEntity;
import com.toxicteddie.witchywonders.block.entity.render.AltarBlockEntityRenderer;
import com.toxicteddie.witchywonders.client.model.CustomPlayerModelThick;
import com.toxicteddie.witchywonders.client.renderer.CustomPlayerThickRenderer;
import com.toxicteddie.witchywonders.client.renderer.CustomPlayerThinRenderer;
import com.toxicteddie.witchywonders.client.renderer.SeatEntityRenderer;
import com.toxicteddie.witchywonders.effect.CallToTheBeastEffect;
import com.toxicteddie.witchywonders.entity.ModEntities;
import com.toxicteddie.witchywonders.entity.client.MandrakeRenderer;
import com.toxicteddie.witchywonders.entity.client.ModModelLayers;
import com.toxicteddie.witchywonders.entity.custom.SeatEntity;
import com.toxicteddie.witchywonders.events.powers.HydrokinesisHandler;
import com.toxicteddie.witchywonders.events.powers.TelekinesisHandler;
import com.toxicteddie.witchywonders.network.EntityMovePacket;
import com.toxicteddie.witchywonders.network.NetworkHandler;
import com.toxicteddie.witchywonders.particle.ModParticles;
import com.toxicteddie.witchywonders.potion.PotionEventHandler;
import com.toxicteddie.witchywonders.recipe.ModRecipes;
import com.toxicteddie.witchywonders.screen.MortarAndPestleMenu;
import com.toxicteddie.witchywonders.screen.MortarAndPestleScreen;
import com.toxicteddie.witchywonders.screen.WitchsOvenMenu;
import com.toxicteddie.witchywonders.screen.WitchsOvenScreen;
import com.toxicteddie.witchywonders.sound.ModSounds;
import com.toxicteddie.witchywonders.util.BetterBrewingRecipe;
import com.toxicteddie.witchywonders.util.ModBrewingRecipe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.text.DateFormat.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WitchyWonders.MODID)
public class WitchyWonders
{
	
    // Define mod id in a common place for everything to reference
    public static final String MODID = "witchywonders";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "witchywonders" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to handle Block Entities which will be registered under the "wichywonders" namespace
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "witchywonders" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold Potions which will all be registered under the "witchywonders" namespace
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, MODID);
    // Create a Deferred Register to hold Mob Effects which will all be registered under the "witchywonders" namespace
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    // Create a Deferred Register to handle Menus wich will be registered under the "witchywonders" namespace
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    
    

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

    // Create functional blocks
    public static final RegistryObject<Block> MORTAR_AND_PESTLE = BLOCKS.register("mortar",
        () -> new MortarAndPestleBlock(BlockBehaviour.Properties.copy(Blocks.STONE).dynamicShape().noOcclusion()));
    public static final RegistryObject<BlockEntityType<MortarAndPestleBlockEntity>> MORTAR_AND_PESTLE_BLOCK_ENTITY =
        BLOCK_ENTITIES.register("mortar_and_pestle_block_entity", () -> BlockEntityType.Builder.of(MortarAndPestleBlockEntity::new,
            MORTAR_AND_PESTLE.get()).build(null));

    public static final RegistryObject<Block> WITCHS_OVEN = BLOCKS.register("witchs_oven",
        () -> new WitchsOvenBlock(BlockBehaviour.Properties.copy(Blocks.IRON_ORE).dynamicShape().noOcclusion()));
    public static final RegistryObject<BlockEntityType<WitchsOvenBlockEntity>> WITCHS_OVEN_BLOCK_ENTITY =
        BLOCK_ENTITIES.register("witchs_oven_block_entity", () -> BlockEntityType.Builder.of(WitchsOvenBlockEntity::new,
            WITCHS_OVEN.get()).build(null));

    public static final RegistryObject<Block> WITCHS_CAULDRON = BLOCKS.register("witchs_cauldron",
        () -> new WitchsCauldronBlock(BlockBehaviour.Properties.copy(Blocks.IRON_ORE).dynamicShape().noOcclusion()));
    public static final RegistryObject<BlockEntityType<WitchsCauldronBlockEntity>> WITCHS_CAULDRON_BLOCK_ENTITY =
        BLOCK_ENTITIES.register("witchs_cauldron_block_entity", () -> BlockEntityType.Builder.of(WitchsCauldronBlockEntity::new,
            WITCHS_CAULDRON.get()).build(null));

    public static final RegistryObject<Block> ALTAR = BLOCKS.register("altar_block",
        () -> new AltarBlock(BlockBehaviour.Properties.copy(Blocks.DARK_OAK_PLANKS).dynamicShape().noOcclusion()));
    public static final RegistryObject<BlockEntityType<AltarBlockEntity>> ALTAR_BLOCK_ENTITY =
        BLOCK_ENTITIES.register("altar_block_entity", () -> BlockEntityType.Builder.of(AltarBlockEntity::new,
            ALTAR.get()).build(null));
    public static final RegistryObject<Block> MEDITATION_BED = BLOCKS.register("meditation_bed_block",
            () -> new MeditationBedBlock(BlockBehaviour.Properties.copy(Blocks.DARK_OAK_PLANKS).dynamicShape().noOcclusion()));
     public static final RegistryObject<BlockEntityType<MeditationBedBlockEntity>> MEDITATION_BED_BLOCK_ENTITY =
        BLOCK_ENTITIES.register("meditation_bed_block_entity", () -> BlockEntityType.Builder.of(MeditationBedBlockEntity::new,
            MEDITATION_BED.get()).build(null));
     public static final RegistryObject<Block> MEDITATION_STOOL = BLOCKS.register("meditation_stool_block",
             () -> new MeditationStoolBlock(BlockBehaviour.Properties.copy(Blocks.DARK_OAK_PLANKS).dynamicShape().noOcclusion()));
      public static final RegistryObject<BlockEntityType<MeditationStoolBlockEntity>> MEDITATION_STOOL_BLOCK_ENTITY =
         BLOCK_ENTITIES.register("meditation_stool_block_entity", () -> BlockEntityType.Builder.of(MeditationStoolBlockEntity::new,
             MEDITATION_STOOL.get()).build(null));
         
        


    //this block will be hidden in the game its just for animation
    public static final RegistryObject<Block> PESTLE = BLOCKS.register("pestle",
        () -> new PestleBlock(BlockBehaviour.Properties.copy(Blocks.STONE).dynamicShape().noOcclusion()));

    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    // create crop blocks
    public static final RegistryObject<Block> HEMLOCK_CROP = BLOCKS.register("hemlock_crop",
        () -> new HemlockCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion().noCollission()));
    public static final RegistryObject<Block> MANDRAKE_CROP = BLOCKS.register("mandrake_crop",
        () -> new MandrakeCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion().noCollission()));
    public static final RegistryObject<Block> BELLADONNA_CROP = BLOCKS.register("belladonna_crop",
        () -> new BelladonnaCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion().noCollission()));
    public static final RegistryObject<Block> WOLFSBANE_CROP = BLOCKS.register("wolfsbane_crop",
        () -> new BelladonnaCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion().noCollission()));
    public static final RegistryObject<Block> VERVAIN_CROP = BLOCKS.register("vervain_crop",
        () -> new BelladonnaCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion().noCollission()));
    public static final RegistryObject<Block> SAGE_CROP = BLOCKS.register("sage_crop",
        () -> new BelladonnaCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion().noCollission()));
    // Create seeds
    public static final RegistryObject<Item> HEMLOCK_SEEDS = ITEMS.register("hemlock_seeds",
        () -> new ItemNameBlockItem(HEMLOCK_CROP.get(), new Item.Properties()));
    public static final RegistryObject<Item> BELLADONNA_SEEDS = ITEMS.register("belladonna_seeds",
        () -> new ItemNameBlockItem(BELLADONNA_CROP.get(), new Item.Properties()));
    public static final RegistryObject<Item> WOLFSBANE_SEEDS = ITEMS.register("wolfsbane_seeds",
        () -> new ItemNameBlockItem(WOLFSBANE_CROP.get(), new Item.Properties()));
    public static final RegistryObject<Item> MANDRAKE_SEEDS = ITEMS.register("mandrake_seeds",
        () -> new ItemNameBlockItem(MANDRAKE_CROP.get(), new Item.Properties()));
    public static final RegistryObject<Item> VERVAIN_SEEDS = ITEMS.register("vervain_seeds",
        () -> new ItemNameBlockItem(VERVAIN_CROP.get(), new Item.Properties()));
    public static final RegistryObject<Item> SAGE_SEEDS = ITEMS.register("sage_seeds",
        () -> new ItemNameBlockItem(SAGE_CROP.get(), new Item.Properties()));
    
    // non-functional seeds
    public static final RegistryObject<Item> SUNFLOWER_SEEDS_ITEM = ITEMS.register("sunflower_seeds", () -> new Item(new Item.Properties()));

    // Create spawn eggs
    public static final RegistryObject<Item> MANDRAKE_SPAWN_EGG = ITEMS.register("mandrake_spawn_egg",
        () -> new ForgeSpawnEggItem(ModEntities.MANDRAKE, 0x40312E, 0x312127,
            new Item.Properties()));

    //create flowers
    public static final RegistryObject<Item> HEMLOCK_ROOT_ITEM = ITEMS.register("hemlock_root", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HEMLOCK_FLOWER_ITEM = ITEMS.register("hemlock_flower", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BELLADONNA_FLOWER_ITEM = ITEMS.register("belladonna_flower", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WOLFSBANE_FLOWER_ITEM = ITEMS.register("wolfsbane_flower", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MANDRAKE_ROOT_ITEM = ITEMS.register("mandrake_root", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VERVAIN_FLOWER_ITEM = ITEMS.register("vervain_flower", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SAGE_LEAF_ITEM = ITEMS.register("sage_leaf", () -> new Item(new Item.Properties()));

    // create witchy items
    public static final RegistryObject<Item> PENTACLE_ITEM = ITEMS.register("pentacle", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_CHALK_ITEM = ITEMS.register("white_chalk", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ATHAME_ITEM = ITEMS.register("athame", () -> new Item(new Item.Properties()));

    // minerals
    public static final RegistryObject<Item> LIMESTONE_CHUNK_ITEM = ITEMS.register("limestone_chunk", () -> new Item(new Item.Properties()));

    // create powder items
    public static final RegistryObject<Item> MANDRAKE_POWDER_ITEM = ITEMS.register("mandrake_powder", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SUNFLOWER_SEED_POWDER_ITEM = ITEMS.register("sunflower_seed_powder", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LIMESTONE_DUST_ITEM = ITEMS.register("limestone_dust", () -> new Item(new Item.Properties()));
    //create effects
    public static final RegistryObject<MobEffect> CALL_TO_THE_BEAST = MOB_EFFECTS.register("call_to_the_beast", () -> new CallToTheBeastEffect(MobEffectCategory.NEUTRAL, 0));
    //create oil items
    public static final RegistryObject<Potion> BOTTLE_OF_OIL = POTIONS.register("bottle_of_oil", () -> new Potion());
    
    private static ItemStack createBottleOfOilPotion() {
        ItemStack potionStack = new ItemStack(Items.POTION);
        CompoundTag tag = new CompoundTag();
        tag.putString("Potion", "witchywonders:bottle_of_oil");
        potionStack.setTag(tag);
        return potionStack;
    }

    public static final RegistryObject<Potion> OIL_OF_ANOINTING = POTIONS.register("oil_of_anointing", () -> new Potion(new MobEffectInstance(CALL_TO_THE_BEAST.get())));
    
    private static ItemStack createOilOfAnointingPotion() {
        ItemStack potionStack = new ItemStack(Items.POTION);
        CompoundTag tag = new CompoundTag();
        tag.putString("Potion", "witchywonders:oil_of_anointing");
        potionStack.setTag(tag);
        return potionStack;
    }
    

    

    // create functional block items
    public static final RegistryObject<Item> MORTAR_AND_PESTLE_ITEM = ITEMS.register("mortar_and_pestle",
        () -> new BlockItem(MORTAR_AND_PESTLE.get(), new Item.Properties()));
    
    public static final RegistryObject<Item> PESTLE_ITEM = ITEMS.register("pestle",
        () -> new BlockItem(PESTLE.get(), new Item.Properties()));

    public static final RegistryObject<Item> WITCHS_OVEN_ITEM = ITEMS.register("witchs_oven",
        () -> new BlockItem(WITCHS_OVEN.get(), new Item.Properties()));

    public static final RegistryObject<Item> WITCHS_CAULDRON_ITEM = ITEMS.register("witchs_cauldron",
        () -> new BlockItem(WITCHS_CAULDRON.get(), new Item.Properties()));

    public static final RegistryObject<Item> ALTAR_ITEM_ACACIA = ITEMS.register("acacia_altar_item",
        () -> new BlockItem(ALTAR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALTAR_ITEM_BAMBOO = ITEMS.register("bamboo_altar_item",
        () -> new BlockItem(ALTAR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALTAR_ITEM_BIRCH = ITEMS.register("birch_altar_item",
        () -> new BlockItem(ALTAR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALTAR_ITEM_CHERRY = ITEMS.register("cherry_altar_item",
        () -> new BlockItem(ALTAR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALTAR_ITEM_CRIMSON = ITEMS.register("crimson_altar_item",
        () -> new BlockItem(ALTAR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALTAR_ITEM_DARK_OAK = ITEMS.register("dark_oak_altar_item",
        () -> new BlockItem(ALTAR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALTAR_ITEM_JUNGLE = ITEMS.register("jungle_altar_item",
        () -> new BlockItem(ALTAR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALTAR_ITEM_MANGROVE = ITEMS.register("mangrove_altar_item",
        () -> new BlockItem(ALTAR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALTAR_ITEM_OAK = ITEMS.register("oak_altar_item",
        () -> new BlockItem(ALTAR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALTAR_ITEM_SPRUCE = ITEMS.register("spruce_altar_item",
        () -> new BlockItem(ALTAR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALTAR_ITEM_WARPED = ITEMS.register("warped_altar_item",
        () -> new BlockItem(ALTAR.get(), new Item.Properties()));

    public static final RegistryObject<Item> MEDITATION_BED_ITEM_ACACIA = ITEMS.register("acacia_meditation_bed_item",
        () -> new BlockItem(MEDITATION_BED.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_BED_ITEM_BAMBOO = ITEMS.register("bamboo_meditation_bed_item",
        () -> new BlockItem(MEDITATION_BED.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_BED_ITEM_BIRCH = ITEMS.register("birch_meditation_bed_item",
        () -> new BlockItem(MEDITATION_BED.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_BED_ITEM_CHERRY = ITEMS.register("cherry_meditation_bed_item",
        () -> new BlockItem(MEDITATION_BED.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_BED_ITEM_CRIMSON = ITEMS.register("crimson_meditation_bed_item",
        () -> new BlockItem(MEDITATION_BED.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_BED_ITEM_DARK_OAK = ITEMS.register("dark_oak_meditation_bed_item",
        () -> new BlockItem(MEDITATION_BED.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_BED_ITEM_JUNGLE = ITEMS.register("jungle_meditation_bed_item",
        () -> new BlockItem(MEDITATION_BED.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_BED_ITEM_MANGROVE = ITEMS.register("mangrove_meditation_bed_item",
        () -> new BlockItem(MEDITATION_BED.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_BED_ITEM_OAK = ITEMS.register("oak_meditation_bed_item",
        () -> new BlockItem(MEDITATION_BED.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_BED_ITEM_SPRUCE = ITEMS.register("spruce_meditation_bed_item",
        () -> new BlockItem(MEDITATION_BED.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_BED_ITEM_WARPED = ITEMS.register("warped_meditation_bed_item",
        () -> new BlockItem(MEDITATION_BED.get(), new Item.Properties()));
    
    public static final RegistryObject<Item> MEDITATION_STOOL_ITEM_ACACIA = ITEMS.register("acacia_meditation_stool_item",
        () -> new BlockItem(MEDITATION_STOOL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_STOOL_ITEM_BAMBOO = ITEMS.register("bamboo_meditation_stool_item",
        () -> new BlockItem(MEDITATION_STOOL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_STOOL_ITEM_BIRCH = ITEMS.register("birch_meditation_stool_item",
        () -> new BlockItem(MEDITATION_STOOL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_STOOL_ITEM_CHERRY = ITEMS.register("cherry_meditation_stool_item",
        () -> new BlockItem(MEDITATION_STOOL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_STOOL_ITEM_CRIMSON = ITEMS.register("crimson_meditation_stool_item",
        () -> new BlockItem(MEDITATION_STOOL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_STOOL_ITEM_DARK_OAK = ITEMS.register("dark_oak_meditation_stool_item",
        () -> new BlockItem(MEDITATION_STOOL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_STOOL_ITEM_JUNGLE = ITEMS.register("jungle_meditation_stool_item",
        () -> new BlockItem(MEDITATION_STOOL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_STOOL_ITEM_MANGROVE = ITEMS.register("mangrove_meditation_stool_item",
        () -> new BlockItem(MEDITATION_STOOL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_STOOL_ITEM_OAK = ITEMS.register("oak_meditation_stool_item",
        () -> new BlockItem(MEDITATION_STOOL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_STOOL_ITEM_SPRUCE = ITEMS.register("spruce_meditation_stool_item",
        () -> new BlockItem(MEDITATION_STOOL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEDITATION_STOOL_ITEM_WARPED = ITEMS.register("warped_meditation_stool_item",
        () -> new BlockItem(MEDITATION_STOOL.get(), new Item.Properties()));
    
    



    // private static final String[] WOOL_COLORS = {
    //         "black", "blue", "brown", "cyan", "gray", "green", "light_blue", "light_gray",
    //         "lime", "magenta", "orange", "pink", "purple", "red", "white", "yellow"
    // };

    // private static final String[] WOOD_VARIANTS = {
    //         "acacia", "bamboo", "birch", "cherry", "crimson", "dark_oak", "jungle", "mangrove",
    //         "oak", "spruce", "warped"
    // };
    // private static Item createMeditationBedItem(String woodType, String clothColor) {
    //     String title = "MEDITATION_BED_" + woodType + "_" + clothColor;
    //     return new BlockItem(((RegistryObject<Block>)(title.toUpperCase())).get(), new Item.Properties()) {
    //         @Override
    //         public ItemStack getDefaultInstance() {
    //             ItemStack stack = new ItemStack(this);
    //             CompoundTag tag = stack.getOrCreateTag();
    //             tag.putString("WoodType", woodType.toUpperCase());
    //             tag.putString("ClothColor", clothColor.toUpperCase());
    //             stack.setTag(tag);
    //             return stack;
    //         }
    //     };
    // }
    
    // private static void registerMeditationBedItems() {
    //     for (String wood : WOOD_VARIANTS) {
    //         for (String color : WOOL_COLORS) {
    //             String itemName = wood + "_" + color + "_meditation_bed_item";
    //             ITEMS.register(itemName, () -> createMeditationBedItem(wood, color));
    //         }
    //     }
    // }
    

    

    // create menus
    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }
    public static final RegistryObject<MenuType<MortarAndPestleMenu>> MORTAR_AND_PESTLE_MENU = registerMenuType("mortar_and_pestle_menu", MortarAndPestleMenu::new);
    public static final RegistryObject<MenuType<WitchsOvenMenu>> WITCHS_OVEN_MENU = registerMenuType("witchs_oven_menu", WitchsOvenMenu::new);

    

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));


    
    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final RegistryObject<CreativeModeTab> WITCH_NATURAL_BLOCKS_TAB = CREATIVE_MODE_TABS.register("witch_natural_blocks_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> PENTACLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(HEMLOCK_SEEDS.get());
                output.accept(BELLADONNA_SEEDS.get());
                output.accept(WOLFSBANE_SEEDS.get());
                output.accept(MANDRAKE_SEEDS.get());
                output.accept(VERVAIN_SEEDS.get());
                output.accept(SAGE_SEEDS.get());

                output.accept(HEMLOCK_ROOT_ITEM.get());
                output.accept(HEMLOCK_FLOWER_ITEM.get());
                output.accept(BELLADONNA_FLOWER_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                output.accept(WOLFSBANE_FLOWER_ITEM.get());
                output.accept(MANDRAKE_ROOT_ITEM.get());
                output.accept(VERVAIN_FLOWER_ITEM.get());
                output.accept(SAGE_LEAF_ITEM.get());

                output.accept(MANDRAKE_SPAWN_EGG.get());

                output.accept(PENTACLE_ITEM.get());

                output.accept(MANDRAKE_POWDER_ITEM.get());

                output.accept(MORTAR_AND_PESTLE_ITEM.get());
                output.accept(WITCHS_OVEN_ITEM.get());
                output.accept(WITCHS_CAULDRON_ITEM.get());
                output.accept(ALTAR_ITEM_ACACIA.get());
                output.accept(ALTAR_ITEM_BAMBOO.get());
                output.accept(ALTAR_ITEM_BIRCH.get());
                output.accept(ALTAR_ITEM_CHERRY.get());
                output.accept(ALTAR_ITEM_CRIMSON.get());
                output.accept(ALTAR_ITEM_DARK_OAK.get());
                output.accept(ALTAR_ITEM_JUNGLE.get());
                output.accept(ALTAR_ITEM_MANGROVE.get());
                output.accept(ALTAR_ITEM_OAK.get());
                output.accept(ALTAR_ITEM_SPRUCE.get());
                output.accept(ALTAR_ITEM_WARPED.get());

//                output.accept(MEDITATION_BED_ITEM_ACACIA.get());
//                output.accept(MEDITATION_BED_ITEM_BAMBOO.get());
//                output.accept(MEDITATION_BED_ITEM_BIRCH.get());
//                output.accept(MEDITATION_BED_ITEM_CHERRY.get());
//                output.accept(MEDITATION_BED_ITEM_CRIMSON.get());
//                output.accept(MEDITATION_BED_ITEM_DARK_OAK.get());
//                output.accept(MEDITATION_BED_ITEM_JUNGLE.get());
//                output.accept(MEDITATION_BED_ITEM_MANGROVE.get());
//                output.accept(MEDITATION_BED_ITEM_OAK.get());
//                output.accept(MEDITATION_BED_ITEM_SPRUCE.get());
//                output.accept(MEDITATION_BED_ITEM_WARPED.get());
                
                output.accept(MEDITATION_STOOL_ITEM_ACACIA.get());
                output.accept(MEDITATION_STOOL_ITEM_BAMBOO.get());
                output.accept(MEDITATION_STOOL_ITEM_BIRCH.get());
                output.accept(MEDITATION_STOOL_ITEM_CHERRY.get());
                output.accept(MEDITATION_STOOL_ITEM_CRIMSON.get());
                output.accept(MEDITATION_STOOL_ITEM_DARK_OAK.get());
                output.accept(MEDITATION_STOOL_ITEM_JUNGLE.get());
                output.accept(MEDITATION_STOOL_ITEM_MANGROVE.get());
                output.accept(MEDITATION_STOOL_ITEM_OAK.get());
                output.accept(MEDITATION_STOOL_ITEM_SPRUCE.get());
                output.accept(MEDITATION_STOOL_ITEM_WARPED.get());
                
                
              

                // Add all meditation bed items to the custom tab
                // for (String wood : WOOD_VARIANTS) {
                //     for (String color : WOOL_COLORS) {
                //         String itemName = wood + "_" + color + "_meditation_bed_item";
                //         ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(WitchyWonders.MODID, itemName)));
                //         itemStack.setCount(1); // Ensure the stack size is 1
                //         output.accept(itemStack);
                //     }
                // }

                output.accept(SUNFLOWER_SEEDS_ITEM.get());
                output.accept(SUNFLOWER_SEED_POWDER_ITEM.get());
                output.accept(createBottleOfOilPotion());
                output.accept(createOilOfAnointingPotion());  // Add the potion
                
                output.accept(LIMESTONE_CHUNK_ITEM.get());
                output.accept(LIMESTONE_DUST_ITEM.get());

                output.accept(WHITE_CHALK_ITEM.get());

                output.accept(ATHAME_ITEM.get());
            }).build());

    
    
    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation("witchywonders", "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );
    public WitchyWonders()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        POTIONS.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);

        // Register the Deferred Register to the mod event bus so block entities get registered
        BLOCK_ENTITIES.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register the Deferred Register to the mod event bus so menus get registered
        MENUS.register(modEventBus);

        // Register the Deferred Register to the mod event bus so serializers get registered
        ModRecipes.SERIALIZERS.register(modEventBus);

        // Register the Deferred Register to the mod event bus so types get registered
        ModRecipes.register(modEventBus);
        LootTableHandler.register();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        CHANNEL.registerMessage(0, EntityMovePacket.class, EntityMovePacket::toBytes, EntityMovePacket::new, EntityMovePacket::handle);
        MinecraftForge.EVENT_BUS.register(TelekinesisHandler.class);
        MinecraftForge.EVENT_BUS.register(HydrokinesisHandler.class);
        MinecraftForge.EVENT_BUS.register(MeditationBedBlock.class);

        // Register particles
        ModParticles.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModSounds.register(modEventBus);

        forgeEventBus.register(this);
        // Register Potion Event Handler
        MinecraftForge.EVENT_BUS.register(PotionEventHandler.class);
        

        // Register Mod Entities
        ModEntities.register(modEventBus);
        // Register Meditation Bed Items
        // registerMeditationBedItems();

        

        


    }
    // Subscribe to the FMLCommonSetupEvent to setup your mod when common setup occurs
    @SubscribeEvent
    public void setup(FMLCommonSetupEvent event) {
    	//ClientEventHandler.register();
        // Enqueue the setup to be run in the proper thread
        event.enqueueWork(() -> {
            NetworkHandler.register();  // Initialize your networking
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey()== CreativeModeTabs.NATURAL_BLOCKS)
            event.accept(HEMLOCK_SEEDS);
        if (event.getTabKey()== CreativeModeTabs.NATURAL_BLOCKS)
            event.accept(BELLADONNA_SEEDS);
        if (event.getTabKey()== CreativeModeTabs.NATURAL_BLOCKS)
            event.accept(WOLFSBANE_SEEDS);
        if (event.getTabKey()== CreativeModeTabs.NATURAL_BLOCKS)
            event.accept(MANDRAKE_SEEDS);
        if (event.getTabKey()== CreativeModeTabs.NATURAL_BLOCKS)
            event.accept(VERVAIN_SEEDS);
        if (event.getTabKey()== CreativeModeTabs.NATURAL_BLOCKS)
            event.accept(SAGE_SEEDS);

        if (event.getTabKey()== CreativeModeTabs.NATURAL_BLOCKS)
            event.accept(HEMLOCK_ROOT_ITEM);
        if (event.getTabKey()== CreativeModeTabs.NATURAL_BLOCKS)
            event.accept(HEMLOCK_FLOWER_ITEM);
        if (event.getTabKey()== CreativeModeTabs.NATURAL_BLOCKS)
            event.accept(BELLADONNA_FLOWER_ITEM);
        if (event.getTabKey()== CreativeModeTabs.NATURAL_BLOCKS)
            event.accept(WOLFSBANE_FLOWER_ITEM);
        if (event.getTabKey()== CreativeModeTabs.NATURAL_BLOCKS)
            event.accept(MANDRAKE_ROOT_ITEM);
        if (event.getTabKey()== CreativeModeTabs.NATURAL_BLOCKS)
            event.accept(VERVAIN_FLOWER_ITEM);
        if (event.getTabKey()== CreativeModeTabs.NATURAL_BLOCKS)
            event.accept(SAGE_LEAF_ITEM);

        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS)
            event.accept(MANDRAKE_SPAWN_EGG);
        
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
            event.accept(MANDRAKE_POWDER_ITEM);

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(MORTAR_AND_PESTLE_ITEM);
            event.accept(WITCHS_OVEN_ITEM);
            event.accept(WITCHS_CAULDRON_ITEM);
            event.accept(ALTAR_ITEM_ACACIA);
            event.accept(ALTAR_ITEM_BAMBOO);
            event.accept(ALTAR_ITEM_BIRCH);
            event.accept(ALTAR_ITEM_CHERRY);
            event.accept(ALTAR_ITEM_CRIMSON);
            event.accept(ALTAR_ITEM_DARK_OAK);
            event.accept(ALTAR_ITEM_JUNGLE);
            event.accept(ALTAR_ITEM_MANGROVE);
            event.accept(ALTAR_ITEM_OAK);
            event.accept(ALTAR_ITEM_SPRUCE);
            event.accept(ALTAR_ITEM_WARPED);
//            event.accept(MEDITATION_BED_ITEM_ACACIA);
//            event.accept(MEDITATION_BED_ITEM_BAMBOO);
//            event.accept(MEDITATION_BED_ITEM_BIRCH);
//            event.accept(MEDITATION_BED_ITEM_CHERRY);
//            event.accept(MEDITATION_BED_ITEM_CRIMSON);
//            event.accept(MEDITATION_BED_ITEM_DARK_OAK);
//            event.accept(MEDITATION_BED_ITEM_JUNGLE);
//            event.accept(MEDITATION_BED_ITEM_MANGROVE);
//            event.accept(MEDITATION_BED_ITEM_OAK);
//            event.accept(MEDITATION_BED_ITEM_SPRUCE);
//            event.accept(MEDITATION_BED_ITEM_WARPED);
            event.accept(MEDITATION_STOOL_ITEM_ACACIA);
            event.accept(MEDITATION_STOOL_ITEM_BAMBOO);
            event.accept(MEDITATION_STOOL_ITEM_BIRCH);
            event.accept(MEDITATION_STOOL_ITEM_CHERRY);
            event.accept(MEDITATION_STOOL_ITEM_CRIMSON);
            event.accept(MEDITATION_STOOL_ITEM_DARK_OAK);
            event.accept(MEDITATION_STOOL_ITEM_JUNGLE);
            event.accept(MEDITATION_STOOL_ITEM_OAK);
            event.accept(MEDITATION_STOOL_ITEM_SPRUCE);
            event.accept(MEDITATION_STOOL_ITEM_WARPED);

            // Add all meditation bed items to the functional blocks tab
            // for (String wood : WOOD_VARIANTS) {
            //     for (String color : WOOL_COLORS) {
            //         String itemName = wood + "_" + color + "_meditation_bed_item";
            //         event.accept(BuiltInRegistries.ITEM.get(new ResourceLocation(WitchyWonders.MODID, itemName)));
            //     }
            // }
        }

        // if(event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS)
        //     event.accept(OIL_OF_ANNOINTING_ITEM);
        
        //minecraft.potion.effect
    
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
            MenuScreens.register(WITCHS_OVEN_MENU.get(), WitchsOvenScreen::new);
            EntityRenderers.register(ModEntities.MANDRAKE.get(), MandrakeRenderer::new);
            EntityRenderers.register(ModEntities.CUSTOM_PLAYER_THICK.get(), CustomPlayerThickRenderer::new);
            EntityRenderers.register(ModEntities.CUSTOM_PLAYER_THIN.get(), CustomPlayerThinRenderer::new);
            //EntityRenderers.register(ModEntities.SEAT.get(), SeatEntityRenderer::new);
            //EntityRenderers.register(ModEntities.SEAT.get(), SeatEntity::new);
            //EntityRenderers.register(ModEntities.MEDITATION_BED.get(), MeditationBedRenderer::new);
            //BlockEntityRenderers.register(WitchyWonders.ALTAR_BLOCK_ENTITY.get(), AltarBlockEntityRenderer::new);
            //MinecraftForge.EVENT_BUS.register(AltarCenterBakedModel.RegisterModel.class);
            BlockEntityRenderers.register(WitchyWonders.ALTAR_BLOCK_ENTITY.get(), AltarBlockEntityRenderer::new);
            // Register custom player renderer
            
         
            

            ItemProperties.register(Items.POTION, new ResourceLocation(MODID, "custom_potion0"),
                (stack, world, entity, seed) -> {
                    if (stack.getItem() instanceof PotionItem) {
                        Potion potion = PotionUtils.getPotion(stack);
                        return potion == WitchyWonders.BOTTLE_OF_OIL.get() ? 1.0F : 0.0F;
                    }
                    return 0.0F;
                });
            ItemProperties.register(Items.POTION, new ResourceLocation(MODID, "custom_potion1"),
                (stack, world, entity, seed) -> {
                    if (stack.getItem() instanceof PotionItem) {
                        Potion potion = PotionUtils.getPotion(stack);
                        return potion == WitchyWonders.OIL_OF_ANOINTING.get() ? 1.0F : 0.0F;
                    }
                    return 0.0F;
                });

        }
        
    }

    
}
