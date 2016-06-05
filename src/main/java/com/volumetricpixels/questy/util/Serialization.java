/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Various serialization and deserialization utilities used by Questy. Mostly for testing purposes.
 */
public final class Serialization {
    /**
     * Commonly used conversions from {@link String}s to other {@link Object}s such as integers or characters.
     */
    private static final List<Function<String, Object>> common = new ArrayList<>();

    static {
        addCommonTypeHandler((str) -> {
            try {
                return Integer.parseInt(str);
            } catch (Exception e) {
                return null;
            }
        });

        addCommonTypeHandler((str) -> {
            try {
                return Double.parseDouble(str);
            } catch (Exception e) {
                return null;
            }
        });

        addCommonTypeHandler((str) -> {
            if (str.length() == 1) {
                return str.charAt(0);
            }
            return null;
        });
    }

    /**
     * Adds the given {@link Function} which takes a {@link String} and produces
     * any {@link Object} to the {@link List} of common types. The given {@link
     * Function} will be assigned highest priority - i.e it is inserted at the
     * top of the {@link List}.
     *
     * @param func the {@link Function} to add
     */
    public static void addCommonTypeHandler(Function<String, Object> func) {
        common.add(0, func);
    }

    public static Object handleCommonTypes(String serialized) {
        for (Function<String, Object> func : common) {
            Object result = func.apply(serialized);
            if (result != null) {
                return result;
            }
        }
        return serialized;
    }

    /**
     * Uses Java's serialization API to write the {@code object} to the given {@code file}.
     *
     * @param file the {@link File} to write the data to
     * @param object the data to write to the file
     */
    public static void writeObject(File file, Object object) {
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace(); // proper exception handling unnecessary as this will most likely never be used in production
        }
    }

    /**
     * Reads the data in the given {@code file} and returns it as a Java {@link Object} if it can. Human modifications to the file will cause problems and are the most likely cause
     * of {@code null} being returned.
     *
     * @param file the {@link File} to read the data from
     * @return a {@link Object} written in the file
     */
    public static Object readObject(File file) {
        try {
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Object result = in.readObject();
            in.close();
            fileIn.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace(); // proper exception handling unnecessary as this will most likely never be used in production
            return null;
        }
    }

    /**
     * Utility method to check if the name of the given {@link File} ends with
     * the given {@link String}, ignoring case.
     *
     * @param file the {@link File} to check the extension of
     * @param extension the {@link String} to check the extension for
     * @return {@code true} if the given file's name ends with the given string,
     *         ignoring case, otherwise {@code false}
     */
    public static boolean checkExtension(File file, String extension) {
        if (!extension.startsWith(".")) {
            extension = ".".concat(extension);
        }
        return file.getName().toLowerCase().endsWith(extension.toLowerCase());
    }

    /**
     * This class never needs to be instantiated.
     */
    private Serialization() {
        throw new UnsupportedOperationException();
    }
}
