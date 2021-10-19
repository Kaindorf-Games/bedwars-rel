package at.kaindorf.games.tournament;

import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class TourneyPlayer {
  private String uuid, username;
  private TourneyTeam team;
  private Player player;
  private int kills, destroyedBeds;

  public TourneyPlayer(String uuid, String username) {
    this.uuid = uuid;
    this.username = username;
//    this.player = Bukkit.getServer().getPlayer(name).getPlayer();
    this.kills = 0;
    this.destroyedBeds = 0;
  }

  public TourneyPlayer(String uuid, String username, int kills, int destroyedBeds) {
    this.uuid = uuid;
    this.username = username;
//    this.player = Bukkit.getServer().getPlayer(name).getPlayer();
    this.kills = kills;
    this.destroyedBeds = destroyedBeds;
  }
}
