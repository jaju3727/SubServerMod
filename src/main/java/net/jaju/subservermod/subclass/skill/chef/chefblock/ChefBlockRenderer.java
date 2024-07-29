package net.jaju.subservermod.subclass.skill.chef.chefblock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.item.ModItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ChefBlockRenderer implements BlockEntityRenderer<ChefBlockEntity> {
    private static final ResourceLocation COOKGAUGE = new ResourceLocation(Subservermod.MOD_ID, "textures/gui/chef/chef_cook_gauge.png");
    private static final ResourceLocation OVERCOOKGAUGE = new ResourceLocation(Subservermod.MOD_ID, "textures/gui/chef/chef_overcook_gauge.png");
    private static final ResourceLocation OVERCOOKGAUGEBACKGROUND = new ResourceLocation(Subservermod.MOD_ID, "textures/gui/chef/chef_overcook_gauge_background.png");
    private static final ResourceLocation COOKGAUGEBACKGROUND = new ResourceLocation(Subservermod.MOD_ID, "textures/gui/chef/chef_cook_gauge_background.png");

    public ChefBlockRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(ChefBlockEntity chefBlockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        List<ItemStack> itemStacks = chefBlockEntity.getItemStacks();
        if (!itemStacks.isEmpty()) {
            Direction direction = chefBlockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            for (ItemStack itemStack : itemStacks) {
                int butterCount = chefBlockEntity.getButterCount();
                if (itemStack.getItem() == ModItem.BUTTER.get()) {
                    renderButter(chefBlockEntity, poseStack, bufferSource, itemStack, combinedOverlay, direction , butterCount);
                }
                long tick = System.currentTimeMillis() - chefBlockEntity.getTick();
                if (itemStack.getItem() == ModItem.COOKING_OIL.get() && tick < 1000) {
                    renderCookingOil(chefBlockEntity, poseStack, bufferSource, itemStack, combinedOverlay, direction, tick);
                }
                if (!(itemStack.getItem() == ModItem.COOKING_OIL.get() || itemStack.getItem() == ModItem.BUTTER.get())) {
                    if (chefBlockEntity.getcookFlag()) {
                        long cookTick = System.currentTimeMillis() - chefBlockEntity.getCookTick();
                        poseStack.pushPose();
                        applyDirectionTranslation(poseStack, direction, 0.40, 1.1, 0.5);
                        poseStack.scale(1.0f, 1.0f, 1.0f);
                        applyDirectionRotation(poseStack, direction);
                        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0f));
                        poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
                        int combinedLightLevel = 0xF000F0;

                        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(itemStack, chefBlockEntity.getLevel(), null, 0);
                        Minecraft.getInstance().getItemRenderer().render(itemStack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, combinedLightLevel, combinedOverlay, model);
                        poseStack.popPose();
                        renderGauge(poseStack, bufferSource, COOKGAUGE, chefBlockEntity, ((float) 4000 - cookTick) / 4000, direction);
                        renderGaugeBackground(poseStack, bufferSource, COOKGAUGEBACKGROUND, chefBlockEntity, ((float) cookTick) / 8000, direction);
                    }
                    if (chefBlockEntity.getoverCookFlag()) {
                        long overCookTick = System.currentTimeMillis() - chefBlockEntity.getOverCookTick();
                        poseStack.pushPose();
                        applyDirectionTranslation(poseStack, direction, 0.40, 1.1, 0.5);
                        poseStack.scale(1.0f, 1.0f, 1.0f);
                        applyDirectionRotation(poseStack, direction);
                        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0f));
                        poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
                        int combinedLightLevel = 0xF000F0;

                        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(itemStack, chefBlockEntity.getLevel(), null, 0);
                        Minecraft.getInstance().getItemRenderer().render(itemStack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, combinedLightLevel, combinedOverlay, model);
                        poseStack.popPose();

                        renderGauge(poseStack, bufferSource, OVERCOOKGAUGE, chefBlockEntity, ((float) 4000 - overCookTick) / 4000, direction);
                        renderGaugeBackground(poseStack, bufferSource, OVERCOOKGAUGEBACKGROUND, chefBlockEntity, ((float) overCookTick) / 8000, direction);
                    }
                }
            }
        }
    }

    private void applyDirectionTranslation(PoseStack poseStack, Direction direction, double x, double y, double z) {
        switch (direction) {
            case NORTH:
                poseStack.translate(-x + 1.0, y, z);
                break;
            case SOUTH:
                poseStack.translate(x, y, -z + 1.0);
                break;
            case WEST:
                poseStack.translate(z, y, x);
                break;
            case EAST:
                poseStack.translate(-z + 1.0, y, -x + 1.0);
                break;
        }
    }

    private void applyDirectionRotation(PoseStack poseStack, Direction direction) {
        switch (direction) {
            case NORTH:
                poseStack.mulPose(Axis.YP.rotationDegrees(0.0F));
                break;
            case SOUTH:
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                break;
            case WEST:
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                break;
            case EAST:
                poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
                break;
        }
    }

    private void renderGauge(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation TEXTURE, ChefBlockEntity chefBlockEntity, float gaugeX, Direction direction) {
        poseStack.pushPose();

        applyDirectionTranslation(poseStack, direction, 0.5, 2.0, 0.5);
        applyDirectionRotation(poseStack, direction);

        poseStack.scale(gaugeX, 0.5f, 0.0f);

        renderTexture(poseStack, bufferSource, TEXTURE, 0xF000F0);

        poseStack.popPose();
    }

    private void renderGaugeBackground(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation TEXTURE, ChefBlockEntity chefBlockEntity, float gaugeX, Direction direction) {
        poseStack.pushPose();

        applyDirectionTranslation(poseStack, direction, gaugeX, 2.05, 0.499);
        applyDirectionRotation(poseStack, direction);

        poseStack.scale(0.3f, 0.3f, 0.0f);

        // 텍스처 렌더링
        renderTexture(poseStack, bufferSource, TEXTURE, 0xF000F0);

        poseStack.popPose();
    }

    private void renderTexture(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation texture, int combinedLight) {
        Minecraft mc = Minecraft.getInstance();
        mc.getTextureManager().bindForSetup(texture);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.text(texture));

        Matrix4f matrix = poseStack.last().pose();

        vertexConsumer.vertex(matrix, -0.5f, 0.5f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0.0f, 1.0f, 0.0f).endVertex();
        vertexConsumer.vertex(matrix, 0.5f, 0.5f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0.0f, 1.0f, 0.0f).endVertex();
        vertexConsumer.vertex(matrix, 0.5f, -0.5f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0.0f, 1.0f, 0.0f).endVertex();
        vertexConsumer.vertex(matrix, -0.5f, -0.5f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0.0f, 1.0f, 0.0f).endVertex();
    }

    private void renderButter(ChefBlockEntity chefBlockEntity,PoseStack poseStack, MultiBufferSource bufferSource, ItemStack itemStack, int combinedOverlay, Direction direction, int butterCount) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.1, 0.5);
        poseStack.scale((float) butterCount / 3.0f, (float) butterCount / 3.0f, (float) butterCount / 3.0f);
        applyDirectionRotation(poseStack, direction);
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        int combinedLightLevel = 0xF000F0;

        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(itemStack, chefBlockEntity.getLevel(), null, 0);
        Minecraft.getInstance().getItemRenderer().render(itemStack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, combinedLightLevel, combinedOverlay, model);
        poseStack.popPose();

    }

    private void renderCookingOil(ChefBlockEntity chefBlockEntity,PoseStack poseStack, MultiBufferSource bufferSource, ItemStack itemStack, int combinedOverlay, Direction direction, long tick) {
        poseStack.pushPose();
        applyDirectionTranslation(poseStack, direction, 0.20, 1.5, 0.5);
        poseStack.scale(0.6f, 0.6f, 0.6f);
        applyDirectionRotation(poseStack, direction);
        if (tick > 600){
            tick = 600;
        }

        poseStack.mulPose(Axis.ZP.rotationDegrees(110 * (float) tick / 600));
        int combinedLightLevel = 0xF000F0;

        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(itemStack, chefBlockEntity.getLevel(), null, 0);
        Minecraft.getInstance().getItemRenderer().render(itemStack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, combinedLightLevel, combinedOverlay, model);
        poseStack.popPose();
    }
}
