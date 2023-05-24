package at.kaindorf.games.commands.development;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;

import java.util.Base64;

public abstract class DevBaseCommand extends BaseCommand implements ICommand {
    public DevBaseCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean isDevCommand() {
        return true;
    }

    @Override
    public String getPermission() {
        return "developer";
    }
}
