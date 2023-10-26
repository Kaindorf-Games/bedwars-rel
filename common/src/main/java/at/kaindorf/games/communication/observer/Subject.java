package at.kaindorf.games.communication.observer;

import at.kaindorf.games.communication.HttpHandler;
import at.kaindorf.games.communication.dto.Leaderboard;

import java.util.LinkedList;
import java.util.List;

public abstract class Subject {
    private transient List<IObserver> observers = new LinkedList<>();

    public Subject() {
        observers.add(new HttpHandler());
    }

    public void registerObserver(IObserver observer) {
        this.observers.add(observer);
    }

    public void unregisterObserver(IObserver observer) {
        this.observers.remove(observer);
    }

    public void updateDataToLeaderboard(Leaderboard leaderboard) {
        updateDataToLeaderboard(leaderboard, false);
    }

    public void updateDataToLeaderboard(Leaderboard leaderboard, boolean force) {
        observers.forEach(o -> o.updateCasualLeaderBoard(leaderboard, force));
    }
}
