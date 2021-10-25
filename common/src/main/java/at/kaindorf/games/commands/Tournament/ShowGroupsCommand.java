package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.models.TourneyGroup;
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
      sender.sendMessage(ChatWriter.wrongPermissionMessage());
      return false;
    }

    int page = 1;
    if (args.size() > 0) {
      page = Integer.parseInt(args.get(0));
    }

    List<TourneyGroup> groups = Tournament.getInstance().getGroups();
    StringBuilder sb = new StringBuilder();

    for (TourneyGroup group : groups) {
      sb.append(ChatColor.YELLOW + ""+group.getName()+"\n");
      group.getTeams().forEach(t -> {
        sb.append(ChatColor.YELLOW + "  "+t.getName()+"\n");
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
}
