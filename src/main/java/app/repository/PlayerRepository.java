package app.repository;

import app.domain.PlayerState;
import java.util.*;

public class PlayerRepository {
    private final Map<Integer, PlayerState> players = new LinkedHashMap<>();
    private int nextId = 1;

    public int nextId() {
        return nextId++;
    }
    public void save(PlayerState p) {
        players.put(p.getId(), p);
    }
    public Optional<PlayerState> findById(int id) {
        return Optional.ofNullable(players.get(id));
    }
    public List<PlayerState> findAll() {
        return new ArrayList<>(players.values());
    }
    public void delete(int id) {
        players.remove(id);
    }

    public boolean existsByName(String n) {
        return players.values().stream().anyMatch(p -> p.getName().equalsIgnoreCase(n));
    }
    public boolean existsByNameExcept(String n, int id) {
        return players.values().stream().anyMatch(p -> p.getId() != id && p.getName().equalsIgnoreCase(n));
    }
}