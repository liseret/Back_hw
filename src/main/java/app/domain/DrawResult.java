package app.domain;

public record DrawResult(String message, PokemonCard card, int quantityOwned, int legendaryPulls) {
}