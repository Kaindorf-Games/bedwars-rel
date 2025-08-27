package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.arguments.CommandArgument;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.commands.arguments.StopType;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StopTournamentCommand extends BaseCommand implements ICommand {

  public StopTournamentCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("tourney." + this.getPermission())) {
      ChatWriter.wrongPermissionMessage(sender);
      return false;
    }

    Optional<StopType> stopType = Optional.of(StopType.SOFT);
    if(!args.isEmpty()) {
      stopType = StopType.fromValue(args.get(0));
    }

    if(stopType.isPresent()) {
      if(stopType.get() == StopType.SOFT) {
        return softStop(sender);
      } else if(stopType.get() == StopType.HARD) {
        return hardStop(sender);
      }
    }

    sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.invalidparams"));
    return true;
  }

  private boolean softStop(CommandSender sender) {

    Tournament.getInstance().setSoftStop(true);
    Tournament.getInstance().setHardStop(false);

    sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("tourney.info.softstop"));
    return true;
  }

  private boolean hardStop(CommandSender sender) {

    Tournament.getInstance().setHardStop(true);
    Tournament.getInstance().setSoftStop(false);

    sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("tourney.info.hardstop"));
    return true;
  }

  @Override
  public CommandArgument[] getNewArguments() {
    return new CommandArgument[]{new CommandArgument("type", StopType.class)};
  }

  @Override
  public String getCommand() {
    return "stop";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.tourney.stoptournament.description");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.tourney.stoptournament.name");
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
