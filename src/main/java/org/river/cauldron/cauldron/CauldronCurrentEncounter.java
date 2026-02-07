package org.river.cauldron.cauldron;

// Dataholder for the encounter that the client can reference.

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.network.ServerPlayerEntity;
import org.river.cauldron.network.s2c.UpdateCurrentEncounterS2CPayload;

import java.util.List;

public class CauldronCurrentEncounter {

    public static CauldronInitiative initiative = new CauldronInitiative();

    public CauldronCurrentEncounter(CauldronInitiative initiativeSet) {
        initiative = initiativeSet;
    }

    public static final PacketCodec<PacketByteBuf, CauldronCurrentEncounter> PACKET_CODEC = PacketCodec.tuple(
            CauldronInitiative.PACKET_CODEC, CauldronCurrentEncounter::getCurrentInitiative,
            CauldronCurrentEncounter::new);


    public CauldronInitiative getCurrentInitiative() {
        return initiative;
    }


    public static void updateToClients(List<ServerPlayerEntity> players) {

        if (initiative == null) {
            initiative = new CauldronInitiative();
        }

        var payload = new UpdateCurrentEncounterS2CPayload(new CauldronCurrentEncounter(initiative));
        players.forEach(x -> { ServerPlayNetworking.send(x, payload);});

    }

}
