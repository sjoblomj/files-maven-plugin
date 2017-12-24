/*
 * Copyright 2017 Lars Tennstedt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ltennstedt.maven.plugin.files.mojo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public final class MoveMojoTest {
    private static final File testarea = new File("testarea");
    private MoveMojo mojo;

    @BeforeClass
    public static void setUpClass() throws IOException {
        testarea.mkdir();
        FileUtils.copyDirectoryToDirectory(new File("src/test/resources/move"), testarea);
        FileUtils.copyDirectoryToDirectory(new File("src/test/resources/check"), testarea);
    }

    @Before
    public void setUp() {
        mojo = new MoveMojo();
    }

    @Test
    public void executeFileShouldSucceed() throws MojoFailureException, MojoExecutionException {
        final File file = new File("testarea/move/fileToMove.txt");
        mojo.file = file;
        mojo.into = new File("testarea/into");
        mojo.execute();
        assertThat(new File("testarea/into/fileToMove.txt")).isFile()
                .hasSameContentAs(new File("src/test/resources/move/fileToMove.txt"));
        assertThat(file).doesNotExist();
    }

    @Test
    public void executeDirShouldSucceed() throws MojoFailureException, MojoExecutionException {
        final File file = new File("testarea/move/dirToMove");
        mojo.file = file;
        mojo.into = new File("testarea/into");
        mojo.execute();
        assertThat(new File("testarea/into")).isDirectory();
        assertThat(new File("testarea/into/subdir/file.txt"))
                .hasSameContentAs(new File("src/test/resources/copy/dirToCopy/subdir/file.txt"));
        assertThat(new File("testarea/into/file.txt"))
                .hasSameContentAs(new File("src/test/resources/copy/dirToCopy/file.txt"));
        assertThat(file).doesNotExist();
    }

    @Test
    public void checkFileDoesNotExistShouldThrowException() {
        mojo.file = (new File("nonExistingFile"));
        assertThatThrownBy(() -> mojo.checkFile()).isExactlyInstanceOf(MojoExecutionException.class)
                .hasMessage("file does not exist");
    }

    @Test
    public void checkFileNotWritableShouldThrowException() {
        final File file = new File("testarea/check/notWritableDir");
        file.setWritable(false);
        mojo.file = file;
        assertThatThrownBy(() -> mojo.checkFile()).isExactlyInstanceOf(MojoExecutionException.class)
                .hasMessage("file not writable");
    }

    @Test
    public void checkIntoIsDirShouldThrowException() {
        mojo.into = new File("testarea/check/intoFile.txt");
        assertThatThrownBy(() -> mojo.checkInto()).isExactlyInstanceOf(MojoExecutionException.class)
                .hasMessage("into is a file");
    }

    @Test
    public void checkIntoNotWrtitableShouldThrowException() {
        final File into = new File("testarea/check/notWritableDir");
        into.setWritable(false);
        mojo.into = into;
        assertThatThrownBy(() -> mojo.checkInto()).isExactlyInstanceOf(MojoExecutionException.class)
                .hasMessage("into not writable");
    }

    @Test
    public void toStringShouldSucceed() {
        final File file = new File("testarea/move/fileToMove.txt");
        mojo.file = file;
        mojo.into = new File("testarea/into");
        assertThat(mojo.toString())
                .isEqualTo(new ToStringBuilder(mojo).append("file", mojo.file).append("into", mojo.into).toString());
    }

    @After
    public void cleanUp() throws IOException {
        FileUtils.deleteDirectory(new File("testarea/into"));
    }

    @AfterClass
    public static void cleanUpClass() throws IOException {
        new File("testarea/check/notWritableDir").setWritable(true);
        FileUtils.deleteDirectory(testarea);
    }

}