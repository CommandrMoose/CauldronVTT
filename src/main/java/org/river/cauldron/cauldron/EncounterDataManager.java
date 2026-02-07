package org.river.cauldron.cauldron;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.river.cauldron.data.CauldronEncounterData;
import org.river.cauldron.entity.GmTokenEntity;
import org.river.cauldron.network.c2s.SetGmTokenCharacterC2SPayload;

import java.util.*;


public class EncounterDataManager {


    public static final Codec<EncounterDataManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CauldronEncounterData.CODEC.listOf().fieldOf("encounters").forGetter(EncounterDataManager::getEncounterDataList)
    ).apply(instance, EncounterDataManager::new));



    private List<CauldronEncounterData> encounterDataList = new ArrayList<>();

    public EncounterDataManager() {

    }

    public EncounterDataManager(List<CauldronEncounterData> encounterData) {
        this.encounterDataList = encounterData;
    }

    public List<CauldronEncounterData> getEncounterDataList() {
        return this.encounterDataList;
    }


    public void listEncounters(PlayerEntity player) {
        encounterDataList.forEach(x -> {
            player.sendMessage(Text.literal(x.ID + " - " + x.displayName), false);
        });
    }

    public boolean deleteEncounter(String id) {
        var characterExists = doesEncounterExist(id);

        if (!characterExists) {
            return false;
        }

        encounterDataList = encounterDataList.stream().filter((x) -> {return !Objects.equals(x.ID.toString(), id);}).toList();
        return true;
    }

    public boolean createEncounter(String name) {
        // Look to see if character could be created
        var characterExists = doesEncounterExistWithName(name);

        if (characterExists) {
            return false;
        }

        encounterDataList.add(new CauldronEncounterData(name));
        System.out.println("Created new character");
        return true;
    }


    public boolean doesEncounterExist(String id) {
        return encounterDataList.stream().anyMatch((x) -> {
            return Objects.equals(x.ID.toString(), id);
        });
    }



    public Optional<CauldronEncounterData> getEncounterByDisplayName(String displayName) {

        var matchingEncounter = encounterDataList.stream().filter(x -> Objects.equals(x.displayName, displayName)).toList();
        if ((long) matchingEncounter.size() > 0) {
            return Optional.of(matchingEncounter.getFirst());
        }

        return Optional.empty();
    }

    public Optional<CauldronEncounterData> getEncounterById(UUID id) {

        var matchingEncounter = encounterDataList.stream().filter(x -> Objects.equals(x.ID, id)).toList();
        if ((long) matchingEncounter.size() > 0) {
            return Optional.of(matchingEncounter.getFirst());
        }

        return Optional.empty();
    }

    private boolean doesEncounterExistWithName(String name) {
        return encounterDataList.stream().anyMatch((x) -> {
            return Objects.equals(x.displayName, name);
        });
    }

    public void addGmTokenToEncounter(String encounterId, GmTokenEntity entity) {
        if (doesEncounterExist(encounterId)) {
            var encounter = getEncounterById(UUID.fromString(encounterId));
            encounter.ifPresent((encounterData -> {

                var tokenData = new GmTokenData(
                        entity.getTokenId(),
                        entity.getBlockPos(),
                        entity.getBodyYaw(),
                        entity.getCharacterId(),
                        entity.getInitiative(),
                        entity.isSpawnOnInit(),
                        entity.isAddToInitiativeOnSpawn()
                );

                if (encounterData.recordedGmTokenEntities == null) {
                    encounterData.recordedGmTokenEntities = new ArrayList<>();
                }


                encounterData.recordedGmTokenEntities.add(entity.getUuidAsString());

                var modifiedData = new ArrayList<>(encounterData.gmTokenData.stream().filter(x -> !x.tokenId.equals(entity.getTokenId())).toList());
                modifiedData.add(tokenData);
                encounterData.gmTokenData = modifiedData;
            }));
        }
    }

    public void removeGmTokenFromEncounter(String encounterId, String tokenId) {
        if (doesEncounterExist(encounterId)) {
            var encounter = getEncounterById(UUID.fromString(encounterId));
            encounter.ifPresent((encounterData -> {
                encounterData.gmTokenData = new ArrayList<>(encounterData.gmTokenData.stream().filter(x -> !x.tokenId.equals(tokenId)).toList());
            }));
        }
    }


    public void readData(ReadView readView) {

        Optional<EncounterDataManager> data = readView.read("encounters", CODEC);
        data.ifPresent((x) -> {
            this.encounterDataList = x.encounterDataList;
        });
    }


    public void writeData(WriteView writeView) {
        writeView.put("encounters", CODEC, this);
    }



}
