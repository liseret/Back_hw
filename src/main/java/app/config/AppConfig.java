package app.config;

import app.domain.PokemonCard;
import app.repository.CardRepository;
import app.repository.PlayerRepository;
import app.service.GameService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static app.service.GameService.COMMON;
import static app.service.GameService.LEGENDARY;

@Configuration
public class AppConfig {

    @Bean
    CardRepository cardRepository() {
        return new CardRepository();
    }

    @Bean
    PlayerRepository playerRepository() {
        return new PlayerRepository();
    }

    @Bean
    GameService gameService(CardRepository cardRepo, PlayerRepository playerRepo) {
        return new GameService(cardRepo, playerRepo);
    }

    @Bean
    ApplicationRunner seedData(CardRepository cardRepo) {
        return _ -> {
            addSeed(cardRepo, "Pikachu", "ELECTRIC", COMMON,
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
            addSeed(cardRepo, "Mew", "PSYCHIC", LEGENDARY,
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
            addSeed(cardRepo, "Bulbasaur", "GRASS", COMMON,
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
            addSeed(cardRepo, "Wigglytuff", "FAIRY", COMMON,
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

            addSeed(cardRepo, "Psyduck", "WATER", COMMON,
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
        };
    }

    private void addSeed(CardRepository repo, String name, String type, String rarity, String art) {
        repo.save(new PokemonCard(repo.nextId(), name, type, rarity, art));
    }
}