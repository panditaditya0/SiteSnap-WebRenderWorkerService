package com.fastrender.CacheWebsite.controller;

import com.fastrender.CacheWebsite.Dto.ErrorResponse;
import com.fastrender.CacheWebsite.Services.CacheService;
import com.fastrender.CacheWebsite.Services.CacheWebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@ConditionalOnProperty(name = "controller.enabled", havingValue = "true", matchIfMissing = false)
@RequestMapping("/api/render")
public class RenderController {

    @Autowired
    private CacheWebsiteService cacheWebsiteService;

    @PostMapping
    public ResponseEntity<?> getPageSource(@RequestParam String websiteUrl,
                                           @RequestParam String websiteDomain) {
        String pageSource = null;
        try {
            pageSource = cacheWebsiteService.getPageSource(websiteUrl);
            cacheWebsiteService.savePageSource(websiteDomain, websiteUrl, pageSource, "");
        } catch (Exception ex) {
            ErrorResponse a = new ErrorResponse(LocalDateTime.now(), ex.getMessage(), ex.getStackTrace().toString());
            return new ResponseEntity<>(a, HttpStatus.OK);
        }
        return ResponseEntity.ok(pageSource);
    }
}