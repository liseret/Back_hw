package app;
import app.domain.exception.ConflictException;
import app.domain.exception.NotFoundException;
import app.domain.exception.ValidationException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
public class GameService {
    public static final String COMMON = "COMMON";
    public static final String LEGENDARY = "LEGENDARY";

    private static final int MAX_CARDS = 5;
    private static final int MAX_NAME = 50;
    private static final int MAX_TYPE = 30;
    private static final int MAX_ART = 1000;

    private final Map<Integer, PokemonCard> cards = new LinkedHashMap<>();
    private final Map<Integer, PlayerState> players = new LinkedHashMap<>();
    private int nextCardId = 1;
    private int nextPlayerId = 1;
    private final Random random = new Random();

    public GameService() {
        addSeedCard("Pikachu", "ELECTRIC", COMMON,
                "`;-.          ___,\n" +
                        "  `.`\\_...._/`.-`\"\n" +
                        "    \\        /      ,\n" +
                        "    /()   () \\    .' `-._\n" +
                        "   |)  .    () \\  /   _.'\n" +
                        "   \\  -'-     ,; '. <\n" +
                        "    ;.__     ,;|   > \\\n" +
                        "   / ,    / ,  |.-'.-'\n" +
                        "  (_/    (_/ ,;|.<`\n" +
                        "    \\    ,     ;-`\n" +
                        "     >   \\    /\n" +
                        "    (_,-'`> .'\n" +
                        "jgs       (_,'");
        addSeedCard("Mew", "PSYCHIC", LEGENDARY,
                    "         .-. \\_/ .-.\n" +
                        "         \\.-\\/=\\/.-/\n" +
                        "      '-./___|=|___\\.-'\n" +
                        "     .--| \\|/`\"`\\|/ |--.\n" +
                        "    (((_)\\  .---.  /(_)))\n" +
                        "     `\\ \\_`-.   .-'_/ /`_\n" +
                        "       '.__       __.'(_))\n" +
                        "           /     \\     //\n" +
                        "          |       |__.'/\n" +
                        "          \\       /--'`\n" +
                        "jgs   .--,-' .--. '----.\n" +
                        "     '----`--'  '--`----'");
        addSeedCard("Bulbasaur", "GRASS", COMMON,
                "             `;,;.;,;.;.'\n" +
                        "              ..:;:;::;: \n" +
                        "        ..--''' '' ' ' '''--.  \n" +
                        "      /' .   .'        '.   .`\\\n" +
                        "     | /    /            \\   '.|\n" +
                        "     | |   :             :    :|\n" +
                        "   .'| |   :             :    :|\n" +
                        " ,: /\\ \\.._\\ __..===..__/_../ /`.\n" +
                        "|'' |  :.|  `'          `'  |.'  ::.\n" +
                        "|   |  ''|    :'';          | ,  `''\\\n" +
                        "|.:  \\/  | /'-.`'   ':'.-'\\ |  \\,   |\n" +
                        "| '  /  /  | / |...   | \\ |  |  |';'|\n" +
                        " \\ _ |:.|  |_\\_|`.'   |_/_|  |.:| _ |\n" +
                        "/,.,.|' \\__       . .      __/ '|.,.,\\\n" +
                        "     | ':`.`----._____.---'.'   |\n" +
                        "l42   \\   `:\"\"\"-------'\"\"' |   |\n" +
                        "       ',-,-',             .'-=,=,");
        addSeedCard("Wigglytuff", "FAIRY", COMMON,
                "                     .~~-.      _.    \n" +
                        "                   .''..    (_~)  ) _.-'. ;   \n" +
                        "                   '.'..'..-(_~ _-'*. .'.'    \n" +
                        "                     ''.'.. _ ~~  _  ';'      \n" +
                        "                      .''. (_)   (_)  '.      \n" +
                        "                      ;      \"...\"     '.     \n" +
                        "                  .''.'.   .''`-'''.    '.''. \n" +
                        "                  '.  '   ;         ;    ;  ; \n" +
                        "                    '.   ;           ;   ' ;  \n" +
                        "                     '.  ;           ;    ;   \n" +
                        "                      '.  ;         ;   .'    \n" +
                        "                      .'...:..___..:..':.     \n" +
                        "                   .''     ..'    '...   ~)   \n" +
                        "                  (.....'''           ''''");
        addSeedCard("Psyduck", "WATER", COMMON,
                "⠀⠀⠀⠀⠀⠀⠀⠀⣤⡀⠀⣶⡄\n" +
                        "⠀⠀⠀⠀⠀⠀⠀⠀⠙⣿⣆⣿⡇\n" +
                        "⠀⠀⠀⠀⠀⠀⠀⠸⣷⣮⣿⣿⣄⣀⣀\n" +
                        "⠀⠀⠀⠀⠀⢀⡠⠒⠉⠀⠀⠀⠀⠀⠀⠈⠁⠲⢖⠒⡀\n" +
                        "⠀⠀⠀⡠⠴⣏⠀⢀⡀⠀⢀⡀⠀⠀⠀⡀⠀⠀⡀⠱⡈⢄\n" +
                        "⠀⠀⢠⠁⠀⢸⠐⠁⠀⠄⠀⢸⠀⠀⢎⠀⠂⠀⠈⡄⢡⠀⢣\n" +
                        "⠀⢀⠂⠀⠀⢸⠈⠢⠤⠤⠐⢁⠄⠒⠢⢁⣂⡐⠊⠀⡄⠸\n" +
                        "⠀⡘⠀⠀⠀⢸⠀⢠⠐⠒⠈⠀⠀⠀⠀⠀⠀⠈⢆⠜⠀⠀⢸\n" +
                        "⠀⡇⠀⠀⠀⠀⡗⢺⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠑⡄⢀⠎\n" +
                        "⠀⢃⠀⠀⠀⢀⠃⢠⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠷⡃\n" +
                        "⠀⠈⠢⣤⠀⠈⠀⠀⠑⠠⠤⣀⣀⣀⣀⣀⡀⠤⠒⠁⢡\n" +
                        "⡀⣀⠀⡆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢘\n" +
                        "⠑⢄⠉⢳⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡸\n" +
                        "⠀⠀⠑⠢⢱⣄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡴⠁\n" +
                        "⠀⠀⠀⠀⢀⠠⠓⠢⠤⣀⣀⡀⠀⣀⣀⡀⠤⠒⠑⢄\n" +
                        "⠀⠀⠀⠰⠥⠤⢄⢀⡠⠄⡈⡀⠀⣇⣀⠠⢄⠀⠒⠤⠣");
    }

