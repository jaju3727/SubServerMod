package net.jaju.subservermod.items;

import net.jaju.subservermod.network.ModNetworking;
import net.jaju.subservermod.util.CoinData;
import net.jaju.subservermod.manager.CoinManager;
import net.jaju.subservermod.network.coinsystem.CoinDataServerSyncPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class CoinItem extends Item {
    private String coinType;

    public CoinItem(Properties properties, String coinType) {
        super(properties);
        this.coinType = coinType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            CoinData coinData = CoinManager.getCoinData((ServerPlayer) player);

            int amount = getAmountFromLore(itemStack);
            if (amount == 0) {
                amount = 1;
            }
            int count = player.isShiftKeyDown() ? itemStack.getCount() : 1;
            amount = count * amount;

            switch (coinType) {
                case "sub_coin" -> coinData.setSubcoin(coinData.getSubcoin() + amount);
                case "chef_coin" -> coinData.setChefcoin(coinData.getChefcoin() + amount);
                case "farmer_coin" -> coinData.setFarmercoin(coinData.getFarmercoin() + amount);
                case "fisherman_coin" -> coinData.setFishermancoin(coinData.getFishermancoin() + amount);
                case "alchemist_coin" -> coinData.setAlchemistcoin(coinData.getAlchemistcoin() + amount);
                case "miner_coin" -> coinData.setMinercoin(coinData.getMinercoin() + amount);
                case "woodcutter_coin" -> coinData.setWoodcuttercoin(coinData.getWoodcuttercoin() + amount);
            }

            coinData.saveToPlayer((ServerPlayer) player);
            ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new CoinDataServerSyncPacket(coinData));
            player.sendSystemMessage(Component.literal(coinType + "이(가) " + amount + " 추가되었습니다!"));

            itemStack.shrink(count);
            return InteractionResultHolder.sidedSuccess(itemStack, false);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    private int getAmountFromLore(ItemStack itemStack) {
        List<Component> lore = itemStack.getTooltipLines(null, TooltipFlag.Default.NORMAL);

        for (Component line : lore) {
            String text = line.getString();
            if (text.endsWith("원")) {
                try {
                    return Integer.parseInt(text.replace("원", "").trim());
                } catch (NumberFormatException e) {
                    // Ignore and continue checking other lore lines
                }
            }
        }
        return 0; // Default amount if no valid lore is found
    }
}
