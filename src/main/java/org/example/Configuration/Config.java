package org.example.Configuration;

public class Config {

    public String getURL() {
        return URL;
    }

    public String getAUTH_URL() {
        return AUTH_URL;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    private  final String URL = "https://"+ВСТАВИТЬ СВОЙ ДОМЕН+"/app/graphql";
    private  final String AUTH_URL = "https://"+ВСТАВИТЬ СВОЙ ДОМЕН+"/app/api/v1/authenticate";
    private  final String USERNAME = ЛОГИН В СИСТЕМЕ;
    private  final String PASSWORD = "ПАРОЛЬ В СИСТЕМЕ";





}
