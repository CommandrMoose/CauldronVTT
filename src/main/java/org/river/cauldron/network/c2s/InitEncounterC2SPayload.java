package org.river.cauldron.network.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.river.cauldron.Cauldron;

public record InitEncounterC2SPayload(String encounterId) implements CustomPayload {

    public static final Identifier INIT_ENCOUNTER_PAYLOAD_ID = Identifier.of(Cauldron.MODID, "init_encounter");
    public static final CustomPayload.Id<InitEncounterC2SPayload> ID = new CustomPayload.Id<>(INIT_ENCOUNTER_PAYLOAD_ID);

    public static final PacketCodec<RegistryByteBuf, InitEncounterC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, InitEncounterC2SPayload::encounterId,
            InitEncounterC2SPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
