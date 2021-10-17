package at.kaindorf.games.tournament;

import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class TourneyPlayer {
  private String uuid;
  private TourneyTeam team;
  private Player player;
  private int kills, destroyedBeds;

  public TourneyPlayer(String uuid) {
    this.uuid = uuid;
//    this.player = Bukkit.getServer().getPlayer(name).getPlayer();
    this.kills = 0;
    this.destroyedBeds = 0;
  }
}
