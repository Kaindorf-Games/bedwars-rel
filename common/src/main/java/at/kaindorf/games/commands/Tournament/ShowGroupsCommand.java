package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.models.CurrentState;
import at.kaindorf.games.tournament.models.TourneyGroup;
import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;

import java.util.ArrayList;
import java.util.List;

public class ShowGroupsCommand extends BaseCommand implements ICommand {

  public ShowGroupsCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("tourney." + this.getPermission())) {
      ChatWriter.wrongPermissionMessage(sender);
      return false;
    }

    if(Tournament.getInstance().getGroups().size() == 0) {
      sender.sendMessage(ChatColor.RED + "No Groups found");
      return true;
    }

    int page = 1;
    if (args.size() > 0) {
      try {
        page = Integer.parseInt(args.get(0));
      } catch (NumberFormatException ex) {
        sender.sendMessage(ChatColor.RED + "Not a number!!!");
        return false;
      }
    }

    String output = buildChatOutput();

    ChatWriter.paginateOutput(sender, output, page);

    return true;
  }

  private String buildChatOutput() {
    List<TourneyGroup> groups = Tournament.getInstance().getGroups();
    StringBuilder sb = new StringBuilder();

    for (TourneyGroup group : groups) {
      sb.append(ChatColor.YELLOW + "" + group.getName() + "\n");
      group.getTeams().forEach(t -> {
        String paused = "";
        if (t.isPaused()) paused = " (paused)";
        sb.append(ChatColor.YELLOW + "  " + t.getName() + paused + " - " + t.calculatePoints(CurrentState.GROUP_STAGE) + " points\n");
      });
    }

    return sb.toString();
  }


  @Override
  public String[] getArguments() {
    return new String[]{};
  }

  @Override
  public String getCommand() {
    return "showgroups";
  }

  @Override
  public String getDescription() {
    return "Print all groups into the chat";
  }

  @Override
  public String getName() {
    return "Show Groups";
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
