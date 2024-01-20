package com.igrium.packmaker.installer.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class ImageEncoder {

    /**
     * Encode an image into Base64.
     * @param stream Image PNG data.
     * @return Base64 string.
     * @throws IOException If an IO exception occurs reading the stream.
     */
    public static String encodeImage(InputStream stream) throws IOException {
        byte[] buffer = new byte[2048];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int readLength;
        while ((readLength = stream.read(buffer, 0, 2048)) != -1) {
            out.write(buffer, 0, readLength);
        }

        byte[] data = out.toByteArray();
        out.close();
        stream.close();
        return "data:image/png;base64," + Base64.getEncoder().withoutPadding().encodeToString(data);
    }
}
