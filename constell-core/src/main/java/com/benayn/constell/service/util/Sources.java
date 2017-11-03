package com.benayn.constell.service.util;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;
import com.google.common.io.Closer;
import com.google.common.io.Resources;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Sources {

    public static Properties asProperties(Class<?> contextClass, String resourceName) throws IOException {
        Closer closer = Closer.create();
        Properties properties;

        try {
            Properties p = new Properties();
            p.load(closer.register(asCharSource(contextClass, resourceName).openStream()));
            properties = p;
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return properties;
    }

    public static String asString(String resourceName) throws IOException {
        URL url = Resources.getResource(resourceName);
        return Resources.toString(url, Charsets.UTF_8);
    }

    public static String asString(Class<?> contextClass, String resourceName) throws IOException {
        /*
            StringBuilder builder = new StringBuilder();
            Path path = Paths.get(contextClass.getResource(resourceName).toURI());
            Files.lines(path, StandardCharsets.UTF_8).forEach(builder::append);

            return builder.toString();
         */

        Closer closer = Closer.create();
        String content;

        try {
            content = CharStreams.toString(
                closer.register(asCharSource(contextClass, resourceName).openStream()));
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return content;
    }

    public static URL asURL(Class<?> contextClass, String resourceName) {
        return Resources.getResource(contextClass, resourceName);
    }

    public static File asFile(Class<?> contextClass, String resourceName) {
        return new File(asURL(contextClass, resourceName).getFile());
    }

    public static CharSource asCharSource(Class<?> contextClass, String resourceName) {
        return asCharSource(contextClass, resourceName, Charsets.UTF_8);
    }

    public static CharSource asCharSource(final Class<?> contextClass, final String resourceName, final Charset charset) {
        return new CharSource() {
            public Reader openStream() throws IOException {
                return Sources.asReader(contextClass.getResourceAsStream(resourceName), charset);
            }
        };
    }

    public static BufferedReader asReader(InputStream is, Charset charset) throws UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader(is, charset.toString()));
    }

}
