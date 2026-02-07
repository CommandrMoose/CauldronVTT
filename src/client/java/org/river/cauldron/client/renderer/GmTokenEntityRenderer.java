package org.river.cauldron.client.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import org.joml.Vector3f;
import org.river.cauldron.client.model.GeoDmTokenModel;
import org.river.cauldron.client.model.GeoLoadedModel;
import org.river.cauldron.entity.CharacterTokenEntity;
import org.river.cauldron.entity.GmTokenEntity;
import org.river.cauldron.item.GmTokenSpawnerItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;


public class GmTokenEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<GmTokenEntity, R> {

    public GmTokenEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new GeoDmTokenModel());

    }

    @Override
    protected float getShadowRadius(R state) {
        return super.getShadowRadius(state) * (state).baseScale;
    }

    @Override
    public void addRenderData(GmTokenEntity animatable, @org.jspecify.annotations.Nullable Void relatedObject, R renderState, float partialTick) {
        super.addRenderData(animatable, relatedObject, renderState, partialTick);

        renderState.addGeckolibData(CharacterTokenEntity.CHARACTER_SKIN_TICKET, animatable.getCharacterSkin());
    }

    @Override
    public void render(R renderState, MatrixStack poseStack, OrderedRenderCommandQueue renderTasks, CameraRenderState cameraState) {

        poseStack.push();

        poseStack.scale(0.5f, 0.5f, 0.5f);

        super.render(renderState, poseStack, renderTasks, cameraState);

        poseStack.pop();
    }

    @Override
    public boolean shouldRender(GmTokenEntity entity, Frustum frustum, double x, double y, double z) {
        if (MinecraftClient.getInstance().player.getMainHandStack().getItem() instanceof GmTokenSpawnerItem || MinecraftClient.getInstance().player.getOffHandStack().getItem() instanceof GmTokenSpawnerItem) {
            entity.setInvisible(false);
            return super.shouldRender(entity, frustum, x, y, z);
        }

        entity.setInvisible(true);
        return false;
    }

}