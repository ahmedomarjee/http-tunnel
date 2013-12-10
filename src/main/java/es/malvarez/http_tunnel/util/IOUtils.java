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
        try {
            byte[] buffer = new byte[bufferSize];
            int totalRead = 0, bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                totalRead += bytesRead;
                output.write(buffer, 0, bytesRead);
            }
            output.flush();
            return totalRead;
        } finally {
            safelyClose(input);
            safelyClose(output);
        }
    }
}
