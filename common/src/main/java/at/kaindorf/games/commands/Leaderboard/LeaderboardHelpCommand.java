package at.kaindorf.games.commands.Leaderboard;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.HelpCommand;

import java.util.*;

public class LeaderboardHelpCommand extends HelpCommand {

    public LeaderboardHelpCommand(BedwarsRel plugin) {
        super(plugin, plugin.getLeaderboardCommands());
    }

    @Override
    public String getDescription() {
        return "Test";
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getPermission() {
        return "user";
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
}
