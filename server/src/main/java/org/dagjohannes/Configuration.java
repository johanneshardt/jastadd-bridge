package org.dagjohannes;

import java.util.List;

public record Configuration(String compilerPath, List<String> compilerArgs, boolean purgeCache) {
}
