package org.river.cauldron.client.model;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.river.cauldron.Cauldron;
import org.river.cauldron.client.ClientUtil;
import org.river.cauldron.entity.CharacterTokenEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;


public class GeoDmTokenModel<T extends CharacterTokenEntity> extends GeoModel<T> {
    private final Identifier model = Identifier.of(Cauldron.MODID, "entity/fallback");
    private final Identifier animations = Identifier.of(Cauldron.MODID, "entity/fallback");
    private final Identifier texture = Identifier.of(Cauldron.MODID, "textures/entity/gm_token_texture.png");

    @Override
    public @NotNull Identifier getModelResource(GeoRenderState geoRenderState) {

        var characterSkin = geoRenderState.getGeckolibData(CharacterTokenEntity.CHARACTER_SKIN_TICKET);

        if (characterSkin != null) {
            var name = characterSkin.replace(' ', '_');
            var ident = "geckolib/models/entity/" + name.trim().toLowerCase() + ".geo.json";

            if (ClientUtil.doesExist(Identifier.of(Cauldron.MODID, ident))) {
                return Identifier.of(Cauldron.MODID, "entity/" + name);
           }
        }

        return model;
    }

    @Override
    public @NotNull Identifier getTextureResource(GeoRenderState geoRenderState) {
        return texture;
    }

    @Override
    public @NotNull Identifier getAnimationResource(CharacterTokenEntity characterTokenEntity) {
        return animations;
    }
}