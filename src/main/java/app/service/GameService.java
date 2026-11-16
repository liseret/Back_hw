package app.service;

import app.domain.*;
import app.domain.Collection;
import app.domain.exception.ConflictException;
import app.domain.exception.NotFoundException;
import app.domain.exception.ValidationException;
import app.repository.CardRepository;
import app.repository.PlayerRepository;

import java.util.*;

public class GameService {
    public static final String COMMON = "COMMON";
    public static final String LEGENDARY = "LEGENDARY";
    private static final int MAX_CARDS = 5;
    private static final int MAX_NAME = 50;
    private static final int MAX_TYPE = 30;
    private static final int MAX_ART = 1000;

    private final CardRepository cards;
    private final PlayerRepository players;
    private final Random random = new Random();

    public GameService(CardRepository cards, PlayerRepository players) {
        this.cards = cards;
        this.players = players;
    }

    public List<PokemonCard> listCards(String rarity, String name) {
        return cards.findAll().stream()
                .filter(c -> rarity == null || c.rarity().equalsIgnoreCase(rarity))
                .filter(c -> name == null || c.name().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }
    public PokemonCard getCard(int id) {
        return findCard(id);
    }

    public PokemonCard createCard(String name, String type, String rarity, String art) {
        name=name.trim();
        type=type.trim();
        rarity=rarity.trim();
        validateCard(name, type, rarity, art);
        String r = rarity.toUpperCase();
        if (r.equals(LEGENDARY) && cards.legendaryExists())
            throw new ConflictException("LEGENDARY_LIMIT", "В каталоге уже есть LEGENDARY-карточка, вторую создать нельзя");
        if (cards.existsByName(name))
            throw new ConflictException("DUPLICATE_CARD", "Карточка с таким названием уже существует");
        if (cards.count() >= MAX_CARDS)
            throw new ConflictException("CARD_LIMIT", "В каталоге может быть максимум " + MAX_CARDS + " карточек");
        PokemonCard card = new PokemonCard(cards.nextId(), name, type, r, art);
        cards.save(card);
        return card;
    }

    public PokemonCard updateCard(int id, String name, String type, String rarity, String art) {
        PokemonCard old = findCard(id);
        name=name.trim();
        type=type.trim();
        rarity=rarity.trim();
        validateCard(name, type, rarity, art);
        String r = rarity.toUpperCase();
        if (old.rarity().equals(LEGENDARY) && !r.equals(LEGENDARY))
            throw new ConflictException("LEGENDARY_PROTECTED", "Редкость LEGENDARY-карточки изменить нельзя");
        if (!old.rarity().equals(LEGENDARY) && r.equals(LEGENDARY) && cards.legendaryExists())
            throw new ConflictException("LEGENDARY_LIMIT", "В каталоге уже есть LEGENDARY-карточка, вторую создать нельзя");
        if (cards.existsByNameExcept(name, id))
            throw new ConflictException("DUPLICATE_CARD", "Карточка с таким названием уже существует");
        PokemonCard update = new PokemonCard(id, name, type, r, art);
        cards.save(update);
        return update;
    }

    public void deleteCard(int id) {
        PokemonCard c = findCard(id);
        if (c.rarity().equals(LEGENDARY))
            throw new ConflictException("LEGENDARY_PROTECTED", "LEGENDARY-карточку нельзя удалить из каталога");
        if (players.findAll().stream().anyMatch(p -> p.getCollection().getOrDefault(id, 0) > 0))
            throw new ConflictException("CARD_IN_COLLECTION", "Нельзя удалить карточку, которая есть у игроков");
        cards.delete(id);
    }

    public List<Player> listPlayers(String name) {
        return players.findAll().stream()
                .filter(p -> name == null || p.getName().toLowerCase().contains(name.toLowerCase()))
                .map(PlayerState::snapshot).toList();
    }
    public Player getPlayer(int id) {
        return findPlayer(id).snapshot();
    }

    public Player createPlayer(String name) {
        name=name.trim();
        validatePlayerName(name);
        if (players.existsByName(name))
            throw new ConflictException("DUPLICATE_PLAYER", "Игрок с таким именем уже существует");
        PlayerState playeri = new PlayerState(players.nextId(), name);
        players.save(playeri);
        return playeri.snapshot();
    }

    public Player updatePlayer(int id, String name) {
        PlayerState playeri = findPlayer(id);
        validatePlayerName(name);
        name=name.trim();
        if (players.existsByNameExcept(name, id))
            throw new ConflictException("DUPLICATE_PLAYER", "Игрок с таким именем уже существует");
        playeri.setName(name);
        return playeri.snapshot();
    }

    public void deletePlayer(int id) { findPlayer(id); players.delete(id); }

    public Collection collection(int playerId) {
        PlayerState p = findPlayer(playerId);
        List<CollectionEntry> rows = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : p.getCollection().entrySet())
            cards.findById(e.getKey()).ifPresent(c -> rows.add(new CollectionEntry(c, e.getValue())));
        return new Collection(p.getId(), p.getName(), p.getLegendaryPulls(), rows);
    }

