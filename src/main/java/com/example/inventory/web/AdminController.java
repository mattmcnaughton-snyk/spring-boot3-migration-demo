package com.example.inventory.web;

import com.example.inventory.service.LegacyExportService;
import org.apache.commons.text.StringSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    // Log4j 2.14.1 is on the classpath (see pom.xml), so this is the Log4Shell sink.
    private static final Logger log = LogManager.getLogger(AdminController.class);

    private final LegacyExportService legacyExportService;

    public AdminController(LegacyExportService legacyExportService) {
        this.legacyExportService = legacyExportService;
    }

    /**
     * VULNERABLE: OS command injection, e.g. {@code ?host=localhost;cat /etc/passwd}.
     */
    @GetMapping("/ping")
    public Map<String, Object> ping(@RequestParam String host) throws IOException, InterruptedException {
        log.info("Pinging host " + host);
        Process process = new ProcessBuilder("sh", "-c", "ping -c 1 " + host).start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return Map.of("exitCode", process.waitFor(), "output", output);
    }

    /**
     * VULNERABLE: deserialisation of untrusted data, with commons-collections 3.2.1
     * on the classpath as a gadget chain.
     */
    @PostMapping("/import")
    public Map<String, String> importBackup(@RequestBody byte[] payload)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(payload))) {
            Object restored = in.readObject();
            return Map.of("restoredType", restored.getClass().getName());
        }
    }

    /**
     * VULNERABLE: Text4Shell (CVE-2022-42889). commons-text 1.9 interpolation evaluates
     * lookups such as {@code ${script:javascript:...}} in user input.
     */
    @GetMapping("/label")
    public Map<String, String> label(@RequestParam String template) {
        StringSubstitutor interpolator = StringSubstitutor.createInterpolator();
        return Map.of("label", interpolator.replace(template));
    }

    /**
     * VULNERABLE: MD5 used to fingerprint credentials.
     */
    @GetMapping("/fingerprint")
    public Map<String, String> fingerprint(@RequestParam String secret) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(secret.getBytes(StandardCharsets.UTF_8));
        return Map.of("md5", String.format("%032x", new BigInteger(1, digest)));
    }

    @GetMapping("/export")
    public Map<String, Object> export(@RequestParam String table) throws Exception {
        return Map.of("rows", legacyExportService.exportTable(table));
    }
}
