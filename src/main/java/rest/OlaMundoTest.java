package rest;

import org.junit.Assert;
import org.junit.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;

import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OlaMundoTest {
	
	@Test
	public void testeOlaMundo() {
		Response response = request(Method.GET, "http://restapi.wcaquino.me/ola");
		
		Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
		Assert.assertTrue(response.statusCode() == 200);
		Assert.assertTrue("O status code deveria ser 200", response.statusCode() == 200);
		Assert.assertEquals(200, response.statusCode());
		
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200); // Verifica se o status code � 200
	}
	
	@Test
	public void deveConhecerOutrasFormasRestAssured() {
		// 1� Forma
		Response response = request(Method.GET, "http://restapi.wcaquino.me/ola");
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);

		// 2� Forma		
		get("http://restapi.wcaquino.me/ola").then().statusCode(200);
		
		// 3� Forma
		given() // Pr�-condi��es
		.when() // A��o
			.get("http://restapi.wcaquino.me/ola")
		.then() // Assertivas
			.statusCode(200)
		;
	}
	
	@Test
	public void deveConhecerMatchersHamcrest() {
		Assert.assertThat("Maria", Matchers.is("Maria"));  // assertThat(actual, expected)
		Assert.assertThat(123, Matchers.is(123));
		Assert.assertThat(123, Matchers.isA(Integer.class));
		Assert.assertThat(123d, Matchers.isA(Double.class));
		Assert.assertThat(123, Matchers.greaterThan(100));
		Assert.assertThat(123, Matchers.lessThan(130));
		
		List<Integer> impares = Arrays.asList(1,3,5,7,9);
		// Usou Import Est�tico para assertThat
		assertThat(impares, hasSize(5));
		assertThat(impares, contains(1,3,5,7,9));
		assertThat(impares, containsInAnyOrder(1,3,5,9,7));
		assertThat(impares, hasItem(1));
		assertThat(impares, hasItems(1,5));
		
		assertThat("Maria", is(not("Jo�o")));
		assertThat("Maria", not("Jo�o"));
		assertThat("Maria", anyOf(is("Maria"), is("Joaquina"))); // OU
		assertThat("Joaquina", allOf(startsWith("Joa"), endsWith("ina"), containsString("qui"))); // E
	}
	
	@Test
	public void deveValidarBody() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/ola")
		.then()
			.statusCode(200)
			.body(is("Ola Mundo!"))
			.body(containsString("Mundo"))
			.body(is(not(nullValue())))
		;
	}

}
