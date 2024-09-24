package org.example.NewRegions;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ExcelCoordinatesParserName {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/reverse";
    private static final double DISTANCE_THRESHOLD_KM = 10.0; // Пороговое значение расстояния для группировки в км

    public static void excelRegionsParser(String inputFilePath,String outputFilePath ) {

        try (FileInputStream fis = new FileInputStream(inputFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Открываем первый лист
            Sheet sheet = workbook.getSheetAt(0);

            // Читаем все координаты
            List<Point> points = new ArrayList<>();
            for (Row row : sheet) {
                Cell latCell = row.getCell(0);  // Столбец A - широта
                Cell lonCell = row.getCell(1);  // Столбец B - долгота

                if (latCell != null && lonCell != null && latCell.getCellType() == CellType.NUMERIC && lonCell.getCellType() == CellType.NUMERIC) {
                    double latitude = latCell.getNumericCellValue();
                    double longitude = lonCell.getNumericCellValue();
                    points.add(new Point(latitude, longitude, row));
                }
            }

            // Группируем точки
            Map<Point, List<Point>> clusters = clusterPoints(points);

            // Множество для хранения уникальных названий регионов
            Set<String> uniqueRegions = new HashSet<>();

            // Обрабатываем каждый кластер
            for (Map.Entry<Point, List<Point>> entry : clusters.entrySet()) {
                Point representative = entry.getKey();
                String locationName = getLocationName(representative.latitude, representative.longitude);

                // Применяем результат ко всем точкам в кластере
                for (Point point : entry.getValue()) {
                    Cell locationCell = point.row.createCell(2);  // Столбец C - для названия города/места
                    locationCell.setCellValue(locationName);
                    uniqueRegions.add(locationName);  // Добавляем название региона в множество
                }
            }

            // Записываем уникальные значения регионов в четвёртый столбец
            int rowIndex = 0;
            for (String region : uniqueRegions) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    row = sheet.createRow(rowIndex);
                }
                Cell regionCell = row.createCell(3);  // Столбец D - для уникальных регионов
                regionCell.setCellValue(region);
                rowIndex++;
            }

            // Сохраняем обновлённый Excel-файл
            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                workbook.write(fos);
            }

            System.out.println("Данные успешно обработаны и сохранены в файл: " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для получения названия места по координатам
    public static String getLocationName(double lat, double lon) {
        try {
            HttpResponse<JsonNode> response = Unirest.get(NOMINATIM_URL)
                    .queryString("lat", lat)
                    .queryString("lon", lon)
                    .queryString("format", "json")
                    .asJson();

            if (response.getStatus() == 200) {
                JSONObject responseObject = response.getBody().getObject();
                JSONObject addressObject = responseObject.optJSONObject("address");

                if (addressObject == null) {
                    return "Неизвестное место";
                }

                String city = addressObject.optString("city", null);

                if (city == null || city.isEmpty()) {
                    city = addressObject.optString("town", null);
                }

                if (city == null || city.isEmpty()) {
                    city = addressObject.optString("village", "Неизвестное место");
                }

                return city;
            } else {
                return "Ошибка при получении данных";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при обработке запроса";
        }
    }

    // Метод для вычисления расстояния между двумя точками в километрах
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Радиус Земли в км
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Расстояние в километрах
    }

    // Метод для кластеризации точек на основе расстояния
    public static Map<Point, List<Point>> clusterPoints(List<Point> points) {
        Map<Point, List<Point>> clusters = new HashMap<>();

        for (Point point : points) {
            boolean addedToCluster = false;

            for (Point clusterRep : clusters.keySet()) {
                if (calculateDistance(point.latitude, point.longitude, clusterRep.latitude, clusterRep.longitude) <= DISTANCE_THRESHOLD_KM) {
                    clusters.get(clusterRep).add(point);
                    addedToCluster = true;
                    break;
                }
            }

            if (!addedToCluster) {
                clusters.put(point, new ArrayList<>(Collections.singletonList(point)));
            }
        }

        return clusters;
    }

    // Вспомогательный класс для хранения координат и строки Excel
    static class Point {
        double latitude;
        double longitude;
        Row row;

        Point(double latitude, double longitude, Row row) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.row = row;
        }
    }
}
