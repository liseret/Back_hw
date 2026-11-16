package app.repository;

import app.domain.PokemonCard;
import java.util.*;

public class CardRepository {
    private final Map<Integer, PokemonCard> cards = new LinkedHashMap<>();
    private int nextId = 1;

    public int nextId() {
        return nextId++;
    }
    public void save(PokemonCard c) {
        cards.put(c.id(), c);
    }
    public Optional<PokemonCard> findById(int id) {
        return Optional.ofNullable(cards.get(id));
    }
    public List<PokemonCard> findAll() {
        return new ArrayList<>(cards.values());
    }
    public void delete(int id) {
        cards.remove(id);
    }
    public int count() {
        return cards.size();
    }

    public boolean existsByName(String n) {
        return cards.values().stream().anyMatch(c -> c.name().equalsIgnoreCase(n));
    }
    public boolean existsByNameExcept(String n, int id) {
        return cards.values().stream().anyMatch(c -> c.id() != id && c.name().equalsIgnoreCase(n));
    }
    public boolean legendaryExists() {
        return cards.values().stream().anyMatch(c -> "LEGENDARY".equalsIgnoreCase(c.rarity()));
    }
}