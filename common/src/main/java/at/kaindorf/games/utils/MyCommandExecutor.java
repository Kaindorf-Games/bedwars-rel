package at.kaindorf.games.utils;

import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.events.BedwarsCommandExecutedEvent;
import at.kaindorf.games.events.BedwarsExecuteCommandEvent;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MyCommandExecutor implements CommandExecutor {

  private BedwarsRel plugin = null;

  public MyCommandExecutor(BedwarsRel plugin) {
    super();

    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    if (cmd.getName().equals("bw")) {
      return execute(sender, args, this.plugin.getBedwarsCommands());
    } else if (cmd.getName().equals("tourney")) {
      return execute(sender, args, this.plugin.getTourneyCommands());
    }
    return false;
  }

  private boolean execute(CommandSender sender, String[] args, ArrayList<BaseCommand> commands) {
    if (args.length < 1) {
      return false;
    }

    String command = args[0];
    ArrayList<String> arguments = new ArrayList<String>(Arrays.asList(args));
    arguments.remove(0);

    for (BaseCommand bCommand : commands) {
      if (bCommand.getCommand().equalsIgnoreCase(command)) {
        if (bCommand.getArguments().length > arguments.size()) {
          sender.sendMessage(
              ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
                  ._l(sender, "errors.argumentslength")));
          return false;
        }

        if (bCommand.blockDuringMode().contains(BedwarsRel.getInstance().getMode())) {
          sender.sendMessage(ChatColor.RED + "You are not allowed to use this command, while Bedwars-mode is "+BedwarsRel.getInstance().getMode()+"!");
          return true;
        }

        BedwarsExecuteCommandEvent commandEvent =
            new BedwarsExecuteCommandEvent(sender, bCommand, arguments);
        BedwarsRel.getInstance().getServer().getPluginManager().callEvent(commandEvent);

        if (commandEvent.isCancelled()) {
          return true;
        }

        boolean result = bCommand.execute(sender, arguments);

        BedwarsCommandExecutedEvent executedEvent =
            new BedwarsCommandExecutedEvent(sender, bCommand, arguments, result);
        BedwarsRel.getInstance().getServer().getPluginManager().callEvent(executedEvent);

        return result;
      }
    }

    return false;
  }

}
