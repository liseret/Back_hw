package app.service;
import app.domain.*;
import app.domain.exception.*;
import app.repository.CardRepository;
import app.repository.PlayerRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameServiceTest {

    private GameService createService() {
        return new GameService(new CardRepository(), new PlayerRepository());
    }


    @Test
    void createCard_card() {
        GameService service = createService();
        PokemonCard card = service.createCard("Kartman", "ELECTRIC", "COMMON",
                    "              ----\n" +
                        "            -===,=--\n" +
                        "          --,-,,,,,,-\n" +
                        "        :-;==:========:\n" +
                        "      :=---------------::\n" +
                        "     :-------------------::\n" +
                        "    :----------------------:\n" +
                        "   :------------------------:\n" +
                        "  :--------------------------:\n" +
                        " :----------=:::==::::-------:\n" +
                        " :------:====--,,,,--====:=---:\n" +
                        ":----:===,=++++...++++..--===::\n" +
                        ":-===--...+    +.+    +.....,-=/\n" +
                        "/==-.....+      +      +......./\n" +
                        ";-......+     - / -     +....../\n" +
                        "/.......+    -# . #-    +....../\n" +
                        "/.......+       +       +....../\n" +
                        "/.......+      +.+      +....../\n" +
                        "/........+    +...+    +......./\n" +
                        "/.........++++.....++++......./\n" +
                        " /..........................-./\n" +
                        " /.-.......................-./\n" +
                        "  /.-......M##M$HM###M....-../\n" +
                        "   /.-......########$....-../\n" +
                        "    /.--......$$$$+-....-.-/\n" +
                        "     /..--.....,,,,...--.//\n" +
                        "      //..----.....---.//\n" +
                        "        ///-..-----.-//\n" +
                        "           //////////");


        assertThat(card.id()).isPositive();
        assertThat(service.getCard(card.id())).isEqualTo(card);
    }

    @Test
    void duplicatePikachu() {
        GameService service = createService();
        service.createCard("Pikachu", "ELECTRIC", "COMMON", "art");
        assertThatThrownBy(() -> service.createCard("Pikachu", "ELECTRIC", "COMMON", "art")).isInstanceOf(ConflictException.class).hasFieldOrPropertyWithValue("code", "DUPLICATE_CARD");
    }

    @Test
    void createCard_cardLimit() {
        GameService service = createService();
        for (int i = 0; i < 5; i++) {
            service.createCard("Card" + i, "TYPE", "COMMON", "art");
        }
        assertThatThrownBy(() -> service.createCard("Extra", "TYPE", "COMMON", "art")).isInstanceOf(ConflictException.class).hasFieldOrPropertyWithValue("code", "CARD_LIMIT");
    }

    @Test
    void createCard_wrongRarity() {
        GameService service = createService();
        assertThatThrownBy(() -> service.createCard("chillorucos", "ELECTRIC", "RARE", "art")).isInstanceOf(ValidationException.class).hasFieldOrPropertyWithValue("code", "INVALID_RARITY");
    }

    @Test
    void createCard_emptyName() {
        GameService service = createService();

        assertThatThrownBy(() -> service.createCard("", "ELECTRIC", "COMMON", "art")).isInstanceOf(ValidationException.class).hasFieldOrPropertyWithValue("code", "INVALID_CARD");
    }

    @Test
    void createCard_tooLongName() {
        GameService service = createService();

        assertThat(service.createCard("g".repeat(50), "TYPE", "COMMON", "art").id()).isPositive();

        assertThatThrownBy(() -> service.createCard("g".repeat(51), "TYPE", "COMMON", "art")).isInstanceOf(ValidationException.class).hasFieldOrPropertyWithValue("code", "NAME_TOO_LONG");
    }

    @Test
    void getCard_cardFindById() {
        GameService service = createService();

        PokemonCard card = service.createCard("Kartman", "ELECTRIC", "COMMON",
                "                  ----\n" +
                        "            -===,=--\n" +
                        "          --,-,,,,,,-\n" +
                        "        :-;==:========:\n" +
                        "      :=---------------::\n" +
                        "     :-------------------::\n" +
                        "    :----------------------:\n" +
                        "   :------------------------:\n" +
                        "  :--------------------------:\n" +
                        " :----------=:::==::::-------:\n" +
                        " :------:====--,,,,--====:=---:\n" +
                        ":----:===,=++++...++++..--===::\n" +
                        ":-===--...+    +.+    +.....,-=/\n" +
                        "/==-.....+      +      +......./\n" +
                        ";-......+     - / -     +....../\n" +
                        "/.......+    -# . #-    +....../\n" +
                        "/.......+       +       +....../\n" +
                        "/.......+      +.+      +....../\n" +
                        "/........+    +...+    +......./\n" +
                        "/.........++++.....++++......./\n" +
                        " /..........................-./\n" +
                        " /.-.......................-./\n" +
                        "  /.-......M##M$HM###M....-../\n" +
                        "   /.-......########$....-../\n" +
                        "    /.--......$$$$+-....-.-/\n" +
                        "     /..--.....,,,,...--.//\n" +
                        "      //..----.....---.//\n" +
                        "        ///-..-----.-//\n" +
                        "           //////////");


        assertThat(service.getCard(card.id())).isEqualTo(card);
    }

    @Test
    void getCard_999() {
        GameService service = createService();
        assertThatThrownBy(() -> service.getCard(999)).isInstanceOf(NotFoundException.class).hasFieldOrPropertyWithValue("code", "CARD_NOT_FOUND");
    }

    @Test
    void updateCard_changesStateCommonCard() {
        GameService service = createService();

        PokemonCard card = service.createCard(
                "Old", "TYPE", "COMMON", "art"
        );

        service.updateCard(
                card.id(), "New", "FIRE", "COMMON", "newArt"
        );

        assertThat(service.getCard(card.id()).name()).isEqualTo("New");
        assertThat(service.getCard(card.id()).type()).isEqualTo("FIRE");
    }

    @Test
    void updateCard_changesStateLegendaryCard() {
        GameService service = createService();
        PokemonCard card = service.createCard("Legendary", "TYPE", "LEGENDARY", "art");
        assertThatThrownBy(() -> service.updateCard(card.id(), "Legendary", "TYPE", "COMMON", "art")).isInstanceOf(ConflictException.class).hasFieldOrPropertyWithValue("code", "LEGENDARY_PROTECTED");
    }

    @Test
    void deleteCardById() {
        GameService service = createService();

        PokemonCard card = service.createCard("Pikachu", "ELECTRIC", "COMMON", "art");
        service.deleteCard(card.id());
        assertThat(service.listCards(null, null)).isEmpty();
    }

    @Test
    void deleteCard_ownedByPlayer() {
        GameService service = createService();

        PokemonCard card = service.createCard("Pikachu", "ELECTRIC", "COMMON", "art");
        Player player = service.createPlayer("melharucos");
        service.draw(player.id());
        assertThatThrownBy(() -> service.deleteCard(card.id())).isInstanceOf(ConflictException.class).hasFieldOrPropertyWithValue("code", "CARD_IN_COLLECTION");
    }

    @Test
    void listCards_filterByRarity() {
        GameService service = createService();

        service.createCard("Pikachu", "ELECTRIC", "COMMON", "art");
        service.createCard("Mew", "PSYCHIC", "LEGENDARY", "art");
        assertThat(service.listCards("LEGENDARY", null)).extracting(PokemonCard::name).containsExactly("Mew");
    }

    @Test
    void listCards_filterByName() {
        GameService service = createService();

        service.createCard("Pikachu", "ELECTRIC", "COMMON", "art");
        service.createCard("Mew", "PSYCHIC", "LEGENDARY", "art");
        assertThat(service.listCards(null, "Pika")).extracting(PokemonCard::name).containsExactly("Pikachu");
    }

    @Test
    void createPlayer_p() {
        GameService service = createService();

        Player player = service.createPlayer("che_dum");
        assertThat(player.id()).isPositive();
        assertThat(player.name()).isEqualTo("che_dum");
    }

    @Test
    void createPlayer_duplicate() {
        GameService service = createService();

        service.createPlayer("kekw");
        assertThatThrownBy(() -> service.createPlayer("kekw")).isInstanceOf(ConflictException.class).hasFieldOrPropertyWithValue("code", "DUPLICATE_PLAYER");
    }

    @Test
    void createPlayer_emptyName() {
        GameService service = createService();

        assertThatThrownBy(() -> service.createPlayer("")).isInstanceOf(ValidationException.class).hasFieldOrPropertyWithValue("code", "INVALID_PLAYER");
    }

    @Test
    void getPlayer_playerFindById() {
        GameService service = createService();

        Player player = service.createPlayer("kekw");
        assertThat(service.getPlayer(player.id())).isEqualTo(player);
    }

    @Test
    void getPlayer_999() {
        GameService service = createService();

        assertThatThrownBy(() -> service.getPlayer(999)).isInstanceOf(NotFoundException.class).hasFieldOrPropertyWithValue("code", "PLAYER_NOT_FOUND");
    }

    @Test
    void updatePlayer_changesState() {
        GameService service = createService();

        Player player = service.createPlayer("kekw");
        service.updatePlayer(player.id(), "lol");
        assertThat(service.getPlayer(player.id()).name()).isEqualTo("lol");
    }

    @Test
    void deletePlayer_success() {
        GameService service = createService();

        Player player = service.createPlayer("kekw");
        service.deletePlayer(player.id());
        assertThat(service.listPlayers(null)).isEmpty();
    }


    @Test
    void collection_emptyForNewPlayer() {
        GameService service = createService();

        Player player = service.createPlayer("kekw");
        assertThat(service.collection(player.id()).cards()).isEmpty();
    }

    @Test
    void collection_pikachux2() {
        GameService service = createService();

        service.createCard("Pikachu", "ELECTRIC", "COMMON", "art");
        Player player = service.createPlayer("kekw");
        service.draw(player.id());
        service.draw(player.id());
        Collection collection = service.collection(player.id());
        assertThat(collection.cards()).hasSize(1);
        assertThat(collection.cards().get(0).quantity()).isEqualTo(2);
    }

    @Test
    void collection_playerNotFound() {
        GameService service = createService();

        assertThatThrownBy(() -> service.collection(999)).isInstanceOf(NotFoundException.class).hasFieldOrPropertyWithValue("code", "PLAYER_NOT_FOUND");
    }


    @Test
    void draw_addsCardToCollection() {
        GameService service = createService();

        service.createCard("Pikachu", "ELECTRIC", "COMMON", "art");
        Player player = service.createPlayer("kekw");
        DrawResult result = service.draw(player.id());
        assertThat(result.quantityOwned()).isEqualTo(1);
        assertThat(service.collection(player.id()).cards()).hasSize(1);
    }

    @Test
    void draw_morePikachu() {
        GameService service = createService();

        service.createCard("Pikachu", "ELECTRIC", "COMMON", "art");
        Player player = service.createPlayer("kekw");
        service.draw(player.id());
        DrawResult second = service.draw(player.id());

        assertThat(second.quantityOwned()).isEqualTo(2);
    }

    @Test
    void draw_legendary() {
        GameService service = createService();

        service.createCard("Mew", "PSYCHIC", "LEGENDARY", "art");
        Player player = service.createPlayer("kekw");
        DrawResult result = service.draw(player.id());
        assertThat(result.card().rarity()).isEqualTo("LEGENDARY");
        assertThat(result.legendaryPulls()).isEqualTo(1);
    }

    @Test
    void draw_emptyCatalog() {
        GameService service = createService();

        Player player = service.createPlayer("kekw");
        assertThatThrownBy(() -> service.draw(player.id())).isInstanceOf(ConflictException.class).hasFieldOrPropertyWithValue("code", "EMPTY_CATALOG");
    }


    @Test
    void Pkachu_problems() {
        GameService service = createService();
        service.createPlayer("Pikachu");
        assertThatThrownBy(() -> service.createPlayer(" Pikachu")).isInstanceOf(ConflictException.class).hasFieldOrPropertyWithValue("code", "DUPLICATE_PLAYER");
    }

    @Test
    void createCard_duble() {
        GameService service = createService();
        service.createCard("Pikachu", "ELECTRIC", "COMMON", "art");
        assertThatThrownBy(() -> service.createCard(" Pikachu", "ELECTRIC", "COMMON", "art")).isInstanceOf(ConflictException.class).hasFieldOrPropertyWithValue("code", "DUPLICATE_CARD");
    }
}
