package app.controller;

import app.ApiException;
import app.domain.*;
import app.service.GameService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ApiController {
    private final GameService service;


    public ApiController(GameService service) {
        this.service = service;
    }


    @GetMapping(value = "/cards", produces = MediaType.TEXT_PLAIN_VALUE)
    public String listCards(@RequestParam(value = "rarity",required = false) String rarity,@RequestParam(value = "name",required = false) String name) {
        return renderCards(service.listCards(rarity, name));
    }

    @PostMapping("/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public PokemonCard createCard(@Valid @RequestBody CardRequest req) {
        return service.createCard(req.name(), req.type(), req.rarity(), req.art());
    }

    @GetMapping(value = "/cards/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getCard(@PathVariable("id") String id) {
        return renderCard(service.getCard(parseId(id)));
    }

    @PutMapping("/cards/{id}")
    public PokemonCard updateCard(@PathVariable("id") String id, @Valid @RequestBody CardRequest req) {
        return service.updateCard(parseId(id), req.name(), req.type(), req.rarity(), req.art());
    }

    @DeleteMapping("/cards/{id}")
    public Map<String, String> deleteCard(@PathVariable("id") String id) {
        service.deleteCard(parseId(id));
        return Map.of("message", "Карточка удалена");
    }

    @GetMapping("/players")
    public List<Player> listPlayers(@RequestParam(value = "name",required = false) String name) {
        return service.listPlayers(name);
    }

    @PostMapping("/players")
    @ResponseStatus(HttpStatus.CREATED)
    public Player createPlayer(@Valid @RequestBody PlayerRequest req) {
        return service.createPlayer(req.name());
    }

    @GetMapping("/players/{id}")
    public Player getPlayer(@PathVariable("id") String id) {
        return service.getPlayer(parseId(id));
    }

    @PutMapping("/players/{id}")
    public Player updatePlayer(@PathVariable("id") String id, @Valid @RequestBody PlayerRequest req) {
        return service.updatePlayer(parseId(id), req.name());
    }
    @DeleteMapping("/players/{id}")
    public Map<String, String> deletePlayer(@PathVariable("id") String id) {
        service.deletePlayer(parseId(id));
        return Map.of("message", "Игрок удалён");
    }
    @GetMapping("/players/{id}/cards")
    public Collection collection(@PathVariable("id") String id) {
        return service.collection(parseId(id));
    }

    @PostMapping("/players/{id}/draw")
    public DrawResult draw(@PathVariable("id") String id) {
        return service.draw(parseId(id));
    }

    public record CardRequest(
            @NotBlank(message = "name") String name,
            @NotBlank(message = "type") String type,
            @NotBlank(message = "rarity") String rarity,
            @NotBlank(message = "art") String art) {}
    public record PlayerRequest(@NotBlank(message = "name") String name) {}

    private String renderCards(List<PokemonCard> cards) {
        if (cards.isEmpty()) return "Карточки не найдены.\n";
        StringBuilder sb = new StringBuilder();
        for (PokemonCard c : cards) sb.append(renderCard(c)).append("\n\n");
        return sb.toString();
    }
    private String renderCard(PokemonCard c) {
        StringBuilder sb = new StringBuilder();
        sb.append("================================\n");
        sb.append("           ").append(c.name()).append("\n");
        sb.append("================================\n");
        sb.append("ID: ").append(c.id()).append("\n");
        sb.append("TYPE: ").append(c.type()).append("\n");
        sb.append("RARITY: ").append(c.rarity()).append("\n");
        sb.append("--------------------------------\n");
        sb.append(c.art());
        if (!c.art().endsWith("\n")) sb.append("\n");
        sb.append("================================\n");
        return sb.toString();
    }
    private int parseId(String v) {
        try {
            int id = Integer.parseInt(v);
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException e) {
            throw new ApiException(400, "INVALID_ID", "ID должен быть положительным целым числом");
        }
    }
}