package at.kaindorf.games.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TournamentStartEvent extends Event implements Cancellable {

  private static final HandlerList handlers = new HandlerList();
  private boolean cancelled = false;
  private final int qualifiedTeams;
  private final boolean rematchKo, rematchFinal;

  public TournamentStartEvent(int qualifiedTeams, boolean rematchKo, boolean rematchFinal) {
    this.qualifiedTeams = qualifiedTeams;
    this.rematchKo = rematchKo;
    this.rematchFinal = rematchFinal;
  }

  public static HandlerList getHandlerList() {
    return TournamentStartEvent.handlers;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }

  @Override
  public void setCancelled(boolean b) {
    this.cancelled = b;
  }

  @Override
  public HandlerList getHandlers() {
    return TournamentStartEvent.handlers;
  }

  public int getQualifiedTeams() {
    return qualifiedTeams;
  }

  public boolean isRematchKo() {
    return rematchKo;
  }

  public boolean isRematchFinal() {
    return rematchFinal;
  }
}
