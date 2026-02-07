package org.river.cauldron.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.river.cauldron.client.providers.CauldronEnglishLangProvider;
import org.river.cauldron.client.providers.CauldronModelProvider;

public class CauldronDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(CauldronModelProvider::new);
        pack.addProvider(CauldronEnglishLangProvider::new);
    }
}
