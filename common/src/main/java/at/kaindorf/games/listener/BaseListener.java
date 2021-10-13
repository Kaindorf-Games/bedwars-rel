package at.kaindorf.games.listener;

import at.kaindorf.games.BedwarsRel;
import org.bukkit.event.Listener;

public abstract class BaseListener implements Listener {

  public BaseListener() {
    this.registerEvents();
  }

  private void registerEvents() {
    BedwarsRel.getInstance().getServer().getPluginManager()
        .registerEvents(this, BedwarsRel.getInstance());
  }

}
