package org.river.cauldron.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;


import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
@Environment(EnvType.CLIENT)
public class CharacterTokenRenderState extends LivingEntityRenderState {

    public boolean isSelected = false;

    public CharacterTokenRenderState() {
        this.isSelected = false;
    }
}
