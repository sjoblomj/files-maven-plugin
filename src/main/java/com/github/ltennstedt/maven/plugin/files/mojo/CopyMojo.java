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

import com.github.ltennstedt.maven.plugin.files.util.Preconditions;
import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Mojo for copying files and directories
 *
 * @author Lars Tennstedt
 * @since 1
 */
@Beta
@Mojo(name = "copy")
public final class CopyMojo extends AbstractMojo {
    /**
     * Source file or directory
     *
     * @since 1
     */
    @Parameter(required = true)
    private File file;

    /**
     * Target directory
     *
     * @since 1
     */
    @Parameter(required = true)
    private File into;

    /**
     * {@inheritDoc}
     *
     * @since 1
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Preconditions.checkFile(file, getLog());
        Preconditions.checkInto(into, getLog());
        getLog().info("Copying " + file.getAbsolutePath() + " into " + into.getAbsolutePath());
        try {
            if (file.isFile()) {
                FileUtils.copyFileToDirectory(file, into);
            } else {
                FileUtils.copyDirectory(file, into);
            }
        } catch (final IOException exception) {
            final String message = "Copying failed";
            getLog().error(message);
            throw new MojoExecutionException(message, exception);
        }
        getLog().info("Copying successful");
    }

    /**
     * {@inheritDoc}
     *
     * @since 1
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("file", file).add("into", into).toString();
    }

    @VisibleForTesting
    public File getFile() {
        return file;
    }

    @VisibleForTesting
    public void setFile(final File file) {
        assert file != null;
        this.file = file;
    }

    @VisibleForTesting
    public File getInto() {
        return into;
    }

    @VisibleForTesting
    public void setInto(final File into) {
        assert into != null;
        this.into = into;
    }
}
