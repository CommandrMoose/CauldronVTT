package org.river.cauldron.client.screen.components;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.impl.screenhandler.client.ClientNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.river.cauldron.Cauldron;
import org.river.cauldron.cauldron.DiceInstance;
import org.river.cauldron.client.screen.SetCharacterScreen;
import org.river.cauldron.helper.MathHelper;
import org.river.cauldron.network.c2s.AnnounceDiceRollC2SPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;


@Environment(EnvType.CLIENT)
public class DiceWidget extends Screen {

    protected int width;
    protected int height;
    private int x;
    private int y;

    protected int backgroundWidth = 176;
    protected int backgroundHeight = 166;

    public ButtonWidget d4;
    public ButtonWidget d6;
    public ButtonWidget d8;
    public ButtonWidget d10;
    public ButtonWidget d12;
    public ButtonWidget d20;

    public ButtonWidget closeButton;
    public ButtonWidget rollButton;
    public ButtonWidget setButton;
    public ButtonWidget resetButton;

    private TextFieldWidget modifierField;

    private final Screen parentScreen;

    private int rolledValue = 0;
    private int modifier = 0;

    private List<DiceInstance> diceInstanceList = new ArrayList<>();


    private static final Identifier TEXTURE = Identifier.of(Cauldron.MODID,"textures/gui/container/dice_roller.png");

    public DiceWidget(Screen parentScreen, int width, int height) {
        super(Text.literal(""));
        this.parentScreen = parentScreen;
        this.width = width;
        this.height = height;
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        this.modifierField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, (this.width - this.backgroundWidth) / 2 - 55, (this.height - this.backgroundHeight) / 2 + 5, 50, 20, Text.literal("Mod"));
        this.modifierField.setFocusUnlocked(false);
        this.modifierField.setEditableColor(-1);
        this.modifierField.setUneditableColor(-1);
        this.modifierField.setDrawsBackground(true);
        this.modifierField.setMaxLength(5);
        this.modifierField.setChangedListener(this::onModifierChanged);
        this.modifierField.setText("+0");
    }

    @Override
    public void init() {

        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        var distanceBetweenButton = 20;

        var diceButtonHeight = (this.height - this.backgroundHeight) / 2 + 5;
        var bottomHeight = (this.height + this.backgroundHeight) / 2 - 25;

        this.d4 =  ButtonWidget.builder(Text.literal("4"), (button) -> {
            addDice(new DiceInstance("d4", 4));
        }).position((this.width - this.backgroundWidth ) / 2 + 5, diceButtonHeight).width(15).build();

        this.d6 = ButtonWidget.builder(Text.literal("6"), (button) -> {
            addDice(new DiceInstance("d6", 6));
        }).position( this.d4.getX() + distanceBetweenButton, diceButtonHeight).width(15).build();

        this.d8 = ButtonWidget.builder(Text.literal("8"), (button) -> {
            addDice(new DiceInstance("d8", 8));
        }).position(this.d6.getX() + distanceBetweenButton, diceButtonHeight).width(15).build();

        this.d10 = ButtonWidget.builder(Text.literal("10"), (button) -> {
            addDice(new DiceInstance("d10", 10));
        }).position( this.d8.getX() + distanceBetweenButton, diceButtonHeight).width(15).build();

        this.d12 =  ButtonWidget.builder(Text.literal("12"), (button) -> {
            addDice(new DiceInstance("d12", 12));
        }).position(this.d10.getX() + distanceBetweenButton, diceButtonHeight).width(15).build();

        this.d20 = ButtonWidget.builder(Text.literal("20"), (button) -> {
            addDice(new DiceInstance("d20", 20));
        }).position(this.d12.getX() + distanceBetweenButton, diceButtonHeight).width(15).build();

        this.resetButton = ButtonWidget.builder(Text.literal("R"), (button) -> {
            this.resetDice();
        }).position(this.d20.getX() + distanceBetweenButton, diceButtonHeight).width(15).build();

        this.closeButton = ButtonWidget.builder(Text.literal("X"), (button) -> {
          this.close();
        }).position((this.width + this.backgroundWidth) / 2 - 25, diceButtonHeight).width(20).build();

        this.rollButton = ButtonWidget.builder(Text.literal("Roll"), (button) -> {
            this.roll();
        }).position((this.width - this.backgroundWidth) / 2 + 5, bottomHeight).width(50).build();

        this.setButton = ButtonWidget.builder(Text.literal("Set"), (button) -> {
            this.close();
        }).position((this.width + this.backgroundWidth) / 2 - 55, bottomHeight).width(50).build();


        this.addDrawableChild(this.modifierField);

        this.addDrawableChild(d4);
        this.addDrawableChild(d6);
        this.addDrawableChild(d8);
        this.addDrawableChild(d10);
        this.addDrawableChild(d12);
        this.addDrawableChild(d20);
        this.addDrawableChild(closeButton);
        this.addDrawableChild(rollButton);
        this.addDrawableChild(setButton);
        this.addDrawableChild(resetButton);

    }

    private void onModifierChanged(String s) {
        this.modifier = MathHelper.tryParseInt(s);
    }


    public void addDice(DiceInstance diceInstance) {
        this.diceInstanceList.add(diceInstance);
    }

    public void resetDice() {
        this.diceInstanceList.clear();
    }

    public void roll() {

        // Roll the dice
        this.diceInstanceList.forEach(DiceInstance::roll);
        this.rolledValue = this.diceInstanceList.stream().mapToInt(DiceInstance::getCurrentValue).sum();

        AnnounceDiceRollC2SPayload payload = new AnnounceDiceRollC2SPayload( (this.rolledValue + modifier) + " (" + this.buildDiceString() + ")");
        ClientPlayNetworking.send(payload);
    }

    @Override
    public void close() {
        if (parentScreen instanceof SetCharacterScreen cs) {
            cs.setInitative(rolledValue);
            assert client != null;
            client.setScreen(cs);
        } else {
            super.close();
        }
    }

    private String buildDiceString() {
        var output = new ArrayList<String>();

        if (this.diceInstanceList.isEmpty()) {
            return "";
        }

        this.diceInstanceList.forEach(x -> output.add(x.getCurrentValue() != -1 ? x.getDiceName() + " §6(" + x.getCurrentValue() + ")§r" : x.getDiceName()));

        var summed = String.join(",§f ", output);
        if (this.modifier != 0) {
            summed += "§7+" + this.modifier;
        }

        return summed;
    }



    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        int i = this.x;
        int j = this.y;

        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
        super.render(context, mouseX, mouseY, delta);

        context.drawText(MinecraftClient.getInstance().textRenderer, Text.literal(buildDiceString()), (this.width - this.backgroundWidth) / 2 + 20, (this.height - this.backgroundHeight) / 2 + 35, Colors.WHITE, true);
        context.drawText(MinecraftClient.getInstance().textRenderer, Text.literal(String.valueOf(rolledValue + this.modifier)), this.width / 2 , (this.height + this.backgroundHeight) / 2 - 25, Colors.WHITE, true);

    }
}

