package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.exceptions.TournamentEntityExistsException;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.TourneyPlayer;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
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
      return false;
    }

    String fileName = args.get(0);
    if (!fileName.endsWith(".json")) {
      return false;
    }
    Bukkit.getLogger().info(getPlugin().getDataFolder().getAbsolutePath());
    File teamFile = new File(getPlugin().getDataFolder(), fileName);

    if (!teamFile.exists()) {
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

        // add group
        String groupName = String.valueOf(team.get("group"));
        tourney.addGroup(groupName);

        // add team
        String teamName = String.valueOf(team.get("teamName"));
        tourney.addTeam(teamName, groupName);

        JSONArray playersObj = (JSONArray) team.get("players");
        List<TourneyPlayer> players = new ArrayList<>();

        for (Object player : playersObj) {
          // add player
          tourney.addPlayer(String.valueOf(player), teamName);
        }
      }
    } catch (TournamentEntityExistsException e) {
      Bukkit.getLogger().log(Level.WARNING, e.getMessage());
      tourney.clear();
      return false;
    }

    tourney.show();


    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"fileName"};
  }

  @Override
  public String getCommand() {
    return "addteams";
  }

  @Override
  public String getDescription() {
    return "Add teams to the Tournament";
  }

  @Override
  public String getName() {
    return "Add Teams";
  }

  @Override
  public String getPermission() {
    return "manage";
  }
}
