package com.fastrender.CacheWebsite.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CacheDataModel implements Serializable {
    private String lastModified;
    private String pageSource;
}
