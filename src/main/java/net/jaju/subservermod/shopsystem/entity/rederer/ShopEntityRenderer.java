package net.jaju.subservermod.shopsystem.entity.rederer;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.shopsystem.entity.ShopEntity;
import net.jaju.subservermod.shopsystem.entity.model.ShopEntityModel;
import net.jaju.subservermod.util.SkinUtils;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ShopEntityRenderer extends MobRenderer<ShopEntity, ShopEntityModel> {
    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(Subservermod.MOD_ID, "textures/entity/default_texture.png");

    public ShopEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ShopEntityModel(context.bakeLayer(ShopEntityModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(ShopEntity entity) {
        String playerName = entity.getSkinPlayerName();
        if (!playerName.isEmpty()) {
            ResourceLocation skinTexture = SkinUtils.getSkinForPlayer(playerName);
            if (skinTexture != null) {
                return skinTexture;
            }
        }
        return DEFAULT_TEXTURE;
    }

    @Override
    protected boolean shouldShowName(ShopEntity entity) {
        return true;
    }

}
