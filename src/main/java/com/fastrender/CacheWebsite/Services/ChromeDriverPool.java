package com.fastrender.CacheWebsite.Services;

import com.fastrender.CacheWebsite.Services.Impl.ChromeDriver;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class ChromeDriverPool {
    @Value("${spring.rabbitmq.listener.simple.concurrency}")
    private int POOL_SIZE;
    private BlockingQueue<ChromeDriver> pool;

    @Value("${chrome.driver.url}")
    public String chromeDriverUrl;

    @PostConstruct
    public void initializePool() {
        pool = new LinkedBlockingQueue<>(POOL_SIZE);
        for (int i = 0; i < POOL_SIZE; i++) {
            ChromeDriver driver = new ChromeDriver(chromeDriverUrl).createSession();

            pool.offer(driver);
            System.out.println(driver.sessionId);
        }
        System.out.println("ChromeDriver pool initialized with " + POOL_SIZE + " instances.");
    }

    public ChromeDriver acquireDriver() throws InterruptedException {
        ChromeDriver driver = pool.take();
        System.out.println("Acquired ChromeDriver: " + driver);
        return driver;
    }

    public void releaseDriver(ChromeDriver driver) {
        if (driver != null) {
            pool.offer(driver);
            System.out.println("Released ChromeDriver: " + driver);
        }
    }

    @PreDestroy
    public void shutdownPool() {
        for (ChromeDriver driver : pool) {
            driver.deleteSession();
            System.out.println("Deleted ChromeDriver session: " + driver);
        }
        System.out.println("ChromeDriver pool shutdown complete.");
    }
}