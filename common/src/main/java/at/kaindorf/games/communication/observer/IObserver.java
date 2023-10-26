package at.kaindorf.games.communication.observer;

import at.kaindorf.games.communication.dto.Leaderboard;

public interface IObserver {

    void updateCasualLeaderBoard(Leaderboard leaderboard, boolean force);
}
