package app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import app.domain.exception.ConflictException;
import app.domain.exception.NotFoundException;
import app.domain.exception.ValidationException;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ApiHandler implements HttpHandler {
    private final GameService service;
    private final JsonMapper mapper = JsonMapper.builder().build();

    public ApiHandler(GameService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            Map<String, String> query =
                    parseQuery(exchange.getRequestURI().getRawQuery());

            String[] parts = Arrays.stream(path.split("/"))
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);

            Object result;
            int status = 200;

            if (parts.length == 1 && parts[0].equals("cards")) {
                if (method.equals("GET")) {
                    List<GameService.PokemonCard> cards =
                            service.listCards(
                                    query.get("rarity"),
                                    query.get("name")
                            );

                    sendText(exchange, 200, renderCards(cards));
                    return;

                }
                else if (method.equals("POST")) {
                    Map<String, Object> body = readBody(exchange);

                    result = service.createCard(required(body, "name"), required(body, "type"), required(body, "rarity"), required(body, "art"));

                    status = 201;
                }
                else {
                    throw methodNotAllowed("GET, POST");
                }

            }
            else if (parts.length == 2 && parts[0].equals("cards")) {
                int id = parseId(parts[1]);

                switch (method) {
                    case "GET" -> {
                        GameService.PokemonCard card = service.getCard(id);

                        sendText(exchange, 200, renderCard(card));
                        return;
                    }

                    case "PUT" -> {
                        Map<String, Object> body = readBody(exchange);

                        result = service.updateCard(
                                id,
                                required(body, "name"),
                                required(body, "type"),
                                required(body, "rarity"),
                                required(body, "art")
                        );
                    }

                    case "DELETE" -> {
                        service.deleteCard(id);
                        result = Map.of("message", "Карточка удалена");
                    }

                    default -> throw methodNotAllowed("GET, PUT, DELETE");
                }

            }
            else if (parts.length == 1 && parts[0].equals("players")) {
                if (method.equals("GET")) {
                    result = service.listPlayers(query.get("name"));

                }
                else if (method.equals("POST")) {
                    Map<String, Object> body = readBody(exchange);

                    result = service.createPlayer(
                            required(body, "name")
                    );

                    status = 201;
                }
                else {
                    throw methodNotAllowed("GET, POST");
                }

            }
            else if (parts.length == 2 && parts[0].equals("players")) {
                int id = parseId(parts[1]);

                switch (method) {
                    case "GET" -> result = service.getPlayer(id);

                    case "PUT" -> {
                        Map<String, Object> body = readBody(exchange);
                        result = service.updatePlayer(
                                id,
                                required(body, "name")
                        );
                    }

                    case "DELETE" -> {
                        service.deletePlayer(id);
                        result = Map.of("message", "Игрок удалён");
                    }

                    default -> throw methodNotAllowed("GET, PUT, DELETE");
                }

            }
            else if (parts.length == 3
                    && parts[0].equals("players")
                    && parts[2].equals("cards")) {

                if (!method.equals("GET")) {
                    throw methodNotAllowed("GET");
                }

                result = service.collection(parseId(parts[1]));

            }
            else if (parts.length == 3
                    && parts[0].equals("players")
                    && parts[2].equals("draw")) {

                if (!method.equals("POST")) {
                    throw methodNotAllowed("POST");
                }

                result = service.draw(parseId(parts[1]));

            }
            else {
                throw new ApiException(
                        404,
                        "NOT_FOUND",
                        "Такого путя нет"
                );
            }

            send(exchange, status, result);

        }
        catch (ApiException e) {
            if (e.allow != null) exchange.getResponseHeaders().set("Allow", e.allow);
            send(exchange, e.status, error(e.code, null, e.getMessage()));

        }
        catch (NotFoundException e) {
            send(exchange, 404, error("NOT_FOUND", e.code(), e.getMessage()));

        }
        catch (ConflictException e) {
            send(exchange, 409, error("CONFLICT", e.code(), e.getMessage()));

        }
        catch (ValidationException e) {
            send(exchange, 400, error("BAD_REQUEST", e.code(), e.getMessage()));

        }
        catch (IllegalArgumentException e) {
            send(exchange, 400, error("BAD_REQUEST", null, "Некорректный запрос"));

        }
        catch (Exception e) {
            e.printStackTrace();

            send(exchange, 500, error("INTERNAL_ERROR", null, "Внутренняя ошибка сервера"));

        }
        finally {
            exchange.close();
        }
    }

    private String renderCards(List<GameService.PokemonCard> cards) {
        if (cards.isEmpty()) {
            return "Карточки не найдены.\n";
        }

        StringBuilder result = new StringBuilder();

        for (GameService.PokemonCard card : cards) {
            result.append(renderCard(card));
            result.append("\n\n");
        }

        return result.toString();
    }

    private String renderCard(GameService.PokemonCard card) {
        StringBuilder result = new StringBuilder();

        result.append("================================\n");
        result.append("           ").append(card.name()).append("\n");
        result.append("================================\n");

        result.append("ID: ").append(card.id()).append("\n");
        result.append("TYPE: ").append(card.type()).append("\n");
        result.append("RARITY: ").append(card.rarity()).append("\n");

        result.append("--------------------------------\n");

        result.append(card.art());

        if (!card.art().endsWith("\n")) {
            result.append("\n");
        }

        result.append("================================\n");

        return result.toString();
    }

    private void sendText(HttpExchange exchange, int status, String text) throws IOException {

        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/plain; charset=utf-8"
        );

        writeResponse(exchange, status, bytes);
    }


    private Map<String, Object> readBody(HttpExchange exchange) throws IOException {

        byte[] bytes;

        try (InputStream in = exchange.getRequestBody()) {
            bytes = in.readNBytes(100_001);
        }

        if (bytes.length == 0) {
            throw new ApiException(
                    400, "EMPTY_BODY", "Тело запроса обязательно"
            );
        }

        if (bytes.length > 100_000) {
            throw new ApiException(
                    413, "BODY_TOO_LARGE", "Тело запроса слишком большое"
            );
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = mapper.readValue(bytes, Map.class);

            if (body == null) {
                throw new ApiException(
                        400, "INVALID_JSON", "Ожидался JSON-объект"
                );
            }

            return body;

        }
        catch (ApiException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ApiException(
                    400, "INVALID_JSON", "Некорректный JSON"
            );
        }
    }

    private String required(Map<String, Object> body, String key) {
        Object value = body.get(key);

        if (!(value instanceof String s) || s.trim().isEmpty()) {
            throw new ApiException(
                    400,
                    "MISSING_FIELD",
                    "Поле " + key + " обязательно и должно быть строкой"
            );
        }

        return s;
    }

    private int parseId(String value) {
        try {
            int id = Integer.parseInt(value);

            if (id <= 0) {
                throw new NumberFormatException();
            }

            return id;

        } catch (NumberFormatException e) {
            throw new ApiException(400, "INVALID_ID", "ID должен быть положительным целым числом");
        }
    }

    private Map<String, String> parseQuery(String raw) {
        Map<String, String> result = new HashMap<>();

        if (raw == null || raw.isBlank()) {
            return result;
        }

        for (String pair : raw.split("&")) {
            String[] kv = pair.split("=", 2);

            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String value;

            if (kv.length > 1) {
                value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
            }
            else {
                value = "";
            }

            result.put(key, value);
        }

        return result;
    }

    private ApiException methodNotAllowed(String allow) {
        return new ApiException(
                405,
                "METHOD_NOT_ALLOWED",
                "Метод не поддерживается. Допустимые методы: " + allow,
                allow
        );
    }


    private Map<String, Object> error(String error, String code, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", error);
        if (code != null){
            body.put("code", code);
        }
        body.put("message", message);
        return body;
    }


    private void send(HttpExchange exchange, int status, Object value) throws IOException {

        byte[] bytes = mapper.writeValueAsBytes(value);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=utf-8"
        );

        writeResponse(exchange, status, bytes);
    }


    private void writeResponse(HttpExchange exchange, int status, byte[] bytes) throws IOException {
        if (exchange.getRequestMethod().equals("HEAD")) {
            exchange.sendResponseHeaders(status, -1);
            return;
        }
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
