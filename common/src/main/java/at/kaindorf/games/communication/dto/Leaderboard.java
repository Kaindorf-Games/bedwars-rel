package at.kaindorf.games.communication.dto;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.communication.observer.Subject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.*;


@Data
@EqualsAndHashCode(callSuper=false)
public class Leaderboard extends Subject implements Serializable {

    private static final long serialVersionUID = 1L;
    private static Leaderboard instance;

    public static Leaderboard getInstance() {
        if (instance == null) {
            instance = new Leaderboard();
            instance.setName("casual-bedwars");
            instance.setWaitDuration(0);
        }
        return instance;
    }

    public static void setInstance(Leaderboard instance) {
        Leaderboard.instance = instance;
    }

    private transient Map<String, LeaderboardPlayer> membersMap;
    private transient String name;
    private transient int waitDuration;
    private List<LeaderboardPlayer> members;
    private Map<Integer, String> sortedBy;
    private List<LeaderboardColumn> columns;


    private void initSortedBy() {
        sortedBy.put(1, "Points");
        sortedBy.put(2, "Wins");
        sortedBy.put(3, "Beds Destroyed");
        sortedBy.put(4, "Final Kills");
        sortedBy.put(5, "Kills");

    }

    public Leaderboard() {
        this.members = new LinkedList<>();
        this.membersMap = new HashMap<>();
        this.sortedBy = new HashMap<>();
        this.columns = LeaderboardColumn.init();
        initSortedBy();
    }

    public Leaderboard(Map<UUID, String> uuids, boolean update) {
        this.members = new LinkedList<>();
        this.membersMap = new HashMap<>();
        this.sortedBy = new HashMap<>();
        this.columns = LeaderboardColumn.init();
        initSortedBy();

        this.name = "casual-bedwars";
        this.waitDuration = 0;

        for(Map.Entry<UUID, String> e: uuids.entrySet()) {
           addMember(String.valueOf(e.getKey()), e.getValue(), update);
        }
    }

    public void addMember(String uuid) {
        Player player = BedwarsRel.getInstance().getServer().getPlayer(uuid);
        if(player != null) {
            addMember(uuid, player.getName());
        }
    }

    public void addMember(String uuid, String name) {
        addMember(uuid, name, true);
    }

    public void addMember(String uuid, String name, boolean update) {
        if (!membersMap.keySet().contains(uuid) && name != null) {
            LeaderboardPlayer lp = new LeaderboardPlayer();
            lp.setName(name);
            members.add(lp);
            membersMap.put(uuid, lp);
        } else if (name != null) {
            membersMap.get(uuid).setName(name);
        }
        if(update) updateDataToLeaderboard(this);
    }

    public void newJoinedGame(String uuid, boolean joined) {
        LeaderboardPlayer lp = membersMap.get(uuid);
        if(lp != null) {
            lp.setJoinedNew(joined);
        }
    }

    public void addMemberFake(String name, double kills, double finalKills, double bedsDestroyed, double deaths, double wins, double points, double games) {
        LeaderboardPlayer lp = new LeaderboardPlayer(name, kills, finalKills, bedsDestroyed, deaths, wins, points, games);
        members.add(lp);
    }

    public void addAttribute(String uuid, String attr) {
        addAttribute(uuid, attr, true);
    }

    public void addAttribute(String uuid, String attr, boolean update) {
        LeaderboardPlayer lp = membersMap.get(uuid);
        if(lp != null) {
            lp.addAttribute(attr);
        }

        if(update) updateDataToLeaderboard(this);
    }

    public void addAttribute(List<String> uuids, String attr) {
        for(String uuid: uuids) {
            addAttribute(uuid, attr, false);
        }
        updateDataToLeaderboard(this);
    }
}
