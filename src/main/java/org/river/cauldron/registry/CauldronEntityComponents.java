package org.river.cauldron.registry;

import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.river.cauldron.Cauldron;
import org.river.cauldron.components.CauldronPlayerComponent;

public final class CauldronEntityComponents implements EntityComponentInitializer {

    public static final ComponentKey<CauldronPlayerComponent> PLAYER =
            ComponentRegistry.getOrCreate(Identifier.of(Cauldron.MODID, "player"), CauldronPlayerComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {
        entityComponentFactoryRegistry.registerForPlayers(PLAYER, CauldronPlayerComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
