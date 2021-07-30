package rest.assured;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class UserJsonTest {
	
	@Test
	public void deveVerificarPrimeiroNivel() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users/1")
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("name", containsString("Silva"))
			.body("age", greaterThan(18))
		;
	}
	
	@Test
	public void deveVerificarSegundoNivel() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users/2")
		.then()
			.statusCode(200)
			.body("name", containsString("Joaquina"))
			.body("endereco.rua", is("Rua dos bobos"))
		;
	}
	
	@Test
	public void deveVerificarLista() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users/3")
		.then()
			.statusCode(200)
			.body("name", containsString("Ana"))
			.body("filhos", hasSize(2))
			.body("filhos[0].name", is("Zezinho"))   //  1º elemento da lista
			.body("filhos[1].name", is("Luizinho"))  //  2º elemento da lista
			.body("filhos.name", hasItem("Zezinho"))
			.body("filhos.name", hasItems("Zezinho", "Luizinho"))
		;
	}
	
	@Test
	public void deveRetornarErroUsuarioInexistente() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users/4")
		.then()
			.statusCode(404)
			.body("error", is("Usuário inexistente"))
		;
	}

}
