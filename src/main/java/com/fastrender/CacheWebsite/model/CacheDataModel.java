package com.fastrender.CacheWebsite.model;

import lombok.*;

import java.io.Serializable;
import java.util.LinkedHashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CacheDataModel implements Serializable {
    private String lastModified;
    private String pageSource;
    private String screenShotBase64;

    // Convert LinkedHashMap to CacheDataModel
    public static CacheDataModel fromMap(LinkedHashMap<String, Object> map) {
        if (map == null) {
            return null;
        }

        return new CacheDataModel(
                (String) map.getOrDefault("lastModified", null),
                (String) map.getOrDefault("pageSource", null),
                (String) map.getOrDefault("screenShotBase64", null)
        );
    }
}
