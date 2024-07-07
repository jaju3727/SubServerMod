package net.jaju.subservermod.shopsystem.entity.model;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.shopsystem.entity.ShopEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShopEntityModel extends HumanoidModel<ShopEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Subservermod.MOD_ID, "custom_entity"), "main");

    public ShopEntityModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, CubeDeformation.NONE), PartPose.ZERO);
        part.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, CubeDeformation.NONE), PartPose.ZERO);
        part.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, CubeDeformation.NONE), PartPose.offset(-5.0F, 2.0F, 0.0F));
        part.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, CubeDeformation.NONE), PartPose.offset(5.0F, 2.0F, 0.0F));
        part.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, CubeDeformation.NONE), PartPose.offset(-1.9F, 12.0F, 0.0F));
        part.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, CubeDeformation.NONE), PartPose.offset(1.9F, 12.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }
}
