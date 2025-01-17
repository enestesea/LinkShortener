package org.example;

import org.example.entity.User;
import org.example.service.ShortenerServiceImpl;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        ShortenerServiceImpl service = new ShortenerServiceImpl();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Registering new user...");
        User user = service.registerUser();
        System.out.println("Your User ID: " + user.getUserId());

        while (true) {
            System.out.println("1. Shorten URL\n2. Redirect\n3. Update max clicks\n4. Delete Link\n5. Exit");
            int choice = -1;
            try {
                choice = scanner.nextInt();
            }
            catch (InputMismatchException e) {
                System.out.println("Invalid number.");
            }
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Enter original URL:");
                    String originalUrl = scanner.nextLine();
                    System.out.println("Enter max clicks:");
                    int maxClicks = scanner.nextInt();
                    scanner.nextLine();

                    String shortUrl = service.shortenUrl(user, originalUrl, maxClicks);
                    System.out.println("Short URL: " + shortUrl);
                    break;

                case 2:
                    System.out.println("Enter short URL:");
                    String shortUrlToRedirect = scanner.nextLine();
                    try {
                        String message = service.redirect(shortUrlToRedirect);
                        System.out.println(message);
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 3:
                    System.out.println("Enter short URL to update max clicks:");
                    String shortUrlToUpdate = scanner.nextLine();
                    System.out.println("Enter new max clicks:");
                    int newMaxClicks = scanner.nextInt();
                    scanner.nextLine();
                    service.updateMaxClicks(user, shortUrlToUpdate, newMaxClicks);
                    break;

                case 4:
                    System.out.println("Enter short URL to delete:");
                    String shortUrlToDelete = scanner.nextLine();
                    service.deleteLink(user, shortUrlToDelete);
                    break;

                case 5:
                    service.shutdown();
                    System.out.println("Goodbye!");
                    return;
            }
        }
    }
}
