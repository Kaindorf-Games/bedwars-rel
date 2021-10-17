package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.exceptions.TournamentEntityExistsException;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.TourneyPlayer;
import at.kaindorf.games.utils.ChatWriter;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
      sender.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + "Permissions required"));
      return false;
    }

    if(args.size() == 0) {
      sender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "Filename is required"));
      return false;
    }

    String fileName = args.get(0);
    if (!fileName.endsWith(".json")) {
      sender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "File has to be .json"));
      return false;
    }
    Bukkit.getLogger().info(getPlugin().getDataFolder().getAbsolutePath());
    File teamFile = new File(getPlugin().getDataFolder(), fileName);

    if (!teamFile.exists()) {
      sender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "File not found"));
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
      sender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + e.getMessage()));
      return false;
    } catch (Exception e) {
      sender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "Invalid file"));
      return false;
    }

    tourney.show();
    sender.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + "All teams are added"));
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
