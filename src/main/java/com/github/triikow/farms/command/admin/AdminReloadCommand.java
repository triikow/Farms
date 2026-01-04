package com.github.triikow.farms.command.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class AdminReloadCommand {

    private final JavaPlugin plugin;

    public AdminReloadCommand(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("reload")
                .requires(src -> src.getSender().hasPermission("farms.admin.reload"))
                .executes(this::execute)
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        plugin.reloadConfig();

        String messagePrefix = plugin.getConfig().getString(
                "messages.prefix",
                "<yellow>[Farms]</yellow> "
        );

        String reloaded = plugin.getConfig().getString(
                "messages.admin.reload.success",
                "<green>Configuration reloaded.</green>"
        );

        sender.sendRichMessage(messagePrefix + reloaded);

        return Command.SINGLE_SUCCESS;
    }
}
