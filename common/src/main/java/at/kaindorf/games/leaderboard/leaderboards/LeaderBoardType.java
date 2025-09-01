package at.kaindorf.games.leaderboard.leaderboards;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

public enum LeaderBoardType {
    @SerializedName("Group")
    GROUP("Group"),
    @SerializedName("KoGroup")
    KOGROUP("KoGroup");

    public String value;

    LeaderBoardType(String value) {
        this.value = value;
    }

    public static Optional<LeaderBoardType> fromValue(String value) {
        return Stream.of(values()).filter(t -> StringUtils.equalsIgnoreCase(t.value, value)).findFirst();
    }
}
