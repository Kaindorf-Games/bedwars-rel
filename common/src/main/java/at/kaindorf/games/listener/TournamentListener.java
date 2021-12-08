package at.kaindorf.games.listener;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.events.TournamentStartEvent;
import at.kaindorf.games.tournament.GameLoop;
import org.bukkit.event.EventHandler;

public class TournamentListener extends BaseListener {

  @EventHandler
  public void onStartEvent(TournamentStartEvent tse) {
    GameLoop gameLoop = new GameLoop(tse.getQualifiedTeams(), tse.isRematchKo(), tse.isRematchFinal());
    BedwarsRel.getInstance().setGameLoopTask(gameLoop.runTaskTimer(BedwarsRel.getInstance(), 0L, 80L));
  }
}
