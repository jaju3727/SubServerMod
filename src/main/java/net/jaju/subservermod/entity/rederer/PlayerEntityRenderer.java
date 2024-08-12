package net.jaju.subservermod.entity.rederer;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.entity.PlayerEntity;
import net.jaju.subservermod.entity.model.PlayerEntityModel;
import net.jaju.subservermod.util.SkinUtils;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PlayerEntityRenderer extends MobRenderer<PlayerEntity, PlayerEntityModel> {
    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(Subservermod.MOD_ID, "textures/entity/default_texture.png");

    public PlayerEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerEntityModel(context.bakeLayer(PlayerEntityModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(PlayerEntity entity) {
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
    protected boolean shouldShowName(PlayerEntity entity) {
        return true;
    }

}
