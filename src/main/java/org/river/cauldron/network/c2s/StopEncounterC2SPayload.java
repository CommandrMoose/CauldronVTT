package org.river.cauldron.network.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.river.cauldron.Cauldron;

public record StopEncounterC2SPayload(String encounterId) implements CustomPayload {

    public static final Identifier STOP_ENCOUNTER_PAYLOAD_ID = Identifier.of(Cauldron.MODID, "stop_encounter");
    public static final Id<StopEncounterC2SPayload> ID = new Id<>(STOP_ENCOUNTER_PAYLOAD_ID);

    public static final PacketCodec<RegistryByteBuf, StopEncounterC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, StopEncounterC2SPayload::encounterId,
            StopEncounterC2SPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
