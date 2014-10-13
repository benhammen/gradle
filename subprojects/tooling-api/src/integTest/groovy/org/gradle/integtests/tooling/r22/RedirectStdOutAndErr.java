/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.integtests.tooling.r22;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

// TODO(radim): reuse from org.gradle.util without class cast exceptions in integTests
/**
 * A Junit rule which replaces stdout and stderr with mocks for the duration of the test, and restores them at the end
 * of the test.
 */
public class RedirectStdOutAndErr implements MethodRule {
    private PrintStream originalStdOut;
    private PrintStream originalStdErr;
    private ByteArrayOutputStream stdoutContent = new ByteArrayOutputStream();
    private ByteArrayOutputStream stderrContent = new ByteArrayOutputStream();
    private PrintStream stdOutPrintStream = new PrintStream(stdoutContent);
    private PrintStream stdErrPrintStream = new PrintStream(stderrContent);

    public Statement apply(final Statement base, FrameworkMethod method, Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                originalStdOut = System.out;
                originalStdErr = System.err;
                try {
                    OutputStream dummy = new OutputStream() {
                        @Override
                        public void write(int b) throws IOException {
                            stdOutPrintStream.write(b);
                        }

                        @Override
                        public void write(byte[] b, int off, int len) throws IOException {
                            stdOutPrintStream.write(b, off, len);
                        }
                    };
                    System.setOut(new PrintStream(dummy));
                    System.setErr(stdErrPrintStream);
                    base.evaluate();
                } finally {
                    System.setOut(originalStdOut);
                    System.setErr(originalStdErr);
                    stdOutPrintStream = null;
                    stdErrPrintStream = null;
                    stdoutContent = null;
                    stderrContent = null;
                }
            }
        };
    }

    public PrintStream getStdOutPrintStream() {
        return stdOutPrintStream;
    }

    public PrintStream getStdErrPrintStream() {
        return stdErrPrintStream;
    }

    public String getStdErr() {
        return stderrContent.toString();
    }

    public String getStdOut() {
        return stdoutContent.toString();
    }
}
