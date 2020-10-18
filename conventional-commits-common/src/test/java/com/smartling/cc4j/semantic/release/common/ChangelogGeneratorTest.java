package com.smartling.cc4j.semantic.release.common;

import com.smartling.cc4j.semantic.release.common.changelog.ChangelogGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ChangelogGeneratorTest {
    private static final String COMMIT_HASH = "2717635691";
    private ChangelogGenerator changelogGenerator;

    private static final String EXPECTED_CHANGELOG = "## 0.2.7 (2020-10-17)\n" +
        "###Breaking changes\n" +
        "* breaking change test (27176356)\n" +
        "###Bug fixes\n" +
        "* fix test (27176356)\n" +
        "* fix test 2 (27176356)\n" +
        "###Feature\n" +
        "* feat test (27176356)\n" +
        "###Docs\n" +
        "* docs test (27176356)\n" +
        "###CI\n" +
        "* ci test (27176356)\n" +
        "###Build\n" +
        "* build test (27176356)";

    @Before
    public void setUp() {
        changelogGenerator = new ChangelogGenerator("", "");
    }

    @Test
    public void testNoChangelogGeneratedOnEmptyChanges() {
        assertNull(changelogGenerator.generate("0.0.1", Collections.emptyMap()));

        Map<ConventionalCommitType, Set<Commit>> commits = new HashMap<>();
        assertNull(changelogGenerator.generate("0.0.1", commits));

        commits.put(ConventionalCommitType.FEAT, new HashSet<>(Collections.singletonList(
            new Commit(
                new DummyCommitAdapter("ci this message will not me included to changelog as there is no colon", COMMIT_HASH)))));
        assertNull(changelogGenerator.generate("0.0.1", commits));
    }

    @Test(expected = NullPointerException.class)
    public void testVersionIsMandatory() {
        changelogGenerator.generate(null, Collections.emptyMap());
    }

    @Test
    public void testChangelogGenerated() {
        Map<ConventionalCommitType, Set<Commit>> commitsByCommitType = getCommitsByCommitType();
        String changelog = changelogGenerator.generate("0.2.7", commitsByCommitType);
        assertEquals(EXPECTED_CHANGELOG, changelog);
    }

    private Map<ConventionalCommitType, Set<Commit>> getCommitsByCommitType() {
        Map<ConventionalCommitType, Set<Commit>> res = new HashMap<>();
        res.put(ConventionalCommitType.BREAKING_CHANGE,
            new HashSet<>(Collections.singletonList(new Commit(new DummyCommitAdapter("breaking change: breaking change test", COMMIT_HASH)))));
        res.put(ConventionalCommitType.FEAT,
            new HashSet<>(Collections.singletonList(new Commit(new DummyCommitAdapter("feat(ui): feat test", COMMIT_HASH)))));
        res.put(ConventionalCommitType.FIX,
            new HashSet<>(Arrays.asList(new Commit(new DummyCommitAdapter("fix(ui): fix test", COMMIT_HASH)),
                new Commit(new DummyCommitAdapter("fix(ui): fix test", COMMIT_HASH)),
                new Commit(new DummyCommitAdapter("fix(ui): fix test 2", COMMIT_HASH)))));
        res.put(ConventionalCommitType.CI,
            new HashSet<>(Arrays.asList(new Commit(new DummyCommitAdapter("ci: ci test", COMMIT_HASH)),
                new Commit(new DummyCommitAdapter("ci this message will not me included to changelog as there is no colon", COMMIT_HASH)))));
        res.put(ConventionalCommitType.BUILD,
            new HashSet<>(Collections.singletonList(new Commit(new DummyCommitAdapter("build: build test", COMMIT_HASH)))));
        res.put(ConventionalCommitType.DOCS,
            new HashSet<>(Collections.singletonList(new Commit(new DummyCommitAdapter("docs: docs test", COMMIT_HASH)))));
        return res;
    }
}
