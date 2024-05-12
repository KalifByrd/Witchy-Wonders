package com.toxicteddie.witchywonders;

import com.mojang.logging.LogUtils;
import com.toxicteddie.witchywonders.block.custom.HemlockCropBlock;
import com.toxicteddie.witchywonders.block.custom.MandrakeCropBlock;
import com.toxicteddie.witchywonders.entity.ModEntities;
import com.toxicteddie.witchywonders.entity.client.MandrakeRenderer;
import com.toxicteddie.witchywonders.events.powers.HydrokinesisHandler;
import com.toxicteddie.witchywonders.events.powers.TelekinesisHandler;
import com.toxicteddie.witchywonders.network.EntityMovePacket;
import com.toxicteddie.witchywonders.network.NetworkHandler;
import com.toxicteddie.witchywonders.particle.ModParticles;
import com.toxicteddie.witchywonders.sound.ModSounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.MinecraftForge;
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
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WitchyWonders.MODID)
public class WitchyWonders
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "witchywonders";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    // create crop blocks
    public static final RegistryObject<Block> HEMLOCK_CROP = BLOCKS.register("hemlock_crop",
        () -> new HemlockCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion().noCollission()));
    public static final RegistryObject<Block> MANDRAKE_CROP = BLOCKS.register("mandrake_crop",
        () -> new MandrakeCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion().noCollission()));
    
    // Create seeds
    public static final RegistryObject<Item> HEMLOCK_SEEDS = ITEMS.register("hemlock_seeds",
        () -> new ItemNameBlockItem(HEMLOCK_CROP.get(), new Item.Properties()));
    public static final RegistryObject<Item> BELLADONNA_SEEDS = ITEMS.register("belladonna_seeds",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WOLFSBANE_SEEDS = ITEMS.register("wolfsbane_seeds",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MANDRAKE_SEEDS = ITEMS.register("mandrake_seeds",
        () -> new ItemNameBlockItem(MANDRAKE_CROP.get(), new Item.Properties()));
    public static final RegistryObject<Item> VERVAIN_SEEDS = ITEMS.register("vervain_seeds",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SAGE_SEEDS = ITEMS.register("sage_seeds",
        () -> new Item(new Item.Properties()));

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

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));


    
    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final RegistryObject<CreativeModeTab> WITCH_NATURAL_BLOCKS_TAB = CREATIVE_MODE_TABS.register("witch_natural_blocks_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> BELLADONNA_FLOWER_ITEM.get().getDefaultInstance())
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

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        CHANNEL.registerMessage(0, EntityMovePacket.class, EntityMovePacket::toBytes, EntityMovePacket::new, EntityMovePacket::handle);
        MinecraftForge.EVENT_BUS.register(TelekinesisHandler.class);
        MinecraftForge.EVENT_BUS.register(HydrokinesisHandler.class);

        // Register particles
        ModParticles.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModSounds.register(modEventBus);

        // Register Mod Entities
        ModEntities.register(modEventBus);

    }
    // Subscribe to the FMLCommonSetupEvent to setup your mod when common setup occurs
    @SubscribeEvent
    public void setup(FMLCommonSetupEvent event) {
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
            EntityRenderers.register(ModEntities.MANDRAKE.get(), MandrakeRenderer::new);
        }
    }

    
}
