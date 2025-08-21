package at.kaindorf.games.leaderboard.observer;

import at.kaindorf.games.leaderboard.HttpHandler;
import at.kaindorf.games.leaderboard.leaderboards.GroupLeaderboard;
import at.kaindorf.games.leaderboard.leaderboards.KoLeaderboard;
import at.kaindorf.games.leaderboard.leaderboards.LeaderBoardType;
import lombok.Data;
import org.bukkit.Bukkit;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

@Data
public abstract class LeaderboardBase {
    private static List<LeaderboardBase> leaderboards;

    static {
        leaderboards = new LinkedList<>();
        leaderboards.add(new GroupLeaderboard());
        leaderboards.add(new KoLeaderboard());
    }

    public static LeaderboardBase findLeaderboard(LeaderBoardType type, String name) {
        try {
            for (LeaderboardBase l : leaderboards) {
                if (l.getType() == type) {
                    LeaderboardBase newLeaderboard = l.getClass().newInstance();
                    newLeaderboard.setName(name);
                    return newLeaderboard;
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    protected LeaderBoardType type;
    protected String name;
    protected transient int waitBetweenUpdates;
    protected transient boolean active;

    private transient List<IObserver> observers = new LinkedList<>();

    public LeaderboardBase() {
        observers.add(new HttpHandler());
    }

    public void registerObserver(IObserver observer) {
        this.observers.add(observer);
    }

    public void unregisterObserver(IObserver observer) {
        this.observers.remove(observer);
    }

    public void updateDataToLeaderboard(LeaderboardBase leaderboard) {
        updateDataToLeaderboard(leaderboard, false);
    }

    public void updateDataToLeaderboard(LeaderboardBase leaderboard, boolean force) {
        observers.forEach(o -> o.updateLeaderBoard(leaderboard, force));
    }

}
