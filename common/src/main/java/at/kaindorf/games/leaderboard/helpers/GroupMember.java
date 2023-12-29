package at.kaindorf.games.leaderboard.helpers;

import at.kaindorf.games.tournament.models.TourneyTeamSorter;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class GroupMember implements Serializable {

    private static final long serialVersionUID = 1L;

    private static Map<String, Integer> attributePoints;
    private static List<String> additionalAttributes;

    static {
        attributePoints = new HashMap<>();
        attributePoints.put("Kills", 0); //
        attributePoints.put("Final Kills", 2); //
        attributePoints.put("Beds Destroyed", 3); //
        attributePoints.put("Deaths", 0); //
        attributePoints.put("Games Played", 0); //
        attributePoints.put("Wins", 5); //

        additionalAttributes = Arrays.asList("Points", "Points/Game", "Win Ratio");
    }

    private int id;
    private String name;
    private String shortName;
    private Map<String, Double> attributes;
    private transient boolean joinedNew;

    public GroupMember() {
        this.attributes = new HashMap<>();

        for (String attribute : additionalAttributes) {
            attributes.put(attribute, 0.0);
        }
        for (String attribute : attributePoints.keySet()) {
            attributes.put(attribute, 0.0);
        }
    }

    public GroupMember(String name, String shortName, TourneyTeamSorter ts) {
        this.id = ts.getId();
        this.name = name;
        this.shortName = shortName;
        this.attributes = new HashMap<>();
        this.attributes.put("Final Kills", ts.getFinalKills());
        this.attributes.put("Beds Destroyed", ts.getDestroyedBeds());
        this.attributes.put("Games Played", ts.getGamesPlayed());
        this.attributes.put("Wins", ts.getWins());
        this.attributes.put("Points", ts.getPoints());
        this.attributes.put("Extra Points", ts.getExtraPoints());
    }

    public void addAttribute(String attr) {
        if (attributePoints.containsKey(attr)) {
            double games = attributes.get("Games Played");
            if (joinedNew) {
                this.attributes.put("Games Played", ++games);
                joinedNew = false;
            }

            double value = this.attributes.get(attr);
            this.attributes.put(attr, value + 1);

            double points = this.attributes.get("Points") + attributePoints.get(attr);
            this.attributes.put("Points", points);
            if (games > 0) {
                this.attributes.put("Points/Game", points / games);

                double wins = attributes.get("Wins");
                this.attributes.put("Win Ratio", wins / games);
            }
        }
    }

}
