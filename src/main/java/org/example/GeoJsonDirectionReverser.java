package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

public class GeoJsonDirectionReverser {

    public static void main(String[] args) {
        // Пример GeoJSON многоугольника
        String geoJson = """
            {
                "type": "Polygon",
                "coordinates": [
                                           [
                                             [
                                               104.21237099999999,
                                               52.303380999999995
                                             ],
                                             [
                                               104.21430199999999,
                                               52.303090999999995
                                             ],
                                             [
                                               104.21463999999999,
                                               52.303002
                                             ],
                                             [
                                               104.21455499999999,
                                               52.3026
                                             ],
                                             [
                                               104.215156,
                                               52.302409
                                             ],
                                             [
                                               104.215589,
                                               52.302285999999995
                                             ],
                                             [
                                               104.215724,
                                               52.302197
                                             ],
                                             [
                                               104.215955,
                                               52.302136
                                             ],
                                             [
                                               104.216242,
                                               52.302031
                                             ],
                                             [
                                               104.216228,
                                               52.301991
                                             ],
                                             [
                                               104.21649099999999,
                                               52.301916999999996
                                             ],
                                             [
                                               104.21660299999999,
                                               52.302122999999995
                                             ],
                                             [
                                               104.21687299999999,
                                               52.302029999999995
                                             ],
                                             [
                                               104.21803,
                                               52.301472999999994
                                             ],
                                             [
                                               104.218673,
                                               52.301324
                                             ],
                                             [
                                               104.219534,
                                               52.301182999999995
                                             ],
                                             [
                                               104.219478,
                                               52.301381
                                             ],
                                             [
                                               104.21940599999999,
                                               52.301525
                                             ],
                                             [
                                               104.219039,
                                               52.302112
                                             ],
                                             [
                                               104.218963,
                                               52.302282999999996
                                             ],
                                             [
                                               104.21891199999999,
                                               52.302507
                                             ],
                                             [
                                               104.21902299999999,
                                               52.303109
                                             ],
                                             [
                                               104.219222,
                                               52.304016
                                             ],
                                             [
                                               104.21836499999999,
                                               52.304342
                                             ],
                                             [
                                               104.21787499999999,
                                               52.305211
                                             ],
                                             [
                                               104.21767,
                                               52.306656
                                             ],
                                             [
                                               104.216042,
                                               52.306835
                                             ],
                                             [
                                               104.214642,
                                               52.307021
                                             ],
                                             [
                                               104.212835,
                                               52.307167
                                             ],
                                             [
                                               104.212768,
                                               52.306515
                                             ],
                                             [
                                               104.21287,
                                               52.306160999999996
                                             ],
                                             [
                                               104.213246,
                                               52.306154
                                             ],
                                             [
                                               104.21279299999999,
                                               52.304604
                                             ],
                                             [
                                               104.21237099999999,
                                               52.303380999999995
                                             ]
                                           ]
                                         ]
            }
        """;

        JSONObject geoJsonObject = new JSONObject(geoJson);
        System.out.println("Оригинальный GeoJSON:");
        System.out.println(geoJsonObject.toString(2));

        reverseCoordinates(geoJsonObject);

        System.out.println("GeoJSON с обратным направлением:");
        System.out.println(geoJsonObject.toString(2));
    }

    public static void reverseCoordinates(JSONObject geoJsonObject) {
        if (!geoJsonObject.getString("type").equals("Polygon")) {
            throw new IllegalArgumentException("GeoJSON object is not a Polygon");
        }

        JSONArray coordinates = geoJsonObject.getJSONArray("coordinates").getJSONArray(0);
        JSONArray reversedCoordinates = new JSONArray();

        // Обратный порядок координат
        for (int i = coordinates.length() - 1; i >= 0; i--) {
            reversedCoordinates.put(coordinates.getJSONArray(i));
        }

        // Замена оригинальных координат на перевернутые
        geoJsonObject.getJSONArray("coordinates").put(0, reversedCoordinates);
    }
}
