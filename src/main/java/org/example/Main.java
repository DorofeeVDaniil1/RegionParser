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

        executeTask(sc, geoPolygonCreator, path);
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


    private static void executeTask(Scanner sc, GeoPolygonCreator geoPolygonCreator, String path) {
        String type = getLaunchType(sc); // Получаем тип запуска
        String[] regions = null;         // Массив для регионов
        String place = null;
        boolean debugMode;

        while (true) {
            if (type.equals("2")) { // Если выбран автоматический режим
                if (regions == null || place == null) { // Если регионы и участок не заданы, запрашиваем их
                    System.out.println("Введите регионы через ; (например: Москва; Санкт-Петербург):");
                    String regionsInput = sc.nextLine();
                    regions = regionsInput.split(";"); // Разделяем регионы по символу ';'

                    System.out.println("Введите Название участка для всех регионов:");
                    place = sc.nextLine();
                } else {
                    System.out.println("Текущие регионы: " + String.join(", ", regions) + ", участок: " + place);
                    System.out.println("Хотите изменить регионы и участок? (Y/N)");
                    if (sc.nextLine().equalsIgnoreCase("Y")) {
                        System.out.println("Введите новые регионы через ;:");
                        String regionsInput = sc.nextLine();
                        regions = regionsInput.split(";");

                        System.out.println("Введите новое Название участка:");
                        place = sc.nextLine();
                    }
                }

                // Обрабатываем каждый регион
                for (String region : regions) {
                    region = region.trim();  // Убираем пробелы в начале и конце
                    System.out.println("Обработка региона: " + region);

                    // Проверяем координаты для каждого региона и участка
                    if (!findCoordinatesRegions(region, place, path)) {
                        System.out.println("Не удалось найти координаты для региона: " + region);
                        continue;  // Продолжаем со следующим регионом, если ошибка
                    }
                }
            }

            if (type.equals("1")) { // Если выбран ручной режим с файлом
                System.out.println("Проверьте, что вы изменили файл по пути " + path);
            }

            // Запускаем основную логику парсера
            debugMode = isDebugMode(sc); // Проверяем режим отладки
            geoPolygonCreator.RunParserDBCoordinates(debugMode, path); // Выполнение с указанными параметрами

            // Если тестовый режим был включен, предлагаем выполнить задачу без теста
            if (debugMode) {
                System.out.println("Хотите выполнить эту же задачу без теста? (Y/N)");
                if (sc.nextLine().equalsIgnoreCase("Y")) {
                    debugMode = false;  // Отключаем тестовый режим
                    geoPolygonCreator.RunParserDBCoordinates(debugMode, path); // Выполнение без теста
                }
            }

            // Предлагаем повторить запуск или сменить тип запуска
            System.out.println("Хотите повторить запуск или сменить тип запуска? (1 - Свой файл, 2 - Автоматический, N - Завершить)");
            String restartChoice = sc.nextLine();
            if (restartChoice.equalsIgnoreCase("N")) {
                break;  // Завершаем цикл, если выбрано завершение
            } else if (restartChoice.equals("1") || restartChoice.equals("2")) {
                type = restartChoice;  // Меняем тип запуска
            }

            // Запрашиваем, нужно ли поменять регионы для автоматического режима
            if (type.equals("2")) {
                System.out.println("Хотите сменить регионы? (Y/N)");
                if (sc.nextLine().equalsIgnoreCase("Y")) {
                    regions = null;  // Обнуляем регионы и участок, чтобы их можно было ввести снова
                    place = null;
                }
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
