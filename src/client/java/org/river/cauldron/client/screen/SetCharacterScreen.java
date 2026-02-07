package org.river.cauldron.client.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameModeSwitcherScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.river.cauldron.cauldron.CauldronCharacters;
import org.river.cauldron.client.screen.components.DiceWidget;
import org.river.cauldron.data.CauldronCharacterData;
import org.river.cauldron.entity.CharacterTokenEntity;
import org.river.cauldron.network.c2s.SetGmTokenCharacterC2SPayload;
import org.river.cauldron.network.c2s.SetTokenCharacterC2SPayload;
import org.river.cauldron.registry.CauldronEntities;

import java.util.List;

public class SetCharacterScreen extends Screen {

    public static CharacterTokenEntity characterEntity;
    public String currentId;
    private int currentIndex;
    private BlockPos origin;
    private int initative;
    private String entityId;

    private boolean isGmMode = false;
    private boolean spawnOnStart = true;
    private boolean addToInitativeOnSpawn = true;

    public ButtonWidget leftButton;
    public ButtonWidget rightButton;
    public ButtonWidget setButton;
    public ButtonWidget diceButton;
    public TextFieldWidget worldNameField;

    public CheckboxWidget checkboxSpawnOnStart;
    public CheckboxWidget checkboxAddToInitiativeOnSpawn;

    private float tickTime = 0;

    public SetCharacterScreen(BlockPos origin, String currentId) {
        super(Text.literal("CharacterScreen"));
        this.currentId = currentId;
        this.origin = origin;
        createEntity();
    }

    public SetCharacterScreen(BlockPos origin, String currentId, int initiative) {
        super(Text.literal("CharacterScreen"));
        this.currentId = currentId;
        this.entityId = entityId;
        this.initative = initiative;
        this.origin = origin;
        createEntity();
    }

    public SetCharacterScreen(String entityId, String currentId, int initiative) {
        super(Text.literal("CharacterScreen"));
        this.currentId = currentId;
        this.entityId = entityId;
        this.initative = initiative;
        createEntity();
    }
    public SetCharacterScreen(String entityId, String currentId, boolean isGmMode, boolean addToInit, boolean spawnOnStart) {
        super(Text.literal("CharacterScreen"));
        this.currentId = currentId;
        this.entityId = entityId;
        this.isGmMode = isGmMode;

        this.addToInitativeOnSpawn = addToInit;
        this.spawnOnStart = spawnOnStart;

        createEntity();
    }



    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        var entityView = drawEntity(characterEntity);

        tickTime += MinecraftClient.getInstance().getRenderTickCounter().getFixedDeltaTicks();

