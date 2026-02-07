package org.river.cauldron.client.providers;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.util.Identifier;
import org.river.cauldron.Cauldron;
import org.river.cauldron.registry.CauldronBlocks;
import org.river.cauldron.registry.CauldronItems;

public class CauldronModelProvider extends FabricModelProvider {
    public CauldronModelProvider(FabricDataOutput output) {
        super(output);
    }


    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(CauldronItems.CHARACTER_MINI_SPAWNER_ITEM, Models.GENERATED);
        itemModelGenerator.register(CauldronItems.GM_TOKEN_SPAWNER_ITEM, Models.GENERATED);
    }

    @Override
    public String getName() {
        return "Cauldron Model Provider";
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerGeneric(CauldronBlocks.ENCOUNTER_CONTROLLER);
    }
}
