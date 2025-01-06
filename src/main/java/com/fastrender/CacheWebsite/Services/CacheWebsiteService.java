package com.fastrender.CacheWebsite.Services;

import com.fastrender.CacheWebsite.model.CacheDataModel;
import com.fastrender.CacheWebsite.model.WebsiteData;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Scope(value = "prototype")
public class CacheWebsiteService {

    @Autowired
    private SeleniumService seleniumService;

    @Autowired
    private CacheService cacheService;

    private WebDriver driver;

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void cacheWebsite(WebsiteData websiteData) {
        try {
            long startTime = System.nanoTime();
            String pageSource = getPageSource(websiteData.getUrl());
            CacheDataModel cacheDataModel = new CacheDataModel();
            cacheDataModel.setLastModified(getDateTime());
            cacheDataModel.setPageSource(pageSource);
            cacheService.saveToDb(websiteData.getExpirationTime(), websiteData.getDomain(), websiteData.getUrl(), cacheDataModel);
            long endTime = System.nanoTime();
            long durationInNano = endTime - startTime;
            long durationInMillis = durationInNano / 1_000_000;
            System.out.println("Execution time in milliseconds: " + durationInMillis);
        } catch (Exception exception) {
            if (null != driver) {
                seleniumService.killWebDriver();
            }
            throw new RuntimeException("ERROR " + exception.getMessage() + exception.getStackTrace());

        } finally {
            if (null != driver) {
                seleniumService.killWebDriver();
            }
        }
    }

    public String getPageSource(String websiteUrl) throws MalformedURLException {
        try {
            driver = seleniumService.getWebDriver();
            driver.get(websiteUrl);
            String pageSource = driver.getPageSource();
            return pageSource;
        } catch (Exception exception) {
            if (null != driver) {
                seleniumService.killWebDriver();
            }
            throw new RuntimeException("ERROR " + exception.getMessage() + exception.getStackTrace());

        } finally {
            if (null != driver) {
                seleniumService.killWebDriver();
            }
        }
    }

    public void savePageSource(String websiteDomain, String websiteUrl, String pageSource) {
        CacheDataModel cacheDataModel = new CacheDataModel();
        cacheDataModel.setLastModified(getDateTime());
        cacheDataModel.setPageSource(pageSource);
        cacheService.saveToDb("100", websiteDomain, websiteUrl, cacheDataModel);
    }
}

