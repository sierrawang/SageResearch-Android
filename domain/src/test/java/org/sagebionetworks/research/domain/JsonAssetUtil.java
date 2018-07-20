package org.sagebionetworks.research.domain;

import static com.google.common.base.Preconditions.checkState;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.refEq;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;

public class JsonAssetUtil {
    public static <T> void assertJsonFileEqualRef(T expected, Gson gson, String filename, Class<T> klass) {
        T result = readJsonFile(gson, filename, klass);
        assertEquals((expected), (result));
    }

    @NonNull
    public static <T> T readJsonFile(Gson gson, String filename, Class<T> klass) {
        ClassLoader loader = klass.getClassLoader();
        URL url = loader.getResource(filename);
        checkState(url != null, "invalid URL for filename: %s", filename);
        T action = readJsonFileHelper(gson, url, klass);
        checkState(action != null, "Failed to read file: %s", filename);
        return action;
    }

    @Nullable
    private static <T> T readJsonFileHelper(Gson gson, URL url, Class<T> klass) {
        try {
            Reader reader = Files.newBufferedReader(new File(url.getFile()).toPath(), UTF_8);
            return gson.fromJson(reader, klass);
        } catch (IOException e) {
            return null;
        }
    }
}
