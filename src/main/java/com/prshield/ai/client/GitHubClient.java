package com.prshield.ai.client;

import com.prshield.ai.dto.GitHubPullRequestDTO;
import com.prshield.ai.dto.GitHubPullRequestFileDTO;
import com.prshield.ai.dto.GitHubRepositoryDTO;
import com.prshield.ai.config.GitHubConfig;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GitHubClient {

    private final RestClient restClient;

    public GitHubClient(GitHubConfig config) {
        this.restClient = RestClient.builder()
                .baseUrl(config.getApiBaseUrl())
                .defaultHeader("Authorization", "Bearer " + config.getToken())
                .build();
    }

    public GitHubRepositoryDTO getRepository(String owner, String repository) {

        return restClient.get()
                .uri("/repos/{owner}/{repository}", owner, repository)
                .retrieve()
                .body(GitHubRepositoryDTO.class);
    }
    public List<GitHubPullRequestDTO> getPullRequests(
            String owner,
            String repository
    ) {

        return restClient.get()
                .uri("/repos/{owner}/{repository}/pulls", owner, repository)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<List<GitHubPullRequestDTO>>() {}
                );
    }
    public List<GitHubPullRequestFileDTO> getPullRequestFiles(
            String owner,
            String repository,
            int pullRequestNumber
    ) {

        return restClient.get()
                .uri(
                        "/repos/{owner}/{repository}/pulls/{pullRequestNumber}/files",
                        owner,
                        repository,
                        pullRequestNumber
                )
                .retrieve()
                .body(
                        new ParameterizedTypeReference<List<GitHubPullRequestFileDTO>>() {}
                );
    }
}