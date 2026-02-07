package org.river.cauldron.network.s2c;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.river.cauldron.Cauldron;
import org.river.cauldron.data.CauldronCharacterData;

import java.util.List;

public record OpenGmTokenScreenS2CPayload(String entityId, List<CauldronCharacterData> characterData, String currentId, boolean addToInitiative, boolean spawnOnStart) implements CustomPayload {
    public static final Identifier OPEN_TOKEN_SCREEN_PAYLOAD_ID = Identifier.of(Cauldron.MODID, "open_gm_token_screen");
    public static final Id<OpenGmTokenScreenS2CPayload> ID = new Id<>(OPEN_TOKEN_SCREEN_PAYLOAD_ID);

    public static final PacketCodec<RegistryByteBuf, OpenGmTokenScreenS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, OpenGmTokenScreenS2CPayload::entityId,
            CauldronCharacterData.PACKET_CODEC.collect(PacketCodecs.toList()), OpenGmTokenScreenS2CPayload::characterData,
            PacketCodecs.STRING, OpenGmTokenScreenS2CPayload::currentId,
            PacketCodecs.BOOLEAN, OpenGmTokenScreenS2CPayload::addToInitiative,
            PacketCodecs.BOOLEAN, OpenGmTokenScreenS2CPayload::spawnOnStart,
            OpenGmTokenScreenS2CPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
