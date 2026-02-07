package org.river.cauldron.client.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.river.cauldron.Cauldron;
import org.river.cauldron.cauldron.CauldronCurrentEncounter;
import org.river.cauldron.cauldron.EncounterManager;
import org.river.cauldron.network.c2s.ChangeInitIndexC2SPayload;
import org.river.cauldron.network.c2s.InitEncounterC2SPayload;
import org.river.cauldron.network.c2s.StopEncounterC2SPayload;

public class GmScreen extends Screen {

    protected int width;
    protected int height;
    private int x;
    private int y;
    protected int backgroundWidth = 176;
    protected int backgroundHeight = 125;

    public ButtonWidget startInitiative;
    public ButtonWidget stopInitative;
    public ButtonWidget downInit;
    public ButtonWidget upInit;


    private static final Identifier TEXTURE = Identifier.of(Cauldron.MODID,"textures/gui/container/gm_screen.png");

    public GmScreen(int width, int height) {
        super(Text.literal("Gm Screen"));
        this.width = width;
        this.height = height;
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
    }

    @Override
    public void init() {
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        if (CauldronCurrentEncounter.initiative.getHasEncounterStarted() && !CauldronCurrentEncounter.initiative.getHasEncounterInit()) {
            this.startInitiative = ButtonWidget.builder(Text.literal("Start init"), (button) -> {
                this.startInit();
            }).position((this.width + this.backgroundWidth) / 2 - 55, (this.height + this.backgroundHeight) / 2).width(50).build();
            addDrawableChild(this.startInitiative);
        }

        if (CauldronCurrentEncounter.initiative.getHasEncounterStarted() && CauldronCurrentEncounter.initiative.getHasEncounterInit()) {
            this.stopInitative = ButtonWidget.builder(Text.literal("Stop init"), (button) -> {
                this.stopInit();
            }).position((this.width + this.backgroundWidth) / 2 - 55, (this.height + this.backgroundHeight) / 2).width(50).build();
            addDrawableChild(this.stopInitative);


            this.downInit = ButtonWidget.builder(Text.literal("-init"), (button) -> {
                this.changeInitIndex(-1);
            }).position((this.width) / 2, (this.height) / 2).width(50).build();
            addDrawableChild(this.downInit);

            this.upInit = ButtonWidget.builder(Text.literal("+init"), (button) -> {
                this.changeInitIndex(1);
            }).position((this.width + 75) / 2, (this.height) / 2).width(50).build();
            addDrawableChild(this.upInit);
        }
    }

    private void startInit() {
        // Run the start init command
        if (!CauldronCurrentEncounter.initiative.getHasEncounterInit()) {
            ClientPlayNetworking.send(new InitEncounterC2SPayload(CauldronCurrentEncounter.initiative.getEncounterId() != null ? CauldronCurrentEncounter.initiative.getEncounterId() : "test" ));
            this.remove(this.startInitiative);
            this.close();
        }
    }
    private void stopInit() {
        // Run the start init command
        if (CauldronCurrentEncounter.initiative.getHasEncounterInit()) {
            ClientPlayNetworking.send(new StopEncounterC2SPayload(CauldronCurrentEncounter.initiative.getEncounterId() != null ? CauldronCurrentEncounter.initiative.getEncounterId() : "test"));
            this.close();
        }
    }

    private void changeInitIndex(int direction) {
        // Send encounter update packet for the content!!!

        var maxInit = CauldronCurrentEncounter.initiative.INIT_PLACEMENTS.size();
        var currentInit = CauldronCurrentEncounter.initiative.getCurrentToken();
        var nextToken = currentInit + direction;

        if (nextToken < 0) {
            nextToken = maxInit - 1;
        } else if (nextToken >= maxInit) {
            nextToken = 0;
        }

        ClientPlayNetworking.send(new ChangeInitIndexC2SPayload(nextToken));
    }


    @Override
    public boolean shouldPause() {
        return false;
    }



    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        int i = this.x;
        int j = this.y;

        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
        super.render(context, mouseX, mouseY, delta);
    }

}
