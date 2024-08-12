package net.jaju.subservermod.subclass.skill.woodcutter.woodcuttingunion;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.block.ModBlockEntities;
import net.jaju.subservermod.item.ModItem;
import net.jaju.subservermod.subclass.network.GaugeSendToClientPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WoodcuttingUnionBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(17) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public boolean flag = false;
    private int minItemNum = Integer.MAX_VALUE;
    private int updateTimer = 0;
    private static final List<Item> ALL_PLANKS = List.of(
            Items.OAK_PLANKS, Items.SPRUCE_PLANKS, Items.BIRCH_PLANKS, Items.JUNGLE_PLANKS,
            Items.ACACIA_PLANKS, Items.DARK_OAK_PLANKS, Items.CRIMSON_PLANKS, Items.WARPED_PLANKS,
            Items.MANGROVE_PLANKS, Items.BAMBOO_PLANKS, Items.CHERRY_PLANKS
    );
    private static final List<Item> ALL_SLABS = List.of(
            Items.OAK_SLAB, Items.SPRUCE_SLAB, Items.BIRCH_SLAB, Items.JUNGLE_SLAB,
            Items.ACACIA_SLAB, Items.DARK_OAK_SLAB, Items.CRIMSON_SLAB, Items.WARPED_SLAB,
            Items.MANGROVE_SLAB, Items.BAMBOO_SLAB, Items.CHERRY_FENCE
    );
    private static final List<Item> ALL_FENCES = List.of(
            Items.OAK_FENCE, Items.SPRUCE_FENCE, Items.BIRCH_FENCE, Items.JUNGLE_FENCE,
            Items.ACACIA_FENCE, Items.DARK_OAK_FENCE, Items.CRIMSON_FENCE, Items.WARPED_FENCE,
            Items.MANGROVE_FENCE, Items.BAMBOO_FENCE, Items.CHERRY_FENCE
    );
    private static final Map<List<Item>, ItemStack> recipes = new HashMap<>();
    private int gaugeX = 100;

    static {
        for (int i = 0; i < 11; i++) {
            recipes.put(List.of(
                    ALL_PLANKS.get(i), Items.AIR, Items.AIR, Items.AIR,
                    ALL_PLANKS.get(i), Items.AIR, Items.AIR, Items.AIR,
                    ALL_SLABS.get(i), ALL_SLABS.get(i), ALL_SLABS.get(i), ALL_SLABS.get(i),
                    ALL_FENCES.get(i), Items.AIR, Items.AIR, ALL_FENCES.get(i)
            ), new ItemStack(ModItem.WOODEN_CHAIR.get()));
            recipes.put(List.of(
                    ALL_SLABS.get(i), ALL_SLABS.get(i), ALL_SLABS.get(i), ALL_SLABS.get(i),
                    ALL_SLABS.get(i), ALL_SLABS.get(i), ALL_SLABS.get(i), ALL_SLABS.get(i),
                    ALL_FENCES.get(i), Items.AIR, Items.AIR, ALL_FENCES.get(i),
                    ALL_FENCES.get(i), Items.AIR, Items.AIR, ALL_FENCES.get(i)
            ), new ItemStack(ModItem.WOODEN_TABLE.get()));
            recipes.put(List.of(
                    Items.AIR, Items.AIR, Items.AIR, Items.AIR,
                    ALL_SLABS.get(i), ALL_SLABS.get(i), ALL_SLABS.get(i), ALL_SLABS.get(i),
                    ALL_SLABS.get(i), ALL_SLABS.get(i), ALL_SLABS.get(i), ALL_SLABS.get(i),
                    Items.AIR, Items.AIR, Items.AIR, Items.AIR
            ), new ItemStack(ModItem.WOODEN_CUTTING_BOARD.get()));
            recipes.put(List.of(
                    Items.AIR, ALL_SLABS.get(i), ALL_SLABS.get(i), Items.AIR,
                    Items.AIR, ALL_SLABS.get(i), ALL_SLABS.get(i), Items.AIR,
                    Items.AIR, ALL_SLABS.get(i), ALL_SLABS.get(i), Items.AIR,
                    Items.AIR, ALL_SLABS.get(i), ALL_SLABS.get(i), Items.AIR
            ), new ItemStack(ModItem.WOODEN_DRAWER.get()));
            recipes.put(List.of(
                    ALL_SLABS.get(i), ALL_SLABS.get(i), ALL_SLABS.get(i), ALL_SLABS.get(i),
                    ALL_SLABS.get(i), ALL_SLABS.get(i), ALL_SLABS.get(i), ALL_SLABS.get(i),
                    Items.STICK, Items.AIR, Items.AIR, Items.STICK,
                    Items.AIR, Items.AIR, Items.AIR, Items.AIR
            ), new ItemStack(ModItem.WOODEN_SHELF.get()));
        }
    }


    public void updateRecipeName(ItemStack resultItemStack, Player player) {
        if (player != null) {
            Inventory playerInventory = player.getInventory();
            HashMap<Item, Integer> playerItems = new HashMap<>();

            for (int i = 0; i < 16; i++) {
                ItemStack playerStack = itemHandler.getStackInSlot(i);
                playerInventory.add(playerStack);
                itemHandler.setStackInSlot(i, new ItemStack(Items.AIR));
            }

            for (int i = 0; i < playerInventory.getContainerSize(); i++) {
                ItemStack playerStack = playerInventory.getItem(i);
                Item playerItem = playerStack.getItem();
                playerItems.put(playerItem, playerItems.getOrDefault(playerItem, 0) + playerStack.getCount());
            }

            for (var entry: recipes.entrySet()) {
                if (resultItemStack.getItem().equals(entry.getValue().getItem())) {
                    HashMap<Item, Integer> ingredientItems = new HashMap<>();
                    List<Item> ingredientList = entry.getKey();
                    boolean flag = true;
                    for (Item item : ingredientList) {
                        if (item.equals(Items.AIR)) continue;
                        ingredientItems.put(item, ingredientItems.getOrDefault(item, 0) + 1);
                    }
                    for (var ingredientEntry: ingredientItems.entrySet()) {
                        if (playerItems.getOrDefault(ingredientEntry.getKey(), 0) < ingredientEntry.getValue()) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        int craftableAmount = Integer.MAX_VALUE;
                        for (var entry1 : ingredientItems.entrySet()) {
                            craftableAmount = Math.min(craftableAmount, playerItems.getOrDefault(entry1.getKey(), 0) / entry1.getValue());
                        }

                        craftableAmount = Math.min(craftableAmount, 64);
                        for (int i = 0; i < ingredientList.size(); i++) {
                            if (!ingredientList.get(i).equals(Items.AIR)) {
                                itemHandler.setStackInSlot(i, new ItemStack(ingredientList.get(i), craftableAmount));
                            }
                        }

                        for (var entry1 : ingredientItems.entrySet()) {
                            int amountToRemove = craftableAmount * entry1.getValue();
                            for (int i = 0; i < playerInventory.getContainerSize() && amountToRemove > 0; i++) {
                                ItemStack playerStack = playerInventory.getItem(i);
                                if (playerStack.getItem() == entry1.getKey()) {
                                    int removeCount = Math.min(playerStack.getCount(), amountToRemove);
                                    playerStack.shrink(removeCount);
                                    amountToRemove -= removeCount;
                                }
                            }
                        }
                        return;
                    }
                }
            }

        }
    }

    public WoodcuttingUnionBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WOODCUTTINGUNION_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Woodcutting");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new WoodcuttingUnionContainer(id, playerInventory, this.getBlockPos());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WoodcuttingUnionBlockEntity entity) {
        if (!level.isClientSide) {
            entity.updateResultSlot();

        }
    }

    public void updateGaugeX(int gaugeX) {
        this.gaugeX = gaugeX;
    }

    private void updateResultSlot() {
        updateTimer++;
        List<Item> itemStacks = new ArrayList<>();
        int itemNum;
        int tempMinNum = Integer.MAX_VALUE;
        for (int i = 0; i < 16; i++) {
            itemStacks.add(itemHandler.getStackInSlot(i).getItem());
            if (!itemHandler.getStackInSlot(i).isEmpty()) itemNum = itemHandler.getStackInSlot(i).getCount();
            else itemNum = Integer.MAX_VALUE;
            if (tempMinNum > itemNum) {
                tempMinNum = itemNum;
            }
        }
        if (tempMinNum != minItemNum) {
            minItemNum = tempMinNum;
            gaugeX = 100 + (minItemNum/4) * 10;
        }
        flag = recipes.containsKey(itemStacks);
        sendGaugeUpdateToClient();

        if (flag) {
            if (gaugeX <= 0) {
                ItemStack resultItem = recipes.get(itemStacks);
                itemHandler.setStackInSlot(16, new ItemStack(resultItem.getItem(), itemHandler.getStackInSlot(16).getCount() + minItemNum));
                for (int i = 0; i < 16; i++) {
                    itemHandler.setStackInSlot(i, new ItemStack(itemHandler.getStackInSlot(i).getItem(), itemHandler.getStackInSlot(i).getCount() - minItemNum));
                }
                flag = false;
            } else {
                if (updateTimer%5 == 0 && gaugeX < 100 + (minItemNum/4) * 10) {
                    gaugeX++;
                }
            }
        }
    }

    private void sendGaugeUpdateToClient() {
        if (level != null && !level.isClientSide) {
            level.players().forEach(player -> {
                if (player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof WoodcuttingUnionContainer container && container.getBlockEntity() == this) {
                    ModNetworking.sendToClient(new GaugeSendToClientPacket(gaugeX, flag, minItemNum), serverPlayer);
                    Subservermod.LOGGER.info(gaugeX + "   " + flag);
                }
            });
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}
