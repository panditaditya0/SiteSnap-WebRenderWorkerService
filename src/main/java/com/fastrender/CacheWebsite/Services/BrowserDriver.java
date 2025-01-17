package com.fastrender.CacheWebsite.Services;

import org.springframework.stereotype.Service;

@Service
public interface BrowserDriver {
    <T> T deleteSession();
    <T> T createSession();
    <T> T navigateToUrl(String url);
    <T> T getPageSource();
    <T> T openNewTab(String url, int tabIndex);
}