package org.river.cauldron.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.river.cauldron.item.CharacterModifierItem;
import org.river.cauldron.registry.CauldronBlockEntities;


public class EncounterBlockEntity extends BlockEntity {
    public EncounterBlockEntity(BlockPos pos, BlockState state) {
        super(CauldronBlockEntities.ENCOUNTER_CONTROLLER_BLOCK_ENTITY, pos, state);
    }

    public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {

        if (player.getStackInHand(player.getActiveHand()).getItem() instanceof CharacterModifierItem) {
            player.sendMessage(Text.literal("Block clicked!"), true);

            var newNpc = new ArmorStandEntity(EntityType.ARMOR_STAND, world);
            var currentPos = getPos().up().toCenterPos();
            newNpc.setPos(currentPos.x, currentPos.y, currentPos.z);
            world.spawnEntity(newNpc);
        }


    }


}