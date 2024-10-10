package net.jaju.subservermod.commands.village;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.jaju.subservermod.manager.VillageProtectionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class VillageCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("protectvillage")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("name", StringArgumentType.string())
                        .then(Commands.argument("pos1", BlockPosArgument.blockPos())
                                .then(Commands.argument("pos2", BlockPosArgument.blockPos())
                                        .then(Commands.argument("protectable", BoolArgumentType.bool())
                                                .executes(context -> {
                                            String name = StringArgumentType.getString(context, "name");
                                            BlockPos pos1 = BlockPosArgument.getLoadedBlockPos(context, "pos1");
                                            BlockPos pos2 = BlockPosArgument.getLoadedBlockPos(context, "pos2");
                                            boolean protectable = BoolArgumentType.getBool(context, "protectable");

                                            VillageProtectionManager.addVillage(name, pos1, pos2, protectable);

                                            context.getSource().sendSuccess(() -> Component.literal("Protected village " + name + " has been created!"), true);
                                            return 1;
                                        }))))));
    }
}