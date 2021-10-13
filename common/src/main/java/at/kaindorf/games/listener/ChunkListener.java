package at.kaindorf.games.listener;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.game.Game;
import at.kaindorf.games.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {

  @EventHandler
  public void onUnload(ChunkUnloadEvent unload) {
    Game game = BedwarsRel.getInstance().getGameManager()
        .getGameByChunkLocation(unload.getChunk().getX(),
            unload.getChunk().getZ());
    if (game == null) {
      return;
    }

    if (game.getState() != GameState.RUNNING) {
      return;
    }

    unload.setCancelled(true);
  }

}
