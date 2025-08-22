package at.kaindorf.games.commands.Leaderboard;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.HelpCommand;

import java.util.*;

public class LeaderboardHelpCommand extends HelpCommand {

    public LeaderboardHelpCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public Map<PermissionType, String> availablePermissions() {
        Map<PermissionType, String> permissions = new HashMap<>();
        permissions.put(PermissionType.MANAGER, "leaderboard.manager");
        permissions.put(PermissionType.USER, "leaderboard.user");
        return permissions;
    }

    @Override
    public String getMainCommand() {
        return "leaderboard";
    }

    @Override
    public List<BaseCommand> getCommandList() {
        return this.getPlugin().getLeaderboardCommands();
    }
}
