package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;

public class TeamNoPauseCommand extends TeamPauseCommand {

  public TeamNoPauseCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public String getCommand() {
    return "nopause";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.tourney.nopause.description");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.tourney.nopause.name");
  }

  @Override
  public String getPermission() {
    return "player";
  }

  @Override
  protected boolean pauseValue() {
    return false;
  }
}
