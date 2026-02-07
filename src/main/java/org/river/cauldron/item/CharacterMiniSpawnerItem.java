package org.river.cauldron.item;


import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import org.river.cauldron.cauldron.CauldronCharacters;
import org.river.cauldron.network.s2c.OpenTokenScreenS2CPayload;

public class CharacterMiniSpawnerItem extends Item {

    public CharacterMiniSpawnerItem(Settings settings) {
        super(settings);
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        if (context.getWorld() instanceof ServerWorld world) {
            OpenTokenScreenS2CPayload payload = new OpenTokenScreenS2CPayload(context.getBlockPos().up(), CauldronCharacters.SERVER_CHARACTER_DATA,  "default");
            ServerPlayNetworking.send((ServerPlayerEntity) context.getPlayer(), payload);
        }

        return super.useOnBlock(context);
    }
}
