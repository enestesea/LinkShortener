
import org.example.service.ShortenerServiceImpl;
import org.example.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.TimeUnit;

public class LinkShortenerTest {

    private ShortenerServiceImpl service;
    private User user1;
    private User user2;

    @BeforeEach
    public void setup() {
        service = new ShortenerServiceImpl();
        user1 = service.registerUser();
        user2 = service.registerUser();
    }

    @Test
    public void testUniqueShortUrlsForDifferentUsers() {
        String originalUrl = "https://example.com";
        String shortUrl1 = service.shortenUrl(user1, originalUrl, 5);
        String shortUrl2 = service.shortenUrl(user2, originalUrl, 5);

        assertNotEquals(shortUrl1, shortUrl2, "Short URLs should be unique for different users");
    }

    @Test
    public void testLinkClickLimit() throws Exception {
        String originalUrl = "https://example.com";
        String shortUrl = service.shortenUrl(user1, originalUrl, 3);

        // 3 clicks
        service.redirect(shortUrl);
        service.redirect(shortUrl);
        service.redirect(shortUrl);

        // 4 click should exceed the limit
        String result = service.redirect(shortUrl);
        assertEquals("Link click limit exceeded.", result, "User should be notified if click limit is exceeded");
    }

    @Test
    public void testLinkExpiration() throws Exception {
        String originalUrl = "https://example.com";
        String shortUrl = service.shortenUrl(user1, originalUrl, 5);


        TimeUnit.MILLISECONDS.sleep(11000);

        // Check if the link expired
        String result = service.redirect(shortUrl);
        assertEquals("Link is unavailable or expired.", result, "Should notify user when link is expired");
    }

    @Test
    public void testLinkCleanupAfterExpiry() throws InterruptedException {
        String originalUrl = "https://example.com";
        String shortUrl = service.shortenUrl(user1, originalUrl, 5);


        TimeUnit.MILLISECONDS.sleep(11000);

        // Check if the link is cleaned up from the system
        assertNull(service.getAllLinks().get(shortUrl), "Link should be removed after expiration");
    }

    @Test
    public void testLinkNotExpiredBeforeExpiry() throws Exception {
        String originalUrl = "https://example.com";
        String shortUrl = service.shortenUrl(user1, originalUrl, 5);

        // Check if the link is still active before expiry time (less than 1 hour)
        String result = service.redirect(shortUrl);
        assertTrue(result.startsWith("Redirecting to: "), "Link should not expire before the expiry time");
    }

    @Test
    public void testUserNotificationOnLinkLimitExceeded() throws Exception {
        String originalUrl = "https://example.com";
        String shortUrl = service.shortenUrl(user1, originalUrl, 2);

        // Simulate 2 clicks
        service.redirect(shortUrl);
        service.redirect(shortUrl);

        // The 3rd click should exceed the limit
        String result = service.redirect(shortUrl);
        assertEquals("Link click limit exceeded.", result, "User should be notified if click limit is exceeded");
    }

    @Test
    public void testUserNotificationOnLinkExpiration() throws Exception {
        String originalUrl = "https://example.com";
        String shortUrl = service.shortenUrl(user1, originalUrl, 5);

        // Wait for link to expire
        TimeUnit.MILLISECONDS.sleep(11000);

        // Try to redirect after expiration
        String result = service.redirect(shortUrl);
        assertEquals("Link is unavailable or expired.", result, "User should be notified when the link is expired");
    }
}
