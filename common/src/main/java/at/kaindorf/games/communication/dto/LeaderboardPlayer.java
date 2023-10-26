package at.kaindorf.games.communication.dto;

import lombok.Data;
import org.bukkit.Bukkit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class LeaderboardPlayer implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient static Map<String, Integer> attributePoints;
    private transient static List<String> additionalAttributes;

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

    private String name;
    private Map<String, Double> attributes;
    private transient boolean joinedNew;

    public LeaderboardPlayer() {
        this.attributes = new HashMap<>();

        for(String attribute: additionalAttributes) {
            attributes.put(attribute, 0.0);
        }
        for(String attribute: attributePoints.keySet()) {
            attributes.put(attribute, 0.0);
        }
    }

    //Fake
    public LeaderboardPlayer(String name, double kills, double finalKills, double bedsDestroyed, double deaths, double wins, double points, double games) {
        this.attributes = new HashMap<>();
        this.name = name;
        this.attributes.put("Kills", kills);
        this.attributes.put("Final Kills", finalKills);
        this.attributes.put("Beds Destroyed", bedsDestroyed);
        this.attributes.put("Deaths", deaths);
        this.attributes.put("Games Played", games);
        this.attributes.put("Wins", wins);
        this.attributes.put("Points", points);
        this.attributes.put("Points/Game", (points/games));
        this.attributes.put("Win Ratio", (wins/games));
    }

    public void addAttribute(String attr) {
        if(attributePoints.containsKey(attr)) {
            double games = attributes.get("Games Played");
            if(joinedNew) {
                this.attributes.put("Games Played", ++games);
                joinedNew = false;
            }

            double value = this.attributes.get(attr);
            this.attributes.put(attr, value + 1);

            double points = this.attributes.get("Points") + attributePoints.get(attr);
            this.attributes.put("Points", points);
            if(games > 0) {
                this.attributes.put("Points/Game", points/games);

                double wins  = attributes.get("Wins");
                this.attributes.put("Win Ratio", wins/games);
            }
        }
    }

}
