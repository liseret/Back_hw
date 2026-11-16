package app.domain;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerState {
    private final int id;
    private String name;
    private int legendaryPulls;
    private final Map<Integer, Integer> collection = new LinkedHashMap<>();

    public PlayerState(int id, String name) {
        this.id = id; this.name = name;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String n) {
        this.name = n;
    }
    public int getLegendaryPulls() {
        return legendaryPulls;
    }
    public void incrementLegendaryPulls() {
        legendaryPulls++;
    }
    public Map<Integer, Integer> getCollection() {
        return collection;
    }
    public int addCard(int cardId) {
        return collection.merge(cardId, 1, Integer::sum);
    }
    public Player snapshot() {
        return new Player(id, name, legendaryPulls, new LinkedHashMap<>(collection));
    }
}