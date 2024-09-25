package org.example;

import org.example.Configuration.Config;
import org.example.NewRegions.GeoPolygonCreator;

import java.util.Scanner;

import static org.example.BearToken.BearTocken.getIdToken;
import static org.example.NewRegions.FindRegionCoordinates.findCoordinatesRegions;

public class Main {

    public static String authToken;
    public static String domain;
    public static String place;

    public static void main(String[] args) {
        GeoPolygonCreator geoPolygonCreator = new GeoPolygonCreator();
        Scanner sc = new Scanner(System.in);

        Config config = getUserConfig(sc);
        String path = config.create_txt_file_from_path();  // Создание файла по указанному пути
        authToken = getIdToken(config);  // Получение JWT токена

        String type = getLaunchType(sc);
        executeTask(sc, geoPolygonCreator, path, type);
    }

    // Метод для получения конфигурации от пользователя
    private static Config getUserConfig(Scanner sc) {
        System.out.println("Введите домен для сайта:");
        domain = sc.nextLine();
        System.out.println("Введите Логин пользователя:");
        String username = sc.nextLine();
        System.out.println("Введите Пароль пользователя:");
        String password = sc.nextLine();
        System.out.println("Введите путь для сохранения файла (например: C:/Users/user/Documents):");
        String outputFilePath = sc.nextLine();

        return new Config(username, password, domain, outputFilePath);
    }

    // Метод для выбора типа запуска
    private static String getLaunchType(Scanner sc) {
        System.out.println("Как вы хотите запустить программу? (Введите цифру 1/2)\n" +
                "1 - Свой GeoJSON файл\n" +
                "2 - Автоматическое создание GeoJson файла по названию региона и участка?");
        return sc.nextLine();
    }


    // Метод для выполнения основной задачи
    private static void executeTask(Scanner sc, GeoPolygonCreator geoPolygonCreator, String path, String type) {
        String region = null;
        boolean debugMode;
        while (true) {
            if (type.equals("2") && region == null) {
                System.out.println("Введите Регион, где будет участок:");
                region = sc.nextLine();
                System.out.println("Введите Название участка:");
                place = sc.nextLine();
                findCoordinatesRegions(region, place, path);
            }

            if (type.equals("1")) {
                System.out.println("Проверьте, что вы изменили файл по пути "+path);
            }

            // Общая логика выполнения парсера
            debugMode = isDebugMode(sc);
            geoPolygonCreator.RunParserDBCoordinates(debugMode, path);

            // Если тестовый режим был включен, предложить выполнить без теста
            if (debugMode) {
                System.out.println("Хотите выполнить эту же строку без теста? Y/N");
                if (sc.nextLine().equalsIgnoreCase("Y")) {
                    debugMode = false; // Отключаем режим тестирования
                    geoPolygonCreator.RunParserDBCoordinates(debugMode, path); // Выполнение без теста
                }
            }

            if (!askForRestart(sc)) {
                break;
            }

            if (askForRegionChange(sc)) {
                region = null;
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
