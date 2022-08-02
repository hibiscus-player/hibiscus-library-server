package me.mrgazdag.hibiscus.library;

public record APIVersion(int major, int minor, int patch) {
    private static final APIVersion DEFAULT = new APIVersion(0,0,0);

    public boolean isCompatible(APIVersion other) {
        return major == other.major;
    }

    @Override
    public String toString() {
        return "APIVersion[" + major + "." + minor + "." + patch + ']';
    }

    public static APIVersion fromString(String source) {
        if (source == null) {
            return DEFAULT;
        }
        int major;
        int minor;
        int patch;

        int majorIndex = source.indexOf('.');
        if (majorIndex == 0 || majorIndex == source.length() - 1)
            throw new IllegalArgumentException("Versions cannot start or end with '.': " + source);
        else if (majorIndex == -1) {
            try {
                major = Integer.parseInt(source);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Bad version: " + source, e);
            }
            minor = 0;
            patch = 0;
        } else {
            try {
                major = Integer.parseInt(source.substring(0, majorIndex));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Bad major version: " + source, e);
            }
            int minorIndex = source.indexOf('.', majorIndex + 1);
            if (minorIndex == source.length() - 1)
                throw new IllegalArgumentException("Versions cannot start or end with '.': " + source);
            else if (minorIndex == -1) {
                try {
                    minor = Integer.parseInt(source.substring(majorIndex + 1));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Bad minor version: " + source, e);
                }
                patch = 0;
            } else {
                try {
                    minor = Integer.parseInt(source.substring(majorIndex + 1, minorIndex));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Bad minor version: " + source, e);
                }
                try {
                    patch = Integer.parseInt(source.substring(minorIndex + 1));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Bad minor version: " + source, e);
                }
            }
        }
        return new APIVersion(major, minor, patch);
    }
}
