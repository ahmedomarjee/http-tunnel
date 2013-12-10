package es.malvarez.http_tunnel.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Input output utilities.
 */
public class IOUtils {

    public static final int DEFAULT_BUFFER_SIZE = 4 * 1024;

    /**
     * Safely close the stream.
     *
     * @param closeable clsoeab
     */
    public static void safelyClose(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            // eat it
        }
    }

    /**
     * Copies using a buffer of {@link IOUtils#DEFAULT_BUFFER_SIZE} bytes
     */
    public static int copy(InputStream input, OutputStream output) throws IOException {
        return copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copy the input stream on to the output stream.
     */
    public static int copy(InputStream input, OutputStream output, int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        int totalRead = 0, bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            totalRead += bytesRead;
            output.write(buffer, 0, bytesRead);
        }
        return totalRead;
    }
}
