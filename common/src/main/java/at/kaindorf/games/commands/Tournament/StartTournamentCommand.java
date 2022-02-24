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
      ChatWriter.wrongPermissionMessage(sender);
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
      sender.sendMessage(ChatColor.RED + "Invalid Inputs!!!");
      return true;
    }

    resetTournament();

    boolean res = Tournament.getInstance().generateGroupMatches(groupSize, groupStageRounds);
    if(!res) {
      sender.sendMessage(ChatColor.RED + "Generating Group Stage failed!!!");
    }

    sender.sendMessage(ChatColor.GREEN+"Tournament started");

    Tournament.getInstance().setQualifiedTeams(qualifiedTeams);
    Tournament.getInstance().setRematchFinal(rematchFinal);
    Tournament.getInstance().setRematchKo(rematchKo);
    TournamentStartEvent event = new TournamentStartEvent(qualifiedTeams, rematchKo, rematchFinal);
    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(event);

    return true;
  }

  private void validateInput(int rounds, int qualifiedTeams, int groupSize, int groupStageRounds) throws Exception {
    if(rounds <= 0 || qualifiedTeams <=0 || groupSize < 2 || groupStageRounds < 1)
      throw new Exception("Invalid Inputs");
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

    // reset remove All statistics
    Tournament.getInstance().getTeams().forEach(t -> t.getStatistics().clear());
  }

  @Override
  public String[] getArguments() {
    return new String[]{"groupSize<int>","groupStageRounds<int>","qualifiedTeams<int>", "rematchKo<bool>", "rematchFinal<bool>"};
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

  @Override
  public BedwarsRel.Mode blockDuringMode() {
    return BedwarsRel.Mode.NORMAL;
  }
}
