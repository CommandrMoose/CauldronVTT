package org.river.cauldron.cauldron;

// Handles the game state for the encounter.

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.river.cauldron.data.CauldronEncounterData;
import org.river.cauldron.entity.CharacterTokenEntity;
import org.river.cauldron.entity.GmTokenEntity;
import org.river.cauldron.registry.CauldronEntities;
import org.river.cauldron.registry.CauldronItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EncounterManager {

    private CauldronEncounterData currentEncounter;
    private boolean hasEncounterStarted = false;
    private boolean encounterIsRunning = false;
    private World world;

    private CauldronInitiative cauldronInitiative;


    public void startEncounter(World world, CauldronEncounterData encounterData) {
        if (world.isClient()) {return;}

        if (hasEncounterStarted) {
            System.out.println("ERR: Encounter has already started");
            return;
        }

        this.currentEncounter = encounterData;

        CauldronCurrentEncounter.initiative.INIT_PLACEMENTS.clear();
        CauldronCurrentEncounter.initiative.setEncounterStarted(true);
        CauldronCurrentEncounter.initiative.setEncounterId(encounterData.ID.toString());
        CauldronCurrentEncounter.updateToClients((List<ServerPlayerEntity>) world.getPlayers());

        this.world = world;
        world.getPlayers().forEach(x -> {

            x.sendMessage(Text.literal("Encounter started!"), false);
            x.sendMessage(Text.literal("Place and select your character token with the Character Mini Spawner"), false);

            x.giveItemStack(new ItemStack(CauldronItems.CHARACTER_MINI_SPAWNER_ITEM, 1));

        });
        currentEncounter = encounterData;
        hasEncounterStarted = true;
    }

    public void stopEncounter() {
        if (world.isClient()) {return;}
        if (!hasEncounterStarted) {
            System.out.println("ERR: Encounter has not started");
            return;
        }

        currentEncounter.spawnedTokenEntitiesIds.forEach(x -> {
            var entity = world.getEntity(UUID.fromString(x));
            if (entity != null) {
                entity.setPosition(entity.getX(), -10000, entity.getZ());
            }
        });
        CauldronCurrentEncounter.initiative.INIT_PLACEMENTS = new ArrayList<>();
        CauldronCurrentEncounter.initiative.setEncounterStarted(false);
        CauldronCurrentEncounter.initiative.setHasEncounterInit(false);
        CauldronCurrentEncounter.initiative.setCurrentToken(0);
        CauldronCurrentEncounter.updateToClients((List<ServerPlayerEntity>) world.getPlayers());


        // Remove all recorded token entities.
        currentEncounter.recordedGmTokenEntities.forEach(x -> {

            GmTokenEntity tokenEntity = (GmTokenEntity) this.world.getEntity(UUID.fromString(x));
            if (tokenEntity != null) {
                tokenEntity.setPosition(new Vec3d(0, -10000, 0));
            }
        });
        ;

        currentEncounter.recordedGmTokenEntities = new ArrayList<>();

        // Respawn these entities as if they were always there.
        currentEncounter.gmTokenData.forEach(x -> {

            var respawnedToken = new GmTokenEntity(CauldronEntities.GM_TOKEN, world);;

            var characterData = CauldronCharacters.SERVER_CHARACTER_DATA.stream().filter(y -> Objects.equals(x.characterId, y.id)).findFirst();


            characterData.ifPresentOrElse(cauldronCharacterData -> {
                        System.out.println("Setting character id");
                        respawnedToken.setCharacterId(cauldronCharacterData.id, cauldronCharacterData.characterSkin);
                    },
                    () -> {
                        System.out.println("Falling back");
                        respawnedToken.setCharacterId("default", "default");
                    });

            respawnedToken.setEncounterId(currentEncounter.ID.toString());
            respawnedToken.setTokenId(x.getTokenId());
            respawnedToken.setPosition(x.spawnPos.toCenterPos());
            respawnedToken.setYaw(x.spawnRotation);
            respawnedToken.setBodyYaw(x.spawnRotation);
            respawnedToken.setHeadYaw(x.spawnRotation);
            respawnedToken.setInitiative(x.getInitiative());
            respawnedToken.setAddToInitiativeOnSpawn(x.addToInitiativeOnSpawn);
            respawnedToken.setSpawnOnInit(x.spawnOnInitiativeStart);
            respawnedToken.setCharacterId(x.getCharacterId(), x.getCharacterId());

            world.spawnEntity(respawnedToken);

            currentEncounter.recordedGmTokenEntities.add(respawnedToken.getUuidAsString());
        });


        currentEncounter = null;
        world = null;
        hasEncounterStarted = false;
    }

    public void addToInitiative(CharacterTokenEntity entity, CauldronInitiativePlacement newPlacement) {

        var spawnedTokenEntityIds =  new ArrayList<>(this.currentEncounter.spawnedTokenEntitiesIds.stream().toList());
        spawnedTokenEntityIds.add(entity.getUuidAsString());
        this.currentEncounter.spawnedTokenEntitiesIds = spawnedTokenEntityIds;

        var initPlacements =  new ArrayList<>(CauldronCurrentEncounter.initiative.INIT_PLACEMENTS.stream().toList());
        initPlacements.add(newPlacement);
        CauldronCurrentEncounter.initiative.INIT_PLACEMENTS = initPlacements;

        CauldronCurrentEncounter.updateToClients((List<ServerPlayerEntity>) world.getPlayers());
    }

    public void initialize() {
        encounterIsRunning = true;

        currentEncounter.recordedGmTokenEntities.forEach(x -> {

            Entity worldEntity = this.world.getEntity(UUID.fromString(x));
            if (worldEntity instanceof GmTokenEntity gmTokenEntity) {
                if (gmTokenEntity != null) {
                    if (gmTokenEntity.isSpawnOnInit()) {
                        gmTokenEntity.spawn();
                    }
                }
            }
        });

        CauldronCurrentEncounter.initiative.sortInitiative();
        CauldronCurrentEncounter.initiative.setHasEncounterInit(true);
        CauldronCurrentEncounter.updateToClients((List<ServerPlayerEntity>) world.getPlayers());

        world.getPlayers().forEach(x -> x.sendMessage(Text.literal("Encounter has begun!"), false));
    }

    public boolean isHasEncounterStarted() {
        return hasEncounterStarted;
    }

    public boolean hasEncounterInitialised() {
        return this.encounterIsRunning;
    }
}
