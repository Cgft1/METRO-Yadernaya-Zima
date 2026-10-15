package ru.metro.yadernaya_zima;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

@Mod(MetroMod.MODID)
public class MetroMod {
    public static final String MODID = "metro_yadernaya_zima";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    public static final RegistryObject<Item> CANNED_FOOD = ITEMS.register("canned_food",
        () -> new Item(new Item.Properties().food(new net.minecraft.world.food.FoodProperties.Builder().nutrition(5).saturationMod(0.4f).build())));
    public static final RegistryObject<Item> FILTER = ITEMS.register("gas_mask_filter",
        () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> MEDKIT = ITEMS.register("medkit",
        () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> GAS_MASK = ITEMS.register("gas_mask",
        () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SCRAP = ITEMS.register("scrap_metal",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<EntityType<TunnelMutant>> TUNNEL_MUTANT = ENTITIES.register("tunnel_mutant",
        () -> EntityType.Builder.of(TunnelMutant::new, MobCategory.MONSTER)
            .sized(0.8f, 1.9f).clientTrackingRange(8)
            .build(new ResourceLocation(MODID, "tunnel_mutant").toString()));

    public MetroMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        ENTITIES.register(bus);
        bus.addListener(this::addCreative);
        bus.addListener(this::entityAttributes);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(CANNED_FOOD);
            event.accept(FILTER);
            event.accept(MEDKIT);
            event.accept(GAS_MASK);
            event.accept(SCRAP);
        }
    }

    private void entityAttributes(EntityAttributeCreationEvent event) {
        event.put(TUNNEL_MUTANT.get(), TunnelMutant.createAttributes().build());
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        Player player = event.player;
        if (player.level().dimension() == Level.OVERWORLD && player.getY() > 62 && !hasGasMask(player)) {
            int timer = player.getPersistentData().getInt("metro_radiation_ticks") + 1;
            if (timer >= 100) {
                player.hurt(player.damageSources().magic(), 1.0f);
                player.getPersistentData().putInt("metro_radiation_ticks", 0);
            } else {
                player.getPersistentData().putInt("metro_radiation_ticks", timer);
            }
        } else {
            player.getPersistentData().putInt("metro_radiation_ticks", 0);
        }
    }

    private boolean hasGasMask(Player player) {
        return player.getMainHandItem().is(GAS_MASK.get()) || player.getOffhandItem().is(GAS_MASK.get());
    }
}
