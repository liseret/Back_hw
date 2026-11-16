package app.domain;
import java.util.List;

public record Collection(int playerId, String playerName, int legendaryPulls, List<CollectionEntry> cards) {

}
