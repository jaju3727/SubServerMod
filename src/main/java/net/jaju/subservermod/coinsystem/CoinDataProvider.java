package net.jaju.subservermod.coinsystem;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CoinDataProvider implements ICapabilitySerializable<CompoundTag> {
    private final CoinData coinData = new CoinData();
    private final LazyOptional<CoinData> coinDataOptional = LazyOptional.of(() -> coinData);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CoinHud.COIN_CAPABILITY ? coinDataOptional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return coinData.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        coinData.deserializeNBT(nbt);
    }
}