    public DrawResult draw(int playerId) {
        PlayerState p = findPlayer(playerId);
        List<PokemonCard> all = cards.findAll();
        if (all.isEmpty()) throw new ConflictException("EMPTY_CATALOG", "В каталоге нет карточек");
        List<PokemonCard> common = all.stream().filter(c -> c.rarity().equals(COMMON)).toList();
        List<PokemonCard> legendary = all.stream().filter(c -> c.rarity().equals(LEGENDARY)).toList();
        PokemonCard picked = (!common.isEmpty() && !legendary.isEmpty())
                ? (random.nextInt(100) < 80
                ? common.get(random.nextInt(common.size()))
                : legendary.get(random.nextInt(legendary.size())))
                : all.get(random.nextInt(all.size()));
        int owned = p.addCard(picked.id());
        if (picked.rarity().equals(LEGENDARY)) p.incrementLegendaryPulls();
        return new DrawResult("Карточка выбита!", picked, owned, p.getLegendaryPulls());
    }

    private void validateCard(String name, String type, String rarity, String art) {
        if (blank(name) || blank(type) || blank(rarity) || blank(art))
            throw new ValidationException("INVALID_CARD", "Поля name, type, rarity и art обязательны");
        if (!rarity.equalsIgnoreCase(COMMON) && !rarity.equalsIgnoreCase(LEGENDARY))
            throw new ValidationException("INVALID_RARITY", "rarity должна быть COMMON или LEGENDARY");
        if (name.length() > MAX_NAME)
            throw new ValidationException("NAME_TOO_LONG", "name не длиннее " + MAX_NAME + " символов");
        if (type.length() > MAX_TYPE)
            throw new ValidationException("TYPE_TOO_LONG", "type не длиннее " + MAX_TYPE + " символов");
        if (art.length() > MAX_ART)
            throw new ValidationException("ART_TOO_LONG", "ASCII-рисунок слишком длинный");
    }
    private void validatePlayerName(String name) {
        if (blank(name))
            throw new ValidationException("INVALID_PLAYER", "Поле name обязательно");
        if (name.length() > MAX_NAME)
            throw new ValidationException("NAME_TOO_LONG", "name не длиннее " + MAX_NAME + " символов");
    }
    private PokemonCard findCard(int id) {
        return cards.findById(id).orElseThrow(() -> new NotFoundException("CARD_NOT_FOUND", "Карточка не найдена"));
    }
    private PlayerState findPlayer(int id) {
        return players.findById(id).orElseThrow(() -> new NotFoundException("PLAYER_NOT_FOUND", "Игрок не найден"));
    }
    private boolean blank(String v) {
        return v == null || v.trim().isEmpty(); }
}