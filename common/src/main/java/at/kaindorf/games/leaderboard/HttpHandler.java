package at.kaindorf.games.leaderboard;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.leaderboard.observer.IObserver;
import at.kaindorf.games.leaderboard.observer.LeaderboardBase;
import com.google.gson.Gson;
import org.bukkit.Bukkit;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class HttpHandler implements IObserver {

    private LocalDateTime lastTimeSent;

    private String getBasicAuthenticationHeader() {
        String username = BedwarsRel.getInstance().getConfig().getString("leaderboard.username");
        String password = BedwarsRel.getInstance().getConfig().getString("leaderboard.password");
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    @Override
    public void updateLeaderBoard(LeaderboardBase leaderboard, boolean force) {
        if (!leaderboard.isActive() || (!force && lastTimeSent != null && ChronoUnit.SECONDS.between(lastTimeSent, LocalDateTime.now()) < leaderboard.getWaitBetweenUpdates())) {
            Bukkit.getLogger().info("Skip leaderboard update");
            return;
        }

        Map<String, Double> map = new HashMap<>();
        map.keySet().stream().map(k -> k + " -> "+map.get(k)).reduce((e1, e2) -> e1+e2).orElse("");

        String hostname = BedwarsRel.getInstance().getConfig().getString("leaderboard.api-url", "http://localhost:8000");

        try {
            URL url = new URL(hostname + "/leaderboard/" + leaderboard.getName());
            Bukkit.getLogger().info(url.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Authorization", getBasicAuthenticationHeader());
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            Gson gson = new Gson();
            String json = gson.toJson(leaderboard, leaderboard.getClass());
            Bukkit.getLogger().info(json);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
        } catch (Exception e) {
            BedwarsRel.getInstance().getLogger().log(Level.SEVERE, e.getMessage(), e.getCause());
        } finally {
            lastTimeSent = LocalDateTime.now();
        }
    }

}
