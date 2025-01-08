package com.fastrender.CacheWebsite.Services;

public interface BrowserDriver {
    <T> T deleteSession();
    <T> T createSession();
    <T> T navigateToUrl(String url);
    <T> T getPageSource();
}