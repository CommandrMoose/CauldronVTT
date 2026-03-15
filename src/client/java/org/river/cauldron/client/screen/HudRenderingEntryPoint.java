package org.river.cauldron.client.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
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
import org.river.cauldron.client.screen.components.InitiativeTrackerLine;
import org.river.cauldron.entity.CharacterTokenEntity;
import org.river.cauldron.registry.CauldronEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HudRenderingEntryPoint {

    public static final InitiativeTrackerLine trackerLine = new InitiativeTrackerLine();

    // Make the barriers the last value, that the player can see
    public static void navigateInitScroll(int direction) {
        trackerLine.navigateInitScroll(direction);
    }

    public static void init() {
        // Called each time the encounter is updated with new characters.
        trackerLine.init();
    }

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        trackerLine.render(context, 5);

    }

}

