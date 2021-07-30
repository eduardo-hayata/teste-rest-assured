package rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
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
			.body("filhos", hasSize(2))              // 2 elementos na lista
			.body("filhos[0].name", is("Zezinho"))   // 1� elemento da lista
			.body("filhos[1].name", is("Luizinho"))  // 2� elemento da lista
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
			.statusCode(404)   // 404 Not Found
			.body("error", is("Usu�rio inexistente"))
		;
	}
	
	@Test
	public void deveVerificarListaRaiz() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			.body("$", hasSize(3))  // $ : Buscando na Raiz (Obs: $ n�o � obrigat�rio, pode deixar em branco)
			.body("", hasSize(3))
			.body("name", hasItems("Jo�o da Silva", "Maria Joaquina", "Ana J�lia"))
			.body("age[1]", is(25))  // Idade do 2� registro
			.body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho")))
			.body("salary", contains(1234.5678f, 2500, null))
		;
	}
	
	@Test
	public void deveFazerVerificacoesAvancadas() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			.body("$", hasSize(3))
			.body("age.findAll{it <= 25}.size()", is(2)) // Qtos usu�rios tem at� 25 anos
			.body("age.findAll{it <= 25 && it > 20}.size()", is(1)) // Qtos usu�rios tem > 20 e <= 25 anos
			.body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina")) // Nome da pessoa que tem > 20 e <= 25 anos
			.body("findAll{it.age <= 25}[0].name", is("Maria Joaquina")) // Nome do 1� elemento da lista
			.body("findAll{it.age <= 25}[-1].name", is("Ana J�lia")) // Nome do �ltimo Registro da lista
			.body("find{it.age <= 25}.name", is("Maria Joaquina")) // find: retorna o 1� registro que encontrar, que satisfaz a condi��o
			.body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana J�lia")) // Nomes que cont�m a letra "n"
			.body("findAll{it.name.length() > 10}.name", hasItems("Jo�o da Silva", "Maria Joaquina")) // Nomes com tamanho > 10 caracteres
			.body("name.collect{it.toUpperCase()}", hasItems("MARIA JOAQUINA"))
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItems("MARIA JOAQUINA"))
			.body("age.collect{it * 2}", hasItems(60, 50, 40)) // Multiplica as idades por 2
			.body("id.max()", is(3)) // Maior ID que tem na cole��o
			.body("salary.min()", is(1234.5678f)) // Menor Sal�rio que tem na cole��o
			.body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001))) // Soma dos Sal�rios, ignorando os que forem "null"
			.body("salary.findAll{it != null}.sum()", allOf(greaterThan(3000d), lessThan(5000d)))
		;
	}
	
	@Test
	public void deveUnirJsonPathComJava() {
		ArrayList<String> names = 
			given()
			.when()
				.get("https://restapi.wcaquino.me/users")
			.then()
				.statusCode(200)
				.extract().path("name.findAll{it.startsWith('Maria')}")
			;
		
		Assert.assertEquals(1, names.size()); // tamanho do registro
		Assert.assertTrue(names.get(0).equalsIgnoreCase("mArIa Joaquina"));  // 1� registro
		Assert.assertEquals(names.get(0).toUpperCase(), "maria joaquina".toUpperCase());
	}

}
