package org.example.entity;

public class Link {
    private final String originalUrl;
    private final String shortUrl;
    private final long creationTime;
    private final long expiryTime;
    private int maxClicks;
    private int clicks;

    public Link(String originalUrl, String shortUrl, long expiryTime, int maxClicks) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.creationTime = System.currentTimeMillis();
        this.expiryTime = expiryTime;
        this.maxClicks = maxClicks;
        this.clicks = 0;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > creationTime + expiryTime;
    }

    public boolean incrementClicks() {
        if (clicks < maxClicks) {
            clicks++;
            return true;
        }
        return false;
    }

    public int getClicks() {
        return clicks;
    }

    public void setMaxClicks(int maxClicks) {
        this.maxClicks = maxClicks;
    }
}
