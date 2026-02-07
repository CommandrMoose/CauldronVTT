package org.river.cauldron.network.s2c;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.river.cauldron.Cauldron;
import org.river.cauldron.data.CauldronCharacterData;

import java.util.List;

public record UpdateCharacterListS2CPayload(List<CauldronCharacterData> characterData) implements CustomPayload {
    public static final Identifier OPEN_TOKEN_SCREEN_PAYLOAD_ID = Identifier.of(Cauldron.MODID, "update_characters");
    public static final Id<UpdateCharacterListS2CPayload> ID = new Id<>(OPEN_TOKEN_SCREEN_PAYLOAD_ID);

    public static final PacketCodec<RegistryByteBuf, UpdateCharacterListS2CPayload> CODEC = PacketCodec.tuple(
            CauldronCharacterData.PACKET_CODEC.collect(PacketCodecs.toList()), UpdateCharacterListS2CPayload::characterData,
            UpdateCharacterListS2CPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
