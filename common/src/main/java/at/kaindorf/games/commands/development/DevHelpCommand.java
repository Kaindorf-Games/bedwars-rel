package at.kaindorf.games.commands.development;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.HelpCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public class DevHelpCommand extends HelpCommand {
    public DevHelpCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public Map<PermissionType, String> availablePermissions() {
        Map<PermissionType, String> permissions = new HashMap<>();
        permissions.put(PermissionType.MANAGER, "dev.developer");
        return permissions;
    }

    @Override
    public String getMainCommand() {
        return "dev";
    }

    @Override
    public List<BaseCommand> getCommandList() {
        return this.getPlugin().getDevCommands();
    }

}
