package com.fastrender.CacheWebsite.Services.Impl;
import com.fastrender.CacheWebsite.Services.BrowserDriver;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.entity.StringEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ChromeDriver implements BrowserDriver{
    private String CHROME_DRIVER_URL;

    public String sessionId;

    public ChromeDriver(String chromeDriverUrl){
        this.CHROME_DRIVER_URL =chromeDriverUrl;
    }

    private static String readResponse(CloseableHttpResponse response) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    public ChromeDriver deleteSession(){
        try{
            String fullUrl = CHROME_DRIVER_URL + "/session/" + this.sessionId;
            CloseableHttpClient client = HttpClients.createDefault();
            HttpDelete delete = new HttpDelete(fullUrl);
            delete.setHeader("Content-Type", "application/json");
            try (CloseableHttpResponse response = client.execute(delete)) {
                String jsonResponse = readResponse(response);
                System.out.println("New Tab Opened Response: " + jsonResponse);
            }
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return this;
    }

    public ChromeDriver createSession() {
        try {
            String url = CHROME_DRIVER_URL + "/session";
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);

            JsonObject capabilities = new JsonObject();
            JsonObject alwaysMatch = new JsonObject();

            // Specify Chrome options
            JsonObject chromeOptions = new JsonObject();
            JsonArray args = new JsonArray();
            args.add("--headless"); // Add headless argument
            args.add("--disable-gpu"); // Recommended for some environments
            args.add("--no-sandbox"); // Avoid sandbox issues
            args.add("--disable-dev-shm-usage"); // Avoid shared memory issues
            args.add("--remote-debugging-port=9222"); // Debugging port
            chromeOptions.add("args", args);

            // Add Chrome options under `goog:chromeOptions`
            alwaysMatch.add("goog:chromeOptions", chromeOptions);
            capabilities.add("capabilities", new JsonObject());
            capabilities.getAsJsonObject("capabilities").add("alwaysMatch", alwaysMatch);

            // Set the request body
            post.setEntity(new StringEntity(capabilities.toString()));
            post.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = client.execute(post)) {
                String jsonResponse = readResponse(response);
                System.out.println(url);
                System.out.println(jsonResponse);
                JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
                this.sessionId = json.getAsJsonObject("value").get("sessionId").getAsString();
                return this;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public ChromeDriver navigateToUrl(String url){
        try{
            String fullUrl = CHROME_DRIVER_URL + "/session/" + this.sessionId + "/url";
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(fullUrl);

            JsonObject payload = new JsonObject();
            payload.addProperty("url", url);

            post.setEntity(new StringEntity(payload.toString()));
            post.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = client.execute(post)) {
                readResponse(response);
            }
            return this;
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public String getPageSource(){
        try{
            String fullUrl = CHROME_DRIVER_URL + "/session/" + this.sessionId + "/source";
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet get = new HttpGet(fullUrl);

            try (CloseableHttpResponse response = client.execute(get)) {
                String jsonResponse = readResponse(response);
                return JsonParser.parseString(jsonResponse).getAsJsonObject().get("value").getAsString();
            }
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}