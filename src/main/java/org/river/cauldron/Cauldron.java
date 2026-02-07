package org.river.cauldron;

import net.fabricmc.api.ModInitializer;
import org.river.cauldron.events.CauldronEvents;
import org.river.cauldron.registry.*;

public class Cauldron implements ModInitializer {

    public static final String MODID = "cauldron";

    @Override
    public void onInitialize() {
        CauldronItems.initialize();
        CauldronBlocks.initialize();
        CauldronBlockEntities.initialize();
        CauldronEntities.initialize();
        CauldronEvents.initialize();
        CauldronCommands.initialize();
        CauldronNetworkingRegistry.initialize();
    }
}
