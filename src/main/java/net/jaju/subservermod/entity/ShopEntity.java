package net.jaju.subservermod.entity;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.entity.packet.ShopEntityPositionPacket;
import net.jaju.subservermod.shopsystem.ShopItem;
import net.jaju.subservermod.shopsystem.network.ShopEntityDataPacket;
import net.jaju.subservermod.shopsystem.screen.ShopEntityContainerProvider;
import net.jaju.subservermod.subclass.BaseClass;
import net.jaju.subservermod.subclass.ClassManagement;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class ShopEntity extends PathfinderMob {
    private static final EntityDataAccessor<String> SKIN_PLAYER_NAME = SynchedEntityData.defineId(ShopEntity.class, EntityDataSerializers.STRING);
    private final List<ShopItem> shopItems = new ArrayList<>();

    public ShopEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
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

    public List<ShopItem> getShopItems() {
        return shopItems;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("SkinPlayerName", this.getSkinPlayerName());
        if (this.hasCustomName()) {
            compound.putString("CustomName", Component.Serializer.toJson(this.getCustomName()));
        }
        compound.putFloat("Yaw", this.getYRot());

        ListTag shopItemsTag = new ListTag();
        for (ShopItem shopItem : shopItems) {
            CompoundTag itemTag = new CompoundTag();
            CompoundTag itemStackTag = new CompoundTag();
            shopItem.getItemStack().save(itemStackTag);
            itemTag.put("ItemStack", itemStackTag);
            itemTag.putInt("BuyPrice", shopItem.getBuyPrice());
            itemTag.putInt("SellPrice", shopItem.getSellPrice());
            itemTag.putInt("DailyBuyLimitNum", shopItem.getDailyBuyLimitNum());
            itemTag.putInt("DailyBuyLimitPlayerNum", shopItem.getDailyBuyLimitPlayerNum());
            itemTag.putInt("DailySellLimitNum", shopItem.getDailySellLimitNum());
            itemTag.putInt("DailySellLimitPlayerNum", shopItem.getDailySellLimitPlayerNum());
            itemTag.putBoolean("IsBuyable", shopItem.getIsBuyable());
            itemTag.putBoolean("IsSellable", shopItem.getIsSellable());
            itemTag.putBoolean("IsDailyBuyLimit", shopItem.getIsDailyBuyLimit());
            itemTag.putBoolean("IsDailySellLimit", shopItem.getIsDailySellLimit());
            itemTag.putString("CoinType", shopItem.getCoinType());

            shopItemsTag.add(itemTag);
        }
        compound.put("ShopItems", shopItemsTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSkinPlayerName(compound.getString("SkinPlayerName"), compound.contains("CustomName") ? Component.Serializer.fromJson(compound.getString("CustomName")).getString() : null);
        this.setYRot(compound.getFloat("Yaw"));

        shopItems.clear();
        ListTag shopItemsTag = compound.getList("ShopItems", Tag.TAG_COMPOUND);
        for (Tag tag : shopItemsTag) {
            CompoundTag itemTag = (CompoundTag) tag;
            ItemStack itemStack = ItemStack.of(itemTag.getCompound("ItemStack")); // ItemStack 전체를 불러오기
            int buyPrice = itemTag.getInt("BuyPrice");
            int sellPrice = itemTag.getInt("SellPrice");
            int dailyBuyLimitNum = itemTag.getInt("DailyBuyLimitNum");
            int dailyBuyLimitPlayerNum = itemTag.getInt("DailyBuyLimitPlayerNum");
            int dailySellLimitNum = itemTag.getInt("DailySellLimitNum");
            int dailySellLimitPlayerNum = itemTag.getInt("DailySellLimitPlayerNum");
            boolean isBuyable = itemTag.getBoolean("IsBuyable");
            boolean isSellable = itemTag.getBoolean("IsSellable");
            boolean isDailyBuyLimit = itemTag.getBoolean("IsDailyBuyLimit");
            boolean isDailySellLimit = itemTag.getBoolean("IsDailySellLimit");
            String coinType = itemTag.getString("CoinType");
            shopItems.add(new ShopItem(itemStack, buyPrice, sellPrice, dailyBuyLimitNum, dailyBuyLimitPlayerNum, dailySellLimitNum, dailySellLimitPlayerNum, isBuyable, isSellable, isDailyBuyLimit, isDailySellLimit, coinType));
        }
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if (!this.level().isClientSide && player instanceof ServerPlayer) {
            Component customName = this.getCustomName();
            assert customName != null;
            if (!player.hasPermissions(2)) {
                if (customName.getString().equals("농부 상점")) {
                    BaseClass baseClassInstance = getBaseClassInstance(player, "Farmer");
                    if (baseClassInstance == null) {
                        ((ServerPlayer) player).sendSystemMessage(Component.literal("전직을 해야 상점을 열 수 있습니다."));
                        return InteractionResult.SUCCESS;
                    }
                } else if (customName.getString().equals("광부 상점")) {
                    BaseClass baseClassInstance = getBaseClassInstance(player, "Miner");
                    if (baseClassInstance == null) {
                        ((ServerPlayer) player).sendSystemMessage(Component.literal("전직을 해야 상점을 열 수 있습니다."));
                        return InteractionResult.SUCCESS;
                    }
                } else if (customName.getString().equals("나무꾼 상점")) {
                    BaseClass baseClassInstance = getBaseClassInstance(player, "Woodcutter");
                    if (baseClassInstance == null) {
                        ((ServerPlayer) player).sendSystemMessage(Component.literal("전직을 해야 상점을 열 수 있습니다."));
                        return InteractionResult.SUCCESS;
                    }
                } else if (customName.getString().equals("연금술사 상점")) {
                    BaseClass baseClassInstance = getBaseClassInstance(player, "Alchemist");
                    if (baseClassInstance == null) {
                        ((ServerPlayer) player).sendSystemMessage(Component.literal("전직을 해야 상점을 열 수 있습니다."));
                        return InteractionResult.SUCCESS;
                    }
                } else if (customName.getString().equals("요리사 상점")) {
                    BaseClass baseClassInstance = getBaseClassInstance(player, "Chef");
                    if (baseClassInstance == null) {
                        ((ServerPlayer) player).sendSystemMessage(Component.literal("전직을 해야 상점을 열 수 있습니다."));
                        return InteractionResult.SUCCESS;
                    }
                } else if (customName.getString().equals("어부 상점")) {
                    BaseClass baseClassInstance = getBaseClassInstance(player, "Fisherman");
                    if (baseClassInstance == null) {
                        ((ServerPlayer) player).sendSystemMessage(Component.literal("전직을 해야 상점을 열 수 있습니다."));
                        return InteractionResult.SUCCESS;
                    }
                }
                else if (customName.getString().equals("농부 2차 상점")) {
                    BaseClass baseClassInstance = getBaseClassInstance(player, "Farmer");
                    if (baseClassInstance == null || baseClassInstance.getLevel() < 2) {
                        ((ServerPlayer) player).sendSystemMessage(Component.literal("전직을 해야 상점을 열 수 있습니다."));
                        return InteractionResult.SUCCESS;
                    }
                } else if (customName.getString().equals("광부 2차 상점")) {
                    BaseClass baseClassInstance = getBaseClassInstance(player, "Miner");
                    if (baseClassInstance == null || baseClassInstance.getLevel() < 2) {
                        ((ServerPlayer) player).sendSystemMessage(Component.literal("전직을 해야 상점을 열 수 있습니다."));
                        return InteractionResult.SUCCESS;
                    }
                } else if (customName.getString().equals("나무꾼 2차 상점")) {
                    BaseClass baseClassInstance = getBaseClassInstance(player, "Woodcutter");
                    if (baseClassInstance == null || baseClassInstance.getLevel() < 2) {
                        ((ServerPlayer) player).sendSystemMessage(Component.literal("2차 전직을 해야 상점을 열 수 있습니다."));
                        return InteractionResult.SUCCESS;
                    }
                } else if (customName.getString().equals("연금술사 2차 상점")) {
                    BaseClass baseClassInstance = getBaseClassInstance(player, "Alchemist");
                    if (baseClassInstance == null || baseClassInstance.getLevel() < 2) {
                        ((ServerPlayer) player).sendSystemMessage(Component.literal("2차 전직을 해야 상점을 열 수 있습니다."));
                        return InteractionResult.SUCCESS;
                    }
                } else if (customName.getString().equals("요리사 2차 상점")) {
                    BaseClass baseClassInstance = getBaseClassInstance(player, "Chef");
                    if (baseClassInstance == null || baseClassInstance.getLevel() < 2) {
                        ((ServerPlayer) player).sendSystemMessage(Component.literal("2차 전직을 해야 상점을 열 수 있습니다."));
                        return InteractionResult.SUCCESS;
                    }
                } else if (customName.getString().equals("어부 2차 상점")) {
                    BaseClass baseClassInstance = getBaseClassInstance(player, "Fisherman");
                    if (baseClassInstance == null || baseClassInstance.getLevel() < 2) {
                        ((ServerPlayer) player).sendSystemMessage(Component.literal("2차 전직을 해야 상점을 열 수 있습니다."));
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            NetworkHooks.openScreen((ServerPlayer) player, new ShopEntityContainerProvider(this), buf -> buf.writeInt(this.getId()));
            ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ShopEntityDataPacket(this));
            return InteractionResult.SUCCESS;
        }
        return super.interactAt(player, vec, hand);
    }

    private BaseClass getBaseClassInstance(Player player, String baseClassName) {
        String playerName = player.getName().getString();
        return ClassManagement.getClasses(playerName).get(baseClassName);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
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

    public void updatePositionAndRotation(BlockPos pos, float yaw) {
        this.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        this.setYRot(yaw);
        this.setYHeadRot(yaw);
        this.setYBodyRot(yaw);

        if (!this.level().isClientSide) {
            ModNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new ShopEntityPositionPacket(this.getId(), this.getX(), this.getY(), this.getZ(), this.getYRot()));
        }
    }
}
