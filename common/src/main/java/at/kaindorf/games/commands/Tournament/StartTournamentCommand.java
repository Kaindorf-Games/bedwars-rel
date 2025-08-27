package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.arguments.CommandArgument;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.events.TournamentStartEvent;
import at.kaindorf.games.game.GameState;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.TourneyProperties;
import at.kaindorf.games.tournament.models.TourneyMatch;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StartTournamentCommand extends BaseCommand implements ICommand {
  public StartTournamentCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("tourney." + this.getPermission())) {
      ChatWriter.wrongPermissionMessage(sender);
      return false;
    }

    if(!this.checkIfTournamentCanBeStarted(sender)) return true;

    int groupStageRounds = 1, qualifiedTeams = 0, groupSize = 0;
    boolean rematchKo = false, rematchFinal = false;

    try {
      groupSize = Integer.parseInt(args.get(0));
      groupStageRounds = Integer.parseInt(args.get(1));
      qualifiedTeams = Integer.parseInt(args.get(2));
      rematchKo = Boolean.parseBoolean(args.get(3));
      rematchFinal = Boolean.parseBoolean(args.get(4));
      validateInput(groupStageRounds, qualifiedTeams, groupSize, groupStageRounds);
    } catch (Exception e) {
      sender.sendMessage(ChatColor.RED + e.getMessage());
      return true;
    }

    resetTournament();

    boolean res = Tournament.getInstance().generateGroupMatches(groupSize, groupStageRounds);
    if(!res) {
      sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.generationgroupfailed"));
    }

    sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("tourney.info.started"));

    Tournament.getInstance().setQualifiedTeams(qualifiedTeams);
    Tournament.getInstance().setRematchFinal(rematchFinal);
    Tournament.getInstance().setRematchKo(rematchKo);
    TournamentStartEvent event = new TournamentStartEvent(qualifiedTeams, rematchKo, rematchFinal);
    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(event);

    return true;
  }

  private boolean checkIfTournamentCanBeStarted(CommandSender sender) {
    if(Tournament.getInstance().getTeams().size() == 0) {
      sender.sendMessage(ChatColor.YELLOW + BedwarsRel._l("tourney.errors.addteamsfirst"));
      return false;
    }

    if(Tournament.getInstance().isTournamentRunning()) {
      sender.sendMessage(ChatColor.YELLOW + BedwarsRel._l("tourney.errors.tournamentalreadystarted"));
      return false;
    }

    return true;
  }

  private void validateInput(int rounds, int qualifiedTeams, int groupSize, int groupStageRounds) throws Exception {
    if(rounds <= 0 || qualifiedTeams <=0 || groupSize < 2 || groupStageRounds < 1)
      throw new Exception(BedwarsRel._l("tourney.errors.invalidinput"));

    if(BedwarsRel.getInstance().getGameManager().getGames().stream().filter(g -> g.getState() != GameState.STOPPED).map(g -> g.getTeams().size()).noneMatch(s -> s >= groupSize))
      throw new Exception(BedwarsRel._l("tourney.errors.nomapwithsize"));
  }

  private void resetTournament() {
    // delete existing log File
    if(TourneyProperties.logFile.exists()) {
      TourneyProperties.logFile.delete();
    }

    Tournament.getInstance().getGroups().clear();
    Tournament.getInstance().getTeams().forEach(t -> t.setGroup(null));

    // set stop to false
    Tournament.getInstance().setHardStop(false);
    Tournament.getInstance().setSoftStop(false);

    // reset groupstage and kostage
    Tournament.getInstance().setKoStage(null);
    Tournament.getInstance().setGroupStage(null);
    TourneyMatch.resetId();

    // reset remove All statistics
    Tournament.getInstance().getTeams().forEach(t -> t.getStatistics().clear());
  }

  @Override
  public String[] getArguments() {
    return new String[]{"groupSize<int>","groupStageRounds<int>","qualifiedTeams<int>", "rematchKo<bool>", "rematchFinal<bool>"};
  }

  @Override
  public CommandArgument[] getNewArguments() {
    return new CommandArgument[]{new CommandArgument("groupSize", Integer.class),
    new CommandArgument("groupStageRounds", Integer.class),
    new CommandArgument("qualifiedTeams", Integer.class),
    new CommandArgument("rematchKo", Boolean.class),
    new CommandArgument("rematchFinal", Boolean.class)};
  }

  @Override
  public String getCommand() {
    return "start";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.tourney.starttournament.description");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.tourney.starttournament.name");
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
