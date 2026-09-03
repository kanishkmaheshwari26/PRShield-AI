package com.prshield.ai.security;

import com.prshield.ai.model.PRShieldChangedFile;
import com.prshield.ai.model.SecurityFinding;

import java.util.List;

public interface SecurityDetector {

    List<SecurityFinding> detect(
            PRShieldChangedFile file,
            String codeLine,
            int lineNumber
    );
}