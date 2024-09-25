package org.example.Configuration;

import java.io.File;
import java.io.IOException;

public class Config {

    private String USERNAME ;
    private String PASSWORD ;
    private String DOMAIN ;
    private String OUTPUT_FILE_PATH;

    public Config(String USERNAME, String PASSWORD, String DOMAIN,String OUTPUT_FILE_PATH) {
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
        this.DOMAIN = DOMAIN;
        this.OUTPUT_FILE_PATH = OUTPUT_FILE_PATH;
    }

    public String getURL() {
        return "https://"+ DOMAIN+ "/app/graphql";
    }

    public String getAUTH_URL() {
        String string = "https://" + DOMAIN + "/app/api/v1/authenticate";
        return string;
    }
    public String getUSERNAME() {
        return USERNAME;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public String create_txt_file_from_path() {
        File directory = new File(OUTPUT_FILE_PATH);
        File file = new File(directory, "coordinates.json");

        try {
            // Проверка и создание директории
            if (!directory.exists()) {
                boolean isDirCreated = directory.mkdirs();
                if (!isDirCreated) {
                    System.out.println("Не удалось создать директорию: " + directory.getAbsolutePath());
                    return null;  // Возвращаем null при неудаче
                }
            }

            // Попытка создать файл
            boolean isFileCreated = file.createNewFile();
            if (isFileCreated) {
                System.out.println("Файл успешно создан: " + file.getAbsolutePath());
            } else if (file.exists()) {
                System.out.println("Файл уже существует: " + file.getAbsolutePath());
            } else {
                System.out.println("Не удалось создать файл.");
                return null;  // Возвращаем null при неудаче
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка при создании файла: " + e.getMessage());
            return null;  // Возвращаем null при исключении
        }

        return file.getAbsolutePath();
    }



}
