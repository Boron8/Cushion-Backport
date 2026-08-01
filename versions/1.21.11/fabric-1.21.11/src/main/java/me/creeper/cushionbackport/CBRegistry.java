package me.creeper.cushionbackport;

import me.creeper.cushionbackport.entity.Cushion;
import me.creeper.cushionbackport.item.CushionItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CBRegistry {
    public static Supplier<EntityType<Cushion>> CUSHION;
    public static Supplier<SoundEvent> CUSHION_BREAK;
    public static Supplier<SoundEvent> CUSHION_PLACE;
    public static Supplier<SoundEvent> CUSHION_SIT;
    public static Supplier<SoundEvent> CUSHION_GET_UP;

    public static final DyeColor[] ALL_COLORS = {
            DyeColor.WHITE,
            DyeColor.ORANGE,
            DyeColor.MAGENTA,
            DyeColor.LIGHT_BLUE,
            DyeColor.YELLOW,
            DyeColor.LIME,
            DyeColor.PINK,
            DyeColor.GRAY,
            DyeColor.LIGHT_GRAY,
            DyeColor.CYAN,
            DyeColor.PURPLE,
            DyeColor.BLUE,
            DyeColor.BROWN,
            DyeColor.GREEN,
            DyeColor.RED,
            DyeColor.BLACK
    };

    private static final Map<DyeColor, Block> WOOL_BY_COLOR = new HashMap<>();
    private static final Map<DyeColor, Supplier<CushionItem>> CUSHION_ITEM_BY_COLOR = new HashMap<>();

    public static CushionItem cushionItemByColor(DyeColor color) {
        return CUSHION_ITEM_BY_COLOR.get(color).get();
    }

    public static Block woolByColor(DyeColor color) {
        return WOOL_BY_COLOR.computeIfAbsent(color, CBRegistry::resolveWoolByColor);
    }

    private static Block resolveWoolByColor(DyeColor color) {
        String woolName = color.getName() + "_wool";
        for (Block block : BuiltInRegistries.BLOCK) {
            if (BuiltInRegistries.BLOCK.getKey(block).getPath().equals(woolName)) {
                return block;
            }
        }
        // wool does not exist? default to white
        return Blocks.WHITE_WOOL;
    }

    public static void init() {
        ResourceKey<EntityType<?>> cushionKey = ResourceKey.create(Registries.ENTITY_TYPE, id("cushion"));
        EntityType<Cushion> cushionType = EntityType.Builder.<Cushion>of(Cushion::new, MobCategory.MISC)
                .sized(1.0f, 0.25f)
                .clientTrackingRange(10)
                .updateInterval(Integer.MAX_VALUE)
                .build(cushionKey);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("cushion"), cushionType);
        CBRegistry.CUSHION = () -> cushionType;

        for (DyeColor color : CBRegistry.ALL_COLORS) {
            ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id(color.getName() + "_cushion"));
            CushionItem item = new CushionItem(new Item.Properties().setId(itemKey).stacksTo(16), color);
            Registry.register(BuiltInRegistries.ITEM, itemKey, item);
            CBRegistry.CUSHION_ITEM_BY_COLOR.put(color, () -> item);
        }

        CBRegistry.CUSHION_BREAK = registerSound("entity.cushion.break");
        CBRegistry.CUSHION_PLACE = registerSound("entity.cushion.place");
        CBRegistry.CUSHION_SIT = registerSound("entity.cushion.sit");
        CBRegistry.CUSHION_GET_UP = registerSound("entity.cushion.get_up");

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COLORED_BLOCKS).register(entries -> {
            for (DyeColor color : CBRegistry.ALL_COLORS) {
                entries.accept(CBRegistry.cushionItemByColor(color));
            }
        });
    }

    private static Supplier<SoundEvent> registerSound(String path) {
        Identifier loc = id(path);
        SoundEvent event = SoundEvent.createVariableRangeEvent(loc);
        Registry.register(BuiltInRegistries.SOUND_EVENT, loc, event);
        return () -> event;
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(CushionBackport.MOD_ID, path);
    }
}
