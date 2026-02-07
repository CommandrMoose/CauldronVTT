package org.river.cauldron.network.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.river.cauldron.Cauldron;
import org.river.cauldron.network.s2c.OpenGmTokenScreenS2CPayload;

public record SetGmTokenCharacterC2SPayload(String entityId, String characterId, String playerId, int initiative, boolean addToInitative, boolean spawnOnStart ) implements CustomPayload {

    public static final Identifier GM_TOKEN_CHARACTER_PAYLOAD_ID = Identifier.of(Cauldron.MODID, "set_gm_token");
    public static final Id<SetGmTokenCharacterC2SPayload> ID = new Id<>(GM_TOKEN_CHARACTER_PAYLOAD_ID);

     public static final PacketCodec<RegistryByteBuf, SetGmTokenCharacterC2SPayload> CODEC = PacketCodec.tuple(
             PacketCodecs.STRING, SetGmTokenCharacterC2SPayload::entityId,
             PacketCodecs.STRING, SetGmTokenCharacterC2SPayload::characterId,
             PacketCodecs.STRING, SetGmTokenCharacterC2SPayload::playerId,
             PacketCodecs.INTEGER, SetGmTokenCharacterC2SPayload::initiative,
             PacketCodecs.BOOLEAN, SetGmTokenCharacterC2SPayload::addToInitative,
             PacketCodecs.BOOLEAN, SetGmTokenCharacterC2SPayload::spawnOnStart,
             SetGmTokenCharacterC2SPayload::new
     );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
