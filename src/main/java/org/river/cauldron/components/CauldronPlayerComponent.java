package org.river.cauldron.components;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.Component;
import org.river.cauldron.entity.CharacterTokenEntity;

import java.util.UUID;

public class CauldronPlayerComponent implements Component {

    private PlayerEntity playerEntity;
    private CharacterTokenEntity characterTokenEntity;
    private String encounterModifierId;

    public CauldronPlayerComponent(PlayerEntity player) {
        super();
        playerEntity = player;
        encounterModifierId = null;
    }


    public void onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {

        if (world.isClient()) {return;}

        if (characterTokenEntity != null) {

            if (player.isSneaking()) {
                characterTokenEntity.setSelected(false);
                characterTokenEntity = null;
                playerEntity.sendMessage(Text.of("Cleared selection"), true);
                return;
            }

            moveActiveToPosition(hitResult.getBlockPos().up());
            characterTokenEntity.setSelected(false);
            characterTokenEntity = null;
            player.sendMessage(Text.of("Moving entity to position"), false);

        }
    }

    public void setActiveCharacter(CharacterTokenEntity cte) {

        if (cte.getEntityWorld().isClient()) {return;}

        if (characterTokenEntity != null) {
            if (cte.getUuid() == characterTokenEntity.getUuid()) {
                characterTokenEntity.setSelected(false);
                characterTokenEntity = null;
                playerEntity.sendMessage(Text.of("Cleared selection"), true);
                return;
            }
        }

        playerEntity.sendMessage(Text.of("Selected: "+ cte.getName().getLiteralString()), true);
        characterTokenEntity = cte;
    }

    public void moveActiveToPosition(BlockPos blockPos) {
        if (characterTokenEntity != null) {
            characterTokenEntity.pathFindToPosition(blockPos);
        }
    }

    public void setEncounterModifierId(String id) {
        this.encounterModifierId = id;
    }

    public String getEncounterModifierId() {
        return this.encounterModifierId;
    }



    @Override
    public void readData(ReadView readView) {

    }

    @Override
    public void writeData(WriteView writeView) {

    }
}
