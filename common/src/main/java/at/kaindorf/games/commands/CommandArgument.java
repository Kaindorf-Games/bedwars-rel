package at.kaindorf.games.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandArgument {
    private String name;
    private boolean optional;
    private Class<?> type;

    public CommandArgument(String name, boolean optional) {
        this.name = name;
        this.optional = optional;
        this.type = null;
    }

    public CommandArgument(String name) {
        this.name = name;
        this.optional = false;
        this.type = null;
    }

    public CommandArgument(String name, Class<?> type) {
        this.name = name;
        this.optional = false;
        this.type = type;
    }
}
