package com.fastrender.CacheWebsite.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WebsiteData implements Serializable {
    private String url;
    private String domain;
    private String expirationTime;
}
