package com.igrium.packmaker.common.util;

import java.net.URI;
import java.net.URISyntaxException;

public class WebUtils {

    /**
     * Create a URI without throwing a checked exception.
     * @param str String to parse.
     * @return URI
     */
    public static URI uri(String str) {
        try {
            return new URI(str);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
