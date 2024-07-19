package net.jaju.subservermod.subclass.skill.fisherman.raw_fished;

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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CuttingBoardBlockRenderer implements BlockEntityRenderer<CuttingBoardBlockEntity> {
    private static final ResourceLocation COOKGAUGE = new ResourceLocation(Subservermod.MOD_ID, "textures/block/gauge.png");
    private static final ResourceLocation OVERCOOKGAUGE = new ResourceLocation(Subservermod.MOD_ID, "textures/block/gauge.png");

    public CuttingBoardBlockRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(CuttingBoardBlockEntity cuttingBoardBlockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        Item item = cuttingBoardBlockEntity.getItem();
        if (!(item == Items.AIR)) {
            ItemStack itemStack = new ItemStack(item);
            Direction direction = cuttingBoardBlockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            poseStack.pushPose();
            applyDirectionTranslation(poseStack, direction, 0.40, 1.1, 0.5);
            poseStack.scale(1.0f, 1.0f, 1.0f);
            applyDirectionRotation(poseStack, direction);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0f));
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
            int combinedLightLevel = 0xF000F0;

            BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(itemStack, cuttingBoardBlockEntity.getLevel(), null, 0);
            Minecraft.getInstance().getItemRenderer().render(itemStack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, combinedLightLevel, combinedOverlay, model);
            poseStack.popPose();
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

    private void renderCustomImage(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation TEXTURE, CuttingBoardBlockEntity cuttingBoardBlockEntity, float gaugeX) {
        poseStack.pushPose();
        poseStack.translate(0.5, 2.0, 0.5);

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            double dx = player.getX() - (cuttingBoardBlockEntity.getBlockPos().getX() + 0.5);
            double dz = player.getZ() - (cuttingBoardBlockEntity.getBlockPos().getZ() + 0.5);
            float angle = (float) (Math.atan2(dz, dx) * (180 / Math.PI)) + 90.0F;

            poseStack.mulPose(Axis.YP.rotationDegrees(-angle));
        }

        poseStack.scale(gaugeX, 0.1f, 0.0f);

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
}
