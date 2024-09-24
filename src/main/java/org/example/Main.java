package org.example;

import static org.example.NewRegions.ExcelCoordinatesParserName.excelRegionsParser;
import static org.example.NewRegions.GeoPolygonCreator.RunParserDBCoordinates;
import static org.example.NewRegions.FindRegionCoordinates.findCoordinatesRegions;

public class Main {
    public static final String INPUT_FILE_PATH = "D:\\Styding\\Java Spring\\NodeParcer\\src\\main\\resources\\1.xlsx";  // Путь к Excel-файлу
    public static final String OUTPUT_FILE_PATH = "D:\\Styding\\Java Spring\\NodeParcer\\src\\main\\resources\\out.xlsx";  // Файл для сохранения с результатами
    public static final String REGION = "Amur Oblast";
    public static final String PLACE ="городской округ Зея";
    private static final boolean DEBUG_MODE = false;

    public static void main(String[] args) {
        //excelRegionsParser(INPUT_FILE_PATH,OUTPUT_FILE_PATH);
       //findCoordinatesRegions(REGION,PLACE);
       RunParserDBCoordinates(DEBUG_MODE);
    }
}
