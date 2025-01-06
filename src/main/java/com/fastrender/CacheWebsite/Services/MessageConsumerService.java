package com.fastrender.CacheWebsite.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    public void receiveMessage(String message) {
        try{
            WebsiteData data = new ObjectMapper().readValue(message, WebsiteData.class);
            cacheWebsiteService.cacheWebsite(data);
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
