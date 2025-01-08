package com.fastrender.CacheWebsite.Services.Impl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.net.MalformedURLException;
import java.net.URL;

@Service
@Scope(value = "prototype")
public class SeleniumService {

    @Value("${SELENIUM_HUB_URL}")
    private String SELENIUM_HUB_URL;

    private WebDriver driver;

    public WebDriver getWebDriver() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new RemoteWebDriver(new URL(SELENIUM_HUB_URL), options);
        return driver;
    }

    public void killWebDriver() {
        try {
            driver.quit();
        } catch (Exception ex) {
            throw new RuntimeException("ERROR " + ex.getMessage() + ex.getStackTrace());
        }
    }
}