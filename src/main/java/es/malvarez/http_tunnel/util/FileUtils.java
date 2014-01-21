package es.malvarez.http_tunnel.util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * http_tunnel
 *
 * @author malvarez
 */
public abstract class FileUtils {

    private static final ThreadLocal<List<File>> FILES = new ThreadLocal<List<File>>();

    public static void init() {
        FILES.set(new LinkedList<File>());
    }

    public static File createTempFile(String prefix) throws IOException {
        File file = File.createTempFile(prefix, "tmp");
        file.deleteOnExit();
        FILES.get().add(file);
        return file;
    }

    public static void destroy() {
        for (File file : FILES.get()) {
            if (!file.delete()) {
                file.deleteOnExit();
            }
        }
        FILES.set(Collections.<File>emptyList());
    }
}
