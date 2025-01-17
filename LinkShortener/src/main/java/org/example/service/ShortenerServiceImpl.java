package org.example.service;
import org.example.entity.Link;
import org.example.entity.User;

import java.awt.Desktop;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;

public class ShortenerServiceImpl implements ShortenerService {
    private final Map<String, User> users;
    private final Map<String, Link> allLinks;
    private final ScheduledExecutorService executor;
    private static final String CONFIG_FILE = "src/main/resources/config.properties";
    private static long DEFAULT_EXPIRY_TIME;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(CONFIG_FILE)) {
            properties.load(inputStream);
            DEFAULT_EXPIRY_TIME = Long.parseLong(properties.getProperty("default.expiry.time", "86400000")); // 24 часа по умолчанию
        } catch (IOException e) {
            System.err.println("Error loading config file: " + e.getMessage());
            // If we have error while loading file set DEFAULT_EXPIRY_TIME to default
            DEFAULT_EXPIRY_TIME = 24 * 60 * 60 * 1000; //24 hours
        }
    }

    public long getDefaultExpiryTime() {
        return DEFAULT_EXPIRY_TIME;
    }
    public ShortenerServiceImpl() {
        this.users = new HashMap<>();
        this.allLinks = new ConcurrentHashMap<>();
        this.executor = Executors.newScheduledThreadPool(1);
        startCleanupTask();
    }

    /**
     * For tests TimeUnit.SECONDS then after testing we can change it to TimeUnit.Hours)
     */
    private void startCleanupTask() {
        executor.scheduleAtFixedRate(() -> {
            allLinks.values().removeIf(Link::isExpired);
        }, 1, 1, TimeUnit.HOURS);
    }

    public User registerUser() {
        User user = new User();
        users.put(user.getUserId().toString(), user);
        return user;
    }

    private String generateShortUrl(User user) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder shortUrl = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            shortUrl.append(characters.charAt(random.nextInt(characters.length())));
        }
        return user.getUserId().toString().substring(0, 8) + shortUrl.toString();
    }

    public String shortenUrl(User user, String originalUrl, int maxClicks) {
        long expiryTime = DEFAULT_EXPIRY_TIME;
        String shortUrl = generateShortUrl(user);
        Link link = new Link(originalUrl, shortUrl, expiryTime, maxClicks);
        user.addLink(link);
        allLinks.put(shortUrl, link);
        return shortUrl;
    }

    public String redirect(String shortUrl) throws Exception {
        Link link = allLinks.get(shortUrl);
        if (link == null || link.isExpired()) {
            return "Link is unavailable or expired.";
        }
        if (!link.incrementClicks()) {
            return "Link click limit exceeded.";
        }
        Desktop.getDesktop().browse(new URI(link.getOriginalUrl()));
        return "Redirecting to: " + link.getOriginalUrl();
    }

    public Map<String, Link> getAllLinks() {
        return allLinks;
    }

    public void shutdown() {
        executor.shutdown();
    }
}
