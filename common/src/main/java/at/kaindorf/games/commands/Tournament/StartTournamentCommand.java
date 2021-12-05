package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.events.TournamentStartEvent;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.TourneyProperties;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class StartTournamentCommand extends BaseCommand implements ICommand {
  public StartTournamentCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("tourney." + this.getPermission())) {
      sender.sendMessage(ChatWriter.wrongPermissionMessage());
      return false;
    }

    if(Tournament.getInstance().getTeams().size() == 0) {
      sender.sendMessage(ChatColor.YELLOW +"You have to add teams first");
      return true;
    }

    if(Tournament.getInstance().isTournamentRunning()) {
      sender.sendMessage(ChatColor.YELLOW +"Tournament has already started");
      return true;
    }

    int rounds = 1, qualifiedTeams = 0;
    boolean rematchKo = false, rematchFinal = false;

    try {
      qualifiedTeams = Integer.parseInt(args.get(0));
      rematchKo = Boolean.parseBoolean(args.get(1));
      rematchFinal = Boolean.parseBoolean(args.get(2));
      validateInput(rounds, qualifiedTeams);
    } catch (Exception e) {
      sender.sendMessage(ChatColor.RED + "Invalid Inputs");
      return false;
    }

    boolean res = Tournament.getInstance().generateGroupMatches();
    if(!res) {
      sender.sendMessage(ChatColor.RED + "groupStage.yml is missing");
    }

    resetTournament();

    sender.sendMessage(ChatColor.GREEN+"Tournament started");

    Tournament.getInstance().setQualifiedTeams(qualifiedTeams);
    Tournament.getInstance().setRematchFinal(rematchFinal);
    Tournament.getInstance().setRematchKo(rematchKo);
    TournamentStartEvent event = new TournamentStartEvent(qualifiedTeams, rematchKo, rematchFinal);
    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(event);

    return true;
  }

  private void validateInput(int rounds, int qualifiedTeams) throws Exception {
    int minTeams = Tournament.getInstance().getGroups().stream().mapToInt(g -> g.getTeams().size()).min().orElse(0);
    if(rounds <= 0 || qualifiedTeams <=0 || qualifiedTeams > minTeams)
      throw new Exception("Invalid Inputs");
  }

  private void resetTournament() {
    // delete existing log File
    if(TourneyProperties.logFile.exists()) {
      TourneyProperties.logFile.delete();
    }

    // set stop to false
    Tournament.getInstance().setHardStop(false);
    Tournament.getInstance().setSoftStop(false);

    // reset remove All statistics
    Tournament.getInstance().getTeams().forEach(t -> t.getStatistics().clear());
  }

  @Override
  public String[] getArguments() {
//    return new String[]{"rounds<int>", "qualifiedTeams<int>", "rematchKo<bool>", "rematchFinal<bool>"};
    return new String[]{"qualifiedTeams<int>", "rematchKo<bool>", "rematchFinal<bool>"};
  }

  @Override
  public String getCommand() {
    return "start";
  }

  @Override
  public String getDescription() {
    return "Command to start the tournament";
  }

  @Override
  public String getName() {
    return "Start Tournament";
  }

  @Override
  public String getPermission() {
    return "manage";
  }
}
