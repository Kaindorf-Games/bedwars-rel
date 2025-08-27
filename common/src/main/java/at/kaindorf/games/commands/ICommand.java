package at.kaindorf.games.commands;

import java.util.ArrayList;

import at.kaindorf.games.commands.arguments.CommandArgument;
import org.bukkit.command.CommandSender;

public interface ICommand {

  public boolean execute(CommandSender sender, ArrayList<String> args);

  public String[] getArguments();

  public CommandArgument[] getNewArguments();

  public String getCommand();

  public String getDescription();

  public String getName();

  public String getPermission();

  public boolean hasPermission(CommandSender sender);

}
