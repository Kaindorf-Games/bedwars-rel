package at.kaindorf.games.leaderboard.leaderboards;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.leaderboard.helpers.GroupColumn;
import at.kaindorf.games.leaderboard.helpers.GroupMember;
import at.kaindorf.games.leaderboard.observer.LeaderboardBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.*;


@Data
@EqualsAndHashCode(callSuper = false)
public class GroupLeaderboard extends LeaderboardBase implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient Map<String, GroupMember> membersMap;
    private List<GroupMember> members;
    private Map<Integer, String> sortedBy;
    private List<GroupColumn> columns;


    private void initLeaderboard() {
        this.members = new LinkedList<>();
        this.membersMap = new HashMap<>();
        this.sortedBy = new HashMap<>();
        this.columns = GroupColumn.initGroupLeaderboardColumns();

        sortedBy.put(1, "Points");
        sortedBy.put(2, "Wins");
        sortedBy.put(3, "Beds Destroyed");
        sortedBy.put(4, "Final Kills");
        sortedBy.put(5, "Kills");

        this.name = BedwarsRel.getInstance().getConfig().getString("leaderboard.name", "bedwars");
        this.type = "Group";
    }

    public GroupLeaderboard() {
        initLeaderboard();
    }

    private GroupMember addMember(String uuid) {
        Player player = BedwarsRel.getInstance().getServer().getPlayer(uuid);
        if (player != null) {
            if (!membersMap.containsKey(uuid) && player.getDisplayName() != null) {
                GroupMember lp = new GroupMember();
                lp.setName(player.getDisplayName());
                members.add(lp);
                membersMap.put(uuid, lp);
                return lp;
            }
        }
        return null;
    }

    public void newJoinedGame(String uuid, boolean joined) {
        GroupMember lp = membersMap.get(uuid);
        if (lp != null) {
            lp.setJoinedNew(joined);
        }
    }

    public void addAttribute(String uuid, String attr) {
        addAttribute(uuid, attr, true);
    }

    public void addAttribute(String uuid, String attr, boolean update) {
        GroupMember lp = membersMap.get(uuid);
        if (lp == null) {
            lp = addMember(uuid);
        }
        if (lp != null) {
            lp.addAttribute(attr);
        }

        if (update) updateDataToLeaderboard(this);
    }

    public void addAttribute(List<String> uuids, String attr) {
        for (String uuid : uuids) {
            addAttribute(uuid, attr, false);
        }
        updateDataToLeaderboard(this);
    }
}
