package org.river.cauldron.client.screen.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
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

public class InitiativeTrackerLine {

    public static final int counterDifference = 0;
    public static final int counterWidth = 32;

    public static final Identifier slot = Identifier.of(Cauldron.MODID, "textures/gui/container/dice_init_slot.png");
    public static final Identifier highLightSlot = Identifier.of(Cauldron.MODID, "textures/gui/container/dice_init_slot_highlight.png");
    public static final Identifier leftArrow = Identifier.of(Cauldron.MODID, "textures/gui/container/init_left_button.png");
    public static final Identifier rightArrow = Identifier.of(Cauldron.MODID, "textures/gui/container/init_right_button.png");
    public static final Identifier leftArrowHighLight = Identifier.of(Cauldron.MODID, "textures/gui/container/init_left_button_highlight.png");
    public static final Identifier rightArrowHighlight = Identifier.of(Cauldron.MODID, "textures/gui/container/init_right_button_highlight.png");


    public static final int MAX_INIT_ON_SCREEN = 8;
    public static int SCROLL_VIEW_INDEX = MAX_INIT_ON_SCREEN - 1;

    private static List<CauldronInitiativePlacement> placementList = new ArrayList<>();
    private static List<CharacterTokenEntity> guiEntities = new ArrayList<>();

    // Make the barriers the last value, that the player can see
    public void navigateInitScroll(int direction) {
        int newIndex = SCROLL_VIEW_INDEX + direction;
        if (newIndex > placementList.size() || newIndex < MAX_INIT_ON_SCREEN) {
            return;
        }

        SCROLL_VIEW_INDEX = newIndex;
    }

    public void init() {
        guiEntities = new ArrayList<>();

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

    public void render(DrawContext context, int posY) {

        if (CauldronCurrentEncounter.initiative == null || !CauldronCurrentEncounter.initiative.getHasEncounterInit()) {
            return;
        }

        var initiativeCount = guiEntities.size();
        if (initiativeCount == 0 || CauldronCurrentEncounter.initiative.INIT_PLACEMENTS.isEmpty()) {
            return;
        }

        var characterCountOffset = Math.min(initiativeCount, MAX_INIT_ON_SCREEN);

        var startingOffset = ((counterDifference + counterWidth) * characterCountOffset ) / 2;
        var firstPoint = new Vector2i(context.getScaledWindowWidth() / 2 - startingOffset, posY);
        var selected = CauldronCurrentEncounter.initiative.INIT_PLACEMENTS.get(CauldronCurrentEncounter.initiative.getCurrentToken());
        var currentCharacterToken = CauldronCharacters.SERVER_CHARACTER_DATA.stream().filter(x -> Objects.equals(x.id, selected.characterId)).findFirst();

        var owner = MinecraftClient.getInstance().world.getPlayers().stream().filter(x -> x.getUuid().toString().equals(selected.playerId)).findFirst();
        var name = owner.isPresent() ? "(" + owner.get().getName().getLiteralString() + ") " : "";

        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.literal( name+ currentCharacterToken.get().getCharacterName() + "'s turn"), context.getScaledWindowWidth() / 2 - 30 + currentCharacterToken.get().getCharacterName().length() - name.length(), 40, -1);

        var startingIndex = SCROLL_VIEW_INDEX - MAX_INIT_ON_SCREEN;
        if (startingIndex < 0) {
            startingIndex = 0;
        }

        var maxInit = startingIndex + MAX_INIT_ON_SCREEN;
        if (maxInit >= initiativeCount) {
            maxInit = initiativeCount;
        }

        for (int i = startingIndex; i < maxInit; i++) {

            var character = guiEntities.get(i);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, slot, firstPoint.x, firstPoint.y, 0.0F, 0.0F, counterWidth, counterWidth, 32, 32);
            var entityView = drawEntity(character);
            float h = ItemEntity.getRotation(MinecraftClient.getInstance().world.getTime(), 0);
            Quaternionf quaternionf2 = (new Quaternionf()).rotateZ((float)Math.PI).rotateY(125 + 180);
            context.addEntity(entityView, counterWidth / 2, new Vector3f(0, 1.25f,  0), quaternionf2, null, firstPoint.x + 2, firstPoint.y + 2, firstPoint.x + counterWidth -2, firstPoint.y + counterWidth -2 );

            firstPoint.add(counterDifference + counterWidth, 0);
        }

        int highlightedIndex = CauldronCurrentEncounter.initiative.getCurrentToken();

        boolean shouldShowHighlight = highlightedIndex >= startingIndex && highlightedIndex <= maxInit - 1;
        if (shouldShowHighlight) {
            int highlightPosOnScreen = highlightedIndex - startingIndex;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, highLightSlot, context.getScaledWindowWidth() / 2 - startingOffset + (highlightPosOnScreen * (counterDifference + counterWidth)), posY, 0.0F, 0.0F, counterWidth, counterWidth, 32, 32);
        }

        // Draw arrows.
        boolean showLeftArrow = SCROLL_VIEW_INDEX > MAX_INIT_ON_SCREEN;
        boolean showRightArrow = SCROLL_VIEW_INDEX <= initiativeCount - 1;

        if (showLeftArrow) {
            boolean highLighted = highlightedIndex < startingIndex;
            var leftArrowPlacement = context.getScaledWindowWidth() / 2 - startingOffset - (counterDifference + counterWidth) + counterDifference;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, highLighted ? leftArrowHighLight : leftArrow,  leftArrowPlacement, posY, 0.0F, 0.0F, counterWidth, counterWidth, 32, 32);
        }

        if (showRightArrow) {
            boolean highLighted = highlightedIndex >= maxInit;
            var rightArrowPlacement = context.getScaledWindowWidth() / 2 - startingOffset + (characterCountOffset * (counterDifference + counterWidth));
            context.drawTexture(RenderPipelines.GUI_TEXTURED, highLighted ? rightArrowHighlight : rightArrow, rightArrowPlacement, posY, 0.0F, 0.0F, counterWidth, counterWidth, 32, 32);
        }

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
