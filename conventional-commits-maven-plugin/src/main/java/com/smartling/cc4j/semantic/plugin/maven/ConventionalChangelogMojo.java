package com.smartling.cc4j.semantic.plugin.maven;

import com.smartling.cc4j.semantic.release.common.Commit;
import com.smartling.cc4j.semantic.release.common.ConventionalCommitType;
import com.smartling.cc4j.semantic.release.common.changelog.ChangelogGenerator;
import com.smartling.cc4j.semantic.release.common.scm.ScmApiException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Mojo(name = "changelog", aggregator = true, defaultPhase = LifecyclePhase.VALIDATE)
public class ConventionalChangelogMojo extends AbstractVersioningMojo {

    private static final String CHANGELOG_FILE_NAME = "CHANGELOG.MD";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Map<ConventionalCommitType, Set<Commit>> commitsByCommitTypes = this
                .getChangelogExtractor()
                .getGroupedCommitsByCommitTypes();

            ChangelogGenerator changelogGenerator = new ChangelogGenerator("", "");
            String changeLogs = changelogGenerator.generate(this.getNextVersion().toString(), commitsByCommitTypes);
            appendChangeLogs(changeLogs);

        } catch (IOException | ScmApiException e) {
            throw new MojoExecutionException("SCM error: " + e.getMessage(), e);
        }
    }

    private void appendChangeLogs(String changeLogs) throws IOException {
        Path changelogPath = Paths.get(this.baseDir.getAbsolutePath(), CHANGELOG_FILE_NAME);
        if(!Files.exists(changelogPath)) {
            Files.createFile(changelogPath);
        }

        List<String> resultChangeLogs = new ArrayList<>();
        resultChangeLogs.add(changeLogs);
        List<String> prevChangeLogs = Files.readAllLines(changelogPath);
        resultChangeLogs.addAll(prevChangeLogs);
        Files.write(changelogPath, resultChangeLogs);
    }
}
