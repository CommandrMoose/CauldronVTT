package org.river.cauldron.client.screen;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.DeltaFeature;
import org.joml.Quaternionf;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.river.cauldron.Cauldron;
import org.river.cauldron.cauldron.CauldronCharacters;
import org.river.cauldron.cauldron.CauldronCurrentEncounter;
import org.river.cauldron.cauldron.CauldronInitiativePlacement;
import org.river.cauldron.entity.CharacterTokenEntity;
import org.river.cauldron.registry.CauldronEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HudRenderingEntryPoint {

    public static final int counterDifference = 0;
    public static final int counterWidth = 32;

    public static final Identifier slot = Identifier.of(Cauldron.MODID, "textures/gui/container/dice_init_slot.png");
    public static final Identifier highLightSlot = Identifier.of(Cauldron.MODID, "textures/gui/container/dice_init_slot_highlight.png");

    private static List<CauldronInitiativePlacement> placementList = new ArrayList<>();
    private static List<CharacterTokenEntity> guiEntities = new ArrayList<>();

    public static void init() {
        // Called each time the encounter is updated with new characters.

        guiEntities = new ArrayList<>();
        placementList = new ArrayList<>();

        var list = CauldronCurrentEncounter.initiative.INIT_PLACEMENTS;
        placementList = list;

        list.forEach(x -> {
            var characterId = x.characterId;
            var token = CauldronCharacters.SERVER_CHARACTER_DATA.stream().filter(y -> Objects.equals(y.id, characterId)).findFirst();

            if (token.isPresent()) {
                var character = new CharacterTokenEntity(CauldronEntities.CHARACTER_TOKEN, MinecraftClient.getInstance().world);
                character.setCharacterId(token.get().id, token.get().characterSkin);
                guiEntities.add(character);
            }
        });



    }

    public static void render(DrawContext context, RenderTickCounter tickCounter) {

        if (CauldronCurrentEncounter.initiative == null || !CauldronCurrentEncounter.initiative.getHasEncounterInit()) {
            return;
        }

        var initiativeCount = guiEntities.size();
        if (initiativeCount == 0 || CauldronCurrentEncounter.initiative.INIT_PLACEMENTS.isEmpty()) {
            return;
        }

        var startingOffset = ((counterDifference + counterWidth) * initiativeCount ) / 2;
        var firstPoint = new Vector2i(context.getScaledWindowWidth() / 2 - startingOffset, 5);
        var selected = CauldronCurrentEncounter.initiative.INIT_PLACEMENTS.get(CauldronCurrentEncounter.initiative.getCurrentToken());
        var characterToken = CauldronCharacters.SERVER_CHARACTER_DATA.stream().filter(x -> Objects.equals(x.id, selected.characterId)).findFirst();

        var owner = MinecraftClient.getInstance().world.getPlayers().stream().filter(x -> x.getUuid().toString().equals(selected.playerId)).findFirst();
        var name = owner.isPresent() ? "(" + owner.get().getName().getLiteralString() + ") " : "";

        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.literal( name+ characterToken.get().getCharacterName() + "'s turn"), context.getScaledWindowWidth() / 2 - 30 + characterToken.get().getCharacterName().length() - name.length(), 40, -1);

        for (int i = 0; i < initiativeCount; i++) {

            var placement = placementList.get(i);
            var character = guiEntities.get(i);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, slot, firstPoint.x, firstPoint.y, 0.0F, 0.0F, counterWidth, counterWidth, 32, 32);
            var entityView = drawEntity(character);
            float h = ItemEntity.getRotation(MinecraftClient.getInstance().world.getTime(), 0);
            Quaternionf quaternionf2 = (new Quaternionf()).rotateZ((float)Math.PI).rotateY(125 + 180);
            context.addEntity(entityView, counterWidth / 2, new Vector3f(0, 1.25f,  0), quaternionf2, null, firstPoint.x + 2, firstPoint.y + 2, firstPoint.x + counterWidth -2, firstPoint.y + counterWidth -2 );
            firstPoint.add(counterDifference + counterWidth, 0);
            character.tick();

        }

        context.drawTexture(RenderPipelines.GUI_TEXTURED, highLightSlot, context.getScaledWindowWidth() / 2 - startingOffset + (CauldronCurrentEncounter.initiative.getCurrentToken() * (counterDifference + counterWidth)), 5, 0.0F, 0.0F, counterWidth, counterWidth, 32, 32);
    }

    private static EntityRenderState drawEntity(LivingEntity entity) {
        EntityRenderManager entityRenderManager = MinecraftClient.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super LivingEntity, ?> entityRenderer = entityRenderManager.getRenderer(entity);
        EntityRenderState entityRenderState = entityRenderer.getAndUpdateRenderState(entity, 1.0F);
        entityRenderState.light = 15728880;
        entityRenderState.shadowPieces.clear();
        entityRenderState.outlineColor = 0;
        return entityRenderState;
    }

}
