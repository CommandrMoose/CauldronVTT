package org.river.cauldron.registry;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.river.cauldron.cauldron.CauldronCurrentEncounter;
import org.river.cauldron.cauldron.CauldronInitiativePlacement;
import org.river.cauldron.components.CauldronWorldDataComponent;
import org.river.cauldron.entity.GmTokenEntity;
import org.river.cauldron.network.c2s.*;
import org.river.cauldron.network.s2c.OpenGmTokenScreenS2CPayload;
import org.river.cauldron.network.s2c.OpenTokenScreenS2CPayload;
import org.river.cauldron.network.s2c.UpdateCharacterListS2CPayload;
import org.river.cauldron.network.s2c.UpdateCurrentEncounterS2CPayload;

import java.util.UUID;

public class CauldronNetworkingRegistry {

    public static void initialize() {

        PayloadTypeRegistry.playC2S().register(SetTokenCharacterC2SPayload.ID, SetTokenCharacterC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AnnounceDiceRollC2SPayload.ID, AnnounceDiceRollC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(InitEncounterC2SPayload.ID, InitEncounterC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(StopEncounterC2SPayload.ID, StopEncounterC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ChangeInitIndexC2SPayload.ID, ChangeInitIndexC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SetGmTokenCharacterC2SPayload.ID, SetGmTokenCharacterC2SPayload.CODEC);

        PayloadTypeRegistry.playS2C().register(OpenTokenScreenS2CPayload.ID, OpenTokenScreenS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(UpdateCharacterListS2CPayload.ID, UpdateCharacterListS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(UpdateCurrentEncounterS2CPayload.ID, UpdateCurrentEncounterS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenGmTokenScreenS2CPayload.ID, OpenGmTokenScreenS2CPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SetTokenCharacterC2SPayload.ID, (payload, context) -> {

            var init = payload.initiative();
            var owner = payload.playerId();

            CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(context.player().getEntityWorld());
            if (comp instanceof CauldronWorldDataComponent && context.player() instanceof ServerPlayerEntity playerEntity) {

                float f = (float) MathHelper.floor((MathHelper.wrapDegrees(playerEntity.getYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;

                var character = comp.GetCharacterCreator().createDummyCharacter(payload.characterId(), context.player().getEntityWorld(), payload.blockPos().toCenterPos(), f);
                if (character.isPresent()) {

                    context.player().sendMessage(Text.literal("Spawned character with id " + payload.characterId()));
                    comp.getEncounterManager().addToInitiative(character.get(), new CauldronInitiativePlacement(owner, payload.characterId(), init));
                }

            }
        });
        ServerPlayNetworking.registerGlobalReceiver(SetGmTokenCharacterC2SPayload.ID, (payload, context) -> {

            var entityId = payload.entityId();
            var init = payload.initiative();
            var owner = payload.playerId();
            var characterId = payload.characterId();

            GmTokenEntity tokenEntity = (GmTokenEntity) context.player().getEntityWorld().getEntity(UUID.fromString(entityId));
            if (tokenEntity != null) {
                CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(context.player().getEntityWorld());
                if (comp instanceof CauldronWorldDataComponent) {
                    var character = comp.GetCharacterCreator().GetCharacter(characterId);
                    if (character.isPresent()) {
                        context.player().sendMessage(Text.literal("Set character to " + character.get().characterName));

                        var spawnOnStart = payload.spawnOnStart();
                        var addToInit = payload.addToInitative();

                        tokenEntity.setCharacterId(characterId, character.get().characterSkin);
                        tokenEntity.setSpawnOnInit(spawnOnStart);
                        tokenEntity.setAddToInitiativeOnSpawn(addToInit);
                        tokenEntity.setInitiative(init);

                        comp.getEncounterDataManager().addGmTokenToEncounter(tokenEntity.getEncounterId(), tokenEntity);

                    }

                }
            }
        });
        
        ServerPlayNetworking.registerGlobalReceiver(AnnounceDiceRollC2SPayload.ID, (payload, context) -> {
            var result = payload.resultSet();
            context.player().getEntityWorld().getPlayers().forEach(x -> x.sendMessage(Text.literal(context.player().getName().getLiteralString() + " rolled " + result), false));
        });

        ServerPlayNetworking.registerGlobalReceiver(InitEncounterC2SPayload.ID, (payload, context) -> {
            var encounterId = payload.encounterId();
            CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(context.player().getEntityWorld());
            if (comp instanceof CauldronWorldDataComponent) {
                var encounter = comp.getEncounterDataManager().getEncounterById(UUID.fromString(encounterId));
                encounter.ifPresent(encounterData -> comp.getEncounterManager().initialize());
            }

        });

        ServerPlayNetworking.registerGlobalReceiver(StopEncounterC2SPayload.ID, (payload, context) -> {
            CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(context.player().getEntityWorld());
            if (comp instanceof CauldronWorldDataComponent) {
                comp.getEncounterManager().stopEncounter();
            }

        });

        ServerPlayNetworking.registerGlobalReceiver(ChangeInitIndexC2SPayload.ID, (payload, context) -> {
            var newInit = payload.newInitIndex();
            CauldronCurrentEncounter.initiative.setCurrentToken(newInit);
            CauldronCurrentEncounter.updateToClients(context.player().getEntityWorld().getPlayers());
        });

    }

}
