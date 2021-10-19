package at.kaindorf.games.utils;

import lombok.SneakyThrows;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class UsernameFetcher {

  @SneakyThrows
  private static String call(String uuid) {
    URL url = new URL("https://api.mojang.com/user/profiles/" + uuid + "/names");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

    return getActiveUsername(reader);
  }

  @SneakyThrows
  private static String getActiveUsername(BufferedReader reader) {
    JSONParser parser = new JSONParser();
    JSONArray arr = (JSONArray) parser.parse(reader);

    JSONObject obj = (JSONObject) arr.get(arr.size()-1);
    return String.valueOf(obj.get("name"));
  }

  public static List<String> getUsernamesFromUUIDs(List<String> uuids) {
    List<String> names = new LinkedList<>();
    for(String uuid : uuids){
      names.add(getUsernameFromUUID(uuid));
    }
    return names;
  }

  public static String getUsernameFromUUID(String uuid) {
    return call(uuid);
  }

}
