package scalable.com.shared.classes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

class ByteClassLoader extends ClassLoader {
    private static final String CLASS_EXT = ".class";
    private static final char PACKAGE_SEPARATOR = '.';

    private ByteClassLoader() {
    }


    public static Class<?> loadClassFromBytes(String name, byte[] b) {
        // Convert the entire array of bytes (0 offset) into a Class
        return new ByteClassLoader().defineClass(name, b, 0, b.length);
    }

    public static Class<?> loadClassByName(String name) throws ClassNotFoundException {
        try {
            final byte[] b = readClassFileAsBytes(name);
            return loadClassFromBytes(name, b);
        } catch (IOException e) {
            throw new ClassNotFoundException("Class not found.", e);
        }
    }

    private static byte[] readClassFileAsBytes(String className) throws IOException {
        byte[] buffer;

        try (final InputStream inputStream = getClassInputStream(className)) {
            if (inputStream == null) {
                throw new IOException("Class file not found.");
            }
            buffer = inputStream.readAllBytes();
        }

        return buffer;
    }

    private static InputStream getClassInputStream(String className) {
        final String path = className.replace(PACKAGE_SEPARATOR, File.separatorChar) + CLASS_EXT;
        return ByteClassLoader.class.getClassLoader().getResourceAsStream(path);
    }

}