package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.TourneyTeam;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;

import java.util.ArrayList;
import java.util.List;

public class ShowTeamsCommand extends BaseCommand implements ICommand {
  public ShowTeamsCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("tourney." + this.getPermission())) {
      return false;
    }

    int page = 1;
    if (args.size() > 0) {
      page = Integer.parseInt(args.get(0));
    }

    List<TourneyTeam> teams = Tournament.getInstance().getTeams();
    StringBuilder sb = new StringBuilder();

    for (TourneyTeam team : teams) {
      sb.append(ChatColor.YELLOW + ""+team.getName()+"\n");
      team.getPlayers().forEach(p -> {
        sb.append(ChatColor.YELLOW + "  "+p.getName()+"\n");
      });
    }

    ChatPaginator.ChatPage chatPage = ChatPaginator.paginate(sb.toString(), page);
    for (String line : chatPage.getLines()) {
      sender.sendMessage(line);
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
    return "showteams";
  }

  @Override
  public String getDescription() {
    return "Print all teams into the chat";
  }

  @Override
  public String getName() {
    return "Show Teams";
  }

  @Override
  public String getPermission() {
    return "player";
  }
}