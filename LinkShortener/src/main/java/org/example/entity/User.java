package org.example.entity;

import java.util.*;

public class User {
    private final UUID userId;
    private final Map<String, Link> links;

    public User() {
        this.userId = UUID.randomUUID();
        this.links = new HashMap<>();
    }

    public UUID getUserId() {
        return userId;
    }

    public Map<String, Link> getLinks() {
        return links;
    }

    public void addLink(Link link) {
        links.put(link.getShortUrl(), link);
    }

    public Link getLink(String shortUrl) {
        return links.get(shortUrl);
    }
}
