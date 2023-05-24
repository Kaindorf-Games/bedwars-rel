package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.game.Game;
import at.kaindorf.games.game.GameState;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.util.ArrayList;
import java.util.Optional;

public class SpectateGameCommand extends BaseCommand implements ICommand {
    public SpectateGameCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (!sender.hasPermission("bw." + this.getPermission())) {
            ChatWriter.wrongPermissionMessage(sender);
            return false;
        }

        Player player = this.getPlugin().getServer().getPlayer(sender.getName());

        // player wants to leave spectator mode
        if(args.get(0).equalsIgnoreCase("leave")) {
            // find game of spectator
            Optional<Game> g = this.getPlugin().getGameManager().getGames().stream().filter(game -> game.getFreePlayers().stream().map(Entity::getUniqueId).anyMatch((fp) -> fp.equals(player.getUniqueId()))).findFirst();
            g.ifPresent(game -> game.removeSpectator(player));
            return true;
        }

        Game game = this.getPlugin().getGameManager().getGame(args.get(0));
        if(args.get(0).equalsIgnoreCase("player")) {
            if(args.size() < 2) {
                sender.sendMessage(ChatColor.RED + "Player name missing");
                return true;
            }

            Player p = this.getPlugin().getServer().getPlayer(args.get(1));
            if (p == null) {
                sender.sendMessage(ChatColor.RED+"Player not found");
                return true;
            }

            Game g = this.getPlugin().getGameManager().getGameOfPlayer(p);
            if (g == null) {
                sender.sendMessage(ChatColor.RED+"Player is in no game");
                return true;
            }
            game = g;
        } else if(args.get(0).equalsIgnoreCase("team")) {
            if(args.size() < 2) {
                sender.sendMessage(ChatColor.RED + "Team name missing");
                return true;
            }

            Optional<TourneyTeam> t = Tournament.getInstance().getTeamPerName(args.get(1));
            if (!t.isPresent()) {
                sender.sendMessage(ChatColor.RED+"Team not found");
                return true;
            }

            Optional<Game> g = this.getPlugin().getGameManager().getGameWithTeam(t.get());
            if (!g.isPresent()) {
                sender.sendMessage(ChatColor.RED+"Player is in no game");
                return true;
            }
            game = g.get();
        }

        if(game != null) {
            if(game.getState() != GameState.RUNNING) {
                sender.sendMessage(ChatColor.RED+"Game must be running to join as spectator");
                return true;
            }
            game.toSpectator(player);

        } else {
            sender.sendMessage(ChatColor.RED+"Game not found 404");
        }


        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[]{"{game|leave|player pn|team tn}"};
    }

    @Override
    public String getCommand() {
        return "spectate";
    }

    @Override
    public String getDescription() {
        return "Spectate a game";
    }

    @Override
    public String getName() {
        return "spectate";
    }

    @Override
    public String getPermission() {
        return "base";
    }
}
