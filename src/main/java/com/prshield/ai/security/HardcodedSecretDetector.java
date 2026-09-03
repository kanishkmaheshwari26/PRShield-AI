package com.prshield.ai.security;

import com.prshield.ai.model.PRShieldChangedFile;
import com.prshield.ai.model.SecurityFinding;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class HardcodedSecretDetector
        implements SecurityDetector {

    private static final List<String> SECURITY_KEYWORDS = List.of(
            "password",
            "apikey",
            "secret",
            "token",
            "credential",
            "authkey",
            "privatekey",
            "accesskey",
            "clientsecret"
    );

    private static final List<String> PLACEHOLDER_VALUES = List.of(
            "password",
            "secret",
            "token",
            "admin",
            "test",
            "example",
            "changeme"
    );

    public List<SecurityFinding> detect(
            PRShieldChangedFile file,
            String codeLine,
            int lineNumber
    ) {

        List<SecurityFinding> findings = new ArrayList<>();

        String lowerCaseLine = codeLine.toLowerCase();

        String normalizedKeywordLine =
                lowerCaseLine.replace("_", "");

        boolean containsSecurityKeyword = false;

        int confidence = 0;

        for (String keyword : SECURITY_KEYWORDS) {

            String pattern = "\\b" + keyword + "\\b";

            if (Pattern.compile(pattern)
                    .matcher(normalizedKeywordLine)
                    .find()) {

                containsSecurityKeyword = true;

                confidence += 30;

                break;
            }
        }

        boolean containsQuotedValue =
                codeLine.contains("\"");

        if (containsQuotedValue) {
            confidence += 10;
        }

        String normalizedLine = codeLine
                .replace("==", "")
                .replace(">=", "")
                .replace("<=", "")
                .replace("!=", "");

        boolean containsAssignment =
                normalizedLine.contains("=");

        if (containsAssignment) {
            confidence += 20;
        }

        if (containsSecurityKeyword
                && containsQuotedValue
                && containsAssignment) {

            int firstQuote = codeLine.indexOf('"');

            int secondQuote =
                    codeLine.indexOf('"', firstQuote + 1);

            if (firstQuote != -1 && secondQuote != -1) {

                String value = codeLine.substring(
                        firstQuote + 1,
                        secondQuote
                );

                if (value.length() >= 12) {
                    confidence += 20;
                }

                if (value.length() >= 20) {
                    confidence += 10;
                }

                String lowerCaseValue =
                        value.toLowerCase().trim();

                boolean isPlaceholder =
                        PLACEHOLDER_VALUES.contains(lowerCaseValue);

                if (!isPlaceholder) {

                    SecurityFinding finding =
                            new SecurityFinding();

                    finding.setFilePath(file.getFilename());
                    finding.setLineNumber(lineNumber);
                    finding.setSeverity("HIGH");
                    finding.setType("HARDCODED_SECRET");
                    finding.setMessage(
                            "Possible hardcoded secret detected."
                    );

                    finding.setConfidence(confidence);

                    findings.add(finding);
                }
            }
        }

        return findings;
    }
}