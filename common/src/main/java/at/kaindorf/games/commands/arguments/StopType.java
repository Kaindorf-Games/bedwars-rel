package at.kaindorf.games.commands.arguments;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

public enum StopType {
    SOFT("soft"), HARD("hard");

    public String value;

    StopType(String value) {
        this.value = value;
    }

    public static Optional<StopType> fromValue(String value) {
        return Stream.of(StopType.values()).filter(t -> StringUtils.equalsIgnoreCase(t.value, value)).findFirst();
    }
}
