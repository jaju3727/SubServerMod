package net.jaju.subservermod.mailbox;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;

@Mod.EventBusSubscriber
public class MailboxCommand {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("mailbox")
                .then(Commands.argument("players", EntityArgument.players())
                        .executes(context -> {
                            ServerPlayer sender = context.getSource().getPlayerOrException();
                            Collection<ServerPlayer> receivers = EntityArgument.getPlayers(context, "players");

                            if (!receivers.isEmpty()) {
                                ItemStack itemStack = sender.getMainHandItem();
                                if (!itemStack.isEmpty()) {
                                    for (ServerPlayer receiver : receivers) {
                                        MailboxManager.getInstance().addItemToMailbox(receiver.getUUID(), itemStack.copy());
                                        context.getSource().sendSuccess(() -> Component.literal("아이템이 " + receiver.getName().getString() + "의 우편함에 저장되었습니다."), true);
                                    }
                                } else {
                                    context.getSource().sendFailure(Component.literal("손에 든 아이템이 없습니다."));
                                }
                            } else {
                                context.getSource().sendFailure(Component.literal("플레이어를 찾을 수 없습니다."));
                            }
                            return 1;
                        })));
    }
}
