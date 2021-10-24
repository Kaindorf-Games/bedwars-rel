package at.kaindorf.games.tournament;

import at.kaindorf.games.BedwarsRel;

import java.io.File;

public class TourneyProperties {

  public static File playersFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/tournament/players.yml");
  public static File groupsFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/tournament/groups.yml");
  public static File teamsFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/tournament/teams.yml");

}
