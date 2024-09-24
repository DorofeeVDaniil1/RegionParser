package org.example;

import org.example.NewRegions.GeoPolygonCreator;

import static org.example.NewRegions.FindRegionCoordinates.findCoordinatesRegions;

public class Main {

    private static final String DOMAIN = "logistics.sah67.ru";
    public static final String REGION = "Смоленская область";
    public static final String PLACE ="Кардымовский район";
    private static final boolean DEBUG_MODE = true;

    public static void main(String[] args) {
        GeoPolygonCreator geoPolygonCreator = new GeoPolygonCreator();

            findCoordinatesRegions(REGION, PLACE);
           geoPolygonCreator.RunParserDBCoordinates(DEBUG_MODE);
    }
}
