package com.fastrender.CacheWebsite.Services;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CacheService {
    <T> void saveToDb(String expirationTime, String websiteDomain, String websiteUrl, T data);
    <T> boolean updateFromDb(T data);
    <T> boolean deleteFromDb(T data);
    <T> boolean existsFromDb(T data);
    <T> Optional loadFromDb(T data);
}