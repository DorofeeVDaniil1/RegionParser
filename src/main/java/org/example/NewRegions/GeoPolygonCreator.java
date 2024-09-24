package org.example.NewRegions;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.example.Configuration.Config;
import org.json.JSONArray;
import org.json.JSONObject;

import static org.example.BearToken.BearTocken.getIdToken;
import static org.example.Main.PLACE;

public class GeoPolygonCreator {

    private static final String DOMAIN = "amur.mytko.ru";

    private static final JSONObject GEOJSON = (JSONObject) loadGeoJsonFromResources("coordinates.json");

    public static void RunParserDBCoordinates(Boolean DEBUG_MODE) {
        JSONArray features = GEOJSON.getJSONArray("features");
        for (int i = 0; i < features.length(); i++) {
            JSONObject feature = features.getJSONObject(i);
            String type = feature.getJSONObject("geometry").getString("type");
            JSONArray coordinates = feature.getJSONObject("geometry").getJSONArray("coordinates");

            if (DEBUG_MODE) {
                debugGeoJSON(type, coordinates);
            } else {
                processGeoJSON(type, coordinates, feature.getJSONObject("properties").optString("NAME", "Unknown"));
            }
        }
    }

    private static JSONObject loadGeoJsonFromResources(String fileName) {
        try (InputStream inputStream = GeoPolygonCreator.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new RuntimeException("Ресурс не найден: " + fileName);
            }
            // Преобразование InputStream в строку
            String json = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
            // Преобразование строки в JSONObject
            return new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void debugGeoJSON(String type, JSONArray coordinates) {
        System.out.println("Type: " + type);
        if ("MultiPolygon".equals(type)) {
            for (int i = 0; i < coordinates.length(); i++) {
                System.out.println("  Island");
                JSONArray polygon = coordinates.getJSONArray(i);
                for (int j = 0; j < polygon.length(); j++) {
                    System.out.println("    " + (j == 0 ? "Land" : "Hole"));
                    System.out.println("    Length: " + polygon.getJSONArray(j).length());
                }
            }
        } else if ("Polygon".equals(type)) {
            System.out.println("  Island");
            for (int i = 0; i < coordinates.length(); i++) {
                System.out.println("    " + (i == 0 ? "Land" : "Hole"));
                System.out.println("    Length: " + coordinates.getJSONArray(i).length());
            }
        }
    }

    private static void processGeoJSON(String type, JSONArray coordinates, String name) {
        if ("MultiPolygon".equals(type)) {
            for (int i = 0; i < coordinates.length(); i++) {
                JSONArray polygon = coordinates.getJSONArray(i);
                for (int j = 0; j < polygon.length(); j++) {
                    String polyType = (j == 0) ? "land" : "hole";
                    setGeoPoly(polygon.getJSONArray(j), name, polyType);
                }
            }
        } else if ("Polygon".equals(type)) {
            for (int i = 0; i < coordinates.length(); i++) {
                String polyType = (i == 0) ? "land" : "hole";
                setGeoPoly(coordinates.getJSONArray(i), name, polyType);
            }
        }
    }

    private static void setGeoPoly(JSONArray coords, String name, String type) {
        String query = "mutation createGeoPolygon($lanlngSet: [LatLngInput]){  createGeoPolygon(lanlngSet: $lanlngSet){ id }}";
        JSONObject json = new JSONObject();
        json.put("query", query);

        JSONArray latLngArray = new JSONArray();
        for (int i = 0; i < coords.length(); i++) {
            JSONArray coord = coords.getJSONArray(i);
            Map<String, Object> latLngMap = new HashMap<>();
            latLngMap.put("order", i);
            latLngMap.put("latitude", coord.getDouble(1));
            latLngMap.put("longitude", coord.getDouble(0));
            latLngArray.put(new JSONObject(latLngMap));
        }

        latLngArray.put(latLngArray.get(0)); // Замыкаем полигон
        latLngArray.getJSONObject(latLngArray.length() - 1).put("order", latLngArray.length() - 1);

        JSONObject variables = new JSONObject();
        variables.put("lanlngSet", latLngArray);
        json.put("variables", variables);

        sendGeoPolyRequest(json, name, type);
    }

    private static void sendGeoPolyRequest(JSONObject json, String name, String type) {
        Config config = new Config();
        String TOKEN = getIdToken();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://" + DOMAIN + "/app/graphql"))
                    .header("Authorization", "Bearer " + TOKEN)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            System.out.println(name + " [" + type + "] : ...");

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject responseBody = new JSONObject(response.body());
            String id = responseBody.getJSONObject("data").getJSONObject("createGeoPolygon").getString("id");
            System.out.println(name + " [" + type + "] : " + id);
            System.out.println("select new_region('"+ PLACE +"' ,'"+id+"', уровень);");;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
