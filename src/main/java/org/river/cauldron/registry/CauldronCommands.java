package org.river.cauldron.registry;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.DefaultPermissions;
import net.minecraft.command.permission.Permission;
import net.minecraft.command.permission.Permissions;
import net.minecraft.server.command.GameModeCommand;
import org.river.cauldron.command.CauldronCharacterCommand;
import org.river.cauldron.command.CauldronEncounterCommand;
import org.river.cauldron.command.CommandConsts;

import static net.minecraft.server.command.CommandManager.*;

public class CauldronCommands implements ModInitializer {
    @Override
    public void onInitialize() {

    }

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal(CommandConsts.COMMAND_ROOT)
                    .then(literal(CommandConsts.CHARACTER)
                            .then(literal(CommandConsts.LIST)
                                    .requires(source -> source.getPermissions().hasPermission(DefaultPermissions.MODERATORS))
                                    .executes(CauldronCharacterCommand::listCharacters)
                            )
                            .then(literal(CommandConsts.CREATE)
                                            .then(argument("character_id", StringArgumentType.string())
                                                    .then(argument("character_name", StringArgumentType.string())
                                                            .then(argument("character_skin", StringArgumentType.string())
                                                                    .requires(source -> source.getPermissions().hasPermission(DefaultPermissions.MODERATORS))
                                                                    .executes(commandContext -> {
                                                                        final String characterId = StringArgumentType.getString(commandContext, "character_id");
                                                                        final String characterName = StringArgumentType.getString(commandContext, "character_name");
                                                                        final String characterSkin = StringArgumentType.getString(commandContext, "character_skin");

                                                                        return CauldronCharacterCommand.createCharacter(commandContext, characterId, characterName, characterSkin);
                                                                    }
                                                                    )

                                                            )
                                                    )
                                            )
                            )
                            .then(literal(CommandConsts.DELETE)
                                    .then(argument("character_id", StringArgumentType.string())
                                            .requires(source -> source.getPermissions().hasPermission(DefaultPermissions.MODERATORS))
                                            .executes(commandContext -> {
                                                        final String characterId = StringArgumentType.getString(commandContext, "character_id");
                                                        return CauldronCharacterCommand.deleteCharacter(commandContext, characterId);
                                                    }
                                            )
                                    )
                            )
                            .then(literal("spawn")
                                    .then(argument("character_id", StringArgumentType.string())
                                            .requires(source -> source.getPermissions().hasPermission(DefaultPermissions.MODERATORS))
                                            .executes(commandContext -> {
                                                        final String characterId = StringArgumentType.getString(commandContext, "character_id");
                                                        return CauldronCharacterCommand.spawnCharacter(commandContext, characterId);
                                                    }
                                            )
                                    )
                            )
                    )
                    .then(literal(CommandConsts.ENCOUNTER)
                            .then(literal(CommandConsts.LIST)
                                    .requires(source -> source.getPermissions().hasPermission(DefaultPermissions.MODERATORS))
                                    .executes(CauldronEncounterCommand::listEncounters)
                            )
                            .then(literal(CommandConsts.CREATE)
                                    .then(argument("displayName", StringArgumentType.string())
                                            .requires(source -> source.getPermissions().hasPermission(DefaultPermissions.MODERATORS))
                                            .executes(commandContext -> {
                                                        final String displayName = StringArgumentType.getString(commandContext, "displayName");
                                                        return CauldronEncounterCommand.createEncounter(commandContext, displayName);
                                                    }
                                            )
                                    )
                            )
                            .then(literal(CommandConsts.DELETE)
                                    .then(argument("encounter_id", StringArgumentType.string())
                                            .requires(source -> source.getPermissions().hasPermission(DefaultPermissions.MODERATORS))
                                            .executes(commandContext -> {
                                                        final String encounterId = StringArgumentType.getString(commandContext, "encounter_id");
                                                        return CauldronEncounterCommand.deleteEncounter(commandContext, encounterId);
                                                    }
                                            )
                                    )
                            )
                            .then(literal("modify")
                                    .then(argument("displayName", StringArgumentType.string())
                                            .requires(source -> source.getPermissions().hasPermission(DefaultPermissions.MODERATORS))
                                            .executes(commandContext -> {
                                                        final String displayName = StringArgumentType.getString(commandContext, "displayName");
                                                        return CauldronEncounterCommand.modifyEncounter(commandContext, displayName);
                                                    }
                                            )
                                    )
                            )
                            .then(literal("current")
                                            .requires(source -> source.getPermissions().hasPermission(DefaultPermissions.MODERATORS))
                                            .executes(CauldronEncounterCommand::whatEncounter)
                            ).then(literal("stop")
                                            .requires(source -> source.getPermissions().hasPermission(DefaultPermissions.MODERATORS))
                                            .executes(CauldronEncounterCommand::stopEncounter)
                            ).then(literal("start")
                                    .then(argument("displayName", StringArgumentType.string())
                                            .requires(source -> source.getPermissions().hasPermission(DefaultPermissions.MODERATORS))
                                            .executes(commandContext -> {
                                                        final String displayName = StringArgumentType.getString(commandContext, "displayName");
                                                        return CauldronEncounterCommand.startEncounter(commandContext, displayName);
                                                    }
                                            )
                                    )
                            )
                            .then(literal("init")
                            .requires(source -> source.getPermissions().hasPermission(DefaultPermissions.MODERATORS))
                            .executes(CauldronEncounterCommand::initRound))
                    )
            );

       

        });
    }
}
