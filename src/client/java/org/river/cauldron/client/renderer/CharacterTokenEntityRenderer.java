package org.river.cauldron.client.renderer;

import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import org.joml.Vector3f;
import org.river.cauldron.client.model.GeoLoadedModel;
import org.river.cauldron.entity.CharacterTokenEntity;

import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;



public class CharacterTokenEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<CharacterTokenEntity, R> {

    public CharacterTokenEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new GeoLoadedModel());

    }

    @Override
    protected float getShadowRadius(R state) {
        return super.getShadowRadius(state) * (state).baseScale;
    }

    @Override
    public void addRenderData(CharacterTokenEntity animatable, @org.jspecify.annotations.Nullable Void relatedObject, R renderState, float partialTick) {
        super.addRenderData(animatable, relatedObject, renderState, partialTick);

        renderState.addGeckolibData(CharacterTokenEntity.CHARACTER_SKIN_TICKET, animatable.getCharacterSkin());
        renderState.addGeckolibData(CharacterTokenEntity.IS_SELECTED_TICKET, animatable.isSelected());


    }

    @Override
    public void render(R renderState, MatrixStack poseStack, OrderedRenderCommandQueue renderTasks, CameraRenderState cameraState) {
        super.render(renderState, poseStack, renderTasks, cameraState);

        if (Boolean.FALSE.equals(renderState.getGeckolibData(CharacterTokenEntity.IS_SELECTED_TICKET))) {
            return;
        }

        var entityPos = renderState.getGeckolibData(DataTickets.POSITION);
        poseStack.push();

        poseStack.scale(1, 1, 1);

        assert entityPos != null;
        var x = entityPos.x;
        var y = entityPos.y;
        var z = entityPos.z;


        var scale = 2;
        var gridSize = 60;
        var offsetFromGrid = (double) -gridSize /2;

        var floorX = Math.floor(x);
        var floorY = Math.floor(y);
        var floorZ = Math.floor(z);

        var posX = x - floorX;
        var posY = y - floorY;
        var posZ = z - floorZ;
        poseStack.translate(new Vec3d(-posX, -posY, -posZ));
        poseStack.translate(new Vec3d(offsetFromGrid, 0, offsetFromGrid));

        var offsetForCenter = (gridSize / 2);

        for (int i = -offsetForCenter; i < offsetForCenter; i++) {
            for (int j = -offsetForCenter; j < offsetForCenter; j++) {

                var color =  new Vector3f(0.5f, 0.5f, 0.5f);
                boolean isCenter = i == 0 && j == 0;
                if (isCenter) {
                    color = new Vector3f(0, 1, 0);
                }

                BlockPos blockPos = new BlockPos((int) floorX + i, (int) floorY, (int) floorZ + j);
                BlockPos blockPos2 = blockPos.add(1, 0, 1);
                GizmoDrawing.box((new Box((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), (double)blockPos2.getX(), isCenter ? blockPos.getY() + 0.25f : blockPos.getY() + 0, (double)blockPos2.getZ())), DrawStyle.stroked(ColorHelper.fromFloats(1.0F, color.x, color.y, color.z)), false);
            }
        }
        poseStack.pop();

    }



    @Override
    public boolean shouldRender(CharacterTokenEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }
}