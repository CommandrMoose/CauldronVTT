package org.river.cauldron.registry;

import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;
import org.river.cauldron.Cauldron;
import org.river.cauldron.components.CauldronWorldDataComponent;

public class CauldronWorldComponent implements WorldComponentInitializer {

    public static final ComponentKey<CauldronWorldDataComponent> WORLD =
            ComponentRegistry.getOrCreate(Identifier.of(Cauldron.MODID, "world"), CauldronWorldDataComponent.class);

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry worldComponentFactoryRegistry) {
        worldComponentFactoryRegistry.register(WORLD, CauldronWorldDataComponent::new);
    }
}
