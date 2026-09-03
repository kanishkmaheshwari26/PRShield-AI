package com.prshield.ai.service;
import com.prshield.ai.model.SecurityFinding;
import com.prshield.ai.security.SecurityAnalyzer;
import com.prshield.ai.mapper.GitHubMapper;
import com.prshield.ai.model.PRShieldChangedFile;
import com.prshield.ai.dto.GitHubRepositoryDTO;
import com.prshield.ai.dto.GitHubPullRequestDTO;
import com.prshield.ai.dto.GitHubPullRequestFileDTO;
import java.util.List;
import com.prshield.ai.client.GitHubClient;
import org.springframework.stereotype.Service;

@Service
public class GitHubService {

    private final GitHubClient gitHubClient;
    private final GitHubMapper gitHubMapper;
    private final SecurityAnalyzer securityAnalyzer;

    public GitHubService(
            GitHubClient gitHubClient,
            GitHubMapper gitHubMapper,
            SecurityAnalyzer securityAnalyzer
    ) {
        this.gitHubClient = gitHubClient;
        this.gitHubMapper = gitHubMapper;
        this.securityAnalyzer = securityAnalyzer;
    }

    public GitHubRepositoryDTO getRepository(String owner, String repository) {
        return gitHubClient.getRepository(owner, repository);
    }
    public List<GitHubPullRequestDTO> getPullRequests(
            String owner,
            String repository
    ) {
        return gitHubClient.getPullRequests(owner, repository);
    }
    public List<PRShieldChangedFile> getPullRequestFiles(
            String owner,
            String repository,
            int pullRequestNumber
    ) {
        List<GitHubPullRequestFileDTO> githubFiles =
                gitHubClient.getPullRequestFiles(
                        owner,
                        repository,
                        pullRequestNumber
                );

        return githubFiles.stream()
                .map(gitHubMapper::toChangedFile)
                .toList();
    }
    public List<SecurityFinding> analyzePullRequest(
            String owner,
            String repository,
            int pullRequestNumber
    ) {

        List<PRShieldChangedFile> changedFiles =
                getPullRequestFiles(
                        owner,
                        repository,
                        pullRequestNumber
                );

        List<SecurityFinding> findings =
                new java.util.ArrayList<>();

        for (PRShieldChangedFile file : changedFiles) {

            findings.addAll(
                    securityAnalyzer.analyze(file)
            );
        }

        return findings;
    }
}