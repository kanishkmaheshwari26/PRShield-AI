package com.prshield.ai;

import com.prshield.ai.client.GitHubClient;
import com.prshield.ai.mapper.GitHubMapper;
import com.prshield.ai.model.PRShieldChangedFile;
import com.prshield.ai.model.SecurityFinding;
import com.prshield.ai.security.SecurityAnalyzer;
import com.prshield.ai.service.GitHubService;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GitHubServiceTest {

    @Test
    void shouldAnalyzePullRequest() {

        GitHubClient gitHubClient =
                mock(GitHubClient.class);

        GitHubMapper gitHubMapper =
                mock(GitHubMapper.class);

        SecurityAnalyzer securityAnalyzer =
                mock(SecurityAnalyzer.class);

        GitHubService gitHubService =
                new GitHubService(
                        gitHubClient,
                        gitHubMapper,
                        securityAnalyzer
                );

        PRShieldChangedFile file =
                new PRShieldChangedFile();

        file.setFilename("PaymentService.java");

        file.setPatch(
                "@@ -10,2 +10,3 @@\n" +
                        " public class PaymentService {\n" +
                        "+    String password = \"secret123\";\n" +
                        " }"
        );

        when(
                gitHubClient.getPullRequestFiles(
                        "octocat",
                        "Hello-World",
                        1
                )
        ).thenReturn(List.of(
                new com.prshield.ai.dto.GitHubPullRequestFileDTO()
        ));

        when(
                gitHubMapper.toChangedFile(
                        any(com.prshield.ai.dto.GitHubPullRequestFileDTO.class)
                )
        ).thenReturn(file);

        SecurityFinding finding =
                new SecurityFinding();

        finding.setFilePath(
                "PaymentService.java"
        );

        finding.setLineNumber(12);

        finding.setSeverity("HIGH");

        finding.setType(
                "HARDCODED_SECRET"
        );

        finding.setMessage(
                "Possible hardcoded secret detected."
        );

        finding.setConfidence(90);

        when(
                securityAnalyzer.analyze(file)
        ).thenReturn(List.of(finding));

        List<SecurityFinding> findings =
                gitHubService.analyzePullRequest(
                        "octocat",
                        "Hello-World",
                        1
                );

        assertEquals(1, findings.size());

        assertEquals(
                "HARDCODED_SECRET",
                findings.get(0).getType()
        );

        assertEquals(
                "PaymentService.java",
                findings.get(0).getFilePath()
        );

        verify(
                securityAnalyzer,
                times(1)
        ).analyze(file);
    }
}