package org.river.cauldron.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.Component;
import org.river.cauldron.cauldron.CharacterCreator;
import org.river.cauldron.cauldron.EncounterDataManager;
import org.river.cauldron.cauldron.EncounterManager;

public class CauldronWorldDataComponent implements Component {

    private World world;
    private CharacterCreator characterCreator;
    private EncounterDataManager encounterDataManager;
    private EncounterManager encounterManager;

    public CauldronWorldDataComponent(World world) {
        super();
        world = world;
        characterCreator = new CharacterCreator();
        encounterDataManager = new EncounterDataManager();
        encounterManager = new EncounterManager();
    }

    public CharacterCreator GetCharacterCreator() {
        return this.characterCreator;
    }

    public EncounterDataManager getEncounterDataManager() {
        return this.encounterDataManager;
    }

    public EncounterManager getEncounterManager() {
        return this.encounterManager;
    }


    @Override
    public void readData(ReadView readView) {
        encounterDataManager.readData(readView);
    }

    @Override
    public void writeData(WriteView writeView) {
        encounterDataManager.writeData(writeView);
    }
}
