package net.jaju.subservermod.shopsystem;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.jaju.subservermod.shopsystem.entity.ModEntities;
import net.jaju.subservermod.shopsystem.entity.ShopEntity;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;

public class ShopCommands {

    private static final List<String> COIN_TYPES = Arrays.asList(
            "sub_coin", "chef_coin", "farmer_coin", "fisherman_coin",
            "alchemist_coin", "miner_coin", "woodcutter_coin"
    );

    private static final SuggestionProvider<CommandSourceStack> COIN_TYPE_SUGGESTIONS =
            (context, builder) -> net.minecraft.commands.SharedSuggestionProvider.suggest(COIN_TYPES, builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(Commands.literal("shop")
                .then(Commands.literal("add")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("buyPrice", IntegerArgumentType.integer())
                                        .then(Commands.argument("sellPrice", IntegerArgumentType.integer())
                                                .then(Commands.argument("dailyBuyLimitNum", IntegerArgumentType.integer())
                                                        .then(Commands.argument("dailySellLimitNum", IntegerArgumentType.integer())
                                                                .then(Commands.argument("isBuyable", BoolArgumentType.bool())
                                                                        .then(Commands.argument("isSellable", BoolArgumentType.bool())
                                                                                .then(Commands.argument("isDailyBuyLimit", BoolArgumentType.bool())
                                                                                        .then(Commands.argument("isDailySellLimit", BoolArgumentType.bool())
                                                                                            .then(Commands.argument("coinType", StringArgumentType.string())
                                                                                                    .suggests(COIN_TYPE_SUGGESTIONS)
                                                                                                    .executes(context -> {
                                                                                                        String name = StringArgumentType.getString(context, "name");
                                                                                                        ItemStack itemStack = context.getSource().getPlayerOrException().getMainHandItem();
                                                                                                        int buyPrice = IntegerArgumentType.getInteger(context, "buyPrice");
                                                                                                        int sellPrice = IntegerArgumentType.getInteger(context, "sellPrice");
                                                                                                        int dailyBuyLimitNum = IntegerArgumentType.getInteger(context, "dailyBuyLimitNum");
                                                                                                        int dailySellLimitNum = IntegerArgumentType.getInteger(context, "dailySellLimitNum");
                                                                                                        boolean isBuyable = BoolArgumentType.getBool(context, "isBuyable");
                                                                                                        boolean isSellable = BoolArgumentType.getBool(context, "isSellable");
                                                                                                        boolean isDailyBuyLimit = BoolArgumentType.getBool(context, "isDailyBuyLimit");
                                                                                                        boolean isDailySellLimit = BoolArgumentType.getBool(context, "isDailySellLimit");
                                                                                                        String coinType = StringArgumentType.getString(context, "coinType");

                                                                                                        ServerLevel world = context.getSource().getLevel();
                                                                                                        int addedEntities = addItemsToShopEntities(world, name, itemStack, buyPrice, sellPrice, dailyBuyLimitNum, dailySellLimitNum, isBuyable, isSellable, isDailyBuyLimit, isDailySellLimit, coinType);

                                                                                                        if (addedEntities > 0) {
                                                                                                            context.getSource().sendSuccess(() -> Component.literal("Added item to " + addedEntities + " shop entities with name: " + name), true);
                                                                                                        } else {
                                                                                                            context.getSource().sendFailure(Component.literal("No shop entities found with name: " + name));
                                                                                                        }
                                                                                                        return 1;
                                                                                                    }))))))))))))
                .then(Commands.literal("set")
                        .then(Commands.argument("id", IntegerArgumentType.integer())
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .then(Commands.argument("buyPrice", IntegerArgumentType.integer())
                                                .then(Commands.argument("sellPrice", IntegerArgumentType.integer())
                                                        .then(Commands.argument("dailyBuyLimitNum", IntegerArgumentType.integer())
                                                                .then(Commands.argument("dailySellLimitNum", IntegerArgumentType.integer())
                                                                        .then(Commands.argument("isBuyable", BoolArgumentType.bool())
                                                                                .then(Commands.argument("isSellable", BoolArgumentType.bool())
                                                                                        .then(Commands.argument("isDailyBuyLimit", BoolArgumentType.bool())
                                                                                                .then(Commands.argument("isDailySellLimit", BoolArgumentType.bool())
                                                                                                        .then(Commands.argument("isDailySellLimit", BoolArgumentType.bool())
                                                                                                                .then(Commands.argument("coinType", StringArgumentType.string())
                                                                                                                .executes(context -> {
                                                                                                                    String name = StringArgumentType.getString(context, "name");
                                                                                                                    ItemStack itemStack = context.getSource().getPlayerOrException().getMainHandItem();
                                                                                                                    int buyPrice = IntegerArgumentType.getInteger(context, "buyPrice");
                                                                                                                    int sellPrice = IntegerArgumentType.getInteger(context, "sellPrice");
                                                                                                                    int dailyBuyLimitNum = IntegerArgumentType.getInteger(context, "dailyBuyLimitNum");
                                                                                                                    int dailySellLimitNum = IntegerArgumentType.getInteger(context, "dailySellLimitNum");
                                                                                                                    int id = IntegerArgumentType.getInteger(context, "id");
                                                                                                                    boolean isBuyable = BoolArgumentType.getBool(context, "isBuyable");
                                                                                                                    boolean isSellable = BoolArgumentType.getBool(context, "isSellable");
                                                                                                                    boolean isDailyBuyLimit = BoolArgumentType.getBool(context, "isDailyBuyLimit");
                                                                                                                    boolean isDailySellLimit = BoolArgumentType.getBool(context, "isDailySellLimit");
                                                                                                                    String coinType = StringArgumentType.getString(context, "coinType");

                                                                                                                    ServerLevel world = context.getSource().getLevel();
                                                                                                                    int setEntities = setItemsToShopEntities(world, name, itemStack, buyPrice, sellPrice, dailyBuyLimitNum, dailySellLimitNum, id, isBuyable, isSellable, isDailyBuyLimit, isDailySellLimit, coinType);

                                                                                                                    if (setEntities > 0) {
                                                                                                                        context.getSource().sendSuccess(() -> Component.literal("Set item to " + setEntities + " shop entities with name: " + name), true);
                                                                                                                    } else {
                                                                                                                        context.getSource().sendFailure(Component.literal("No shop entities found with name: " + name));
                                                                                                                    }
                                                                                                                    return 1;
                                                                                                                }))))))))))))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");
                                    ServerLevel world = context.getSource().getLevel();
                                    int removedEntities = removeShopEntitiesByName(world, name);

                                    if (removedEntities > 0) {
                                        context.getSource().sendSuccess(() -> Component.literal("Removed " + removedEntities + " shop entities with name: " + name), true);
                                    } else {
                                        context.getSource().sendFailure(Component.literal("No shop entities found with name: " + name));
                                    }
                                    return 1;
                                })
                                .then(Commands.argument("startId", IntegerArgumentType.integer())
                                        .then(Commands.argument("endId", IntegerArgumentType.integer())
                                            .executes(context -> {
                                                int startId = IntegerArgumentType.getInteger(context, "startId");
                                                int endId = IntegerArgumentType.getInteger(context, "endId");
                                                String name = StringArgumentType.getString(context, "name");
                                                ServerLevel world = context.getSource().getLevel();
                                                int removedEntities = removeShopEntitiesByIdAndName(world, startId, endId, name);

                                                if (removedEntities > 0) {
                                                    context.getSource().sendSuccess(() -> Component.literal("Removed " + removedEntities + " shop entities with ID: " + startId + "~" + endId + " and name: " + name), true);
                                                } else {
                                                    context.getSource().sendFailure(Component.literal("No shop entities found with ID: " + startId + "~" + endId + " and name: " + name));
                                                }

                                                return 1;
                                            })))))
                .then(Commands.literal("list")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");
                                    ServerLevel world = context.getSource().getLevel();
                                    return listShopItems(world, name, context.getSource());
                                })))
                .then(Commands.literal("move")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(context -> {
                                            CommandSourceStack source = context.getSource();
                                            String name = StringArgumentType.getString(context, "name");
                                            BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
                                            ServerLevel world = source.getLevel();
                                            boolean found = false;

                                            List<ShopEntity> entities = (List<ShopEntity>) world.getEntities(EntityTypeTest.forClass(ShopEntity.class), entity -> entity.hasCustomName() && entity.getCustomName().getString().equals(name));

                                            for (ShopEntity entity : entities) {
                                                entity.moveTo(pos.getX(), pos.getY(), pos.getZ());
                                                source.sendSuccess(() -> Component.literal("Moved entity " + name + " to " + pos.toShortString()), true);
                                                found = true;
                                                break;
                                            }

                                            if (!found) {
                                                source.sendFailure(Component.literal("No entity found with name: " + name));
                                            }
                                            return 1;
                                }))))
                .then(Commands.literal("create")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("skinName", StringArgumentType.string())
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    String name = StringArgumentType.getString(context, "name");
                                    String skinName = StringArgumentType.getString(context, "skinName");

                                    ServerLevel world = source.getLevel();
                                    Vec3 position = source.getPosition();
                                    ShopEntity entity = ModEntities.CUSTOM_ENTITY.get().create(world);

                                    if (entity != null) {
                                        entity.setSkinPlayerName(skinName, name);
                                        entity.moveTo(position.x, position.y, position.z, source.getRotation().y, 0);
                                        world.addFreshEntity(entity);
                                        source.sendSuccess(() -> Component.literal("Spawned shop entity with player skin: " + skinName), true);
                                    } else {
                                        source.sendFailure(Component.literal("Failed to create shop entity"));
                                    }
                                    return 1;
                                }))))
        );
    }

    private static int removeShopEntitiesByName(ServerLevel world, String name) {
        int removedCount = 0;
        List<ShopEntity> entities = (List<ShopEntity>) world.getEntities(EntityTypeTest.forClass(ShopEntity.class), entity -> entity.hasCustomName() && entity.getCustomName().getString().equals(name));
        for (ShopEntity entity : entities ) {
            if (entity.getCustomName() != null && entity.getCustomName().getString().equals(name)) {
                entity.remove(Entity.RemovalReason.DISCARDED);
                removedCount++;
            }
        }
        return removedCount;
    }

    private static int removeShopEntitiesByIdAndName(ServerLevel world, int startId, int endId, String name) {
        int removedCount = 0;
        List<ShopEntity> entities = (List<ShopEntity>) world.getEntities(EntityTypeTest.forClass(ShopEntity.class), entity -> entity.hasCustomName() && entity.getCustomName().getString().equals(name));
        for (ShopEntity entity : entities) {
            if (entity.getCustomName() != null && entity.getCustomName().getString().equals(name)) {
                List<ShopItem> items = entity.getShopItems();
                if (startId >= 0 && startId <= endId && endId < items.size()) {
                    for (int i = startId; i <= endId; i++) {
                        entity.getShopItems().remove(i);
                        removedCount++;
                    }
                }
            }
        }
        return removedCount;
    }

    private static int addItemsToShopEntities(ServerLevel world, String name, ItemStack itemStack, int buyPrice, int sellPrice, int dailyBuyLimitNum, int dailySellLimitNum, boolean isBuyable, boolean isSellable, boolean isDailyBuyLimit, boolean isDailySellLimit, String coinType) {
        int addedCount = 0;
        List<ShopEntity> entities = (List<ShopEntity>) world.getEntities(EntityTypeTest.forClass(ShopEntity.class), entity -> entity.hasCustomName() && entity.getCustomName().getString().equals(name));
        for (ShopEntity entity : entities) {
            if (entity.getCustomName() != null && entity.getCustomName().getString().equals(name)) {
                entity.getShopItems().add(new ShopItem(itemStack, buyPrice, sellPrice, dailyBuyLimitNum, dailyBuyLimitNum, dailySellLimitNum , dailySellLimitNum, isBuyable, isSellable, isDailyBuyLimit, isDailySellLimit, coinType));
                addedCount++;
            }
        }
        return addedCount;
    }

    private static int setItemsToShopEntities(ServerLevel world, String name, ItemStack itemStack, int buyPrice, int sellPrice, int dailyBuyLimitNum, int dailySellLimitNum, int id, boolean isBuyable, boolean isSellable, boolean isDailyBuyLimit, boolean isDailySellLimit, String coinType) {
        int addedCount = 0;
        List<ShopEntity> entities = (List<ShopEntity>) world.getEntities(EntityTypeTest.forClass(ShopEntity.class), entity -> entity.hasCustomName() && entity.getCustomName().getString().equals(name));
        for (ShopEntity entity : entities) {
            if (entity.getCustomName() != null && entity.getCustomName().getString().equals(name)) {
                entity.getShopItems().set(id, new ShopItem(itemStack, buyPrice, sellPrice, dailyBuyLimitNum, dailyBuyLimitNum, dailySellLimitNum, dailySellLimitNum, isBuyable, isSellable, isDailyBuyLimit, isDailySellLimit, coinType));
                addedCount++;
            }
        }
        return addedCount;
    }

    private static int listShopItems(ServerLevel world, String name, CommandSourceStack source) {
        List<ShopEntity> entities = (List<ShopEntity>) world.getEntities(EntityTypeTest.forClass(ShopEntity.class), entity -> entity.hasCustomName() && entity.getCustomName().getString().equals(name));
        for (ShopEntity entity : entities) {
            if (entity.getCustomName() != null && entity.getCustomName().getString().equals(name)) {
                List<ShopItem> shopItems = entity.getShopItems();
                if (shopItems.isEmpty()) {
                    source.sendFailure(Component.literal("No items found in shop entity with name: " + name));
                } else {
                    source.sendSuccess(() -> Component.literal("Items in shop entity with name: " + name + ":"), false);
                    for (ShopItem item : shopItems) {
                        source.sendSuccess(() -> Component.literal("- " + item.getItemStack().getHoverName().getString() + ": Buy for " + item.getBuyPrice() + ", Sell for " + item.getSellPrice()), false);
                    }
                }
                return 1;
            }
        }
        source.sendFailure(Component.literal("No shop entities found with name: " + name));
        return 0;
    }
}
