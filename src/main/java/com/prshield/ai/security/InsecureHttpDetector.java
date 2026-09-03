package com.prshield.ai.security;

import com.prshield.ai.model.PRShieldChangedFile;
import com.prshield.ai.model.SecurityFinding;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class InsecureHttpDetector
        implements SecurityDetector {

    @Override
    public List<SecurityFinding> detect(
            PRShieldChangedFile file,
            String codeLine,
            int lineNumber
    ) {

        List<SecurityFinding> findings =
                new ArrayList<>();

        String lowerCaseLine =
                codeLine.toLowerCase();

        boolean isComment =
                lowerCaseLine.startsWith("//")
                        || lowerCaseLine.startsWith("/*")
                        || lowerCaseLine.startsWith("*")
                        || lowerCaseLine.startsWith("*/");

        if (isComment) {
            return findings;
        }

        boolean containsHttp =
                lowerCaseLine.contains("http://");

        if (containsHttp) {

            SecurityFinding finding =
                    new SecurityFinding();

            finding.setFilePath(
                    file.getFilename()
            );

            finding.setLineNumber(
                    lineNumber
            );

            finding.setSeverity(
                    "MEDIUM"
            );

            finding.setType(
                    "INSECURE_HTTP"
            );

            finding.setMessage(
                    "Insecure HTTP URL detected. Use HTTPS when possible."
            );

            finding.setConfidence(
                    90
            );

            findings.add(finding);
        }

        return findings;
    }
}