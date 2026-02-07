package org.river.cauldron.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;

public class CauldronCharacterData {

    public String id;
    public String characterSkin;
    public String characterName;

    public CauldronCharacterData(String id, String name, String skinName) {
        this.id = id;
        this.characterName = name;
        this.characterSkin = skinName;
    }

    public static final PacketCodec<PacketByteBuf, CauldronCharacterData> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, CauldronCharacterData::getId,
            PacketCodecs.STRING, CauldronCharacterData::getCharacterSkin,
            PacketCodecs.STRING, CauldronCharacterData::getCharacterName,
            CauldronCharacterData::new);



    public String getId() {
        return id;
    }

    public String getCharacterSkin() {
        return characterSkin;
    }

    public String getCharacterName() {
        return characterName;
    }
}
