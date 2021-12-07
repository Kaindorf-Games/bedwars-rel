package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.models.CurrentState;
import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.utils.ChatWriter;
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
      sender.sendMessage(ChatWriter.wrongPermissionMessage());
      return false;
    }

    int page = 1;
    if (args.size() > 0) {
      page = Integer.parseInt(args.get(0));
    }

    String output = buildChatOutput();
    ChatPaginator.ChatPage chatPage = ChatPaginator.paginate(output, page);
    for (String line : chatPage.getLines()) {
      sender.sendMessage(line);
    }

    sender.sendMessage(ChatColor.GREEN + "---------- "
        + "Page " + chatPage.getPageNumber() + " of " + chatPage.getTotalPages() + " ----------");

    return true;
  }

  private String buildChatOutput() {
    List<TourneyTeam> teams = Tournament.getInstance().getTeams();
    StringBuilder sb = new StringBuilder();

    for (TourneyTeam team : teams) {
      sb.append(ChatColor.YELLOW + "" + team.getName() + "\n");
      team.getPlayers().forEach(p -> {
        sb.append(ChatColor.YELLOW + "  " + p.getUsername() + "\n");
      });
      sb.append(ChatColor.YELLOW + "  Points group stage: " + team.calculatePoints(CurrentState.GROUP_STAGE)+"\n");
      sb.append(ChatColor.YELLOW + "  Points ko stage: " + team.calculatePoints(CurrentState.KO_STAGE)+"\n\n");
    }
    return sb.toString();
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

  @Override
  public BedwarsRel.Mode blockDuringMode() {
    return BedwarsRel.Mode.NORMAL;
  }
}
