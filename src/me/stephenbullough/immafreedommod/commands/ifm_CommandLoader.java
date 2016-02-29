package me.stephenbullough.immafreedommod.commands;

import java.io.IOException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import me.stephenbullough.immafreedommod.ifm_util;
import me.stephenbullough.immafreedommod.immafreedommod;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

public class ifm_CommandLoader
{
    public static final Pattern COMMAND_PATTERN;
    private static final List<ifm_CommandInfo> COMMAND_LIST;

    static
    {
        COMMAND_PATTERN = Pattern.compile(ifm_CommandHandler.COMMAND_PATH.replace('.', '/') + "/(" + ifm_CommandHandler.COMMAND_PREFIX + "[^\\$]+)\\.class");
        COMMAND_LIST = new ArrayList<ifm_CommandInfo>();
    }

    private ifm_CommandLoader()
    {
        throw new AssertionError();
    }

    public static void scan()
    {
        CommandMap commandMap = getCommandMap();
        if (commandMap == null)
        {
            immafreedommod.logger.log("Error loading commandMap.");
            return;
        }
        COMMAND_LIST.clear();
        COMMAND_LIST.addAll(getCommands());

        for (ifm_CommandInfo commandInfo : COMMAND_LIST)
        {
            ifm_DynamicCommand dynamicCommand = new TFM_DynamicCommand(commandInfo);

            Command existing = commandMap.getCommand(dynamicCommand.getName());
            if (existing != null)
            {
                unregisterCommand(existing, commandMap);
            }

            commandMap.register(immafreedommod.plugin.getDescription().getName(), dynamicCommand);
        }

        immafreedommod.plugin.logger.info("TFM commands loaded.");
    }

    public immafreedommod.plugin.logger void unregisterCommand(String commandName)
    {
        CommandMap commandMap = getCommandMap();
        if (commandMap != null)
        {
            Command command = commandMap.getCommand(commandName.toLowerCase());
            if (command != null)
            {
                unregisterCommand(command, commandMap);
            }
        }
    }