    private void addSeedCard(String name, String type, String rarity, String art) {
        int id = nextCardId++;
        cards.put(id, new PokemonCard(id, name, type, rarity, art));
    }



    public synchronized List<PokemonCard> listCards(String rarity, String name) {
        return cards.values().stream()
                .filter(c -> rarity == null || c.rarity().equalsIgnoreCase(rarity))
                .filter(c -> name == null || c.name().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    public synchronized PokemonCard getCard(int id) {
        return findCard(id);
    }


    public synchronized PokemonCard createCard(String name, String type, String rarity, String art) {
        validateCard(name, type, rarity, art);
        String r = rarity.toUpperCase();
        if (r.equals(LEGENDARY) && legendaryExists())
            throw new ConflictException("LEGENDARY_LIMIT", "В каталоге уже есть LEGENDARY-карточка, вторую создать нельзя");
        if (cards.values().stream().anyMatch(c -> c.name().equalsIgnoreCase(name)))
            throw new ConflictException("DUPLICATE_CARD", "Карточка с таким названием уже существует");
        if (cards.size() >= MAX_CARDS)
            throw new ConflictException("CARD_LIMIT", "В каталоге может быть максимум " + MAX_CARDS + " карточек");
        int id = nextCardId++;
        PokemonCard card = new PokemonCard(id, name, type, r, art);
        cards.put(id, card);
        return card;
    }


    public synchronized PokemonCard updateCard(int id, String name, String type, String rarity, String art) {
        PokemonCard old = findCard(id);
        validateCard(name, type, rarity, art);
        String r = rarity.toUpperCase();
        if (old.rarity().equals(LEGENDARY) && !r.equals(LEGENDARY))
            throw new ConflictException("LEGENDARY_PROTECTED", "Редкость LEGENDARY-карточки изменить нельзя");
        if (!old.rarity().equals(LEGENDARY) && r.equals(LEGENDARY) && legendaryExists())
            throw new ConflictException("LEGENDARY_LIMIT", "В каталоге уже есть LEGENDARY-карточка, вторую создать нельзя");
        if (cards.values().stream().anyMatch(c -> c.id() != id && c.name().equalsIgnoreCase(name)))
            throw new ConflictException("DUPLICATE_CARD", "Карточка с таким названием уже существует");
        PokemonCard updated = new PokemonCard(id, name, type, r, art);
        cards.put(id, updated);
        return updated;
    }

    public synchronized void deleteCard(int id) {
        PokemonCard card = findCard(id);
        if (card.rarity().equals(LEGENDARY))
            throw new ConflictException("LEGENDARY_PROTECTED", "LEGENDARY-карточку нельзя удалить из каталога");
        boolean owned = players.values().stream().anyMatch(p -> p.collection.getOrDefault(id, 0) > 0);
        if (owned)
            throw new ConflictException("CARD_IN_COLLECTION", "Нельзя удалить карточку, которая есть у игроков");
        cards.remove(id);
    }

    private boolean legendaryExists() {
        return cards.values().stream().anyMatch(c -> c.rarity().equals(LEGENDARY));
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

    private PokemonCard findCard(int id) {
        PokemonCard card = cards.get(id);
        if (card == null) throw new NotFoundException("CARD_NOT_FOUND", "Карточка не найдена");
        return card;
    }


    public synchronized List<Player> listPlayers(String name) {
        return players.values().stream()
                .filter(p -> name == null || p.name.toLowerCase().contains(name.toLowerCase()))
                .map(PlayerState::snapshot)
                .toList();
    }

    public synchronized Player getPlayer(int id) {
        return findPlayer(id).snapshot();
    }

    public synchronized Player createPlayer(String name) {
        validatePlayerName(name);
        if (players.values().stream().anyMatch(p -> p.name.equalsIgnoreCase(name)))
            throw new ConflictException("DUPLICATE_PLAYER", "Игрок с таким именем уже существует");
        int id = nextPlayerId++;
        PlayerState player = new PlayerState(id, name);
        players.put(id, player);
        return player.snapshot();
    }

    public synchronized Player updatePlayer(int id, String name) {
        PlayerState player = findPlayer(id);
        validatePlayerName(name);
        if (players.values().stream().anyMatch(p -> p.id != id && p.name.equalsIgnoreCase(name)))
            throw new ConflictException("DUPLICATE_PLAYER", "Игрок с таким именем уже существует");
        player.name = name;
        return player.snapshot();
    }

    public synchronized void deletePlayer(int id) {
        findPlayer(id);
        players.remove(id);
    }

    private void validatePlayerName(String name) {
        if (blank(name)) throw new ValidationException("INVALID_PLAYER", "Поле name обязательно");
        if (name.length() > MAX_NAME)
            throw new ValidationException("NAME_TOO_LONG", "name не длиннее " + MAX_NAME + " символов");
    }

    private PlayerState findPlayer(int id) {
        PlayerState player = players.get(id);
        if (player == null) throw new NotFoundException("PLAYER_NOT_FOUND", "Игрок не найден");
        return player;
    }



    public synchronized Collection collection(int playerId) {
        PlayerState player = findPlayer(playerId);
        List<CollectionEntry> rows = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : player.collection.entrySet()) {
            PokemonCard card = cards.get(entry.getKey());
            if (card != null) rows.add(new CollectionEntry(card, entry.getValue()));
        }
        return new Collection(player.id, player.name, player.legendaryPulls, rows);
    }


    public synchronized DrawResult draw(int playerId) {
        PlayerState player = findPlayer(playerId);
        if (cards.isEmpty()) throw new ConflictException("EMPTY_CATALOG", "В каталоге нет карточек");
        List<PokemonCard> common = cards.values().stream().filter(c -> c.rarity().equals(COMMON)).toList();
        List<PokemonCard> legendary = cards.values().stream().filter(c -> c.rarity().equals(LEGENDARY)).toList();

        PokemonCard picked;
        if (!common.isEmpty() && !legendary.isEmpty()) {
            picked = random.nextInt(100) < 80
                    ? common.get(random.nextInt(common.size()))
                    : legendary.get(random.nextInt(legendary.size()));
        } else {
            List<PokemonCard> available = new ArrayList<>(cards.values());
            picked = available.get(random.nextInt(available.size()));
        }
        int owned = player.collection.merge(picked.id(), 1, Integer::sum);
        if (picked.rarity().equals(LEGENDARY)) player.legendaryPulls++;
        return new DrawResult("Карточка выбита!", picked, owned, player.legendaryPulls);
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }



    public record PokemonCard(int id, String name, String type, String rarity, String art) {
    }


    public record Player(int id, String name, int legendaryPulls, Map<Integer, Integer> collection) {
    }

    public record CollectionEntry(PokemonCard card, int quantity) {
    }

    public record Collection(int playerId, String playerName, int legendaryPulls, List<CollectionEntry> cards) {
    }

    public record DrawResult(String message, PokemonCard card, int quantityOwned, int legendaryPulls) {
    }


    private static final class PlayerState {
        final int id;
        String name;
        int legendaryPulls;
        final Map<Integer, Integer> collection = new LinkedHashMap<>();

        PlayerState(int id, String name) {
            this.id = id;
            this.name = name;
        }

        Player snapshot() {
            return new Player(id, name, legendaryPulls, new LinkedHashMap<>(collection));
        }
    }
}
