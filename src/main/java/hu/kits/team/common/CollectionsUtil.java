package hu.kits.team.common;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class CollectionsUtil {

    public static <T> Set<T> union(Set<T> setA, Set<T> setB) {
        Set<T> result = new HashSet<>(setA);
        result.addAll(setB);
        return Collections.unmodifiableSet(result);
    }
    
    public static <T> List<T> concat(List<? extends T> listA, List<? extends T> listB) {
        return Stream.concat(listA.stream(), listB.stream()).collect(toList());
    }
    
}
