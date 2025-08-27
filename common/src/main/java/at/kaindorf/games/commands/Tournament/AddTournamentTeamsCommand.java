package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.arguments.CommandArgument;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.exceptions.exist.TournamentEntityExistsException;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.utils.ChatWriter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class AddTournamentTeamsCommand extends BaseCommand implements ICommand {

  public AddTournamentTeamsCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @SneakyThrows
  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    Tournament tourney = Tournament.getInstance();
    tourney.clear();

    if (!sender.hasPermission("tourney." + this.getPermission())) {
      ChatWriter.wrongPermissionMessage(sender);
      return false;
    }

    if(args.size() == 0) {
      sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.filenamereq"));
      return false;
    }

    String fileName = args.get(0);
    if (!fileName.endsWith(".json")) {
      sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.filenamejson"));
      return false;
    }
    File teamFile = new File(getPlugin().getDataFolder()+"/tournament/", fileName);

    if (!teamFile.exists()) {
      sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.filenotfound"));
      return false;
    }
    JSONParser parser = new JSONParser();
    FileReader reader = new FileReader(teamFile);

    Object obj = parser.parse(reader);
    JSONObject teams = (JSONObject) obj;
    JSONArray teamsList = (JSONArray) teams.get("teams");

    try {
      for (Object teamObj : teamsList) {

        JSONObject team = (JSONObject) teamObj;

        // add team
        String teamName = String.valueOf(team.get("teamName")).toLowerCase();
        tourney.addTeam(teamName);

        JSONArray playersObj = (JSONArray) team.get("players");

        for (Object player : playersObj) {
          // add player
          tourney.addPlayer(String.valueOf(player), teamName);
        }
      }
    } catch (TournamentEntityExistsException e) {
      Bukkit.getLogger().log(Level.WARNING, e.getMessage());
      tourney.clear();
      sender.sendMessage(ChatColor.RED + e.getMessage());
      return false;
    } catch (Exception e) {
      sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.invalidfile"));
      return false;
    }

    tourney.show();
    sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("tourney.info.teamsadded"));
    return true;
  }

  @Override
  public CommandArgument[] getNewArguments() {
    return new CommandArgument[]{
            new CommandArgument("fileName", String.class)};
  }

  @Override
  public String getCommand() {
    return "addteams";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.tourney.addteams.description");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.tourney.addteams.name");
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
