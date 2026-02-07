package org.river.cauldron.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.river.cauldron.Cauldron;
import org.river.cauldron.blockentity.EncounterBlockEntity;

public class CauldronBlockEntities {


    // Dummy function so the class is loaded and we register the blocks.
    public static void initialize() {

    }

    public static final BlockEntityType<EncounterBlockEntity> ENCOUNTER_CONTROLLER_BLOCK_ENTITY =
            register("encounter_controller", EncounterBlockEntity::new, CauldronBlocks.ENCOUNTER_CONTROLLER);


    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        Identifier id = Identifier.of(Cauldron.MODID, name);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

}
