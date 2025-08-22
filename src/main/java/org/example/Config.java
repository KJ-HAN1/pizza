package org.example;

import io.github.cdimascio.dotenv.Dotenv;

//.env를 읽어오기 위한 클래스
public class Config {
    private static final Dotenv dotenv = Dotenv.load();
    public static final String ADMIN_PW = dotenv.get("ADMIN_PW");
}
