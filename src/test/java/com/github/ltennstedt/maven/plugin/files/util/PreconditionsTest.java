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

package com.github.ltennstedt.maven.plugin.files.util;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ltennstedt.maven.plugin.files.mojo.CopyMojo;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class PreconditionsTest {
    private static final File testarea = new File("testarea");
    private final CopyMojo mojo = new CopyMojo();

    @BeforeClass
    public static void setUpClass() throws IOException {
        testarea.mkdir();
        FileUtils.copyDirectoryToDirectory(new File("src/test/resources/check"), testarea);
    }

    @Test
    public void checkFileDoesNotExistShouldThrowException() {
        mojo.setFile(new File("nonExistingFile"));
        assertThatThrownBy(() -> Preconditions.checkFile(mojo.getFile(), mojo.getLog()))
                .isExactlyInstanceOf(MojoExecutionException.class).hasMessage("file does not exist");
    }

    @Test
    public void checkFileNotReadableShouldThrowException() {
        final File file = new File("testarea/check/notReadableFile.txt");
        file.setReadable(false);
        mojo.setFile(file);
        assertThatThrownBy(() -> Preconditions.checkFile(mojo.getFile(), mojo.getLog()))
                .isExactlyInstanceOf(MojoExecutionException.class).hasMessage("file not readable");
    }

    @Test
    public void checkIntoIsFileShouldThrowException() {
        mojo.setInto(new File("testarea/check/intoFile.txt"));
        assertThatThrownBy(() -> Preconditions.checkInto(mojo.getInto(), mojo.getLog()))
                .isExactlyInstanceOf(MojoExecutionException.class).hasMessage("into is a file");
    }

    @Test
    public void checkIntoNotWrtitableShouldThrowException() {
        final File into = new File("testarea/check/notWritableDir");
        into.setWritable(false);
        mojo.setInto(into);
        assertThatThrownBy(() -> Preconditions.checkInto(mojo.getInto(), mojo.getLog()))
                .isExactlyInstanceOf(MojoExecutionException.class).hasMessage("into not writable");
    }

    @AfterClass
    public static void cleanUpClass() throws IOException {
        new File("testarea/check/notWritableDir").setWritable(true);
        FileUtils.deleteDirectory(testarea);
    }
}
