package com.onlineshop.shop.common;

import jakarta.annotation.PostConstruct;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class SwaggerUiOpener {
    @EventListener(ContextRefreshedEvent.class)
    public void openSwaggerUi() {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            // get the port from environment variable
            String serverPort = System.getenv("SERVER_PORT");
            if (serverPort == null || serverPort.isEmpty()) {
                serverPort = "8080"; // Default to 8080 if the environment variable is not set
            }

            if (os.contains("win")) {
                // For Windows
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler http://localhost:" + serverPort + "/swagger-ui/index.html");
            } else if (os.contains("mac")) {
                // For macOS
                Runtime.getRuntime().exec("open http://localhost:" + serverPort + "/swagger-ui/index.html");
            } else if (os.contains("nix") || os.contains("nux")) {
                // For Linux
                Runtime.getRuntime().exec("xdg-open http://localhost:" + serverPort + "/swagger-ui/index.html");
            } else {
                System.out.println("Unsupported operating system. Please open http://localhost:" + serverPort + "/swagger-ui/index.html manually.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