        float h = ItemEntity.getRotation(tickTime, 0);
        Quaternionf quaternionf2 = (new Quaternionf()).rotateZ((float)Math.PI).rotateY(-h);
        context.addEntity(entityView, height/ 5, new Vector3f(0, 1f,  0), quaternionf2, null, 0, 0, this.width, this.height );
    }


    @Override
    protected void init() {
        super.init();

        this.leftButton = ButtonWidget.builder(Text.literal("<"), (button) -> {
            this.cycleSelection(-1);
        }).position((int) ((float) width / 2) - 25 , (int) ((float) height / 5 * 4)).width(25).build();
        this.rightButton = ButtonWidget.builder(Text.literal(">"), (button) -> this.cycleSelection(1)).position((int) ((float) width / 2) + 25, (int) ((float) height / 5 * 4)).width(25).build();
        this.diceButton = ButtonWidget.builder(Text.literal("[D]"), (button) -> {
            MinecraftClient.getInstance().setScreen(new DiceWidget(this, width, height));
        }).position(width - (width / 8), height - (height / 5) - 25).width(50).build();
        this.setButton = ButtonWidget.builder(Text.literal("Confirm"), (button) -> this.sendTokenToServer()).position(width - (width / 8), height - (height / 5)).width(50).build();

        this.worldNameField = new TextFieldWidget(this.textRenderer, 55, 20, Text.translatable("selectWorld.enterName"));
        this.worldNameField.setPosition(width - (width / 4), height - (height / 5));

        this.checkboxAddToInitiativeOnSpawn = CheckboxWidget.builder(Text.literal("Add to initiative"),  MinecraftClient.getInstance().textRenderer).checked(this.addToInitativeOnSpawn).callback((checkboxWidget, isChecked) -> {
            this.addToInitativeOnSpawn = isChecked;
        }).pos((width / 8), height - (height / 5) - 45).build();

        this.checkboxSpawnOnStart = CheckboxWidget.builder(Text.literal("Spawn on start"),  MinecraftClient.getInstance().textRenderer).checked(this.spawnOnStart).callback((checkboxWidget, isChecked) -> {
            this.spawnOnStart = isChecked;
        }).pos((width / 8), height - (height / 5) - 25).build();



        this.addDrawableChild(leftButton);
        this.addDrawableChild(rightButton);
        this.addDrawableChild(setButton);
        this.addDrawableChild(worldNameField);
        this.addDrawableChild(diceButton);

        if (this.isGmMode) {
            this.addDrawableChild(checkboxAddToInitiativeOnSpawn);
            this.addDrawableChild(checkboxSpawnOnStart);
        }


    }


    public void cycleSelection(int direction) {
        var newIndex = currentIndex + direction;
        if (newIndex < 0) {
            newIndex = CauldronCharacters.SERVER_CHARACTER_DATA.size() - 1;
        } else {
            if (newIndex >= CauldronCharacters.SERVER_CHARACTER_DATA.size()) {
                newIndex = 0;
            }
        }

        var cauldronCharacterData = CauldronCharacters.SERVER_CHARACTER_DATA.get(newIndex);
        characterEntity.setCharacterId(cauldronCharacterData.id, cauldronCharacterData.characterSkin);
        this.currentIndex = newIndex;
    }

    public void sendTokenToServer() {
        var currentCharacter = CauldronCharacters.SERVER_CHARACTER_DATA.get(currentIndex);
        assert MinecraftClient.getInstance().player != null;



        if (this.entityId != null && !this.entityId.isEmpty()) {
            SetGmTokenCharacterC2SPayload payload = new SetGmTokenCharacterC2SPayload(this.entityId, currentCharacter.id,
                    MinecraftClient.getInstance().player.getUuid().toString(), initative, this.addToInitativeOnSpawn, this.spawnOnStart);
            ClientPlayNetworking.send(payload);
        } else {
            SetTokenCharacterC2SPayload payload = new SetTokenCharacterC2SPayload(origin, currentCharacter.id, MinecraftClient.getInstance().player.getUuid().toString(), initative);
            ClientPlayNetworking.send(payload);
        }



        MinecraftClient.getInstance().setScreen(null);
    }

    private void createEntity() {
        assert MinecraftClient.getInstance().world != null;
        characterEntity = new CharacterTokenEntity(CauldronEntities.CHARACTER_TOKEN, MinecraftClient.getInstance().world);

        if (this.currentId != null) {
            var character = CauldronCharacters.SERVER_CHARACTER_DATA.stream().filter(x -> x.id.equals(this.currentId)).findFirst();
            if (character.isPresent()) {
                var cauldronCharacterData = character.get();
                characterEntity.setCharacterId(cauldronCharacterData.id, cauldronCharacterData.characterSkin);
                this.currentIndex = CauldronCharacters.SERVER_CHARACTER_DATA.indexOf(cauldronCharacterData);
                return;
            }


        }

        if (!CauldronCharacters.SERVER_CHARACTER_DATA.isEmpty()) {
            this.currentIndex = 0;
            var cauldronCharacterData = CauldronCharacters.SERVER_CHARACTER_DATA.getFirst();
            characterEntity.setCharacterId(cauldronCharacterData.id, cauldronCharacterData.characterSkin);
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


    public int getInitative() {
        return initative;
    }

    public void setInitative(int initative) {
        this.initative = initative;
    }
}
