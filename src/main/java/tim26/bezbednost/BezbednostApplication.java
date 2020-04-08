package tim26.bezbednost;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tim26.bezbednost.service.IKeyStoreService;
import tim26.bezbednost.service.KeyStoreService;

@SpringBootApplication
public class BezbednostApplication {

	public static void main(String[] args) {
		SpringApplication.run(BezbednostApplication.class, args);


	}




}
