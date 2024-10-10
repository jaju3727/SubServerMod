package net.jaju.subservermod.items;

import net.jaju.subservermod.manager.ClassManager;
import net.jaju.subservermod.subclass.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FormerItem extends Item {

    private final short level;
    private final String className;

    public FormerItem(Properties properties, short level, String className) {
        super(properties);
        this.level = level;
        this.className = className;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer) player;
                setPlayerClass(serverPlayer, this.className, this.level);
                player.displayClientMessage(Component.literal(this.className + " " + this.level + "로 전직되었습니다."), true);
                ItemStack itemStack = player.getItemInHand(hand);
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                return InteractionResultHolder.success(itemStack);
            }
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    private void setPlayerClass(ServerPlayer player, String className, int level) {
        String playerName = player.getName().getString();

        BaseClass playerClass;
        switch (className.toLowerCase()) {
            case "farmer":
                playerClass = new Farmer(level, playerName);
                playerClass.performSkill("give", player);
                break;
            case "miner":
                playerClass = new Miner(level, playerName);
                playerClass.performSkill("give", player);
                break;
            case "alchemist":
                playerClass = new Alchemist(level, playerName);
                playerClass.performSkill("give", player);
                break;
            case "woodcutter":
                playerClass = new Woodcutter(level, playerName);
                playerClass.performSkill("give", player);
                break;
            case "chef":
                playerClass = new Chef(level, playerName);
                playerClass.performSkill("give", player);
                break;
            case "fisherman":
                playerClass = new Fisherman(level, playerName);
                playerClass.performSkill("give", player);
                break;
            default:
                return;
        }

        if (level == 1) {
            ClassManager.addClass(playerName, playerClass);
            return;
        }

        ClassManager.setClass(playerName, playerClass);
    }
}
