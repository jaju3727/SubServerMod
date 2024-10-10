package net.jaju.subservermod.entity;

import net.jaju.subservermod.network.ModNetworking;
import net.jaju.subservermod.network.entity.packet.PlayerEntityPositionPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

public class PlayerEntity extends PathfinderMob {
    private static final EntityDataAccessor<String> SKIN_PLAYER_NAME = SynchedEntityData.defineId(PlayerEntity.class, EntityDataSerializers.STRING);

    public PlayerEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.noCulling = true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SKIN_PLAYER_NAME, "");
    }

    public void setSkinPlayerName(String skinName, String name) {
        this.entityData.set(SKIN_PLAYER_NAME, skinName);
        this.setCustomName(Component.literal(name));
    }

    public String getSkinPlayerName() {
        return this.entityData.get(SKIN_PLAYER_NAME);
    }


    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("SkinPlayerName", this.getSkinPlayerName());
        compound.putFloat("Yaw", this.getYRot());
        if (this.hasCustomName()) {
            compound.putString("CustomName", Component.Serializer.toJson(this.getCustomName()));
        }

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setYRot(compound.getFloat("Yaw"));
        this.setSkinPlayerName(compound.getString("SkinPlayerName"), compound.contains("CustomName") ? Component.Serializer.fromJson(compound.getString("CustomName")).getString() : null);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 이 엔티티는 모든 유형의 피해에 면역이 됩니다.
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        // 모든 피해 원천에 대해 면역
        return true;
    }

    @Override
    public void aiStep() {
        // AI 비활성화: 아무 것도 하지 않음
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0);  // 움직임 속도를 0으로 설정
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    public void updatePositionAndRotation(BlockPos pos, float yaw) {
        this.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        this.setYRot(yaw);
        this.setYHeadRot(yaw);
        this.setYBodyRot(yaw);

        if (!this.level().isClientSide) {
            ModNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new PlayerEntityPositionPacket(this.getId(), this.getX(), this.getY(), this.getZ(), this.getYRot()));
        }
    }
}
