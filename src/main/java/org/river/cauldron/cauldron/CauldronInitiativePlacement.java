package org.river.cauldron.cauldron;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.river.cauldron.data.CauldronCharacterData;

import java.util.UUID;

public class CauldronInitiativePlacement {

    public String playerId;
    public String characterId;
    public int intNumber;

    public CauldronInitiativePlacement(String playerId, String characterId, int intNumber) {
        this.playerId = playerId;
        this.characterId = characterId;
        this.intNumber = intNumber;
    }

    public static final PacketCodec<PacketByteBuf, CauldronInitiativePlacement> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, CauldronInitiativePlacement::getPlayerId,
            PacketCodecs.STRING, CauldronInitiativePlacement::getCharacterId,
            PacketCodecs.INTEGER, CauldronInitiativePlacement::getIntNumber,
            CauldronInitiativePlacement::new);


    public String getCharacterId() {
        return this.characterId;
    }
    public int getIntNumber() {
        return this.intNumber;
    }
    public String getPlayerId() {
        return this.playerId;
    }

}
