package com.prshield.ai.mapper;

import com.prshield.ai.dto.GitHubPullRequestFileDTO;
import com.prshield.ai.model.PRShieldChangedFile;
import org.springframework.stereotype.Component;

@Component
public class GitHubMapper {

    public PRShieldChangedFile toChangedFile(
            GitHubPullRequestFileDTO githubFile
    ) {

        PRShieldChangedFile changedFile = new PRShieldChangedFile();

        changedFile.setFilename(githubFile.getFilename());
        changedFile.setStatus(githubFile.getStatus());
        changedFile.setAdditions(githubFile.getAdditions());
        changedFile.setDeletions(githubFile.getDeletions());
        changedFile.setChanges(githubFile.getChanges());
        changedFile.setPatch(githubFile.getPatch());

        return changedFile;
    }
}