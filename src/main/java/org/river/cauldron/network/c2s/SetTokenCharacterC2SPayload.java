package org.river.cauldron.network.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.river.cauldron.Cauldron;

public record SetTokenCharacterC2SPayload(BlockPos blockPos, String characterId, String playerId, int initiative) implements CustomPayload {

    public static final Identifier SET_CHARACTER_TOKEN_PAYLOAD_ID = Identifier.of(Cauldron.MODID, "set_character_token");
    public static final CustomPayload.Id<SetTokenCharacterC2SPayload> ID = new CustomPayload.Id<>(SET_CHARACTER_TOKEN_PAYLOAD_ID);

     public static final PacketCodec<RegistryByteBuf, SetTokenCharacterC2SPayload> CODEC = PacketCodec.tuple(
             BlockPos.PACKET_CODEC, SetTokenCharacterC2SPayload::blockPos,
             PacketCodecs.STRING, SetTokenCharacterC2SPayload::characterId,
             PacketCodecs.STRING, SetTokenCharacterC2SPayload::playerId,
             PacketCodecs.INTEGER, SetTokenCharacterC2SPayload::initiative,
             SetTokenCharacterC2SPayload::new
     );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
