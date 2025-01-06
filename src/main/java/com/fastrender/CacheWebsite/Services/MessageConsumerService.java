package com.fastrender.CacheWebsite.Services;

import com.fastrender.CacheWebsite.model.WebsiteData;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Service
@ConditionalOnProperty(name = "rabbit.listener.enabled", havingValue = "true", matchIfMissing = false)
public class MessageConsumerService {

    @Autowired
    private CacheWebsiteService cacheWebsiteService;

    @RabbitListener(queues = "sitemap-links-queue")
    public void receiveMessage(WebsiteData message) {
        cacheWebsiteService.cacheWebsite(message);
    }
}
