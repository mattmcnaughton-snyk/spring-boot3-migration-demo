package com.example.inventory.web;

import com.example.inventory.service.LinkPreviewService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final LinkPreviewService linkPreviewService;
    private final Random random = new Random();

    public PublicController(LinkPreviewService linkPreviewService) {
        this.linkPreviewService = linkPreviewService;
    }

    /**
     * VULNERABLE: reflected XSS, user input written into an HTML response unescaped.
     */
    @GetMapping(value = "/greet", produces = MediaType.TEXT_HTML_VALUE)
    public String greet(@RequestParam(defaultValue = "guest") String name) {
        return "<html><body><h1>Welcome to the inventory, " + name + "!</h1></body></html>";
    }

    /**
     * VULNERABLE: open redirect to any user-supplied URL.
     */
    @GetMapping("/redirect")
    public RedirectView redirect(@RequestParam String next) {
        return new RedirectView(next);
    }

    /**
     * VULNERABLE: session token from java.util.Random, set in a cookie without the
     * Secure or HttpOnly flags.
     */
    @GetMapping("/session")
    public Map<String, String> session(HttpServletResponse response) {
        String token = Long.toHexString(random.nextLong());
        Cookie cookie = new Cookie("INVENTORY_SESSION", token);
        cookie.setPath("/");
        response.addCookie(cookie);
        return Map.of("token", token);
    }

    /**
     * VULNERABLE: server-side request forgery, fetches any URL the caller provides.
     */
    @GetMapping("/preview")
    public Map<String, Object> preview(@RequestParam String url) {
        return linkPreviewService.preview(url);
    }
}
