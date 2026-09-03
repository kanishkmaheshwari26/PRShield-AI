package com.prshield.ai.security;

import com.prshield.ai.model.PRShieldChangedFile;
import com.prshield.ai.model.SecurityFinding;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SecurityAnalyzer {

    private final List<SecurityDetector> detectors;

    public SecurityAnalyzer(
            List<SecurityDetector> detectors
    ) {
        this.detectors = detectors;
    }

    public List<SecurityFinding> analyze(
            PRShieldChangedFile file
    ) {

        List<SecurityFinding> findings =
                new ArrayList<>();

        String patch = file.getPatch();

        if (patch == null || patch.isEmpty()) {
            return findings;
        }

        String[] lines = patch.split("\n");

        int currentLineNumber = 0;

        for (String line : lines) {

            if (line.startsWith("@@")) {

                int plusIndex = line.indexOf('+');

                String newFileInfo =
                        line.substring(plusIndex + 1);

                int commaIndex =
                        newFileInfo.indexOf(',');

                String startingLine =
                        newFileInfo.substring(
                                0,
                                commaIndex
                        );

                currentLineNumber =
                        Integer.parseInt(startingLine);

            } else if (
                    line.startsWith("+")
                            && !line.startsWith("+++")
            ) {

                String codeLine =
                        line.substring(1).trim();

                for (SecurityDetector detector
                        : detectors) {

                    findings.addAll(
                            detector.detect(
                                    file,
                                    codeLine,
                                    currentLineNumber
                            )
                    );
                }

                currentLineNumber++;

            } else if (!line.startsWith("-")) {

                currentLineNumber++;
            }
        }

        return findings;
    }
}