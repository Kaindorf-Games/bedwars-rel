package at.kaindorf.games.shop.Specials;

import at.kaindorf.games.events.BedwarsGameStartEvent;
import at.kaindorf.games.game.GameState;
import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class TrackerListener implements Listener {

  @EventHandler
  public void onInteract(PlayerInteractEvent ev) {
    Player player = ev.getPlayer();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

    if (game == null) {
      return;
    }

    if (game.getState() != GameState.RUNNING) {
      return;
    }

    if (game.isSpectator(player)) {
      return;
    }

    Tracker tracker = new Tracker();
    if (!ev.getMaterial().equals(tracker.getItemMaterial())) {
      return;
    }

    if (ev.getAction().equals(Action.LEFT_CLICK_AIR)
        || ev.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
      return;
    }

    tracker.setPlayer(player);
    tracker.setGame(game);
    tracker.trackPlayer();
    ev.setCancelled(true);
  }

  @EventHandler
  public void onStart(BedwarsGameStartEvent ev) {
    final Game game = ev.getGame();

    if (game == null) {
      return;
    }

    Tracker tracker = new Tracker();
    tracker.setGame(game);
    tracker.createTask();

  }

}
