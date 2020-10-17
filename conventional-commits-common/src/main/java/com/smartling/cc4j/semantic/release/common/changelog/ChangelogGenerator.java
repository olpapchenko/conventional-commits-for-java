package com.smartling.cc4j.semantic.release.common.changelog;

import com.smartling.cc4j.semantic.release.common.Commit;
import com.smartling.cc4j.semantic.release.common.ConventionalCommitType;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ChangelogGenerator {
    private static final String CHANGELOG_FORMAT = "## %s (%s)" +
        "%s";
    private static final String BUG_FIXES_HEADER = "Bug fixes";
    private static final String FEATURE_HEADER = "Feature";
    private static final String BREAKING_HEADER = "Breaking changes";
    private static final String DOCS_HEADER = "Docs";
    private static final String CI_HEADER = "CI";
    private static final String BUILD_HEADER = "Build";

    public String generate(String nextVersion, Map<ConventionalCommitType, Set<Commit>> commitsByCommitType) {
        Objects.requireNonNull(nextVersion, "next version can not be null");

        if (commitsByCommitType == null || commitsByCommitType.isEmpty()) {
            return null;
        }

        List<String> sections = new ArrayList<>();

        if (commitsByCommitType.get(ConventionalCommitType.BREAKING_CHANGE) != null) {
            sections.add(getSection(BREAKING_HEADER, commitsByCommitType.get(ConventionalCommitType.BREAKING_CHANGE)));
        }

        if (commitsByCommitType.get(ConventionalCommitType.FIX) != null) {
            sections.add(getSection(BUG_FIXES_HEADER, commitsByCommitType.get(ConventionalCommitType.FIX)));
        }

        if (commitsByCommitType.get(ConventionalCommitType.FEAT) != null) {
            sections.add(getSection(FEATURE_HEADER, commitsByCommitType.get(ConventionalCommitType.FEAT)));
        }

        if (commitsByCommitType.get(ConventionalCommitType.DOCS) != null) {
            sections.add(getSection(DOCS_HEADER, commitsByCommitType.get(ConventionalCommitType.DOCS)));
        }

        if (commitsByCommitType.get(ConventionalCommitType.CI) != null) {
            sections.add(getSection(CI_HEADER, commitsByCommitType.get(ConventionalCommitType.CI)));
        }

        if (commitsByCommitType.get(ConventionalCommitType.BUILD) != null) {
            sections.add(getSection(BUILD_HEADER, commitsByCommitType.get(ConventionalCommitType.BUILD)));
        }

        sections = sections.stream().filter(Objects::nonNull).collect(Collectors.toList());

        if(sections.isEmpty()) {
            return null;
        }

        return String.format(CHANGELOG_FORMAT, nextVersion, LocalDate.now(), "\n" + String.join("\n", sections));
    }

    private String getSection(String header, Set<Commit> commits) {
        String sectionEntries = getSectionEntries(commits);
        if(sectionEntries != null && !sectionEntries.trim().equals("")) {
            return "###" + header + "\n" + sectionEntries;
        }

        return null;
    }

    private String getSectionEntries(Set<Commit> commits) {
        Set<String> uniqueMessages = new HashSet<>();
        return commits.stream()
            .filter(commit -> uniqueMessages.add(commit.getCommitMessage()))
            .map(this::getChangeLogEntry)
            .filter(Objects::nonNull)
            .sorted()
            .collect(Collectors.joining("\n"));
    }

    private String getChangeLogEntry(Commit commit) {
        if (commit.getCommitMessage() != null && !commit.getCommitMessage().trim().equals("")) {
            return "*" + commit.getCommitMessage() + " (" + commit.getCommitHash().substring(0, 8) + ")";
        }
        return null;
    }
}
