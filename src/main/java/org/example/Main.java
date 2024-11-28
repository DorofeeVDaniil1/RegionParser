package org.example;

import org.example.Configuration.Config;
import org.example.NewRegions.GeoPolygonCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.example.BearToken.BearTocken.getIdToken;
import static org.example.NewRegions.FindRegionCoordinates.findCoordinatesRegions;

public class Main {

    public static String authToken;
    public static String domain;
    public static String place;
    public static String place_bd;
    public static String parent;
    public static String color;
    public static void main(String[] args) {
        GeoPolygonCreator geoPolygonCreator = new GeoPolygonCreator();
        Scanner sc = new Scanner(System.in);

        Config config = getUserConfig(sc);
        String path = config.create_txt_file_from_path();  // Создание файла по указанному пути
        authToken = getIdToken(config);  // Получение JWT токена

        executeTask(sc, geoPolygonCreator, path);
    }

    // Метод для получения конфигурации от пользователя
    private static Config getUserConfig(Scanner sc) {
        System.out.println("Введите домен для сайта:");
        domain = "disp.t1.groupstp.ru";
        System.out.println("Введите Логин пользователя:");
        String username ="sysadmin";
        System.out.println("Введите Пароль пользователя:");
        String password = "MOdSqw9S";
        System.out.println("Введите путь для сохранения файла (например: C:/Users/user/Documents):");
        String outputFilePath = "D:\\samara";

        return new Config(username, password, domain, outputFilePath);
    }

    // Метод для выбора типа запуска
    private static String getLaunchType(Scanner sc) {
        System.out.println("Как вы хотите запустить программу? (Введите цифру 1/2)\n" +
                "1 - Свой GeoJSON файл\n" +
                "2 - Автоматическое создание GeoJson файла по названию региона и участка?");
        return sc.nextLine();
    }


    private static void executeTask(Scanner sc, GeoPolygonCreator geoPolygonCreator, String path) {
        String type = getLaunchType(sc); // Получаем тип запуска
        String region = "Bogatovsky District, Samara Oblast"; // Фиксированный регион
        List<String> places = new ArrayList<>(); // Список участков


        while (true) {
            if (type.equals("2")) { // Если выбран автоматический режим
                System.out.println("Укажите имя родителя участка");
                parent = sc.nextLine();
                System.out.println("Укажите цвет районов");
                color = sc.nextLine();
                if (places.isEmpty()) { // Если участки не заданы, запрашиваем их
                    System.out.println("Введите участки через ';':");
                    String placesInput = sc.nextLine();

                    places = Arrays.asList(placesInput.split(";")); // Разделяем участки по символу ';'
                } else {
                    System.out.println("Текущие участки: " + String.join(", ", places));
                    System.out.println("Хотите изменить список участков? (Y/N)");
                    if (sc.nextLine().equalsIgnoreCase("Y")) {
                        places.clear(); // Очищаем список участков
                        System.out.println("Введите новые участки через ';':");
                        String placesInput = sc.nextLine();
                        places = Arrays.asList(placesInput.split(";")); // Обновляем список участков
                    }
                }

                // Обрабатываем фиксированный регион и введенные участки
                for (String place : places) {
                    place = place.trim(); // Убираем пробелы в начале и конце
                    place_bd=place;
                    System.out.println("Обработка региона: " + region + ", участка: " + place);

                    // Проверяем координаты для фиксированного региона и участков
                    if (!findCoordinatesRegions(region, place, path)) {
                        System.out.println("Не удалось найти координаты для участка: " + place);
                        continue; // Продолжаем с следующим участком, если ошибка
                    }else {
                        runTask(geoPolygonCreator,path);
                    }

                }
            }

            if (type.equals("1")) { // Если выбран ручной режим с файлом
                System.out.println("Укажите название для региона:");
                place_bd= sc.next();
                System.out.println("Проверьте, что вы изменили файл по пути " + path);
                runTask(geoPolygonCreator,path);
            }



            // Предлагаем повторить запуск или сменить тип запуска
            System.out.println("Хотите повторить запуск или сменить тип запуска? (1 - Свой файл, 2 - Автоматический, N - Завершить)");
            String restartChoice = sc.nextLine();
            if (restartChoice.equalsIgnoreCase("N")) {
                break; // Завершаем цикл, если выбрано завершение
            } else if (restartChoice.equals("1") || restartChoice.equals("2")) {
                type = restartChoice; // Меняем тип запуска
            }

            // Запрашиваем, нужно ли поменять участки для автоматического режима
            if (type.equals("2")) {
                System.out.println("Хотите сменить список участков? (Y/N)");
                if (sc.nextLine().equalsIgnoreCase("Y")) {
                    places.clear(); // Очищаем список участков, чтобы их можно было ввести снова
                }
            }
        }
    }
    private static void runTask(GeoPolygonCreator geoPolygonCreator,String path) {
        boolean debugMode;
        Scanner sc = new Scanner(System.in);
        // Запускаем основную логику парсера
        debugMode = isDebugMode(sc); // Проверяем режим отладки
        geoPolygonCreator.RunParserDBCoordinates(debugMode, path); // Выполнение с указанными параметрами

        // Если тестовый режим был включен, предлагаем выполнить задачу без теста
        if (debugMode) {
            System.out.println("Хотите выполнить эту же задачу без теста? (Y/N)");
            if (sc.nextLine().equalsIgnoreCase("Y")) {
                debugMode = false; // Отключаем тестовый режим
                geoPolygonCreator.RunParserDBCoordinates(debugMode, path); // Выполнение без теста
            }
        }
    }
    private static void runTask(GeoPolygonCreator geoPolygonCreator,String path,String name) {
        boolean debugMode;
        Scanner sc = new Scanner(System.in);
        // Запускаем основную логику парсера
        debugMode = isDebugMode(sc); // Проверяем режим отладки
        geoPolygonCreator.RunParserDBCoordinates(debugMode, path); // Выполнение с указанными параметрами

        // Если тестовый режим был включен, предлагаем выполнить задачу без теста
        if (debugMode) {
            System.out.println("Хотите выполнить эту же задачу без теста? (Y/N)");
            if (sc.nextLine().equalsIgnoreCase("Y")) {
                debugMode = false; // Отключаем тестовый режим
                geoPolygonCreator.RunParserDBCoordinates(debugMode, path); // Выполнение без теста
            }
        }
    }




    // Метод для проверки режима отладки
    private static boolean isDebugMode(Scanner sc) {
        System.out.println("Хотите запустить без тестового запуска? Y/N");
        return !sc.nextLine().equalsIgnoreCase("Y");
    }

    // Метод для запроса на повторный запуск
    private static boolean askForRestart(Scanner sc) {
        System.out.println("Повторить запуск? Y/N");
        return sc.nextLine().equalsIgnoreCase("Y");
    }

    // Метод для запроса на смену региона
    private static boolean askForRegionChange(Scanner sc) {
        System.out.println("Повторить запуск и сменить регион? YES/NO");
        return sc.nextLine().equalsIgnoreCase("YES");
    }
}
