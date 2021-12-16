package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.material.Bed;
import org.bukkit.util.ChatPaginator;

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
      msg = generateHelpMessage(commands);
    } else if (sender.hasPermission("tourney.player")) {
      msg = generateHelpMessage(commands.stream().filter(c -> c.getPermission().equals("player")).collect(Collectors.toList()));
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
//    ChatPaginator.ChatPage chatPage = ChatPaginator.paginate(msg, page);
//    for (String l : chatPage.getLines()) {
//      sender.sendMessage(l);
//    }
//
//    sender.sendMessage(ChatColor.GREEN + "---------- "
//        + "Page " + chatPage.getPageNumber() + " of " + chatPage.getTotalPages() + " ----------");

    return true;
  }

  private String generateHelpMessage(List<BaseCommand> commands) {
    StringBuilder sb = new StringBuilder();
    for (BaseCommand command : commands) {
      if (command.blockDuringMode() == BedwarsRel.getInstance().getMode()) {
        continue;
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

    return sb.toString();
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
    return "Base command for all Tournament commands";
  }

  @Override
  public String getName() {
    return "Help for Tourney";
  }

  @Override
  public String getPermission() {
    return "player";
  }
}
