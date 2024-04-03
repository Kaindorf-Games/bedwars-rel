package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.utils.ChatWriter;
import com.avaje.ebeaninternal.server.transaction.BulkEventListenerMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TournamentHelpCommand extends BaseCommand implements ICommand {

  public TournamentHelpCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {

    ArrayList<BaseCommand> commands = BedwarsRel.getInstance().getTourneyCommands();
    String msg = "";
    // generate the messages for the two main permissions
    if (sender.hasPermission("tourney.manage")) {
      msg = generateMessage(commands);
    } else if (sender.hasPermission("tourney.player")) {
      msg = generateMessage(commands.stream().filter(c -> c.getPermission().equals("player")).collect(Collectors.toList()));
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
    StringBuilder sb = new StringBuilder();
    sb.append(ChatColor.GREEN+"--------- " + BedwarsRel._l("tourney.others.tourneyhelp") + " ---------\n");
    for (BaseCommand command : commands.stream().filter(c -> !c.getPermission().equalsIgnoreCase("manage")).collect(Collectors.toList())) {
      generateLine(command, sb);
    }

    List<BaseCommand> adminCommands = commands.stream().filter(c -> c.getPermission().equalsIgnoreCase("manage")).collect(Collectors.toList());
    if(adminCommands.size() > 0) {
      sb.append(ChatColor.BLUE+"---------- " + BedwarsRel._l("tourney.others.adminhelp") + " ----------\n");
      for (BaseCommand command : adminCommands) {
        generateLine(command, sb);

      }
    }

    return sb.toString();
  }

  private void generateLine(BaseCommand command, StringBuilder sb) {
    if (command.blockDuringMode().contains(BedwarsRel.getInstance().getMode())) {
      return;
    }
    String arguments = "";
    for (String arg : command.getArguments()) {
      arguments += " {" + arg + "}";
    }

    if (command.getCommand().equalsIgnoreCase("help") ||
        command.getCommand().equalsIgnoreCase("showteams") ||
        command.getCommand().equalsIgnoreCase("showgroups") ||
        command.getCommand().equalsIgnoreCase("matches")) {
      arguments += " {page?}";
    } else if (command.getCommand().equalsIgnoreCase("clear")) {
      arguments += " {saves|config}";
    } else if (command.getCommand().equalsIgnoreCase("stop")) {
      arguments += "{soft|hard}";
    }

    sb.append(ChatColor.YELLOW + "/" + "tourney"
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
    return BedwarsRel._l("commands.tourney.help.description");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.tourney.help.name");
  }

  @Override
  public String getPermission() {
    return "player";
  }
}
