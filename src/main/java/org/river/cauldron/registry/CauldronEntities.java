package org.river.cauldron.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.river.cauldron.Cauldron;
import org.river.cauldron.entity.CharacterTokenEntity;
import org.river.cauldron.entity.GmTokenEntity;

public class CauldronEntities {

    public static final RegistryKey<EntityType<?>> CHARACTER_TOKEN_KEY = RegistryKey.of(Registries.ENTITY_TYPE.getKey(), Identifier.of(Cauldron.MODID, "character_token"));
    public static final RegistryKey<EntityType<?>> GM_TOKEN_KEY = RegistryKey.of(Registries.ENTITY_TYPE.getKey(), Identifier.of(Cauldron.MODID, "gm_token"));

    public static final EntityType<CharacterTokenEntity> CHARACTER_TOKEN = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Cauldron.MODID, "character_token"),
            EntityType.Builder.create(CharacterTokenEntity::new, SpawnGroup.MISC).dimensions(0.5f, 1.75f).build(CHARACTER_TOKEN_KEY)
    );
    public static final EntityType<GmTokenEntity> GM_TOKEN = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Cauldron.MODID, "gm_token"),
            EntityType.Builder.create(GmTokenEntity::new, SpawnGroup.MISC).dimensions(0.5f, 1.75f).build(CHARACTER_TOKEN_KEY)
    );


    public static void initialize() {
        FabricDefaultAttributeRegistry.register(CHARACTER_TOKEN, CharacterTokenEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GM_TOKEN, GmTokenEntity.createAttributes());

    }

}
