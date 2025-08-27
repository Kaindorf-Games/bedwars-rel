package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.arguments.ClearType;
import at.kaindorf.games.commands.arguments.CommandArgument;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.utils.ChatWriter;
import at.kaindorf.games.utils.Saver;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ClearTournamentCommand extends BaseCommand implements ICommand {
  public ClearTournamentCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("tourney." + this.getPermission())) {
      ChatWriter.wrongPermissionMessage(sender);
      return false;
    }
    if (args.isEmpty()) {
      Tournament.getInstance().clear();
      Saver.clear();
      Tournament.getInstance().clearRunningTournament();
    } else {
      Optional<ClearType> optionalC = ClearType.fromValue(args.get(0));
      if(!optionalC.isPresent()) {
        sender.sendMessage(ChatColor.RED + "Type not known");
        return true;
      }
      ClearType ct = optionalC.get();
      if (ct == ClearType.SAVES) {
        Saver.clear();
      } else if (ct == ClearType.CONFIG) {
        Tournament.getInstance().clear();
      } else if (ct == ClearType.RUNNING_TOURNAMENT) {
        Tournament.getInstance().clearRunningTournament();
      }
    }

    sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("tourney.info.cleardone"));
    return true;
  }

  @Override
  public CommandArgument[] getNewArguments() {
    return new CommandArgument[]{
            new CommandArgument("type", true, ClearType.class),
    };
  }

  @Override
  public String getCommand() {
    return "clear";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.tourney.cleartournament.description");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.tourney.cleartournament.name");
  }

  @Override
  public String getPermission() {
    return "manage";
  }

  @Override
  public List<BedwarsRel.Mode> blockDuringMode() {
    return Arrays.asList(BedwarsRel.Mode.NORMAL);
  }
}
