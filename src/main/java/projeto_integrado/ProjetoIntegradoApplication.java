package projeto_integrado;

import com.mercadopago.MercadoPagoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ProjetoIntegradoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetoIntegradoApplication.class, args);
	}


}
