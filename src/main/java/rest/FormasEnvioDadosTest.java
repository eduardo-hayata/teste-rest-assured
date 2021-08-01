package rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

import io.restassured.http.ContentType;

public class FormasEnvioDadosTest {
	
	@Test
	public void deveEnviarValorViaQuery() {
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/v2/users?format=json")  // Mostrar em formato JSON
			//.get("https://restapi.wcaquino.me/v2/users?format=xml") // Mostrar em formato XML
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.JSON)
			//.contentType(ContentType.XML)
		;
	}
	
	@Test
	public void deveEnviarValorViaParam() {  // Query Parametrizável
		given()
			.log().all()
			.queryParam("format", "xml")   // https://restapi.wcaquino.me/v2/users?format=xml
			.queryParam("outra", "coisa")  // https://restapi.wcaquino.me/v2/users?format=xml&outra=coisa  --> Esse param vai ser ignorado
		.when()
			.get("https://restapi.wcaquino.me/v2/users")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.XML)
			.contentType(containsString("utf-8"))
		;
	}
	
	@Test
	public void deveEnviarValorViaHeader() {
		given()
			.log().all()
			.accept(ContentType.JSON)  // accept: Especificar o que eu quero que venha de Resposta
		.when()
			.get("https://restapi.wcaquino.me/v2/users")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.JSON)
		;
	}

}
