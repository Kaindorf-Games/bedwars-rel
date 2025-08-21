package at.kaindorf.games.leaderboard.leaderboards;

import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

public enum LeaderBoardType {
    GROUP("Group"), KOGROUP("KoGroup");

    public String value;

    LeaderBoardType(String value) {
        this.value = value;
    }

    public static LeaderBoardType fromValue(String value) {
        return Stream.of(values()).filter(t -> StringUtils.equalsIgnoreCase(t.value, value)).findFirst().orElse(null);
    }
}
