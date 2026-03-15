package org.river.cauldron.client.screen;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.river.cauldron.client.CauldronClient;
import org.river.cauldron.client.screen.components.InitiativeTrackerLine;

public class InitiativeEditorScreen extends Screen {

    protected InitiativeEditorScreen() {
        super(Text.literal("Initiative Editor Screen"));

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

        HudRenderingEntryPoint.trackerLine.render(context, this.height / 2 - InitiativeTrackerLine.counterWidth);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {

        System.out.println("Mouse clicked");

        return super.mouseClicked(click, doubled);
    }
}
