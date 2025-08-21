package at.kaindorf.games.commands;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.leaderboard.leaderboards.LeaderBoardType;
import at.kaindorf.games.utils.ChatWriter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class HelpCommand extends BaseCommand implements ICommand {

    private List<BaseCommand> commands;

    public HelpCommand(BedwarsRel plugin, List<BaseCommand> commands) {
        super(plugin);
        this.commands = commands;
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        Map<PermissionType, String> commandPermission = availablePermissions();

        String msg = "";
        // generate the messages for the two main permissions
        if (commandPermission.containsKey(PermissionType.MANAGER) && sender.hasPermission(commandPermission.get(PermissionType.MANAGER))) {
            msg = generateMessage(commands);
        } else if (commandPermission.containsKey(PermissionType.USER) && sender.hasPermission(commandPermission.get(PermissionType.USER))) {
            String userPermission = Arrays.stream(commandPermission.get(PermissionType.USER).split(".")).skip(1).findFirst().orElse("");
            msg = generateMessage(commands.stream().filter(c -> StringUtils.equals(c.getPermission(), userPermission)).collect(Collectors.toList()));
        } else {
            ChatWriter.wrongPermissionMessage(sender);
            return false;
        }

        // get the current page number
        int page = 1;
        if (args != null && args.size() > 0) {
            page = Integer.parseInt(args.get(0));
        }

        // send the single lines
        ChatWriter.paginateOutput(sender, msg, page);

        return true;
    }

    private String generateMessage(List<BaseCommand> commands) {
        if(commands.isEmpty()) {
            return ChatColor.BLUE + "Nothing to show";
        }

        StringBuilder sb = new StringBuilder();
        for (BaseCommand command : commands.stream().sorted(Comparator.comparing(BaseCommand::getCommand)).collect(Collectors.toList())) {
            generateLine(command, sb);
        }

        return sb.toString();
    }

    private void generateLine(BaseCommand command, StringBuilder sb) {
        // if the command is not usable in the current mode => do not show in help text
        if (command.blockDuringMode().contains(BedwarsRel.getInstance().getMode())) {
            return;
        }

        sb.append(ChatColor.LIGHT_PURPLE + "/" + getMainCommand() + ChatColor.GOLD
                + " " + command.getCommand() + buildArguments(command) + " - " + ChatColor.YELLOW + command.getDescription() + "\n");
    }

    @Override
    public String[] getArguments() {
        return new String[]{};
    }

    @Override
    public CommandArgument[] getNewArguments() {
        return new CommandArgument[]{new CommandArgument("page", true)};
    }

    @Override
    public String getCommand() {
        return "help";
    }

    public enum PermissionType {
        MANAGER, USER
    }

    public abstract Map<PermissionType, String> availablePermissions();

    public abstract String getMainCommand();

    private String buildArguments(BaseCommand command) {
        CommandArgument[] arguments = command.getNewArguments();
        if (arguments == null) {
            return buildLegacyArguments(command);
        }

        StringBuilder sb = new StringBuilder();
        for (CommandArgument arg : arguments) {
            sb.append(ChatColor.GREEN).append(" ");
            if (arg.isOptional()) {
                sb.append("[");
            }
            sb.append(arg.getName());
            if (arg.getType() != null) {
                sb.append("<").append(getDatatypeAbbreviation(arg.getType())).append(">");
            }
            if (arg.isOptional()) {
                sb.append("]");
            }
        }

        return sb.toString();
    }

    private String buildLegacyArguments(BaseCommand command) {
        StringBuilder arguments = new StringBuilder();
        for (String arg : command.getArguments()) {
            arguments.append(" {").append(arg).append("}");
        }
        return arguments.toString();
    }

    private String getDatatypeAbbreviation(Class<?> type) {
        if (type == null) {
            return "";
        }

        if (type.equals(String.class)) {
            return "string";
        } else if (type.equals(Integer.class) || type.equals(Long.class)) {
            return "int";
        } else if (type.equals(Boolean.class)) {
            return "bool";
        } else if (type.equals(LeaderBoardType.class)) {
            return Stream.of(LeaderBoardType.values()).map(t -> t.value).reduce((e1, e2) -> e1 + "|" + e2).orElse("");
        }

        return "N/A";
    }

}
