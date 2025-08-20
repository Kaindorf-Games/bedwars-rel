package at.kaindorf.games.commands.development;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DevHelpCommand extends DevBaseCommand implements ICommand {
    public DevHelpCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {

        if (!sender.hasPermission("dev." + this.getPermission())) {
            ChatWriter.wrongPermissionMessage(sender);
            return false;
        }

        // generate the message
        String msg = generateMessage(BedwarsRel.getInstance().getDevCommands());

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
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GREEN + "--------- " + BedwarsRel._l("dev.help") + " ---------\n");
        for (BaseCommand command : commands) {
            generateLine(command, sb);
        }
        return sb.toString();
    }

    private void generateLine(BaseCommand command, StringBuilder sb) {
        String arguments = "";
        for (String arg : command.getArguments()) {
            arguments += " {" + arg + "}";
        }

        if (command.getCommand().equalsIgnoreCase("help")) {
            arguments += " {page?}";
        }

        sb.append(ChatColor.YELLOW + "/" + "dev"
                + " " + command.getCommand() + arguments + " - " + command.getDescription() + "\n");
    }


    @Override
    public String[] getArguments() {
        return new String[]{};
    }

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public String getDescription() {
        return BedwarsRel._l("commands.dev.help.description");
    }

    @Override
    public String getName() {
        return BedwarsRel._l("commands.dev.help.name");
    }
}
