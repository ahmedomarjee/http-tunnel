package es.malvarez.http_tunnel.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * http_tunnel
 *
 * @author malvarez
 */

public class IOUtils {

    public static final int DEFAULT_BUFFER_SIZE = 4 * 1024;

    public static void safelyClose(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            // eat it
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        return copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    public static int copy(InputStream input, OutputStream output, int bufferSize) throws IOException {
        return copy(input, output, new CopyAdapter(), bufferSize);
    }

    public static int copy(InputStream input, OutputStream output, CopyAdapter adapter) throws IOException {
        return copy(input, output, adapter, DEFAULT_BUFFER_SIZE);
    }

    public static int copy(InputStream input, OutputStream output, CopyAdapter adapter, int bufferSize) throws IOException {
        adapter.before(input, output);
        int totalRead = 0, bytesRead;
        try {
            byte[] buffer = new byte[bufferSize];
            while ((bytesRead = input.read(buffer)) != -1) {
                totalRead += bytesRead;
                output.write(buffer, 0, bytesRead);
                adapter.onChunk(input, output, bytesRead);
            }
            output.flush();
            return totalRead;
        } finally {
            adapter.after(input, output, totalRead);

        }
    }

    public static class CopyAdapter {

        public void before(InputStream input, OutputStream output) throws IOException {
        }

        public void onChunk(InputStream input, OutputStream output, int bytesReaded) throws IOException {
        }

        public void after(InputStream input, OutputStream output, int total) throws IOException {
            safelyClose(input);
            safelyClose(output);
        }
    }
}
