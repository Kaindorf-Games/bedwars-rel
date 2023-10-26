package at.kaindorf.games.communication;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.communication.dto.Leaderboard;
import at.kaindorf.games.communication.observer.IObserver;
import com.google.gson.Gson;
import org.bukkit.Bukkit;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;

import java.net.URL;

public class HttpHandler implements IObserver {

    private LocalDateTime lastTimeSent;


    private String getBasicAuthenticationHeader() {
        String username = BedwarsRel.getInstance().getConfig().getString("leaderboard.username");
        String password = BedwarsRel.getInstance().getConfig().getString("leaderboard.password");
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    @Override
    public void updateCasualLeaderBoard(Leaderboard leaderboard, boolean force) {
        if(!force && lastTimeSent != null && ChronoUnit.SECONDS.between(lastTimeSent, LocalDateTime.now()) < leaderboard.getWaitDuration()) {
            Bukkit.getLogger().info("Skip leaderboard update");
            return;
        }

        String hostname = BedwarsRel.getInstance().getConfig().getString("leaderboard.api-url");

        try {
            URL url = new URL(hostname+"/leaderboard/"+leaderboard.getName());
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Authorization", getBasicAuthenticationHeader());
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            Gson gson = new Gson();
            String json = gson.toJson(leaderboard, Leaderboard.class);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
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
