package me.stephenbullough.immafreedommod.commands;

import me.stephenbullough.immafreedommod.immafreedommod;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ifm_CommandHandler
{
    public static final String COMMAND_PATH = ifm_Command.class.getPackage().getName(); // "me.stephenbullough.immafreedommod.commands";
    public static final String COMMAND_PREFIX = "Command_";

    public static boolean handleCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        final Player playerSender;
        final boolean senderIsConsole;

        if (sender instanceof Player)
        {
            senderIsConsole = false;
            playerSender = (Player) sender;

            TFM_Log.info(String.format("[PLAYER_COMMAND] %s (%s): /%s %s",
                    playerSender.getName(),
                    ChatColor.stripColor(playerSender.getDisplayName()),
                    commandLabel,
                    StringUtils.join(args, " ")), true);
        }
        else
        {
            senderIsConsole = true;
            playerSender = null;

            immafreedommod.plugin.info(String.format("[CONSOLE_COMMAND] %s: /%s %s",
                    sender.getName(),
                    commandLabel,
                    StringUtils.join(args, " ")), true);
        }

        final ifm_Command dispatcher;
        try
        {
            final ClassLoader classLoader = immafreedommod.class.getClassLoader();
            dispatcher = (ifm_Command) classLoader.loadClass(String.format("%s.%s%s",
                    COMMAND_PATH,
                    COMMAND_PREFIX,
                    cmd.getName().toLowerCase())).newInstance();
            dispatcher.setup(immafreedommod.plugin, sender, dispatcher.getClass());
        }
        catch (Exception ex)
        {
            immafreedommod.plugin.log("Could not load command: " + cmd.getName());
            immafreedommod.plugin.log(ex);

            sender.sendMessage(ChatColor.RED + "Command Error! Could not load command: " + cmd.getName());
            return true;
        }

        if (!dispatcher.senderHasPermission())
        {
            sender.sendMessage(ifm_Command.MSG_NO_PERMS);
            return true;
        }

        try
        {
            return dispatcher.run(sender, playerSender, cmd, commandLabel, args, senderIsConsole);
        }
        catch (Exception ex)
        {
            immafreedommod.plugin.info("Command Error: " + commandLabel);
            immafreedommod.plugin.info(ex);
            sender.sendMessage(ChatColor.RED + "Command Error: " + ex.getMessage());
        }

        return true;
    }
}
