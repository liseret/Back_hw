package app;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringApp {
    private static final int PORT = 8080;

    public static void main(String[] args){
        SpringApplication.run(SpringApp.class,args);
        System.out.println("Pokemon Kazik: http://localhost:" + PORT);
    }
}
