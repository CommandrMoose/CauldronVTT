package org.river.cauldron.network.s2c;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.river.cauldron.Cauldron;
import org.river.cauldron.cauldron.CauldronCurrentEncounter;
import org.river.cauldron.data.CauldronCharacterData;

import java.util.List;

public record UpdateCurrentEncounterS2CPayload(CauldronCurrentEncounter currentEncounter) implements CustomPayload {
    public static final Identifier UPDATE_CURRENT_ENCOUNTER_PAYLOAD_ID = Identifier.of(Cauldron.MODID, "update_current_encounter");
    public static final Id<UpdateCurrentEncounterS2CPayload> ID = new Id<>(UPDATE_CURRENT_ENCOUNTER_PAYLOAD_ID);

    public static final PacketCodec<RegistryByteBuf, UpdateCurrentEncounterS2CPayload> CODEC = PacketCodec.tuple(
            CauldronCurrentEncounter.PACKET_CODEC, UpdateCurrentEncounterS2CPayload::currentEncounter,
            UpdateCurrentEncounterS2CPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
