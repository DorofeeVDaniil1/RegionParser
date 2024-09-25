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

        // Получение пользовательских данных для конфигурации
        System.out.println("Введите домен для сайта:");
        domain = sc.nextLine();
        System.out.println("Введите Логин пользователя:");
        String username = sc.nextLine();
        System.out.println("Введите Пароль пользователя:");
        String password = sc.nextLine();
        System.out.println("Введите путь для сохранения файла (например: C:/Users/user/Documents):");
        String outputFilePath = sc.nextLine();

        // Создание объекта config для подключения к базе Личного кабинета
        Config config = new Config(username, password, domain, outputFilePath);
        String path = config.create_txt_file_from_path();  // Создание файла по указанному пути

        // Получение JWT токена
        authToken = getIdToken(config);

        // Начало цикла выполнения
        System.out.println("Начать запуск? Y/N");
        String trigger = sc.nextLine();

        if (trigger.equalsIgnoreCase("Y")) {
            String region = null;

            while (true) {
                // Ввод региона, если он не задан
                if (region == null) {
                    System.out.println("Введите Регион, где будет участок:");
                    region = sc.nextLine();
                }

                // Ввод названия участка
                System.out.println("Введите Название участка:");
                place = sc.nextLine();
                findCoordinatesRegions(region, place, path);

                // Запуск процесса в зависимости от отладочного режима
                System.out.println("Хотите запустить без тестового запуска? Y/N");
                boolean debugMode = !sc.nextLine().equalsIgnoreCase("Y");
                geoPolygonCreator.RunParserDBCoordinates(debugMode, path);

                // Спросить о повторном запуске
                System.out.println("Повторить запуск? Y/N");
                if (!sc.nextLine().equalsIgnoreCase("Y")) {
                    break;  // Завершаем цикл, если пользователь не хочет повторять
                }

                // Спросить о смене региона
                System.out.println("Изменить регион? Y/N");
                if (sc.nextLine().equalsIgnoreCase("Y")) {
                    region = null;  // Сбрасываем регион для нового ввода
                }
            }
        } else {
            System.out.println("Запуск отменен.");
        }
    }
}
