package at.kaindorf.games;

import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.Bedwars.*;
import at.kaindorf.games.commands.Tournament.*;
import at.kaindorf.games.commands.development.DevHelpCommand;
import at.kaindorf.games.commands.development.DevKillAllCommand;
import at.kaindorf.games.database.DatabaseManager;
import at.kaindorf.games.game.*;
import at.kaindorf.games.listener.*;
import at.kaindorf.games.localization.LocalizationConfig;
import at.kaindorf.games.shop.Specials.SpecialItem;
import at.kaindorf.games.statistics.PlayerStatistic;
import at.kaindorf.games.statistics.PlayerStatisticManager;
import at.kaindorf.games.statistics.StorageType;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.TourneyProperties;
import at.kaindorf.games.updater.ConfigUpdater;
import at.kaindorf.games.updater.PluginUpdater;
import at.kaindorf.games.utils.*;
import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class BedwarsRel extends JavaPlugin {

  public static int PROJECT_ID = 91743;
  private static BedwarsRel instance = null;
  private static Boolean locationSerializable = null;
  private List<Material> breakableTypes = null;
  @Getter
  private Bugsnag bugsnag;
  private ArrayList<BaseCommand> bwCommands = new ArrayList<>();
  private ArrayList<BaseCommand> tourneyCommands = new ArrayList<>();
  private ArrayList<BaseCommand> devCommands = new ArrayList<>();
  private Package craftbukkit = null;
  private DatabaseManager dbManager = null;
  @Getter
  private GameManager gameManager = null;
  private IHologramInteraction holographicInteraction = null;
  private boolean isSpigot = false;
  @Getter
  private HashMap<String, LocalizationConfig> localization = new HashMap<>();
  private Package minecraft = null;
  @Getter
  private HashMap<UUID, String> playerLocales = new HashMap<>();
  private PlayerStatisticManager playerStatisticManager = null;
  private ScoreboardManager scoreboardManager = null;
  private YamlConfiguration shopConfig = null;
  private BukkitTask timeTask = null;
  private BukkitTask updateChecker = null;
  private String version = null;
  @Setter
  @Getter
  private BukkitTask gameLoopTask = null;
  @Getter
  private Mode mode = Mode.TOURNAMENT;
  @Getter
  private final boolean isDevMode = false;
  @Getter
  @Setter
  private boolean leaderBoardActive = false;


  public static String _l(CommandSender commandSender, String key, String singularValue,
                          Map<String, String> params) {
    return BedwarsRel
        ._l(BedwarsRel.getInstance().getSenderLocale(commandSender), key, singularValue, params);
  }

  public static String _l(String locale, String key, String singularValue,
                          Map<String, String> params) {
    if ("1".equals(params.get(singularValue))) {
      return BedwarsRel._l(locale, key + "-one", params);
    }
    return BedwarsRel._l(locale, key, params);
  }

  public static String _l(CommandSender commandSender, String key, Map<String, String> params) {
    return BedwarsRel._l(BedwarsRel.getInstance().getSenderLocale(commandSender), key, params);
  }

  public static String _l(String locale, String key, Map<String, String> params) {
    if (!BedwarsRel.getInstance().localization.containsKey(locale)) {
      BedwarsRel.getInstance().loadLocalization(locale);
    }
    return (String) BedwarsRel.getInstance().getLocalization().get(locale).get(key, params);
  }

  public static String _l(CommandSender commandSender, String key) {
    return BedwarsRel._l(BedwarsRel.getInstance().getSenderLocale(commandSender), key);
  }

  public static String _l(String key) {
    return BedwarsRel._l(BedwarsRel.getInstance().getConfig().getString("locale"), key);
  }

  public static String _l(String locale, String key) {
    if (!BedwarsRel.getInstance().localization.containsKey(locale)) {
      BedwarsRel.getInstance().loadLocalization(locale);
    }
    return (String) BedwarsRel.getInstance().getLocalization().get(locale).get(key);
  }

  public static BedwarsRel getInstance() {
    return BedwarsRel.instance;
  }

  public boolean allPlayersBackToMainLobby() {
    if (this.getConfig().contains("endgame.all-players-to-mainlobby")
        && this.getConfig().isBoolean("endgame.all-players-to-mainlobby")) {
      return this.getConfig().getBoolean("endgame.all-players-to-mainlobby");
    }

    return false;

  }

  private void checkUpdates() {
    try {
      if (this.getBooleanConfig("check-updates", true)) {
        this.updateChecker = new BukkitRunnable() {

          @Override
          public void run() {
            final BukkitRunnable task = this;
            PluginUpdater.UpdateCallback callback = new PluginUpdater.UpdateCallback() {

              @Override
              public void onFinish(PluginUpdater updater) {
                if (updater.getResult() == PluginUpdater.UpdateResult.SUCCESS) {
                  task.cancel();
                }
              }
            };

            new PluginUpdater(
                BedwarsRel.getInstance(), BedwarsRel.PROJECT_ID, BedwarsRel.getInstance().getFile(),
                PluginUpdater.UpdateType.DEFAULT, callback,
                BedwarsRel.getInstance().getBooleanConfig("update-infos", true));
          }

        }.runTaskTimerAsynchronously(BedwarsRel.getInstance(), 40L, 36000L);
      }
    } catch (Exception ex) {
      BedwarsRel.getInstance().getBugsnag().notify(ex);
      this.getServer().getConsoleSender().sendMessage(
          ChatWriter.pluginMessage(ChatColor.RED + "Check for updates not successful: Error!"));
    }
  }


  private void disableBugsnag() {
    this.bugsnag.addCallback(new Callback() {
      @Override
      public void beforeNotify(Report report) {
        report.cancel();
      }
    });
  }

  public void dispatchRewardCommands(List<String> commands, Map<String, String> replacements) {
    for (String command : commands) {
      command = command.trim();
      if ("".equals(command)) {
        continue;
      }

      if ("none".equalsIgnoreCase(command)) {
        break;
      }

      if (command.startsWith("/")) {
        command = command.substring(1);
      }

      for (Entry<String, String> entry : replacements.entrySet()) {
        command = command.replace(entry.getKey(), entry.getValue());
      }

      BedwarsRel.getInstance().getServer()
          .dispatchCommand(BedwarsRel.getInstance().getServer().getConsoleSender(), command);
    }
  }

  private void enableBugsnag() {
    this.bugsnag.addCallback(new Callback() {
      @Override
      public void beforeNotify(Report report) {
        Boolean shouldBeSent = false;
        for (StackTraceElement stackTraceElement : report.getException().getStackTrace()) {
          if (stackTraceElement.toString().contains("at.kaindorf.games.BedwarsRel")) {
            shouldBeSent = true;
            break;
          }
        }
        if (!shouldBeSent) {
          report.cancel();
        }

        report.setUserId(SupportData.getIdentifier());
        if (!SupportData.getPluginVersionBuild().equalsIgnoreCase("unknown")) {
          report.addToTab("Server", "Version Build",
              BedwarsRel.getInstance().getDescription().getVersion() + " "
                  + SupportData.getPluginVersionBuild());
        }
        report.addToTab("Server", "Version", SupportData.getServerVersion());
        report.addToTab("Server", "Version Bukkit", SupportData.getBukkitVersion());
        report.addToTab("Server", "Server Mode", SupportData.getServerMode());
        report.addToTab("Server", "Plugins", SupportData.getPlugins());
      }
    });
  }

  private ArrayList<BaseCommand> filterCommandsByPermission(ArrayList<BaseCommand> commands, String permission) {
    Iterator<BaseCommand> it = commands.iterator();

    while (it.hasNext()) {
      BaseCommand command = it.next();
      if (!command.getPermission().equals(permission)) {
        it.remove();
      }
    }

    return commands;
  }

  public List<String> getAllowedCommands() {
    FileConfiguration config = this.getConfig();
    if (config.contains("allowed-commands") && config.isList("allowed-commands")) {
      return config.getStringList("allowed-commands");
    }

    return new ArrayList<String>();
  }

  @SuppressWarnings("unchecked")
  public ArrayList<BaseCommand> getBaseCommands() {
    ArrayList<BaseCommand> commands = (ArrayList<BaseCommand>) this.bwCommands.clone();
    commands = this.filterCommandsByPermission(commands, "base");

    return commands;
  }

  public boolean getBooleanConfig(String key, boolean defaultBool) {
    FileConfiguration config = this.getConfig();
    if (config.contains(key) && config.isBoolean(key)) {
      return config.getBoolean(key);
    }
    return defaultBool;
  }

  public String getBungeeHub() {
    if (this.getConfig().contains("bungeecord.hubserver")) {
      return this.getConfig().getString("bungeecord.hubserver");
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  public ArrayList<BaseCommand> getBedwarsCommands() {
    return (ArrayList<BaseCommand>) this.bwCommands.clone();
  }

  @SuppressWarnings("unchecked")
  public ArrayList<BaseCommand> getDevCommands() {
    return (ArrayList<BaseCommand>) this.devCommands.clone();
  }

  @SuppressWarnings("unchecked")
  public ArrayList<BaseCommand> getTourneyCommands() {
    return (ArrayList<BaseCommand>) this.tourneyCommands.clone();
  }

  @SuppressWarnings("unchecked")
  public ArrayList<BaseCommand> getCommandsByPermission(String permission) {
    ArrayList<BaseCommand> commands = (ArrayList<BaseCommand>) this.bwCommands.clone();
    commands.addAll((ArrayList<BaseCommand>) tourneyCommands.clone());
    commands = this.filterCommandsByPermission(commands, permission);

    return commands;
  }

  public Package getCraftBukkit() {
    try {
      if (this.craftbukkit == null) {
        return Package.getPackage("org.bukkit.craftbukkit."
            + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]);
      } else {
        return this.craftbukkit;
      }
    } catch (Exception ex) {
      BedwarsRel.getInstance().getBugsnag().notify(ex);
      this.getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED
          + BedwarsRel._l(this.getServer().getConsoleSender(), "errors.packagenotfound",
          ImmutableMap.of("package", "craftbukkit"))));
      return null;
    }
  }

  @SuppressWarnings("rawtypes")
  public Class getCraftBukkitClass(String classname) {
    try {
      if (this.craftbukkit == null) {
        this.craftbukkit = this.getCraftBukkit();
      }

      return Class.forName(this.craftbukkit.getName() + "." + classname);
    } catch (Exception ex) {
      BedwarsRel.getInstance().getBugsnag().notify(ex);
      this.getServer().getConsoleSender()
          .sendMessage(ChatWriter.pluginMessage(
              ChatColor.RED + BedwarsRel
                  ._l(this.getServer().getConsoleSender(), "errors.classnotfound",
                      ImmutableMap.of("package", "craftbukkit", "class", classname))));
      return null;
    }
  }

  public String getCurrentVersion() {
    return this.version;
  }

  public DatabaseManager getDatabaseManager() {
    return this.dbManager;
  }

  public String getFallbackLocale() {
    return "en_US";
  }

  public IHologramInteraction getHolographicInteractor() {
    return this.holographicInteraction;
  }

  public int getIntConfig(String key, int defaultInt) {
    FileConfiguration config = this.getConfig();
    if (config.contains(key) && config.isInt(key)) {
      return config.getInt(key);
    }
    return defaultInt;
  }

  private boolean getIsSpigot() {
    try {
      Package spigotPackage = Package.getPackage("org.spigotmc");
      return (spigotPackage != null);
    } catch (Exception e) {
      BedwarsRel.getInstance().getBugsnag().notify(e);
    }

    return false;
  }

  /**
   * Returns the max length of a game in seconds
   *
   * @return The length of the game in seconds
   */
  public int getMaxLength() {
    if (this.getConfig().contains("gamelength") && this.getConfig().isInt("gamelength")) {
      return this.getConfig().getInt("gamelength") * 60;
    }

    // fallback time is 60 minutes
    return 60 * 60;
  }

  public Package getMinecraftPackage() {
    try {
      if (this.minecraft == null) {
        return Package.getPackage("net.minecraft.server."
            + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]);
      } else {
        return this.minecraft;
      }
    } catch (Exception ex) {
      BedwarsRel.getInstance().getBugsnag().notify(ex);
      this.getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED
          + BedwarsRel._l(this.getServer().getConsoleSender(), "errors.packagenotfound",
          ImmutableMap.of("package", "minecraft server"))));
      return null;
    }
  }

  @SuppressWarnings("rawtypes")
  public Class getMinecraftServerClass(String classname) {
    try {
      if (this.minecraft == null) {
        this.minecraft = this.getMinecraftPackage();
      }

      return Class.forName(this.minecraft.getName() + "." + classname);
    } catch (Exception ex) {
      BedwarsRel.getInstance().getBugsnag().notify(ex);
      this.getServer().getConsoleSender()
          .sendMessage(ChatWriter.pluginMessage(
              ChatColor.RED + BedwarsRel
                  ._l(this.getServer().getConsoleSender(), "errors.classnotfound",
                      ImmutableMap.of("package", "minecraft server", "class", classname))));
      return null;
    }
  }

  public String getMissingHoloDependency() {
    if (!BedwarsRel.getInstance().isHologramsEnabled()) {
      String missingHoloDependency = null;
      if (this.getServer().getPluginManager().isPluginEnabled("HologramAPI")
          || this.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
        if (this.getServer().getPluginManager().isPluginEnabled("HologramAPI")) {
          missingHoloDependency = "PacketListenerApi";
          return missingHoloDependency;
        }
        if (this.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
          missingHoloDependency = "ProtocolLib";
          return missingHoloDependency;
        }
      } else {
        missingHoloDependency = "HolographicDisplays and ProtocolLib";
        return missingHoloDependency;
      }
    }
    return null;
  }

  public PlayerStatisticManager getPlayerStatisticManager() {
    return this.playerStatisticManager;
  }

  public Integer getRespawnProtectionTime() {
    FileConfiguration config = this.getConfig();
    if (config.contains("respawn-protection") && config.isInt("respawn-protection")) {
      return config.getInt("respawn-protection");
    }
    return 0;
  }

  public ScoreboardManager getScoreboardManager() {
    return this.scoreboardManager;
  }

  public String getSenderLocale(CommandSender commandSender) {
    String locale = BedwarsRel.getInstance().getConfig().getString("locale");
    if (commandSender instanceof Player) {
      Player player = (Player) commandSender;
      if (BedwarsRel.getInstance().getPlayerLocales().containsKey(player.getUniqueId())) {
        locale = BedwarsRel.getInstance().getPlayerLocales().get(player.getUniqueId());
      }
    }
    return locale;
  }

  @SuppressWarnings("unchecked")
  public ArrayList<BaseCommand> getSetupCommands() {
    ArrayList<BaseCommand> commands = (ArrayList<BaseCommand>) this.bwCommands.clone();
    commands = this.filterCommandsByPermission(commands, "setup");

    return commands;
  }

  public FileConfiguration getShopConfig() {
    return this.shopConfig;
  }

  public StorageType getStatisticStorageType() {
    String storage = this.getStringConfig("statistics.storage", "yaml");
    return StorageType.getByName(storage);
  }

  public String getStringConfig(String key, String defaultString) {
    FileConfiguration config = this.getConfig();
    if (config.contains(key) && config.isString(key)) {
      return config.getString(key);
    }
    return defaultString;
  }

  public Class<?> getVersionRelatedClass(String className) {
    try {
      Class<?> clazz = Class.forName(
          "at.kaindorf.games.com." + this.getCurrentVersion().toLowerCase() + "." + className);
      return clazz;
    } catch (Exception ex) {
      BedwarsRel.getInstance().getBugsnag().notify(ex);
      this.getServer().getConsoleSender()
          .sendMessage(ChatWriter.pluginMessage(ChatColor.RED
              + "Couldn't find version related class at.kaindorf.games.com."
              + this.getCurrentVersion() + "." + className));
    }

    return null;
  }

  public String getYamlDump(YamlConfiguration config) {
    try {
      String fullstring = config.saveToString();
      String endstring = fullstring;
      endstring = Utils.unescape_perl_string(fullstring);

      return endstring;
    } catch (Exception ex) {
      BedwarsRel.getInstance().getBugsnag().notify(ex);
      ex.printStackTrace();
    }

    return null;
  }

  public boolean isBreakableType(Material type) {
    return ((BedwarsRel.getInstance().getConfig().getBoolean("breakable-blocks.use-as-blacklist")
        && !this.breakableTypes.contains(type))
        || (!BedwarsRel.getInstance().getConfig().getBoolean("breakable-blocks.use-as-blacklist")
        && this.breakableTypes.contains(type)));
  }

  public boolean isBungee() {
    return this.getConfig().getBoolean("bungeecord.enabled");
  }

  public boolean isHologramsEnabled() {
    return (this.getServer().getPluginManager().isPluginEnabled("HologramAPI")
        && this.getServer().getPluginManager().isPluginEnabled("PacketListenerApi"))
        || (this.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")
        && this.getServer().getPluginManager().isPluginEnabled("ProtocolLib"));
  }

  public boolean isLocationSerializable() {
    if (BedwarsRel.locationSerializable == null) {
      try {
        Location.class.getMethod("serialize");
        BedwarsRel.locationSerializable = true;
      } catch (Exception ex) {
        BedwarsRel.getInstance().getBugsnag().notify(ex);
        BedwarsRel.locationSerializable = false;
      }
    }

    return BedwarsRel.locationSerializable;
  }

  public boolean isMineshafterPresent() {
    try {
      Class.forName("mineshafter.MineServer");
      return true;
    } catch (Exception e) {
      // NO ERROR
      return false;
    }
  }

  public boolean isSpigot() {
    return this.isSpigot;
  }

  public void loadConfigInUTF() {
    File configFile = new File(this.getDataFolder(), "config.yml");
    if (!configFile.exists()) {
      return;
    }

    try {
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
      this.getConfig().load(reader);
    } catch (Exception e) {
      BedwarsRel.getInstance().getBugsnag().notify(e);
      e.printStackTrace();
    }

    if (this.getConfig() == null) {
      return;
    }

    // load breakable materials
    this.breakableTypes = new ArrayList<Material>();
    for (String material : this.getConfig().getStringList("breakable-blocks.list")) {
      if (material.equalsIgnoreCase("none")) {
        continue;
      }

      Material mat = Utils.parseMaterial(material);
      if (mat == null) {
        continue;
      }

      if (this.breakableTypes.contains(mat)) {
        continue;
      }

      this.breakableTypes.add(mat);
    }
  }

  private void loadDatabase() {
    if (!this.getBooleanConfig("statistics.enabled", false)
        || !"database".equals(this.getStringConfig("statistics.storage", "yaml"))) {
      return;
    }

    this.getServer().getConsoleSender()
        .sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + "Initialize database ..."));

    String host = this.getStringConfig("database.host", null);
    int port = this.getIntConfig("database.port", 3306);
    String user = this.getStringConfig("database.user", null);
    String password = this.getStringConfig("database.password", null);
    String db = this.getStringConfig("database.db", null);
    String tablePrefix = this.getStringConfig("database.table-prefix", "bw_");

    if (host == null || user == null || password == null || db == null) {
      return;
    }

    this.dbManager = new DatabaseManager(host, port, user, password, db, tablePrefix);
    this.dbManager.initialize();

    this.getServer().getConsoleSender()
        .sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + "Update database ..."));

    this.getServer().getConsoleSender()
        .sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + "Done."));
  }

  private void loadLocalization(String locale) {
    if (!this.localization.containsKey(locale)) {
      this.localization.put(locale, new LocalizationConfig(locale));
    }
  }

  public void loadShop() {
    File file = new File(BedwarsRel.getInstance().getDataFolder(), "shop.yml");
    if (!file.exists()) {
      // create default file
      this.saveResource("shop.yml", false);

      // wait until it's really saved
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        BedwarsRel.getInstance().getBugsnag().notify(e);
        e.printStackTrace();
      }
    }

    this.shopConfig = new YamlConfiguration();

    try {
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
      this.shopConfig.load(reader);
    } catch (Exception e) {
      BedwarsRel.getInstance().getBugsnag().notify(e);
      this.getServer().getConsoleSender().sendMessage(
          ChatWriter.pluginMessage(ChatColor.RED + "Couldn't load shop! Error in parsing shop!"));
      e.printStackTrace();
    }
  }

  private void loadStatistics() {
    this.playerStatisticManager = new PlayerStatisticManager();
    this.playerStatisticManager.initialize();
  }

  private String loadVersion() {
    String packName = Bukkit.getServer().getClass().getPackage().getName();
    return packName.substring(packName.lastIndexOf('.') + 1);
  }

  public boolean metricsEnabled() {
    if (this.getConfig().contains("plugin-metrics")
        && this.getConfig().isBoolean("plugin-metrics")) {
      return this.getConfig().getBoolean("plugin-metrics");
    }

    return false;
  }

  @Override
  public void onDisable() {
    if (gameLoopTask != null) {
      Bukkit.getLogger().info("Cancel: " + gameLoopTask.getTaskId());
      gameLoopTask.cancel();
    }

    this.stopTimeListener();
    this.gameManager.unloadGames();


    if (this.isHologramsEnabled() && this.holographicInteraction != null) {
      this.holographicInteraction.unloadHolograms();
    }

  }

  @Override
  public void onEnable() {
    BedwarsRel.instance = this;

    if (this.getDescription().getVersion().contains("-SNAPSHOT")
        && System.getProperty("IReallyKnowWhatIAmDoingISwear") == null) {
      this.getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "*** Warning, you are using a development build ***"));
      this.getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "*** You will get NO support regarding this build ***"));
      this.getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "*** Please download a stable build from https://github.com/BedwarsRel/BedwarsRel/releases ***"));
      this.getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "*** Server will start in 10 seconds ***"));
      try {
        Thread.sleep(TimeUnit.SECONDS.toMillis(10));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    this.registerBugsnag();

    // register classes
    this.registerConfigurationClasses();

    // save default config
    this.saveDefaultConfig();
    this.loadConfigInUTF();

    this.getConfig().options().copyDefaults(true);
    this.getConfig().options().copyHeader(true);

    this.craftbukkit = this.getCraftBukkit();
    this.minecraft = this.getMinecraftPackage();
    this.version = this.loadVersion();

    ConfigUpdater configUpdater = new ConfigUpdater();
    configUpdater.addConfigs();
    this.saveConfiguration();
    this.loadConfigInUTF();

    if (this.getBooleanConfig("send-error-data", true) && this.bugsnag != null) {
      this.enableBugsnag();
    } else {
      this.disableBugsnag();
    }

    this.loadShop();

    this.isSpigot = this.getIsSpigot();
    this.loadDatabase();

    this.registerCommands();
    this.registerListener();

    this.gameManager = new GameManager();

    // bungeecord
    if (BedwarsRel.getInstance().isBungee()) {
      this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    this.loadStatistics();
    this.loadLocalization(this.getConfig().getString("locale"));

    this.checkUpdates();

    // Loading
    this.scoreboardManager = Bukkit.getScoreboardManager();
    this.gameManager.loadGames();
    this.startTimeListener();
    this.startMetricsIfEnabled();

    // holograms
    if (this.isHologramsEnabled()) {
      if (this.getServer().getPluginManager().isPluginEnabled("HologramAPI")) {
        this.holographicInteraction = new HologramAPIInteraction();
      } else if (this.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
        this.holographicInteraction = new HolographicDisplaysInteraction();
      }
      this.holographicInteraction.loadHolograms();
    }

    // Tournament mode
    loadTournamentProps();
    Tournament.getInstance();
  }

  @SneakyThrows
  public void loadTournamentProps() {
    TourneyProperties.readTourneyProperties();
  }

  private void registerBugsnag() {
    try {
      this.bugsnag = new Bugsnag("c23593c1e2f40fc0da36564af1bd00c6");
      this.bugsnag.setAppVersion(SupportData.getPluginVersion());
      this.bugsnag.setProjectPackages("at.kaindorf.games");
      this.bugsnag.setReleaseStage(SupportData.getPluginVersionType());
    } catch (Exception e) {
      this.getServer().getConsoleSender().sendMessage(
          ChatWriter.pluginMessage(ChatColor.GOLD + "Couldn't register Bugsnag."));
    }
  }

  private void registerCommands() {
    MyCommandExecutor executor = new MyCommandExecutor(this);

    // Bedwars Commands
    this.bwCommands.add(new HelpCommand(this));
    this.bwCommands.add(new SetSpawnerCommand(this));
    this.bwCommands.add(new AddGameCommand(this));
    this.bwCommands.add(new StartGameCommand(this));
    this.bwCommands.add(new StopGameCommand(this));
    this.bwCommands.add(new SetRegionCommand(this));
    this.bwCommands.add(new AddTeamCommand(this));
    this.bwCommands.add(new SaveGameCommand(this));
    this.bwCommands.add(new JoinGameCommand(this));
    this.bwCommands.add(new SetSpawnCommand(this));
    this.bwCommands.add(new SetLobbyCommand(this));
    this.bwCommands.add(new LeaveGameCommand(this));
    this.bwCommands.add(new SetTargetCommand(this));
    this.bwCommands.add(new SetBedCommand(this));
    this.bwCommands.add(new ReloadCommand(this));
    this.bwCommands.add(new SetMainLobbyCommand(this));
    this.bwCommands.add(new ListGamesCommand(this));
    this.bwCommands.add(new RegionNameCommand(this));
    this.bwCommands.add(new RemoveTeamCommand(this));
    this.bwCommands.add(new RemoveGameCommand(this));
    this.bwCommands.add(new ClearSpawnerCommand(this));
    this.bwCommands.add(new GameTimeCommand(this));
    this.bwCommands.add(new StatsCommand(this));
    this.bwCommands.add(new SetMinPlayersCommand(this));
    this.bwCommands.add(new SetGameBlockCommand(this));
    this.bwCommands.add(new SetBuilderCommand(this));
    this.bwCommands.add(new SetAutobalanceCommand(this));
    this.bwCommands.add(new KickCommand(this));
    this.bwCommands.add(new AddTeamJoinCommand(this));
    this.bwCommands.add(new AddHoloCommand(this));
    this.bwCommands.add(new RemoveHoloCommand(this));
    this.bwCommands.add(new DebugPasteCommand(this));
    this.bwCommands.add(new ItemsPasteCommand(this));
    this.bwCommands.add(new AutoConnectCommand(this));
    this.bwCommands.add(new SwitchBedwarsModeCommand(this));
    this.bwCommands.add(new ShowBedwarsModeCommand(this));
    this.bwCommands.add(new MovePlayersIntoGame(this));
    this.bwCommands.add(new SpectateGameCommand(this));
    this.bwCommands.add(new StartLeaderboardCommand(this));
    this.bwCommands.add(new StopLeaderboardCommand(this));
    this.bwCommands.add(new ClearLeaderboardCommand(this));
    this.bwCommands.add(new LeaderboardWaitCommand(this));
    this.bwCommands.add(new UpdateLeaderboardCommand(this));
    this.getCommand("bw").setExecutor(executor);

    // Tournament Commands
    this.tourneyCommands.add(new TournamentHelpCommand(this));
    this.tourneyCommands.add(new AddTournamentTeamsCommand(this));
    this.tourneyCommands.add(new ClearTournamentCommand(this));
    this.tourneyCommands.add(new SaveTourneyEntitiesCommand(this));
    this.tourneyCommands.add(new ShowTeamsCommand(this));
    this.tourneyCommands.add(new ShowGroupsCommand(this));
    this.tourneyCommands.add(new LoadTourneyEntitiesCommand(this));
    this.tourneyCommands.add(new StartTournamentCommand(this));
    this.tourneyCommands.add(new StopTournamentCommand(this));
    this.tourneyCommands.add(new ContinueStoppedTournamentCommand(this));
    this.tourneyCommands.add(new TeamPauseCommand(this));
    this.tourneyCommands.add(new TeamNoPauseCommand(this));
    this.tourneyCommands.add(new RemoveTourneyTeamCommand(this));
    this.tourneyCommands.add(new SkipMatchCommand(this));
    this.tourneyCommands.add(new ShowMatchesToDoCommand(this));
    this.tourneyCommands.add(new AddPointsCommand(this));
    this.tourneyCommands.add(new RemoveMissingTeamsCommands(this));
    this.tourneyCommands.add(new LANCreateTeamCommand(this));
    this.tourneyCommands.add(new LANSetTeamSizeCommand(this));
    this.tourneyCommands.add(new LANJoinTeamCommand(this));
    this.tourneyCommands.add(new LANLeaveTeamCommand(this));
    this.getCommand("tourney").setExecutor(executor);

    if(this.isDevMode) {
      this.devCommands.add(new DevHelpCommand(this));
      this.devCommands.add(new DevKillAllCommand(this));
    }
    this.getCommand("dev").setExecutor(executor);
  }

  private void registerConfigurationClasses() {
    ConfigurationSerialization.registerClass(ResourceSpawner.class, "RessourceSpawner");
    ConfigurationSerialization.registerClass(Team.class, "Team");
    ConfigurationSerialization.registerClass(PlayerStatistic.class, "PlayerStatistic");
  }

  private void registerListener() {
    new TournamentListener();
    new WeatherListener();
    new BlockListener();
    new PlayerListener();
    new HangingListener();
    new EntityListener();
    new ServerListener();
    new SignListener();
    new ChunkListener();

    if (this.isSpigot()) {
      new PlayerSpigotListener();
    }

    SpecialItem.loadSpecials();
  }

  public void reloadLocalization() {
    this.localization = new HashMap<>();
    this.loadLocalization(this.getConfig().getString("locale"));
  }

  public void saveConfiguration() {
    File file = new File(BedwarsRel.getInstance().getDataFolder(), "config.yml");
    try {
      file.mkdirs();

      String data = this.getYamlDump((YamlConfiguration) this.getConfig());

      FileOutputStream stream = new FileOutputStream(file);
      OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");

      try {
        writer.write(data);
      } finally {
        writer.close();
        stream.close();
      }
    } catch (Exception ex) {
      BedwarsRel.getInstance().getBugsnag().notify(ex);
      ex.printStackTrace();
    }
  }

  public boolean spectationEnabled() {
    if (this.getConfig().contains("spectation-enabled")
        && this.getConfig().isBoolean("spectation-enabled")) {
      return this.getConfig().getBoolean("spectation-enabled");
    }
    return true;
  }

  public void startMetricsIfEnabled() {
    if (this.metricsEnabled()) {
      new BStatsMetrics(this);
      try {
        McStatsMetrics mcStatsMetrics = new McStatsMetrics(this);
        mcStatsMetrics.start();
      } catch (Exception ex) {
        BedwarsRel.getInstance().getBugsnag().notify(ex);
        this.getServer().getConsoleSender().sendMessage(ChatWriter
            .pluginMessage(ChatColor.RED + "Metrics are enabled, but couldn't send data!"));
      }
    }
  }

  private void startTimeListener() {
    this.timeTask = this.getServer().getScheduler().runTaskTimer(this, new Runnable() {

      @Override
      public void run() {
        for (Game g : BedwarsRel.getInstance().getGameManager().getGames()) {
          if (g.getState() == GameState.RUNNING) {
            g.getRegion().getWorld().setTime(g.getTime());
          }
        }
      }
    }, (long) 5 * 20, (long) 5 * 20);
  }

  public boolean statisticsEnabled() {
    return this.getBooleanConfig("statistics.enabled", false);
  }

  private void stopTimeListener() {
    try {
      this.timeTask.cancel();
    } catch (Exception ex) {
      // Timer isn't running. Just ignore.
    }

    try {
      this.updateChecker.cancel();
    } catch (Exception ex) {
      // Timer isn't running. Just ignore.
    }
  }

  public boolean toMainLobby() {
    if (this.getConfig().contains("endgame.mainlobby-enabled")) {
      return this.getConfig().getBoolean("endgame.mainlobby-enabled");
    }

    return false;
  }

  public void changeModeTo(Mode mode) {
    this.mode = mode;
  }

  public enum Mode {
    TOURNAMENT, NORMAL, LAN
  }

}
