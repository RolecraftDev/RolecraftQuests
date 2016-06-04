package com.volumetricpixels.questy.util;

/**
 * General utility methods which are used throughout the Questy framework. More specific utilities can be found in other classes in the util package.
 */
public final class General {
    /**
     * Appends the given {@link String}, {@code append}, to the given {@link StringBuilder}, {@code builder}, only if {@code check == true}.
     *
     * @param check the check to perform before appending
     * @param builder the {@link StringBuilder} to append the string to
     * @param append the {@link String} to append to the builder if the check is passes
     * @return the given {@link StringBuilder} if {@code check == false}, else the given {@link StringBuilder} with the given {@link String} appended
     */
    public static StringBuilder appendIf(boolean check, StringBuilder builder, String append) {
        if (check) {
            return builder.append(append);
        }
        return builder;
    }

    /**
     * This class never needs to be instantiated.
     */
    private General() {
        throw new UnsupportedOperationException();
    }
}
