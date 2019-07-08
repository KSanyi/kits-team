package hu.kits.team.domain;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

public class Members {

    private final List<Member> entries;

    public Members(List<Member> entries) {
        this.entries = entries;
    }

    public Member forId(String id) {
        return findById(id).get();
    }
    
    public Optional<Member> findById(String id) {
        return entries.stream().filter(e -> e.id.equals(id)).findAny();
    }
    
    public List<Member> entries() {
        return entries.stream().sorted(comparing(m -> m.id)).collect(toList());
    }
    
}
