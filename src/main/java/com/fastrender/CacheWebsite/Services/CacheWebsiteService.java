package com.fastrender.CacheWebsite.Services;

import com.fastrender.CacheWebsite.Services.Impl.ChromeDriver;
import com.fastrender.CacheWebsite.model.CacheDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CacheWebsiteService {
    @Autowired
    private CacheService cacheService;

    @Autowired
    private ObjectFactory<ChromeDriver> chromeDriverFactory;

    @Value("${chrome.driver.url}")
    public String chromeDriverUrl;

    private Logger logger = LoggerFactory.getLogger(CacheWebsiteService.class);

    @Value("${no.of.tabs}")
    public int noOfTabs;

    @Value("${save.screenshot}")
    public boolean saveScreenshot;

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void cacheWebsite(ArrayList<String> urls, String domain) {
        try {
            long startTime = System.nanoTime();
            this.openMultipleWebsiteAndCache(urls, domain);
            long endTime = System.nanoTime();
            long durationInNano = endTime - startTime;
            long durationInMillis = durationInNano / 1_000_000;
            logger.info("Execution time in milliseconds: " + durationInMillis );
        } catch (Exception exception) {
            logger.error("ERROR " + exception.getMessage() + exception.getStackTrace());
            throw new RuntimeException("ERROR " + exception.getMessage() + exception.getStackTrace());
        }
    }

    public String getPageSource(String websiteUrl) throws InterruptedException {
        ChromeDriver chromeDriver = chromeDriverFactory.getObject();
        try {
            String pageSource =  chromeDriver
                    .navigateToUrl(websiteUrl)
                    .getPageSource();

            chromeDriver.deleteSession();
            return pageSource;
        } catch (Exception exception) {
            chromeDriver.deleteSession();
            logger.error("ERROR " + exception.getMessage() + exception.getStackTrace());
            throw new RuntimeException("ERROR " + exception.getMessage() + exception.getStackTrace());
        }
    }

    public void savePageSource(String websiteDomain, String websiteUrl, String pageSource, String screenShotAsBase64) {
        CacheDataModel cacheDataModel = new CacheDataModel();
        cacheDataModel.setLastModified(getDateTime());
        cacheDataModel.setPageSource(pageSource);
        cacheDataModel.setScreenShotBase64(screenShotAsBase64);
        cacheService.saveToDb("100", websiteDomain, websiteUrl, cacheDataModel);
    }

    public void openMultipleWebsiteAndCache(ArrayList<String> urls, String websiteDomain) throws InterruptedException  {
        ChromeDriver chromeDriver = chromeDriverFactory.getObject();
        try{
            chromeDriver.createSession();
            HashMap<String, String> tabHandles = new HashMap<>();
            for (int i = 0; i < noOfTabs; i++) {
                tabHandles.put(chromeDriver.openNewTab(urls.get(i), i + 1), urls.get(i));
            }
            tabHandles.forEach((windowHHandle, url) -> {
                chromeDriver.switchTab(windowHHandle);
                while (!chromeDriver.isPageLoaded(windowHHandle)) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                String pageSource = chromeDriver.getPageSourceOfTab(windowHHandle);
                String screenShot =  saveScreenshot ? chromeDriver.takeScreenshot() :  "NA";
                savePageSource(websiteDomain, url, pageSource, screenShot);
            });
            chromeDriver.deleteSession();
        } catch (Exception exception) {
            logger.error("ERROR " + exception.getMessage() + exception.getStackTrace());
            chromeDriver.deleteSession();
            throw new RuntimeException("ERROR " + exception.getMessage() + exception.getStackTrace());
        }
    }
}