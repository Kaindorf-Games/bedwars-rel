package at.kaindorf.games.utils;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;


public class NameTagHandler {

  private class NameTag {
    private String uuid;
    private Set<String> tags;
    private ChatColor tagColor;
    private ChatColor nameColor;

    public NameTag(String uuid, ChatColor tagColor, ChatColor nameColor) {
      this.uuid = uuid;
      this.tags = new HashSet<>();
      this.tagColor = tagColor;
      this.nameColor = nameColor;
      updateListName();
    }

    private void updateListName() {
      Player player = Bukkit.getServer().getPlayer(UUID.fromString(uuid));
      if (player != null) {
        String colorTag = "";
        String colorName = "";
        if (tagColor != null) {
          colorTag = String.valueOf(tagColor);
        }
        if (nameColor != null) {
          colorName = String.valueOf(nameColor);
        }

        String newName = colorTag + tags.stream().map(t -> "[" + t + "]").reduce((e1, e2) -> e1 + " " + e2).orElse("");
        newName += (StringUtils.isBlank(newName)?"":" ") + colorName + player.getName();
        player.setPlayerListName(newName);
        player.setDisplayName(newName);
      }
    }

    public void removeTag(String tag) {
      tags.remove(tag);
      updateListName();
    }

    public void addTag(String tag) {
      tags.add(tag);
      updateListName();
    }

    public void addTags(Set<String> tags) {
      this.tags.addAll(tags);
      updateListName();
    }

    public void setTagColor(ChatColor tagColor) {
      this.tagColor = tagColor;
      updateListName();
    }

    public void setNameColor(ChatColor nameColor) {
      this.nameColor = nameColor;
      updateListName();
    }
  }

  private static NameTagHandler instance;
  public static final ChatColor DEFAULT_TAG_COLOR = ChatColor.GRAY;
  public static final ChatColor DEFAULT_NAME_COLOR = ChatColor.WHITE;

  private NameTagHandler() {
  }

  public static NameTagHandler getInstance() {
    if (instance == null) {
      instance = new NameTagHandler();
    }
    return instance;
  }

  private Map<String, NameTag> nameTags = new HashMap<>();

  public void addTagToPlayer(Player player, String tag) {
    addTagToPlayer(player, tag, DEFAULT_TAG_COLOR, DEFAULT_NAME_COLOR);
  }

  public void addTagToPlayerWithoutColorUpdate(Player player, String tag) {
    addTagToPlayer(player, tag, null, null);
  }

  public void addTagToPlayer(Player player, String tag, ChatColor tagColor, ChatColor nameColor) {
    String uuid = String.valueOf(player.getUniqueId());
    NameTag nameTag;
    if (nameTags.get(uuid) == null) {
      nameTag = new NameTag(uuid, DEFAULT_TAG_COLOR, DEFAULT_NAME_COLOR);
      nameTags.put(uuid, nameTag);
    } else {
      nameTag = nameTags.get(uuid);
    }
    if (tagColor != null) {
      nameTag.setTagColor(tagColor);
    }
    if (nameColor != null) {
      nameTag.setNameColor(nameColor);
    }
    if (!StringUtils.isBlank(tag)) {
      nameTag.addTag(tag);
    }
  }

  public void addTagToPlayerUUID(List<String> uuids, String tag) {
    addTagToPlayerUUID(uuids, tag, ChatColor.GRAY, ChatColor.WHITE);
  }

  public void remove(Player player) {
    nameTags.remove(String.valueOf(player.getUniqueId()));
  }

  public void addTagToPlayerUUID(List<String> uuids, String tag, ChatColor tagColor, ChatColor nameColor) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (uuids.contains(String.valueOf(player.getUniqueId()))) {
        addTagToPlayer(player, tag, tagColor, nameColor);
      }
    }
  }

  public void clearAllTags() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      player.setPlayerListName(player.getName());
    }
    nameTags.clear();
  }

  public void changeNameColor(Player player, ChatColor color) {
    addTagToPlayer(player, null, null, color);
  }

  public void resetColors(Player player) {
    addTagToPlayer(player, null, DEFAULT_TAG_COLOR, DEFAULT_NAME_COLOR);
  }

  public void changeTagColor(Player player, ChatColor color) {
    addTagToPlayer(player, null, color, null);
  }

  public void removeTagFromPlayer(Player player, String tag) {
    NameTag nameTag = nameTags.get(String.valueOf(player.getUniqueId()));
    if (nameTag != null) {
      nameTag.removeTag(tag);
    }
  }
}
