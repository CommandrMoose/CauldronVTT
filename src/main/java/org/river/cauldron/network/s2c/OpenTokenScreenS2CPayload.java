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

public record OpenTokenScreenS2CPayload(BlockPos origin, List<CauldronCharacterData> characterData, String currentId) implements CustomPayload {
    public static final Identifier OPEN_TOKEN_SCREEN_PAYLOAD_ID = Identifier.of(Cauldron.MODID, "open_token_screen");
    public static final CustomPayload.Id<OpenTokenScreenS2CPayload> ID = new CustomPayload.Id<>(OPEN_TOKEN_SCREEN_PAYLOAD_ID);

    public static final PacketCodec<RegistryByteBuf, OpenTokenScreenS2CPayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, OpenTokenScreenS2CPayload::origin,
            CauldronCharacterData.PACKET_CODEC.collect(PacketCodecs.toList()), OpenTokenScreenS2CPayload::characterData,
            PacketCodecs.STRING, OpenTokenScreenS2CPayload::currentId,
            OpenTokenScreenS2CPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
