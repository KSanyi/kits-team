package hu.kits.team.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CollectionsUtil {

    public static <T> Set<T> union(Set<T> setA, Set<T> setB) {
        Set<T> result = new HashSet<>(setA);
        result.addAll(setB);
        return Collections.unmodifiableSet(result);
    }
    
}
