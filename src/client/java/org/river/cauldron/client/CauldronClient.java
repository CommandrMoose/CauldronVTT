package org.river.cauldron.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.river.cauldron.Cauldron;
import org.river.cauldron.cauldron.CauldronCharacters;
import org.river.cauldron.cauldron.CauldronCurrentEncounter;
import org.river.cauldron.client.renderer.CharacterTokenEntityRenderer;
import org.river.cauldron.client.renderer.GmTokenEntityRenderer;
import org.river.cauldron.client.screen.GmScreen;
import org.river.cauldron.client.screen.HudRenderingEntryPoint;
import org.river.cauldron.client.screen.SetCharacterScreen;
import org.river.cauldron.client.screen.components.DiceWidget;
import org.river.cauldron.network.s2c.OpenGmTokenScreenS2CPayload;
import org.river.cauldron.network.s2c.OpenTokenScreenS2CPayload;
import org.river.cauldron.network.s2c.UpdateCharacterListS2CPayload;
import org.river.cauldron.network.s2c.UpdateCurrentEncounterS2CPayload;
import org.river.cauldron.registry.CauldronEntities;


public class CauldronClient implements ClientModInitializer {


    private static KeyBinding openDiceRollerKeybinding;
    private static KeyBinding openGMScreenKeybinding;
    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of(Cauldron.MODID, "cauldron"));

    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(CauldronEntities.CHARACTER_TOKEN, CharacterTokenEntityRenderer::new);
        EntityRendererRegistry.register(CauldronEntities.GM_TOKEN, GmTokenEntityRenderer::new);


        ClientPlayNetworking.registerGlobalReceiver(OpenTokenScreenS2CPayload.ID, (payload, context) -> {
            var blocPos = payload.origin();
            var currentCharacter = payload.currentId();
            MinecraftClient.getInstance().setScreen(new SetCharacterScreen(blocPos, currentCharacter));
        });

        ClientPlayNetworking.registerGlobalReceiver(UpdateCharacterListS2CPayload.ID, (payload, context) -> {
            CauldronCharacters.SERVER_CHARACTER_DATA = payload.characterData();
            System.out.println("Updating character data");
        });

        ClientPlayNetworking.registerGlobalReceiver(UpdateCurrentEncounterS2CPayload.ID, (payload, context) -> {
            System.out.println("Updating encounter data");
            CauldronCurrentEncounter.initiative = payload.currentEncounter().getCurrentInitiative();
            HudRenderingEntryPoint.init();
        });
        ClientPlayNetworking.registerGlobalReceiver(OpenGmTokenScreenS2CPayload.ID, (payload, context) -> {
            System.out.println("Updating encounter data");
            var currentCharacter = payload.currentId();
            var entityId = payload.entityId();
            var addToInit = payload.addToInitiative();
            var spawn = payload.spawnOnStart();

            MinecraftClient.getInstance().setScreen(new SetCharacterScreen(entityId, currentCharacter, true, addToInit, spawn));
        });

        openDiceRollerKeybinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cauldron.open_dice", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_B, // The keycode of the key
                CATEGORY // The category of the key - you'll need to add a translation for this!
        ));
        openGMScreenKeybinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cauldron.open_gm_screen", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_M, // The keycode of the key
                CATEGORY // The category of the key - you'll need to add a translation for this!
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openDiceRollerKeybinding.isPressed()) {

                if (MinecraftClient.getInstance().currentScreen instanceof DiceWidget) {
                    MinecraftClient.getInstance().currentScreen.close();
                    return;
                }

                MinecraftClient.getInstance().setScreen(new DiceWidget(null, MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight()));
            }

            while (openGMScreenKeybinding.isPressed()) {
                MinecraftClient.getInstance().setScreen(new GmScreen( MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight()));
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) -> {

            if (!minecraftClient.isInSingleplayer()) {
                CauldronCharacters.SERVER_CHARACTER_DATA.clear();
            }
        });

        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of(Cauldron.MODID, "before_chat"), HudRenderingEntryPoint::render);


    }
}
