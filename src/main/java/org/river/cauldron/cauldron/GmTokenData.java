package org.river.cauldron.cauldron;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;

public class GmTokenData {
    public String tokenId;
    public BlockPos spawnPos;
    public float spawnRotation;
    public String characterId;
    public float initiative;
    public boolean spawnOnInitiativeStart;
    public boolean addToInitiativeOnSpawn;

    public GmTokenData(String id, BlockPos spawnPos, float spawnRotation, String characterId, float initiative, boolean spawnOnInitiativeStart, boolean addToInitiativeOnSpawn) {
        this.tokenId = id;
        this.spawnPos = spawnPos;
        this.spawnRotation = spawnRotation;
        this.characterId = characterId;
        this.spawnOnInitiativeStart = spawnOnInitiativeStart;
        this.addToInitiativeOnSpawn = addToInitiativeOnSpawn;
        this.initiative = initiative;
    }

    public static final Codec<GmTokenData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(GmTokenData::getTokenId),
            BlockPos.CODEC.fieldOf("spawnPos").forGetter(GmTokenData::getSpawnPos),
            Codec.FLOAT.fieldOf("spawnRotation").forGetter(GmTokenData::getSpawnRotation),
            Codec.STRING.fieldOf("characterId").forGetter(GmTokenData::getCharacterId),
            Codec.FLOAT.fieldOf("initiative").forGetter(GmTokenData::getInitiative),
            Codec.BOOL.fieldOf("spawnOnInitiativeStart").forGetter(GmTokenData::isSpawnOnInitiativeStart),
            Codec.BOOL.fieldOf("addToInitiativeOnSpawn").forGetter(GmTokenData::isAddToInitiativeOnSpawn)
    ).apply(instance, GmTokenData::new));


    public BlockPos getSpawnPos() {
        return spawnPos;
    }

    public float getSpawnRotation() {
        return spawnRotation;
    }

    public String getCharacterId() {
        return characterId;
    }

    public boolean isAddToInitiativeOnSpawn() {
        return addToInitiativeOnSpawn;
    }

    public boolean isSpawnOnInitiativeStart() {
        return spawnOnInitiativeStart;
    }

    public float getInitiative() {
        return initiative;
    }

    public String getTokenId() {
        return tokenId;
    }
}
