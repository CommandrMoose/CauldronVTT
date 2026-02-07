package org.river.cauldron.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.ActionResult;
import org.river.cauldron.cauldron.CauldronCharacters;
import org.river.cauldron.cauldron.CauldronCurrentEncounter;
import org.river.cauldron.network.s2c.UpdateCharacterListS2CPayload;
import org.river.cauldron.network.s2c.UpdateCurrentEncounterS2CPayload;
import org.river.cauldron.registry.CauldronEntityComponents;

public class CauldronEvents {

   public static void initialize() {

       UseBlockCallback.EVENT.register((player, world, hand, pos) -> {

           var playerComponent = CauldronEntityComponents.PLAYER.get(player);
           playerComponent.onUseBlock(player, world, hand, pos);

           return ActionResult.PASS;
       });

       ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
           // Read from the files
           CauldronCharacters.readFromFiles();
       });

       ServerLifecycleEvents.SERVER_STOPPED.register((minecraftServer -> {
           CauldronCharacters.writeToFile();
       }));

       ServerPlayConnectionEvents.JOIN.register(((serverPlayNetworkHandler, packetSender, minecraftServer) -> {

           // Send a new packet to the player!
           UpdateCharacterListS2CPayload payload = new UpdateCharacterListS2CPayload(CauldronCharacters.SERVER_CHARACTER_DATA);
           ServerPlayNetworking.send(serverPlayNetworkHandler.getPlayer(), payload);
           System.out.println("Sent characters to the player!");

           UpdateCurrentEncounterS2CPayload encounterS2CPayload = new UpdateCurrentEncounterS2CPayload(new CauldronCurrentEncounter(CauldronCurrentEncounter.initiative));
           ServerPlayNetworking.send(serverPlayNetworkHandler.getPlayer(), payload);

       }));



   }

}
