package at.kaindorf.games.leaderboard.helpers;

import lombok.Builder;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Builder
public class GroupColumn implements Serializable {

    private static final long serialVersionUID = 1L;

    private String columnName;
    private String name;
    private boolean attribute;
    private boolean isFloat;

    public static List<GroupColumn> initGroupLeaderboardColumns() {
        List<GroupColumn> columns = new LinkedList<>();

        columns.add(GroupColumn.builder().columnName("Name").name("name").attribute(false).isFloat(false).build());
        columns.add(GroupColumn.builder().columnName("Games Played").name("Games Played").attribute(true).isFloat(false).build());
        columns.add(GroupColumn.builder().columnName("Kills").name("Kills").attribute(true).isFloat(false).build());
        columns.add(GroupColumn.builder().columnName("Final Kills").name("Final Kills").attribute(true).isFloat(false).build());
        columns.add(GroupColumn.builder().columnName("Beds Destroyed").name("Beds Destroyed").attribute(true).isFloat(false).build());
        columns.add(GroupColumn.builder().columnName("Wins").name("Wins").attribute(true).isFloat(false).build());
        columns.add(GroupColumn.builder().columnName("Points").name("Points").attribute(true).isFloat(false).build());

        return columns;
    }

    public static List<GroupColumn> initKoLeaderboardColumns() {
        List<GroupColumn> columns = new LinkedList<>();

        columns.add(GroupColumn.builder().columnName("Name").name("name").attribute(false).isFloat(false).build());
        columns.add(GroupColumn.builder().columnName("Final Kills").name("Final Kills").attribute(true).isFloat(false).build());
        columns.add(GroupColumn.builder().columnName("Beds Destroyed").name("Beds Destroyed").attribute(true).isFloat(false).build());
        columns.add(GroupColumn.builder().columnName("Games Played").name("Games Played").attribute(true).isFloat(false).build());
        columns.add(GroupColumn.builder().columnName("Wins").name("Wins").attribute(true).isFloat(false).build());
        columns.add(GroupColumn.builder().columnName("Extra Points").name("Extra Points").attribute(true).isFloat(false).build());
        columns.add(GroupColumn.builder().columnName("Points").name("Points").attribute(true).isFloat(false).build());

        return columns;
    }

}
