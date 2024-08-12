package net.jaju.subservermod.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
public class HelpCommand {
    private static final List<String> playerNameList = Arrays.asList("Sub_I", "yangpadaepa", "kimkanghyeon", "heewon615", "pigwar123", "JONJAEGAM", "ZZINDDANYEON","Dev");

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("문의")
                        .then(Commands.argument("내용", StringArgumentType.greedyString())
                                .executes(ctx -> sendHelpMessage(ctx.getSource(), StringArgumentType.getString(ctx, "내용"), ctx.getSource().getPlayer().getName().getString())))
        );
    }

    private static int sendHelpMessage(CommandSourceStack source, String message, String name) {
        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            if (playerNameList.contains(player.getGameProfile().getName())) {
                player.sendSystemMessage(Component.literal("§5"+name+" §f문의 내용: " + message));
            }
        }
        return 1;
    }
}
