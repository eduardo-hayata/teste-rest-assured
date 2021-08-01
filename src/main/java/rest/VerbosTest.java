package rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class VerbosTest {
	
	@BeforeClass
	public static void setup() {
		RestAssured.baseURI = "https://restapi.wcaquino.me";
		//RestAssured.port = 443;
		//RestAssured.basePath = "";		
	}
	
	//---------------------------------------------------------------------------------------------------------------
	// POST
	
	@Test
	public void deveSalvarUsuario() {
		given()
			.log().all()
			.contentType("application/json")  // Informar que está enviando um objeto "json"
			.body("{ \"name\": \"Jose\", \"age\": 50 }")
		.when()
			.post("/users")  // POST
		.then()
			.log().all()
			.statusCode(201)  // 201 Created
			.body("id", is(notNullValue()))
			.body("name", is("Jose"))
			.body("age", is(50))
		;
	}
	
	@Test
	public void naoDeveSalvarUsuarioSemNome() {
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"age\": 50 }")
		.when()
			.post("/users")  // POST
		.then()
			.log().all()
			.statusCode(400)  // 400 Bad Request
			.body("id", is(nullValue()))
			.body("error", is("Name é um atributo obrigatório"))
		;
	}
	
	@Test
	public void deveSalvarUsuarioViaXML() {
		given()
			.log().all()
			//.contentType("application/xml")
			.contentType(ContentType.XML)
			.body("<user><name>Jose</name><age>50</age></user>")
		.when()
			.post("/usersXML")  // POST
		.then()
			.log().all()
			.statusCode(201)  // 201 Created
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Jose"))
			.body("user.age", is("50"))
		;
	}
	
	//---------------------------------------------------------------------------------------------------------------
	// PUT
	
	@Test
	public void deveAlterarUsuario() {
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"name\": \"Usuario Alterado\", \"age\": 60 }")
		.when()
			.put("/users/1")  // PUT
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Usuario Alterado"))
			.body("age", is(60))
			.body("salary", is(1234.5678f))
		;
	}
	
	@Test
	public void deveCustomizarURL() {  // URL Parametrizável
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"name\": \"Usuario Alterado\", \"age\": 60 }")
			.pathParam("entidade", "users")
			.pathParam("userId", 1)
		.when()
			//.put("/{entidade}/{userId}", "users", "1")
			.put("/{entidade}/{userId}")  // PUT
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Usuario Alterado"))
			.body("age", is(60))
			.body("salary", is(1234.5678f))
		;
	}
	
	//---------------------------------------------------------------------------------------------------------------
	// DELETE
	
	@Test
	public void deveRemoverUsuario() {
		given()
			.log().all()
		.when()
			.delete("/users/1")  // DELETE
		.then()
			.log().all()
			.statusCode(204)  // 204 No Content
		;
	}
	
	@Test
	public void naoDeveRemoverUsuarioInexistente() {
		given()
			.log().all()
		.when()
			.delete("/users/100")  // DELETE
		.then()
			.log().all()
			.statusCode(400)  // 400 Bad Reques
			.body("error", is("Registro inexistente"))
		;
	}
	

}


