package org.river.cauldron.network.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.river.cauldron.Cauldron;

public record AnnounceDiceRollC2SPayload(String resultSet) implements CustomPayload {

    public static final Identifier ANNOUNCE_DICE_ROLL_PAYLOAD_ID = Identifier.of(Cauldron.MODID, "announce_dice_roll");
    public static final Id<AnnounceDiceRollC2SPayload> ID = new Id<>(ANNOUNCE_DICE_ROLL_PAYLOAD_ID);

     public static final PacketCodec<RegistryByteBuf, AnnounceDiceRollC2SPayload> CODEC = PacketCodec.tuple(
             PacketCodecs.STRING, AnnounceDiceRollC2SPayload::resultSet,
             AnnounceDiceRollC2SPayload::new
     );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
