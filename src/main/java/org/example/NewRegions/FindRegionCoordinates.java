package org.example.NewRegions;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;


public class FindRegionCoordinates {

    private static final String USER_AGENT = "Mozilla/5.0";
    public static boolean findCoordinatesRegions(String region, String place, String path) {
        try {
            // Получаем границы области (bounding box) для заданного региона
            String regionBoundingBox = getRegionBoundingBox(region);

            if (regionBoundingBox == null) {
                System.out.println("Не удалось получить границы для региона.");
                return false;
            }

            // Теперь ищем место в пределах этой области
            String query = URLEncoder.encode(place, StandardCharsets.UTF_8.toString());

            // Формируем URL для поиска места внутри границ региона
            String url = "https://nominatim.openstreetmap.org/search?q=" + query
                    + "&format=json&polygon_geojson=1&viewbox=" + regionBoundingBox
                    + "&bounded=1&extratags=1&type=village|town|administrative|city";

            // Отправляем запрос и получаем ответ
            String jsonResponse = sendGetRequest(url);

            // Обрабатываем ответ и форматируем в нужный JSON
            JSONArray jsonArray = new JSONArray(jsonResponse);
            JSONObject featureCollection = new JSONObject();
            JSONArray features = new JSONArray();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject result = jsonArray.getJSONObject(i);
                String placeType = result.getString("type");

                // Выводим результат только для городов, поселков и деревень и округов
                if (placeType.equals("village") || placeType.equals("town") || placeType.equals("city")||placeType.equals("administrative")) {
                    if (result.has("geojson")) {
                        JSONObject geojson = result.getJSONObject("geojson");
                        if (geojson.has("coordinates")) {
                            JSONArray coordinates = geojson.getJSONArray("coordinates");

                            // Создание объекта Feature
                            JSONObject feature = new JSONObject();
                            feature.put("type", "Feature");

                            // Свойства объекта Feature
                            JSONObject properties = new JSONObject();
                            properties.put("NAME", result.getString("display_name"));
                            feature.put("properties", properties);

                            // Геометрия объекта Feature
                            JSONObject geometry = new JSONObject();
                            geometry.put("type", "Polygon");
                            geometry.put("coordinates", coordinates);
                            feature.put("geometry", geometry);

                            // Добавляем Feature в массив features
                            features.put(feature);
                        }
                    }
                }
            }

            // Завершаем создание объекта FeatureCollection
            featureCollection.put("type", "FeatureCollection");
            featureCollection.put("features", features);

            // Записываем форматированный JSON в файл
            try (FileWriter file = new FileWriter(path)) {
                file.write(featureCollection.toString(2)); // Форматированный вывод
                System.out.println("JSON записан в файл: " + path);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    // Метод для получения границ региона (bounding box) по названию
    private static String getRegionBoundingBox(String region) throws Exception {
        String query = URLEncoder.encode(region, StandardCharsets.UTF_8.toString());
        String url = "https://nominatim.openstreetmap.org/search?q=" + query + "&format=json&polygon_geojson=1";

        String jsonResponse = sendGetRequest(url);
        JSONArray jsonArray = new JSONArray(jsonResponse);

        if (jsonArray.length() > 0) {
            JSONObject firstResult = jsonArray.getJSONObject(0);
            if (firstResult.has("boundingbox")) {
                JSONArray boundingBox = firstResult.getJSONArray("boundingbox");
                // boundingBox содержит координаты в формате [юг, север, запад, восток]
                String south = boundingBox.getString(0);
                String north = boundingBox.getString(1);
                String west = boundingBox.getString(2);
                String east = boundingBox.getString(3);

                return west + "," + north + "," + east + "," + south;
            }
        }
        return null;
    }

    // Метод для отправки GET-запроса
    private static String sendGetRequest(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Настройка GET-запроса
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Возвращаем ответ в виде строки
        return response.toString();
    }
}
