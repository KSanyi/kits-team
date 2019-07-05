package hu.kits.team.domain;

import java.util.List;

public interface ChampionshipRepository {

    List<Championship> loadAll();
    
    void delete(long id);
    
    Championship save(String name);
    
    void update(Championship championship);
    
}
