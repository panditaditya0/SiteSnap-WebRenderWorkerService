package com.fastrender.CacheWebsite.Services;

import com.fastrender.CacheWebsite.Services.Impl.ChromeDriver;
import com.fastrender.CacheWebsite.model.CacheDataModel;
import com.fastrender.CacheWebsite.model.WebsiteData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class CacheWebsiteService {
    @Autowired
    private CacheService cacheService;

    @Autowired
    private ChromeDriverPool chromeDriverPool;

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
            System.out.println("Execution time in milliseconds: " + durationInMillis + " "+websiteData.getUrl());
        } catch (Exception exception) {
            throw new RuntimeException("ERROR " + exception.getMessage() + exception.getStackTrace());
        }
    }

    public String getPageSource(String websiteUrl) throws InterruptedException {
        ChromeDriver chromeDriver = chromeDriverPool.acquireDriver();
        try {
            String pageSource =  chromeDriver
                    .navigateToUrl(websiteUrl)
                    .getPageSource();
            chromeDriverPool.releaseDriver(chromeDriver);
            return pageSource;
        } catch (Exception exception) {
            chromeDriverPool.releaseDriver(chromeDriver);
            throw new RuntimeException("ERROR " + exception.getMessage() + exception.getStackTrace());
        }
    }

    public void savePageSource(String websiteDomain, String websiteUrl, String pageSource) {
        CacheDataModel cacheDataModel = new CacheDataModel();
        cacheDataModel.setLastModified(getDateTime());
        cacheDataModel.setPageSource(pageSource);
        cacheService.saveToDb("100", websiteDomain, websiteUrl, cacheDataModel);
    }
}