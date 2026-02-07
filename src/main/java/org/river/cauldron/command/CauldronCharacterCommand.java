package org.river.cauldron.command;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.river.cauldron.cauldron.CauldronCharacters;
import org.river.cauldron.components.CauldronWorldDataComponent;
import org.river.cauldron.network.s2c.UpdateCharacterListS2CPayload;
import org.river.cauldron.registry.CauldronWorldComponent;

import java.util.Objects;


public class CauldronCharacterCommand{

    public static int listCharacters(CommandContext<ServerCommandSource> ctx) {
        CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(ctx.getSource().getWorld());
        if (comp instanceof CauldronWorldDataComponent) {
            comp.GetCharacterCreator().ListCharacters(ctx.getSource().getPlayer());
            return 1;
        }

        return 0;
    }

    public static int createCharacter(CommandContext<ServerCommandSource> ctx, String id, String characterName, String characterSkin) {

        World world = ctx.getSource().getWorld();
        CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(ctx.getSource().getWorld());
        if (comp instanceof CauldronWorldDataComponent && world instanceof ServerWorld) {
            var result = comp.GetCharacterCreator().createCharacter(id, characterName, characterSkin);
            if (result) {

                UpdateCharacterListS2CPayload payload = new UpdateCharacterListS2CPayload(CauldronCharacters.SERVER_CHARACTER_DATA);
                ctx.getSource().getWorld().getPlayers().forEach(x -> {
                    ServerPlayNetworking.send(x, payload);
                });

                ctx.getSource().sendFeedback(() -> Text.literal("Created new character ("+characterName+")"), true);
                return 1;
            }
        }

        return 0;
    }

    public static int deleteCharacter(CommandContext<ServerCommandSource> ctx, String id) {
        World world = ctx.getSource().getWorld();
        CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(world);
        if (comp instanceof CauldronWorldDataComponent && world instanceof ServerWorld) {
            if (comp.GetCharacterCreator().deleteCharacter(id)) {

                UpdateCharacterListS2CPayload payload = new UpdateCharacterListS2CPayload(CauldronCharacters.SERVER_CHARACTER_DATA);
                ctx.getSource().getWorld().getPlayers().forEach(x -> {
                    ServerPlayNetworking.send(x, payload);
                });

                ctx.getSource().sendFeedback(() -> Text.literal("Deleted "+id), true);
                return 1;
            }

        }

        return 0;
    }

    public static int spawnCharacter(CommandContext<ServerCommandSource> ctx, String id) {
        World world = ctx.getSource().getWorld();
        CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(ctx.getSource().getWorld());
        if (comp instanceof CauldronWorldDataComponent) {
            comp.GetCharacterCreator().createDummyCharacter(id, world, ctx.getSource().getPosition(), 0);
            ctx.getSource().sendFeedback(() -> Text.literal("Spawned character"), true);
            return 1;
        }

        return 0;
    }

}
