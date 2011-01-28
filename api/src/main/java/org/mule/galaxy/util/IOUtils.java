/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.mule.galaxy.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public final class IOUtils {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    
    private IOUtils() {
        
    }
    
    public static int copy(final InputStream input, final OutputStream output)
        throws IOException {
        return copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    public static int copyAndCloseInput(final InputStream input, final OutputStream output)
        throws IOException {
        try {
            return copy(input, output, DEFAULT_BUFFER_SIZE);
        } finally {
            input.close();
        }
    }
    public static int copyAndCloseInput(final InputStream input,
                                        final OutputStream output,
                                        int bufferSize)
        throws IOException {
        try {
            return copy(input, output, bufferSize);
        } finally {
            input.close();
        }
    }
    public static int copy(final InputStream input,
                            final OutputStream output,
                            int bufferSize)
        throws IOException {
        int avail = input.available();
        if (avail > 262144) {
            avail = 262144;
        }
        if (avail > bufferSize) {
            bufferSize = avail;
        }
        final byte[] buffer = new byte[bufferSize];
        int n = 0;
        n = input.read(buffer);
        int total = 0;
        while (-1 != n) {
            output.write(buffer, 0, n);
            total += n;
            n = input.read(buffer);
        }
        return total;
    }
    public static void copy(final Reader input,
                            final Writer output,
                            final int bufferSize)
        throws IOException {
        final char[] buffer = new char[bufferSize];
        int n = 0;
        n = input.read(buffer);
        while (-1 != n) {
            output.write(buffer, 0, n);
            n = input.read(buffer);
        }
    }

    
    public static String toString(final InputStream input) 
        throws IOException {
        
        StringBuilder buf = new StringBuilder();
        final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int n = 0;
        n = input.read(buffer);
        while (-1 != n) {
            buf.append(new String(buffer, 0, n));
            n = input.read(buffer);
        }
        input.close();
        return buf.toString();
    }
    public static String toString(final Reader input) 
        throws IOException {
        
        StringBuilder buf = new StringBuilder();
        final char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        int n = 0;
        n = input.read(buffer);
        while (-1 != n) {
            buf.append(new String(buffer, 0, n));
            n = input.read(buffer);
        }
        input.close();
        return buf.toString();
    }
    
    public static String readStringFromStream(InputStream in) throws IOException {

        StringBuilder sb = new StringBuilder(1024);

        for (int i = in.read(); i != -1; i = in.read()) {
            sb.append((char)i);
        }

        in.close();

        return sb.toString();
    }

    public static byte[] readBytesFromStream(InputStream in) throws IOException {
        int i = in.available();
        if (i < DEFAULT_BUFFER_SIZE) {
            i = DEFAULT_BUFFER_SIZE;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(i);
        copy(in, bos);
        in.close();
        return bos.toByteArray();
    }
}
