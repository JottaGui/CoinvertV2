package projeto_integrado.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/perfil")
public class perfil {

	@GetMapping
	public String perfilpagina() {	
		return "perfil";	
	}
	
	
	
}
