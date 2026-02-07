package org.river.cauldron.item;


import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ArmorStandItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.river.cauldron.cauldron.CauldronCharacters;
import org.river.cauldron.components.CauldronPlayerComponent;
import org.river.cauldron.entity.GmTokenEntity;
import org.river.cauldron.network.s2c.OpenTokenScreenS2CPayload;
import org.river.cauldron.registry.CauldronEntities;
import org.river.cauldron.registry.CauldronEntityComponents;

import java.util.UUID;

public class GmTokenSpawnerItem extends Item {

    public GmTokenSpawnerItem(Settings settings) {
        super(settings.useCooldown(3));
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        var player = context.getPlayer();
        if (context.getWorld() instanceof ServerWorld world && player instanceof ServerPlayerEntity) {

            CauldronPlayerComponent playerComponent = CauldronEntityComponents.PLAYER.get(player);
            if (playerComponent.getEncounterModifierId() == null) {
                player.sendMessage(Text.literal("You must be modifying an encounter to create tokens."), true);
                return ActionResult.FAIL;
            }


            float f = (float) MathHelper.floor((MathHelper.wrapDegrees(context.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;

            GmTokenEntity tokenEntity = new GmTokenEntity(CauldronEntities.GM_TOKEN, world);
            tokenEntity.setEncounterId(playerComponent.getEncounterModifierId());
            tokenEntity.setPosition(context.getBlockPos().up().toCenterPos());
            tokenEntity.setTokenId(UUID.randomUUID().toString());
            tokenEntity.setBodyYaw(f);
            tokenEntity.setHeadYaw(f);

            world.spawnEntity(tokenEntity);
        }

        return super.useOnBlock(context);
    }
}
