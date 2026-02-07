package org.river.cauldron.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.river.cauldron.cauldron.EncounterDataManager;
import org.river.cauldron.cauldron.GmTokenData;
import org.river.cauldron.entity.CharacterTokenEntity;
import org.river.cauldron.entity.GmTokenEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CauldronEncounterData {

    public UUID ID;
    public String displayName;
    public List<String> spawnedTokenEntitiesIds = new ArrayList<>();
    public List<String> recordedGmTokenEntities = new ArrayList<>();
    public List<GmTokenData> gmTokenData = new ArrayList<>();



    public CauldronEncounterData(String displayName) {
        this.ID = UUID.randomUUID();
        this.displayName = displayName;
    }

    public CauldronEncounterData(String id, String displayName, List<String> spawnedTokenEntityUUIDs, List<GmTokenData> gmTokenData, List<String> recordedGmTokenEntities) {
        this.ID = UUID.fromString(id);
        this.displayName = displayName;
        this.spawnedTokenEntitiesIds = spawnedTokenEntityUUIDs;
        this.recordedGmTokenEntities = recordedGmTokenEntities;
        this.gmTokenData = gmTokenData;
    }

    private String idAsString() {
        return this.ID.toString();
    }

    public static final Codec<CauldronEncounterData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(CauldronEncounterData::idAsString),
            Codec.STRING.fieldOf("displayName").forGetter(CauldronEncounterData::getDisplayName),
            Codec.STRING.listOf().fieldOf("spawnedTokenEntitiesIds").forGetter(CauldronEncounterData::getSpawnedTokenEntities),
            GmTokenData.CODEC.listOf().fieldOf("gmTokenData").forGetter(CauldronEncounterData::getGmTokenData),
            Codec.STRING.listOf().fieldOf("recordedGmTokenEntities").forGetter(CauldronEncounterData::getRecordedGmTokenEntities)

    ).apply(instance, CauldronEncounterData::new));


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getSpawnedTokenEntities() {
        return spawnedTokenEntitiesIds;
    }

    public List<GmTokenData> getGmTokenData() {
        return gmTokenData;
    }

    private List<String> getRecordedGmTokenEntities() {
        return recordedGmTokenEntities;
    }
}
