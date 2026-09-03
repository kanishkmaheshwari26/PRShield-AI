package com.prshield.ai;

import com.prshield.ai.model.PRShieldChangedFile;
import com.prshield.ai.model.SecurityFinding;
import com.prshield.ai.security.HardcodedSecretDetector;
import com.prshield.ai.security.SecurityAnalyzer;
import com.prshield.ai.security.InsecureHttpDetector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecurityAnalyzerTest {

    @Test
    void shouldDetectHardcodedSecrets() {

        PRShieldChangedFile file = new PRShieldChangedFile();

        file.setFilename("LoginService.java");

        file.setPatch(
                "@@ -10,3 +10,5 @@\n" +
                        " public class LoginService {\n" +
                        " \n" +
                        "+    String password = \"admin123\";\n" +
                        "+    String token = \"abc123\";\n" +
                        " }"
        );

        HardcodedSecretDetector detector =
                new HardcodedSecretDetector();

        SecurityAnalyzer analyzer =
                new SecurityAnalyzer(
                        List.of(detector)
                );

        List<SecurityFinding> findings =
                analyzer.analyze(file);

        assertEquals(2, findings.size());

        assertEquals(
                "LoginService.java",
                findings.get(0).getFilePath()
        );

        assertEquals(
                12,
                findings.get(0).getLineNumber()
        );

        assertEquals(
                "HIGH",
                findings.get(0).getSeverity()
        );
        assertEquals(
                60,
                findings.get(0).getConfidence()
        );

        assertEquals(
                "HARDCODED_SECRET",
                findings.get(0).getType()
        );

        assertEquals(
                "LoginService.java",
                findings.get(1).getFilePath()
        );

        assertEquals(
                13,
                findings.get(1).getLineNumber()
        );

        assertEquals(
                "HIGH",
                findings.get(1).getSeverity()
        );
        assertEquals(
                60,
                findings.get(1).getConfidence()
        );

        assertEquals(
                "HARDCODED_SECRET",
                findings.get(1).getType()
        );
    }

    @Test
    void shouldNotDetectNormalVariable() {

        PRShieldChangedFile file = new PRShieldChangedFile();

        file.setFilename("LoginService.java");

        file.setPatch(
                "@@ -10,3 +10,4 @@\n" +
                        " public class LoginService {\n" +
                        " \n" +
                        "+    String password = getPassword();\n" +
                        " }"
        );

        HardcodedSecretDetector detector =
                new HardcodedSecretDetector();

        SecurityAnalyzer analyzer =
                new SecurityAnalyzer(
                        List.of(detector)
                );
        List<SecurityFinding> findings =
                analyzer.analyze(file);

        assertEquals(0, findings.size());
    }

    @Test
    void shouldNotDetectPasswordMentionInMessage() {

        PRShieldChangedFile file = new PRShieldChangedFile();

        file.setFilename("LoginService.java");

        file.setPatch(
                "@@ -10,3 +10,4 @@\n" +
                        " public class LoginService {\n" +
                        " \n" +
                        "+    System.out.println(\"Enter your password\");\n" +
                        " }"
        );

        HardcodedSecretDetector detector =
                new HardcodedSecretDetector();

        SecurityAnalyzer analyzer =
                new SecurityAnalyzer(
                        List.of(detector)
                );

        List<SecurityFinding> findings =
                analyzer.analyze(file);

        assertEquals(0, findings.size());
    }

    @Test
    void shouldNotDetectPasswordComparison() {

        PRShieldChangedFile file = new PRShieldChangedFile();

        file.setFilename("LoginService.java");

        file.setPatch(
                "@@ -10,3 +10,4 @@\n" +
                        " public class LoginService {\n" +
                        " \n" +
                        "+    if (password == \"admin123\") {\n" +
                        " }"
        );

        HardcodedSecretDetector detector =
                new HardcodedSecretDetector();

        SecurityAnalyzer analyzer =
                new SecurityAnalyzer(
                        List.of(detector)
                );

        List<SecurityFinding> findings =
                analyzer.analyze(file);

        assertEquals(0, findings.size());
    }
    @Test
    void shouldDetectApiKey() {

        PRShieldChangedFile file = new PRShieldChangedFile();

        file.setFilename("ApiService.java");

        file.setPatch(
                "@@ -20,3 +20,4 @@\n" +
                        " public class ApiService {\n" +
                        " \n" +
                        "+    String apiKey = \"ABC123XYZ\";\n" +
                        " }"
        );

        HardcodedSecretDetector detector =
                new HardcodedSecretDetector();

        SecurityAnalyzer analyzer =
                new SecurityAnalyzer(
                        List.of(detector)
                );

        List<SecurityFinding> findings =
                analyzer.analyze(file);

        assertEquals(1, findings.size());

        assertEquals(
                "HARDCODED_SECRET",
                findings.get(0).getType()
        );

        assertEquals(
                "HIGH",
                findings.get(0).getSeverity()
        );
    }
    @Test
    void shouldNotDetectPasswordManagerVariable() {

        PRShieldChangedFile file = new PRShieldChangedFile();

        file.setFilename("UserService.java");

        file.setPatch(
                "@@ -20,3 +20,4 @@\n" +
                        " public class UserService {\n" +
                        " \n" +
                        "+    String passwordManager = \"MyApplication\";\n" +
                        " }"
        );

        HardcodedSecretDetector detector =
                new HardcodedSecretDetector();

        SecurityAnalyzer analyzer =
                new SecurityAnalyzer(
                        List.of(detector)
                );

        List<SecurityFinding> findings =
                analyzer.analyze(file);

        assertEquals(0, findings.size());
    }
    @Test
    void shouldDetectApiKeyWithUnderscore() {

        PRShieldChangedFile file = new PRShieldChangedFile();

        file.setFilename("ApiService.java");

        file.setPatch(
                "@@ -20,3 +20,4 @@\n" +
                        " public class ApiService {\n" +
                        " \n" +
                        "+    String api_key = \"ABC123XYZ\";\n" +
                        " }"
        );

        HardcodedSecretDetector detector =
                new HardcodedSecretDetector();

        SecurityAnalyzer analyzer =
                new SecurityAnalyzer(
                        List.of(detector)
                );

        List<SecurityFinding> findings =
                analyzer.analyze(file);

        assertEquals(1, findings.size());

        assertEquals(
                "HARDCODED_SECRET",
                findings.get(0).getType()
        );

        assertEquals(
                "HIGH",
                findings.get(0).getSeverity()
        );
    }
    @Test
    void shouldNotDetectPlaceholderPassword() {

        PRShieldChangedFile file = new PRShieldChangedFile();

        file.setFilename("LoginService.java");

        file.setPatch(
                "@@ -10,3 +10,4 @@\n" +
                        " public class LoginService {\n" +
                        " \n" +
                        "+    String password = \"password\";\n" +
                        " }"
        );

        HardcodedSecretDetector detector =
                new HardcodedSecretDetector();

        SecurityAnalyzer analyzer =
                new SecurityAnalyzer(
                        List.of(detector)
                );
        List<SecurityFinding> findings =
                analyzer.analyze(file);

        assertEquals(0, findings.size());
    }
    @Test
    void shouldDetectRealLookingApiKey() {

        PRShieldChangedFile file = new PRShieldChangedFile();

        file.setFilename("PaymentService.java");

        file.setPatch(
                "@@ -30,3 +30,4 @@\n" +
                        " public class PaymentService {\n" +
                        " \n" +
                        "+    String apiKey = \"sk_live_51AbCdEfGh123456789\";\n" +
                        " }"
        );

        HardcodedSecretDetector detector =
                new HardcodedSecretDetector();

        SecurityAnalyzer analyzer =
                new SecurityAnalyzer(
                        List.of(detector)
                );

        List<SecurityFinding> findings =
                analyzer.analyze(file);

        assertEquals(1, findings.size());

        assertEquals(
                "PaymentService.java",
                findings.get(0).getFilePath()
        );

        assertEquals(
                32,
                findings.get(0).getLineNumber()
        );

        assertEquals(
                "HIGH",
                findings.get(0).getSeverity()
        );
        assertEquals(
                90,
                findings.get(0).getConfidence()
        );

        assertEquals(
                "HARDCODED_SECRET",
                findings.get(0).getType()
        );
    }
    @Test
    void shouldDetectInsecureHttp() {

        PRShieldChangedFile file = new PRShieldChangedFile();

        file.setFilename("ApiService.java");

        file.setPatch(
                "@@ -20,3 +20,4 @@\n" +
                        " public class ApiService {\n" +
                        " \n" +
                        "+    String url = \"http://api.example.com\";\n" +
                        " }"
        );

        InsecureHttpDetector httpDetector =
                new InsecureHttpDetector();

        SecurityAnalyzer analyzer =
                new SecurityAnalyzer(
                        List.of(httpDetector)
                );

        List<SecurityFinding> findings =
                analyzer.analyze(file);

        assertEquals(1, findings.size());

        assertEquals(
                "INSECURE_HTTP",
                findings.get(0).getType()
        );
    }
    @Test
    void shouldNotDetectSecureHttps() {

        PRShieldChangedFile file =
                new PRShieldChangedFile();

        file.setFilename("ApiService.java");

        file.setPatch(
                "@@ -20,3 +20,4 @@\n" +
                        " public class ApiService {\n" +
                        " \n" +
                        "+    String url = \"https://api.example.com\";\n" +
                        " }"
        );

        InsecureHttpDetector httpDetector =
                new InsecureHttpDetector();

        SecurityAnalyzer analyzer =
                new SecurityAnalyzer(
                        List.of(httpDetector)
                );

        List<SecurityFinding> findings =
                analyzer.analyze(file);

        assertEquals(0, findings.size());
    }
    @Test
    void shouldNotDetectHttpInsideComment() {

        PRShieldChangedFile file =
                new PRShieldChangedFile();

        file.setFilename("ApiService.java");

        file.setPatch(
                "@@ -20,3 +20,4 @@\n" +
                        " public class ApiService {\n" +
                        " \n" +
                        "+    // Example: http://api.example.com\n" +
                        " }"
        );

        InsecureHttpDetector httpDetector =
                new InsecureHttpDetector();

        SecurityAnalyzer analyzer =
                new SecurityAnalyzer(
                        List.of(httpDetector)
                );

        List<SecurityFinding> findings =
                analyzer.analyze(file);

        assertEquals(0, findings.size());
    }
    @Test
    void shouldDetectMultipleSecurityIssues() {

        PRShieldChangedFile file =
                new PRShieldChangedFile();

        file.setFilename("PaymentService.java");

        file.setPatch(
                "@@ -10,3 +10,5 @@\n" +
                        " public class PaymentService {\n" +
                        " \n" +
                        "+    String password = \"MySecretPassword123\";\n" +
                        "+    String url = \"http://api.example.com\";\n" +
                        " }"
        );

        HardcodedSecretDetector secretDetector =
                new HardcodedSecretDetector();

        InsecureHttpDetector httpDetector =
                new InsecureHttpDetector();

        SecurityAnalyzer analyzer =
                new SecurityAnalyzer(
                        List.of(
                                secretDetector,
                                httpDetector
                        )
                );

        List<SecurityFinding> findings =
                analyzer.analyze(file);

        assertEquals(2, findings.size());
    }
}