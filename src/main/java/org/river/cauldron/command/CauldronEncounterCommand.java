package org.river.cauldron.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.river.cauldron.components.CauldronPlayerComponent;
import org.river.cauldron.components.CauldronWorldDataComponent;
import org.river.cauldron.registry.CauldronEntityComponents;
import org.river.cauldron.registry.CauldronWorldComponent;

import java.util.UUID;

public class CauldronEncounterCommand {


    public static int listEncounters(CommandContext<ServerCommandSource> ctx) {
        CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(ctx.getSource().getWorld());
        if (comp instanceof CauldronWorldDataComponent) {
            comp.getEncounterDataManager().listEncounters(ctx.getSource().getPlayer());
            return 1;
        }

        return 0;
    }

    public static int createEncounter(CommandContext<ServerCommandSource> ctx, String displayName) {
        CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(ctx.getSource().getWorld());
        if (comp instanceof CauldronWorldDataComponent) {
            var result = comp.getEncounterDataManager().createEncounter(displayName);
            if (result) {
                ctx.getSource().sendFeedback(() -> Text.literal("Created new encounter"), true);
                return 1;
            }
        }

        return 0;
    }

    public static int deleteEncounter(CommandContext<ServerCommandSource> ctx, String id) {
        World world = ctx.getSource().getWorld();
        CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(world);
        if (comp instanceof CauldronWorldDataComponent) {
            if (comp.getEncounterDataManager().deleteEncounter(id)) {
                ctx.getSource().sendFeedback(() -> Text.literal("Deleted encounter "+id), true);
                return 1;
            }
        }

        return 0;
    }

    public static int modifyEncounter(CommandContext<ServerCommandSource> ctx, String displayName) {

        World world = ctx.getSource().getWorld();
        CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(world);
        if (comp instanceof CauldronWorldDataComponent) {

            var encounter = comp.getEncounterDataManager().getEncounterByDisplayName(displayName);
            if (encounter.isPresent()) {

                CauldronPlayerComponent playerComponent = CauldronEntityComponents.PLAYER.getNullable(ctx.getSource().getPlayer());
                if (playerComponent instanceof CauldronPlayerComponent) {
                    playerComponent.setEncounterModifierId(encounter.get().ID.toString());
                    ctx.getSource().sendFeedback(() -> Text.literal("Now modifying: " + displayName), true);
                }

            } else {
                ctx.getSource().sendError( Text.literal("Could not find encounter with the name: " + displayName));
            }
        }

        return 0;
    }
    public static int whatEncounter(CommandContext<ServerCommandSource> ctx) {

        World world = ctx.getSource().getWorld();

        CauldronPlayerComponent playerComponent = CauldronEntityComponents.PLAYER.getNullable(ctx.getSource().getPlayer());
        if (playerComponent instanceof CauldronPlayerComponent) {
            var encounterUUID = playerComponent.getEncounterModifierId();
            if (encounterUUID != null) {

                CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(world);
                if (comp instanceof CauldronWorldDataComponent) {
                    var encounter = comp.getEncounterDataManager().getEncounterById(UUID.fromString(encounterUUID));
                    encounter.ifPresent(cauldronEncounterData -> ctx.getSource().sendFeedback(() -> Text.literal("Currently modifying: " + cauldronEncounterData.displayName), false));
                }
            } else {
                ctx.getSource().sendError(Text.literal("Not currently modifying an encounter."));
            }
        }

        return 0;
    }

    public static int startEncounter(CommandContext<ServerCommandSource> ctx, String encounterDisplay) {

        World world = ctx.getSource().getWorld();
        CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(world);
        if (comp instanceof CauldronWorldDataComponent) {
            var encounter = comp.getEncounterDataManager().getEncounterByDisplayName(encounterDisplay);
            encounter.ifPresent(encounterData -> comp.getEncounterManager().startEncounter(world, encounterData));
        }

        return 0;
    }

    public static int stopEncounter(CommandContext<ServerCommandSource> ctx) {

        World world = ctx.getSource().getWorld();
        CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(world);
        if (comp instanceof CauldronWorldDataComponent) {
            comp.getEncounterManager().stopEncounter();
            return 1;
        }

        return 0;
    }

    public static int initRound(CommandContext<ServerCommandSource> ctx) {
        World world = ctx.getSource().getWorld();
        CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(world);
        if (comp instanceof CauldronWorldDataComponent) {
            if (comp.getEncounterManager().hasEncounterInitialised()) {
                comp.getEncounterManager().initialize();
                ctx.getSource().sendError(Text.literal("Encounter has already started."));
            } else {
                comp.getEncounterManager().initialize();
                ctx.getSource().sendFeedback(() -> Text.literal("Starting encounter!!!"), false);
            }

        }

        return 0;
    }

}
