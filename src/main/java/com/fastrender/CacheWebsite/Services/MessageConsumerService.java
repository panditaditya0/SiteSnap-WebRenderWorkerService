package com.fastrender.CacheWebsite.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastrender.CacheWebsite.model.WebsiteData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(name = "rabbit.listener.enabled", havingValue = "true", matchIfMissing = false)
public class MessageConsumerService {

    private Logger logger = LoggerFactory.getLogger(MessageConsumerService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<WebsiteData> messageBuffer = new ArrayList<>();

    @Value("${no.of.tabs}")
    private int BATCH_SIZE;

    @Autowired
    private ObjectFactory<CacheWebsiteService> cacheWebsiteServiceObjectFactory;

    @RabbitListener(queues = "#{@rabbitQueueName}")
    public void receiveMessage(String message) {
        try {
            WebsiteData data = objectMapper.readValue(message, WebsiteData.class);
            messageBuffer.add(data);
            if (messageBuffer.size() >= BATCH_SIZE) {
                processBatch();
            }
        } catch (Exception ex) {
            logger.error("Error while processing website message: {}", ex.getMessage(), ex);
        }
    }

    private void processBatch() {
        try {
            CacheWebsiteService cacheWebsiteService = cacheWebsiteServiceObjectFactory.getObject();
            logger.info("Processing batch of {} messages", messageBuffer.size());
            ArrayList<String> urls = new ArrayList<>();
            for (WebsiteData data : messageBuffer) {
                urls.add(data.getUrl());
            }
            cacheWebsiteService.cacheWebsite(urls, messageBuffer.get(0).getDomain());
            messageBuffer.clear();
        } catch (Exception ex) {
            logger.error("Error while processing batch: {}", ex.getMessage(), ex);
        }
    }
}