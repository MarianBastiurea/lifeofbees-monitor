package main.java.com.lifeofbees.monitor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

public class WebsiteChecker {

    public WebsiteStatus check(String url) throws IOException {
        HttpURLConnection connection =
                (HttpURLConnection) URI.create(url).toURL().openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        long startTime = System.currentTimeMillis();

        int statusCode = connection.getResponseCode();

        long responseTime = System.currentTimeMillis() - startTime;

        boolean available = statusCode >= 200 && statusCode < 400;

        connection.disconnect();

        return new WebsiteStatus(
                url,
                available,
                statusCode,
                responseTime
        );
    }
}