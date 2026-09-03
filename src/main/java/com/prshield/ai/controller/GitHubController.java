package com.prshield.ai.controller;
import com.prshield.ai.model.SecurityFinding;
import com.prshield.ai.model.PRShieldChangedFile;
import com.prshield.ai.dto.GitHubPullRequestDTO;
import com.prshield.ai.dto.GitHubPullRequestFileDTO;
import com.prshield.ai.dto.GitHubRepositoryDTO;
import com.prshield.ai.service.GitHubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/repository/{owner}/{repository}")
    public GitHubRepositoryDTO getRepository(
            @PathVariable String owner,
            @PathVariable String repository) {

        return gitHubService.getRepository(owner, repository);
    }
    @GetMapping("/repository/{owner}/{repository}/pulls")
    public List<GitHubPullRequestDTO> getPullRequests(
            @PathVariable String owner,
            @PathVariable String repository
    ) {
        return gitHubService.getPullRequests(owner, repository);
    }
    @GetMapping("/repository/{owner}/{repository}/pulls/{pullRequestNumber}/files")
    public List<PRShieldChangedFile> getPullRequestFiles(
            @PathVariable String owner,
            @PathVariable String repository,
            @PathVariable int pullRequestNumber
    ) {
        return gitHubService.getPullRequestFiles(
                owner,
                repository,
                pullRequestNumber
        );
    }
    @GetMapping(
            "/{owner}/{repository}/pulls/{pullRequestNumber}/security"
    )
    public List<SecurityFinding> analyzePullRequest(
            @PathVariable String owner,
            @PathVariable String repository,
            @PathVariable int pullRequestNumber
    ) {

        return gitHubService.analyzePullRequest(
                owner,
                repository,
                pullRequestNumber
        );
    }
}