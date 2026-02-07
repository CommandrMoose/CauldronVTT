package org.river.cauldron.cauldron;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.ArrayList;
import java.util.List;

public class CauldronInitiative {
    public List<CauldronInitiativePlacement> INIT_PLACEMENTS = new ArrayList<>();
    private int currentRound;
    private int currentToken;
    private boolean hasEncounterStarted = false;
    private boolean hasEncounterInit = false;

    private String encounterId;

    public CauldronInitiative(List<CauldronInitiativePlacement> placements, int currentRound, int currentToken,
                              boolean hasEncounterStarted, boolean hasEncounterInit, String encounterId) {
        this.currentRound = currentRound;
        this.INIT_PLACEMENTS = placements;
        this.currentToken = currentToken;
        this.hasEncounterStarted = hasEncounterStarted;
        this.hasEncounterInit = hasEncounterInit;
        this.encounterId = encounterId;
    }
    public CauldronInitiative() {
        this.currentRound = 0;
        this.currentToken = 0;
        this.encounterId = "NOT_SET";
    }

    public void sortInitiative() {
        this.INIT_PLACEMENTS.sort((a, b) -> b.intNumber - a.intNumber);
    }


    public static final PacketCodec<PacketByteBuf, CauldronInitiative> PACKET_CODEC = PacketCodec.tuple(
            CauldronInitiativePlacement.PACKET_CODEC.collect(PacketCodecs.toList()), CauldronInitiative::getInitiativePlacements,
            PacketCodecs.INTEGER, CauldronInitiative::getCurrentRound,
            PacketCodecs.INTEGER, CauldronInitiative::getCurrentToken,
            PacketCodecs.BOOLEAN, CauldronInitiative::getHasEncounterStarted,
            PacketCodecs.BOOLEAN, CauldronInitiative::getHasEncounterInit,
            PacketCodecs.STRING, CauldronInitiative::getEncounterId,
            CauldronInitiative::new);


    public List<CauldronInitiativePlacement> getInitiativePlacements() {
        return this.INIT_PLACEMENTS;
    }
    public int getCurrentRound() {
        return this.currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public int getCurrentToken() {
        return this.currentToken;
    }

    public void setCurrentToken(int currentToken) {
        this.currentToken = currentToken;
    }

    public boolean getHasEncounterStarted() {
        return this.hasEncounterStarted;
    }

    public boolean getHasEncounterInit() {
        return this.hasEncounterInit;
    }

    public void setEncounterStarted(boolean value) {
        this.hasEncounterStarted = value;
    }
    public void setHasEncounterInit(boolean value) {
        this.hasEncounterInit = value;
    }

    public String getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }
}

