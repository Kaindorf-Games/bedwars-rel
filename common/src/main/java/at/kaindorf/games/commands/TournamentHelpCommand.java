package at.kaindorf.games.commands;

import at.kaindorf.games.BedwarsRel;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;

import java.util.ArrayList;

public class TournamentHelpCommand extends BaseCommand implements ICommand {

  public TournamentHelpCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    Bukkit.getLogger().info("execute thelp" + this.getPermission());

    if (!sender.hasPermission("tourney." + this.getPermission())) {
      return false;
    }

    int pages = 1;
    if (args != null && args.size() > 0) {
      pages = Integer.parseInt(args.get(0));
    }

    ArrayList<BaseCommand> commands = BedwarsRel.getInstance().getTourneyCommands();
    StringBuilder sb = new StringBuilder();
    for (BaseCommand command : commands) {
      String arguments = "";
      for (String arg : command.getArguments()) {
        arguments += " {" + arg + "}";
      }

      if (command.getCommand().equalsIgnoreCase("thelp")) {
        arguments += " {page?}";
      }

      sb.append(ChatColor.YELLOW + "/" + "tourney"
          + " " + command.getCommand() + arguments + " - " + command.getDescription() + "\n");
    }

    ChatPaginator.ChatPage chatPage = ChatPaginator.paginate(sb.toString(), pages);
    for (String l : chatPage.getLines()) {
      sender.sendMessage(l);
    }
    sender.sendMessage(ChatColor.GREEN + "---------- "
        + "Page " + chatPage.getPageNumber() + " of " + chatPage.getTotalPages() + " ----------");

    return true;
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
    return "manage";
  }
}
