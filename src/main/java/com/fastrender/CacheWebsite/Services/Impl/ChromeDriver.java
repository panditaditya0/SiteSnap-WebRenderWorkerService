package com.fastrender.CacheWebsite.Services.Impl;

import com.fastrender.CacheWebsite.Services.BrowserDriver;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.UUID;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ChromeDriver implements BrowserDriver {
    @Value("${chrome.driver.url}")
    private String CHROME_DRIVER_URL;

    public String sessionId;
    private Logger logger = LoggerFactory.getLogger(ChromeDriver.class);

    public String takeScreenshot() {
        String screenshotUrl = CHROME_DRIVER_URL + "/session/" + sessionId + "/screenshot";
        String screenShotResponse = this.sendApi(screenshotUrl, "GET", null);
        JsonObject screenShotResponseJson = JsonParser.parseString(screenShotResponse).getAsJsonObject();
        return screenShotResponseJson.getAsJsonObject("value").getAsString().replaceAll("\"","");
    }

    private String readResponse(CloseableHttpResponse response) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    public ChromeDriver deleteSession() {
        String fullUrl = CHROME_DRIVER_URL + "/session/" + this.sessionId;
        this.sendApi(fullUrl, "DELETE", null);
        return this;
    }

    public ChromeDriver createSession() {
        try {
            String url = CHROME_DRIVER_URL + "/session";
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(url);
                JsonObject capabilities = new JsonObject();
                JsonObject alwaysMatch = new JsonObject();
                alwaysMatch.addProperty("pageLoadStrategy", "none");

                JsonObject chromeOptions = new JsonObject();
                JsonArray args = new JsonArray();
//            args.add("--headless");
//            args.add("--disable-gpu");
//            args.add("--no-sandbox");
                args.add("--disable-dev-shm-usage");
                args.add("--user-data-dir=/tmp/session" + UUID.randomUUID());
//                args.add("--remote-debugging-port=9222");
                chromeOptions.add("args", args);
                chromeOptions.getAsJsonArray("args").add("--blink-settings=imagesEnabled=false");

                alwaysMatch.add("goog:chromeOptions", chromeOptions);
                capabilities.add("capabilities", new JsonObject());
                capabilities.getAsJsonObject("capabilities").add("alwaysMatch", alwaysMatch);

                post.setEntity(new StringEntity(capabilities.toString()));
                post.setHeader("Content-Type", "application/json");

                try (CloseableHttpResponse response = client.execute(post)) {
                    String jsonResponse = readResponse(response);
                    JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
                    this.sessionId = json.getAsJsonObject("value").get("sessionId").getAsString();
                    System.out.println(this.sessionId);
                    return this;
                }
            } catch (Exception ex) {
                logger.error("ERROR in createSession chrome driver " + ex.getMessage() + " " + ex.getStackTrace());
                throw new RuntimeException(ex);
            }
        } catch (Exception ex) {
            logger.error("ERROR in createSession chrome driver " + ex.getMessage() + " " + ex.getStackTrace());
            throw new RuntimeException(ex);
        }
    }

    public ChromeDriver navigateToUrl(String url) {
        String fullUrl = CHROME_DRIVER_URL + "/session/" + this.sessionId + "/url";
        HashMap<String, String> jsonPayloas = new HashMap<>();
        jsonPayloas.put("url", url);
        this.sendApi(fullUrl, "POST", jsonPayloas);
        return this;
    }

    public String getPageSource() {
        String fullUrl = CHROME_DRIVER_URL + "/session/" + this.sessionId + "/source";
        return JsonParser.parseString(this.sendApi(fullUrl, "GET", null)).getAsJsonObject().get("value").getAsString();
    }

    public void switchTab(String newTabHandle) {
        String switchTabUrl = CHROME_DRIVER_URL + "/session/" + sessionId + "/window";
        HashMap<String, String> jsonPayload = new HashMap<>();
        jsonPayload.put("handle", newTabHandle);
        this.sendApi(switchTabUrl, "POST", jsonPayload);
    }

    public String openNewTab(String url, int tabIndex) {
        String newTabUrl = CHROME_DRIVER_URL + "/session/" + sessionId + "/window/new";
        HashMap<String, String> jsonObject = new HashMap<>();
        jsonObject.put("type", "tab");
        this.sendApi(newTabUrl, "POST", jsonObject);

        String handlesUrl = CHROME_DRIVER_URL + "/session/" + sessionId + "/window/handles";
        String response2 = this.sendApi(handlesUrl, "GET", jsonObject);
        String[] handles = response2.split("\"value\":\\[")[1].split("]")[0].replace("\"", "").split(",");
        String newTabHandle = handles[handles.length - 1];


        this.switchTab(newTabHandle);
//        String switchTabUrl = CHROME_DRIVER_URL + "/session/" + sessionId + "/window";
//        HashMap<String, String> jsonObject2 = new HashMap<>();
//        jsonObject2.put("handle", newTabHandle);
//        this.sendApi(switchTabUrl, "POST",jsonObject2);

        String navigateUrl = CHROME_DRIVER_URL + "/session/" + sessionId + "/url";
        HashMap<String, String> jsonObject3 = new HashMap<>();
        jsonObject3.put("url", url);
        this.sendApi(navigateUrl, "POST", jsonObject3);

        return newTabHandle;
    }

    public String getPageSourceOfTab(String tabHandle) {
        String switchTabUrl = CHROME_DRIVER_URL + "/session/" + sessionId + "/window";
        HashMap<String, String> jsonPayload = new HashMap<>();
        jsonPayload.put("handle", tabHandle);
        String response = sendApi(switchTabUrl, "POST", jsonPayload);
        response.split("\"value\":")[1].replaceAll("(^\"|\"$)", "");
        return getPageSource();
    }

    public boolean isPageLoaded(String tabHandle) {
        String executeUrl = CHROME_DRIVER_URL + "/session/" + sessionId + "/execute/sync";
        HashMap<String, String> jsonPayloas = new HashMap<>();
        jsonPayloas.put("script", "return document.readyState;");
        String response = sendApi(executeUrl, "POST", jsonPayloas);
        return response.contains("\"value\":\"complete\"");
    }

    private String sendApi(String url, Object HTTP_METHOD, HashMap<String, String> jsonPayload) {
        String response = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            switch (HTTP_METHOD.toString()) {
                case "POST":
                    HttpPost post = new HttpPost(url);
                    post.setHeader("Content-Type", "application/json");
                    JsonObject payload = new JsonObject();
                    jsonPayload.forEach((key, value) -> payload.addProperty(key, value));
                    payload.add("args", new com.google.gson.JsonArray());
                    post.setEntity(new StringEntity(payload.toString()));
                    return EntityUtils.toString(client.execute(post).getEntity());

                case "GET":
                    HttpGet get = new HttpGet(url);
                    return EntityUtils.toString(client.execute(get).getEntity());

                case "DELETE":
                    HttpDelete delete = new HttpDelete(url);
                    return EntityUtils.toString(client.execute(delete).getEntity());
                default:
                    throw new RuntimeException();
            }
        } catch (Exception ex) {
            logger.error("ERROR in getPageSource chrome driver " + ex.getMessage() + " " + ex.getStackTrace());
            throw new RuntimeException(ex);
        }
    }
}