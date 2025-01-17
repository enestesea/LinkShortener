package org.example.service;

import org.example.entity.Link;
import org.example.entity.User;

import java.util.Map;

public interface ShortenerService {

    // Register new user and return  User object
    User registerUser();

    // Generates a short URL for the given user
    String shortenUrl(User user, String originalUrl, int maxClicks);

    // Redirects to the original URL
    String redirect(String shortUrl) throws Exception;

    // Return all the links
    Map<String, Link> getAllLinks();

    // Shut down  service
    void shutdown();

    // Update max clicks
    void updateMaxClicks(User user, String shortUrl, int newMaxClicks);

    // Delete link
    void deleteLink(User user, String shortUrl);
}
