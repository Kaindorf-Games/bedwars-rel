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
    return "removes pause status of the team";
  }

  @Override
  public String getName() {
    return "nopause";
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
