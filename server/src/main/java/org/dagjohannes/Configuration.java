package org.dagjohannes;

import java.util.List;
import java.util.Objects;

public final class Configuration {
    private final String compilerPath;
    private final List<String> compilerArgs;
    private final boolean purgeCache;

    public Configuration(String compilerPath, List<String> compilerArgs, boolean purgeCache) {
        this.compilerPath = compilerPath;
        this.compilerArgs = compilerArgs;
        this.purgeCache = purgeCache;
    }

    public String compilerPath() {
        return compilerPath;
    }

    public List<String> compilerArgs() {
        return compilerArgs;
    }

    public boolean purgeCache() {
        return purgeCache;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Configuration) obj;
        return Objects.equals(this.compilerPath, that.compilerPath) &&
                Objects.equals(this.compilerArgs, that.compilerArgs) &&
                this.purgeCache == that.purgeCache;
    }

    @Override
    public int hashCode() {
        return Objects.hash(compilerPath, compilerArgs, purgeCache);
    }

    @Override
    public String toString() {
        return "Configuration[" +
                "compilerPath=" + compilerPath + ", " +
                "compilerArgs=" + compilerArgs + ", " +
                "purgeCache=" + purgeCache + ']';
    }

}
