package at.kaindorf.games.tournament.models;

import at.kaindorf.games.BedwarsRel;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class TourneyPlayer {
  private int id;
  private String uuid, username;
  private TourneyTeam team;
  private Player player;
  private int kills, destroyedBeds;
  private static int currentId;

  public TourneyPlayer(String uuid, String username) {
    this.uuid = uuid;
    this.username = username;
    this.kills = 0;
    this.destroyedBeds = 0;

    this.id = currentId++;
  }

  public TourneyPlayer(String uuid, String username, int kills, int destroyedBeds) {
    this.uuid = uuid;
    this.username = username;
    this.kills = kills;
    this.destroyedBeds = destroyedBeds;

    this.id = currentId++;
  }

  public TourneyPlayer(int id, String uuid, String username, int kills, int destroyedBeds) {
    this.uuid = uuid;
    this.username = username;
    this.kills = kills;
    this.destroyedBeds = destroyedBeds;

    this.id = id;
    if(currentId < id) {
      currentId = id;
    }
  }

  public void initPlayer() {
    player = BedwarsRel.getInstance().getServer().getPlayer(UUID.fromString(uuid));
  }
}
