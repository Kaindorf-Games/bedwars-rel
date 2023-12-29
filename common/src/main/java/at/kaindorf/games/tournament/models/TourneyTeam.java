package at.kaindorf.games.tournament.models;

import at.kaindorf.games.game.Game;
import at.kaindorf.games.game.TeamColor;
import at.kaindorf.games.tournament.TourneyProperties;
import at.kaindorf.games.utils.NameTagHandler;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class TourneyTeam implements Serializable {
    public static Set<String> usedShortNames = new HashSet<>();

    private int id;
    private transient List<TourneyPlayer> players;
    private transient TourneyGroup group;
    private List<TourneyGameStatistic> statistics;
    private String name;
    private String shortname;
    private Game game;
    private boolean paused = false;

    private TeamColor teamColor;

    public static int currentId = 0;

    public TourneyTeam(int id, String name, String shortname) {
        this.id = id;
        this.name = name;
        this.players = new LinkedList<>();
        this.statistics = new LinkedList<>();
        this.game = null;
        this.group = null;
        this.shortname = shortname;

        if (this.id > currentId) {
            currentId = this.id;
        }

        if (this.shortname == null) {
            this.shortname = generateShortName(this.name);
        }
    }

    public void addPlayer(TourneyPlayer player) {
        this.players.add(player);
        player.setTeam(this);
        NameTagHandler.getInstance().addTagToPlayerUUID(Collections.singletonList(player.getUuid()), this.shortname);
    }

    public void addStatistic(TourneyGameStatistic teamStatistics) {
        this.statistics.add(teamStatistics);
    }

    public boolean inGame() {
        return game != null;
    }

    public boolean isReady() {
//    return numberOfPlayersOnline() >= 2 && !inGame() && !paused;
        return numberOfPlayersOnline() >= 1 && !inGame() && !paused;
    }

    public long numberOfPlayersOnline() {
        return players.stream().map(TourneyPlayer::getPlayer).filter(p -> p != null && p.isOnline()).count();
    }

    public int calculatePoints(CurrentState state) {
        int points = 0;

        for (TourneyGameStatistic stat : statistics) {
            if (state != null) {
                if (!(state == CurrentState.GROUP_STAGE && stat.getMatch() instanceof TourneyGroupMatch) && !(state == CurrentState.KO_STAGE && stat.getMatch() instanceof TourneyKoMatch)) {
                    continue;
                }
            }
            points += stat.getFinalKills() * TourneyProperties.pointsForFinalKill;
            points += stat.getDestroyedBeds() * TourneyProperties.pointsForBed;
            points += (stat.isWin() ? 1 : 0) * TourneyProperties.pointsForWin;
            points += stat.getExtraPoints();
        }

        return points;
    }

    private String generateShortName(String name) {
        Random r = new Random();
        name = name.toUpperCase().replaceAll("[^a-zA-Z0-9]", "");

        if (name.length() <= 5) {
            while (name.length() < 6) {
                name += (char) (r.nextInt(26) + 'A');
            }
        }

        String sn = null;

        int index = 0;
        do {
            String tmp = name.substring(index, index + 3);
            if (!usedShortNames.contains(tmp)) {
                sn = tmp;
                usedShortNames.add(tmp);
            }

            index++;
        } while (sn == null && index < name.length() - 3);

        while (sn == null) {
            String tmp = "" + (char) (r.nextInt(26) + 'A') + (char) (r.nextInt(26) + 'A') + (char) (r.nextInt(26) + 'A');
            if (!usedShortNames.contains(tmp)) {
                sn = tmp;
                usedShortNames.add(tmp);
            }
        }
        return sn;
    }

    public int calculatePoints() {
        return calculatePoints(null);
    }
}

