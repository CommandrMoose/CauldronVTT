package org.river.cauldron.client.providers;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import org.river.cauldron.registry.CauldronBlocks;
import org.river.cauldron.registry.CauldronItems;

import java.util.concurrent.CompletableFuture;;

public class CauldronEnglishLangProvider extends FabricLanguageProvider {
    public CauldronEnglishLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        // Specifying en_us is optional, as it's the default language code
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {

        // Items
        registerItem(translationBuilder, CauldronBlocks.ENCOUNTER_CONTROLLER.asItem(), "Encounter Cauldron");
        registerItem(translationBuilder, CauldronItems.GM_TOKEN_SPAWNER_ITEM, "Game Master Token");
        registerItem(translationBuilder, CauldronItems.CHARACTER_MINI_SPAWNER_ITEM, "Player Token");


        translationBuilder.add("itemGroup.cauldron", "Cauldron");

    }

    public void registerItem(FabricLanguageProvider.TranslationBuilder translationBuilder, Item item, String name) {
        translationBuilder.add(item.asItem().getTranslationKey(), name);
    }
}