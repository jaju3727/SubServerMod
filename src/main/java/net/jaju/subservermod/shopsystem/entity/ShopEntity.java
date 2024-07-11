package net.jaju.subservermod.shopsystem.entity;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.shopsystem.ShopItem;
import net.jaju.subservermod.shopsystem.network.ShopEntityDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        this.setCustomName(Component.literal(name));  // 엔티티의 커스텀 네임 설정
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

        ListTag shopItemsTag = new ListTag();
        for (ShopItem shopItem : shopItems) {
            CompoundTag itemTag = new CompoundTag();
            CompoundTag itemStackTag = new CompoundTag();
            shopItem.getItemStack().save(itemStackTag);
            itemTag.put("ItemStack", itemStackTag); // ItemStack 전체를 저장
            itemTag.putInt("BuyPrice", shopItem.getBuyPrice());
            itemTag.putInt("SellPrice", shopItem.getSellPrice());
            itemTag.putBoolean("IsBuyable", shopItem.getIsBuyable());
            itemTag.putBoolean("IsSellable", shopItem.getIsSellable());

            shopItemsTag.add(itemTag);
        }
        compound.put("ShopItems", shopItemsTag);

        if (this.hasCustomName()) {
            compound.putString("CustomName", this.getCustomName().getString()); // 이름을 JSON이 아닌 문자열로 저장
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSkinPlayerName(compound.getString("SkinPlayerName"), compound.getString("CustomName"));

        shopItems.clear();
        ListTag shopItemsTag = compound.getList("ShopItems", Tag.TAG_COMPOUND);
        for (Tag tag : shopItemsTag) {
            CompoundTag itemTag = (CompoundTag) tag;
            ItemStack itemStack = ItemStack.of(itemTag.getCompound("ItemStack")); // ItemStack 전체를 불러오기
            int buyPrice = itemTag.getInt("BuyPrice");
            int sellPrice = itemTag.getInt("SellPrice");
            boolean isBuyable = itemTag.getBoolean("IsBuyable");
            boolean isSellable = itemTag.getBoolean("IsSellable");
            shopItems.add(new ShopItem(itemStack, buyPrice, sellPrice, isBuyable, isSellable));
        }

        if (compound.contains("CustomName", 8)) {
            this.setCustomName(Component.literal(compound.getString("CustomName"))); // JSON이 아닌 문자열로부터 이름을 설정
        }
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if (!this.level().isClientSide && player instanceof ServerPlayer) {
            NetworkHooks.openScreen((ServerPlayer) player, new ShopEntityContainerProvider(this), buf -> buf.writeInt(this.getId()));
            ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ShopEntityDataPacket(this));
            return InteractionResult.SUCCESS;
        }
        return super.interactAt(player, vec, hand);
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
}
