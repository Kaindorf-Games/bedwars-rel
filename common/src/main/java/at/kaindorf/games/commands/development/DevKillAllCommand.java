package at.kaindorf.games.commands.development;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.events.BedwarsPlayerKilledEvent;
import at.kaindorf.games.events.BedwarsTargetBlockDestroyedEvent;
import at.kaindorf.games.game.Game;
import at.kaindorf.games.game.GameState;
import at.kaindorf.games.game.Team;
import at.kaindorf.games.tournament.models.TourneyMatch;
import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DevKillAllCommand extends DevBaseCommand implements ICommand {
    public DevKillAllCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (!sender.hasPermission("dev." + this.getPermission())) {
            ChatWriter.wrongPermissionMessage(sender);
            return false;
        }

        Game game = BedwarsRel.getInstance().getGameManager().getGame(args.get(0));
        if(game.getState() != GameState.RUNNING) {
            return ChatWriter.errorMessage(sender, "Game has to be running");
        } else if(game.getMatch() == null) {
            return ChatWriter.errorMessage(sender, "Game  has to be a tourney match");
        } else if(game.getTeam(args.get(1)) == null) {
            return ChatWriter.errorMessage(sender, "Team has to play in the games match");
        }


        Team winner = game.getTeam(args.get(1));
        List<Team> teams = game.getTeams().values().stream().filter(t -> !t.getName().equals(winner.getName())).collect(Collectors.toList());

        Random r = new Random();
        for (Team team: teams) {
            game.handleDestroyTargetMaterial(winner.getPlayers().get(r.nextInt(winner.getPlayers().size())), team.getHeadTarget());
        }

        Bukkit.getLogger().info("1");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Bukkit.getLogger().info("2");

        for (Player player: teams.stream().map(Team::getPlayers).flatMap(Collection::stream).collect(Collectors.toList())) {
            Bukkit.getLogger().info(player.getDisplayName());
            player.setHealth(0);
//            player.
//            game.getCycle().onPlayerDies(player, winner.getPlayers().get(r.nextInt(winner.getPlayers().size())));
        }
        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[]{"game", "bwTeam"};
    }

    @Override
    public String getCommand() {
        return "killall";
    }

    @Override
    public String getDescription() {
        return "make a team win a match instant";
    }

    @Override
    public String getName() {
        return "killall";
    }
}
