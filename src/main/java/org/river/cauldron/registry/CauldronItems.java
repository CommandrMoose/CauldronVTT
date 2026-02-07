package org.river.cauldron.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.CauldronBlock;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.river.cauldron.Cauldron;
import org.river.cauldron.item.CharacterMiniSpawnerItem;
import org.river.cauldron.item.GmTokenSpawnerItem;

import java.util.function.Function;

public class CauldronItems {

    public static final RegistryKey<ItemGroup> CAULDRON_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(Cauldron.MODID, "cauldron_item_group"));
    public static final ItemGroup CAULDRON_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(CauldronBlocks.ENCOUNTER_CONTROLLER))
            .displayName(Text.translatable("itemGroup.cauldron"))
            .build();

    public static final Item CHARACTER_MINI_SPAWNER_ITEM =
            register("character_spawner_item", CharacterMiniSpawnerItem::new, new Item.Settings().useCooldown(5) );

    public static final Item GM_TOKEN_SPAWNER_ITEM =
            register("gm_token_spawner_item", GmTokenSpawnerItem::new, new Item.Settings().useCooldown(5) );

    public static void initialize() {
        Registry.register(Registries.ITEM_GROUP, CAULDRON_ITEM_GROUP_KEY, CAULDRON_ITEM_GROUP);

        ItemGroupEvents.modifyEntriesEvent(CAULDRON_ITEM_GROUP_KEY)
                .register((itemGroup) -> {
                    itemGroup.add(CauldronItems.CHARACTER_MINI_SPAWNER_ITEM);
                    itemGroup.add(CauldronItems.GM_TOKEN_SPAWNER_ITEM);
                });
    }

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        // Create the item key.
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Cauldron.MODID, name));

        // Create the item instance.
        Item item = itemFactory.apply(settings.registryKey(itemKey));

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

}
