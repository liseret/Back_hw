package app.domain;

import java.util.Map;

public record Player(int id, String name, int legendaryPulls, Map<Integer, Integer> collection) {
}
