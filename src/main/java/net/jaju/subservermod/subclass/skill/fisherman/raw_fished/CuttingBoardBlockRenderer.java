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

    public CuttingBoardBlockRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(CuttingBoardBlockEntity cuttingBoardBlockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        Item item = cuttingBoardBlockEntity.getItem();
        if (!(item == Items.AIR)) {
            ItemStack itemStack = new ItemStack(item);
            Direction direction = cuttingBoardBlockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            poseStack.pushPose();
            applyDirectionTranslation(poseStack, direction, 0.40, 0.09, 0.5);
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
}