    public static void unregisterCommand(Command command, CommandMap commandMap)
    {
        try
        {
            command.unregister(commandMap);
            HashMap<String, Command> knownCommands = getKnownCommands(commandMap);
            if (knownCommands != null)
            {
                knownCommands.remove(command.getName());
                for (String alias : command.getAliases())
                {
                    knownCommands.remove(alias);
                }
            }
        }
        catch (Exception ex)
        {
            immafreedom.plugin.log(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static CommandMap getCommandMap()
    {
        final Object commandMap = ifm_util.getField(Bukkit.getServer().getPluginManager(), "commandMap");
        if (commandMap != null)
        {
            if (commandMap instanceof CommandMap)
            {
                return (CommandMap) commandMap;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, Command> getKnownCommands(CommandMap commandMap)
    {
        Object knownCommands = ifm_util.getField(commandMap, "knownCommands");
        if (knownCommands != null)
        {
            if (knownCommands instanceof HashMap)
            {
                return (HashMap<String, Command>) knownCommands;
            }
        }
        return null;
    }

    private static List<ifm_CommandInfo> getCommands()
    {
        List<ifm_CommandInfo> commandList = new ArrayList<ifm_CommandInfo>();

        try
        {
            CodeSource codeSource = immafreedommod.class.getProtectionDomain().getCodeSource();
            if (codeSource != null)
            {
                ZipInputStream zip = new ZipInputStream(codeSource.getLocation().openStream());
                ZipEntry zipEntry;
                while ((zipEntry = zip.getNextEntry()) != null)
                {
                    String entryName = zipEntry.getName();
                    Matcher matcher = COMMAND_PATTERN.matcher(entryName);
                    if (matcher.find())
                    {
                        try
                        {
                            Class<?> commandClass = Class.forName(ifm_CommandHandler.COMMAND_PATH + "." + matcher.group(1));

                            CommandPermissions commandPermissions = commandClass.getAnnotation(CommandPermissions.class);
                            CommandParameters commandParameters = commandClass.getAnnotation(CommandParameters.class);

                            if (commandPermissions != null && commandParameters != null)
                            {
                                ifm_CommandInfo commandInfo = new ifm_CommandInfo(
                                        commandClass,
                                        matcher.group(1).split("_")[1],
                                        commandPermissions.level(),
                                        commandPermissions.source(),
                                        commandPermissions.blockHostConsole(),
                                        commandParameters.description(),
                                        commandParameters.usage(),
                                        commandParameters.aliases());

                                commandList.add(commandInfo);
                            }
                        }
                        catch (ClassNotFoundException ex)
                        {
                            immafreedommod.plugin.log(ex);
                        }
                    }
                }
            }
        }
        catch (IOException ex)
        {
            immafreedommod.plugin.log(ex);
        }

        return commandList;
    }

    public static class ifm_CommandInfo
    {
        private final String commandName;
        private final Class<?> commandClass;
        private final StaffLevel level;
        private final SourceType source;
        private final boolean blockHostConsole;
        private final String description;
        private final String usage;
        private final List<String> aliases;

        public ifm_CommandInfo(Class<?> commandClass, String commandName, StaffLevel level, SourceType source, boolean blockHostConsole, String description, String usage, String aliases)
        {
            this.commandName = commandName;
            this.commandClass = commandClass;
            this.level = level;
            this.source = source;
            this.blockHostConsole = blockHostConsole;
            this.description = description;
            this.usage = usage;
            this.aliases = ("".equals(aliases) ? new ArrayList<String>() : Arrays.asList(aliases.split(",")));
        }

        public List<String> getAliases()
        {
            return Collections.unmodifiableList(aliases);
        }

        public Class<?> getCommandClass()
        {
            return commandClass;
        }

        public String getCommandName()
        {
            return commandName;
        }

        public String getDescription()
        {
            return description;
        }

        public String getDescriptionPermissioned()
        {
            String _description = description;

            switch (this.getLevel())
            {
                case STAFF2:
                    _description = "STAFF2 " + (this.getSource() == SourceType.ONLY_CONSOLE ? "Console" : "") + " Command - " + _description;
                    break;
                case STAFF1:
                    _description = "STAFF1 Command - " + _description;
                    break;
                case OP:
                    _description = "OP Command - " + _description;
                    break;
            }

            return _description;
        }

        public StaffLevel getLevel()
        {
            return level;
        }

        public SourceType getSource()
        {
            return source;
        }

        public String getUsage()
        {
            return usage;
        }

        public boolean getBlockHostConsole()
        {
            return blockHostConsole;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("commandName: ").append(commandName);
            sb.append("\ncommandClass: ").append(commandClass.getName());
            sb.append("\nlevel: ").append(level);
            sb.append("\nsource: ").append(source);
            sb.append("\nblock_host_console: ").append(blockHostConsole);
            sb.append("\ndescription: ").append(description);
            sb.append("\nusage: ").append(usage);
            sb.append("\naliases: ").append(aliases);
            return sb.toString();
        }
    }

    public static class TFM_DynamicCommand extends Command implements PluginIdentifiableCommand
    {
        private final ifm_CommandInfo commandInfo;

        private ifm_DynamicCommand(ifm_CommandInfo commandInfo)
        {
            super(commandInfo.getCommandName(), commandInfo.getDescriptionPermissioned(), commandInfo.getUsage(), commandInfo.getAliases());

            this.commandInfo = commandInfo;
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args)
        {
            boolean success = false;

            if (!getPlugin().isEnabled())
            {
                return false;
            }

            try
            {
                success = getPlugin().onCommand(sender, this, commandLabel, args);
            }
            catch (Throwable ex)
            {
                throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + getPlugin().getDescription().getFullName(), ex);
            }

            if (!success && getUsage().length() > 0)
            {
                for (String line : getUsage().replace("<command>", commandLabel).split("\n"))
                {
                    sender.sendMessage(line);
                }
            }

            return success;
        }

        @Override
        public Plugin getPlugin()
        {
            return immafreedommod.plugin;
        }

        public ifm_CommandInfo getCommandInfo()
        {
            return commandInfo;
        }
    }
}
