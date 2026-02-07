package org.river.cauldron.network.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.river.cauldron.Cauldron;

public record ChangeInitIndexC2SPayload(int newInitIndex) implements CustomPayload {

    public static final Identifier CHANGE_INIT_PAYLOAD_ID = Identifier.of(Cauldron.MODID, "change_init_index");
    public static final Id<ChangeInitIndexC2SPayload> ID = new Id<>(CHANGE_INIT_PAYLOAD_ID);

    public static final PacketCodec<RegistryByteBuf, ChangeInitIndexC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ChangeInitIndexC2SPayload::newInitIndex,
            ChangeInitIndexC2SPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
