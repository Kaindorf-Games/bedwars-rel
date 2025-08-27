package at.kaindorf.games.commands.arguments;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

public enum ClearType {
    SAVES("saves"), CONFIG("config"), RUNNING_TOURNAMENT("runningTournament");

    public final String value;

    ClearType(String value) {
        this.value = value;
    }

    public static Optional<ClearType> fromValue(String value) {
        return Stream.of(ClearType.values()).filter(t -> StringUtils.equalsIgnoreCase(t.value, value)).findFirst();
    }

}
