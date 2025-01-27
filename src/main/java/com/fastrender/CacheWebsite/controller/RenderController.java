package com.fastrender.CacheWebsite.controller;

import com.fastrender.CacheWebsite.Dto.ErrorResponse;
import com.fastrender.CacheWebsite.Services.CacheService;
import com.fastrender.CacheWebsite.Services.CacheWebsiteService;
import com.fastrender.CacheWebsite.model.CacheDataModel;
import com.fastrender.CacheWebsite.model.WebsiteData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Optional;

@RestController
@ConditionalOnProperty(name = "controller.enabled", havingValue = "true", matchIfMissing = false)
@RequestMapping("/api/render")
public class RenderController {

    @Autowired
    private CacheWebsiteService cacheWebsiteService;

    @Autowired
    private CacheService cacheService;

    @PostMapping
    public ResponseEntity<?> getPageSource(@RequestParam String websiteUrl,
                                           @RequestParam String websiteDomain) {
        WebsiteData a = new WebsiteData();
        a.setUrl(websiteUrl);
        a.setDomain(websiteDomain);
        Optional optinalModel = cacheService.loadFromDb(a) ;
        if (optinalModel.isPresent()) {
            LinkedHashMap<String, Object> fromDb = (LinkedHashMap<String, Object>) optinalModel.get();
            CacheDataModel cacheData = CacheDataModel.fromMap(fromDb);
            return new ResponseEntity<>(cacheData.getPageSource(), HttpStatus.OK);
        }
        String pageSource = null;
        try {
            pageSource = cacheWebsiteService.getPageSource(websiteUrl);
            cacheWebsiteService.savePageSource(websiteDomain, websiteUrl, pageSource, "");
        } catch (Exception ex) {
            ErrorResponse b = new ErrorResponse(LocalDateTime.now(), ex.getMessage(), ex.getStackTrace().toString());
            return new ResponseEntity<>(b, HttpStatus.OK);
        }
        return ResponseEntity.ok(pageSource);
    }
}