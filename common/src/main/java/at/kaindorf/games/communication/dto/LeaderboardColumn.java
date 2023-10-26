package at.kaindorf.games.communication.dto;

import lombok.Builder;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Builder
public class LeaderboardColumn implements Serializable {

    private static final long serialVersionUID = 1L;

    private String columnName;
    private String name;
    private boolean attribute;
    private boolean isFloat;

    public static List<LeaderboardColumn> init() {
        List<LeaderboardColumn> columns = new LinkedList<>();

        columns.add(LeaderboardColumn.builder().columnName("Name").name("name").attribute(false).isFloat(false).build());
        columns.add(LeaderboardColumn.builder().columnName("Games Played").name("Games Played").attribute(true).isFloat(false).build());
        columns.add(LeaderboardColumn.builder().columnName("Kills").name("Kills").attribute(true).isFloat(false).build());
        columns.add(LeaderboardColumn.builder().columnName("Final Kills").name("Final Kills").attribute(true).isFloat(false).build());
        columns.add(LeaderboardColumn.builder().columnName("Beds Destroyed").name("Beds Destroyed").attribute(true).isFloat(false).build());
        columns.add(LeaderboardColumn.builder().columnName("Wins").name("Wins").attribute(true).isFloat(false).build());
        columns.add(LeaderboardColumn.builder().columnName("Points").name("Points").attribute(true).isFloat(false).build());

        return columns;
    }

}
