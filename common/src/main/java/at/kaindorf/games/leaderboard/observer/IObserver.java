package at.kaindorf.games.leaderboard.observer;

public interface IObserver {
    void updateLeaderBoard(LeaderboardBase leaderboard, boolean force);
}
