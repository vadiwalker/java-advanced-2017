package ru.ifmo.ctddev.berinchik.walk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by vadim on 13.02.17.
 */
public class FVNHash {

    private static final int INIT_VALUE = 0x811c9dc5;
    private static final int PRIME = 0x01000193;

    public static int calc(final File file) throws IOException {
        InputStream is = new FileInputStream(file);
        byte b[] = new byte[1024];
        int n = 0;
        int x = INIT_VALUE;
        while ((n = is.read(b)) != -1) {
            for (int i = 0; i < n; i++) {
                x = (x * PRIME) ^ (0xff & b[i]);
            }
        }
        return x;
    }
}
