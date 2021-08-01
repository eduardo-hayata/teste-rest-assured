package rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.path.xml.NodeImpl;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class UserXMLTest {

	public static RequestSpecification reqSpec;
	public static ResponseSpecification resSpec;
	
	@BeforeClass
	public static void setup() {
		RestAssured.baseURI = "https://restapi.wcaquino.me";
		//RestAssured.port = 443;
		//RestAssured.basePath = "";
		
		// Config. de Log na Request
		RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
		reqBuilder.log(LogDetail.ALL);
		reqSpec = reqBuilder.build();
		
		// Config. de Log na Response
		ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
		resBuilder.expectStatusCode(200);
		resSpec = resBuilder.build();
		
		RestAssured.requestSpecification = reqSpec;   // Com isso, NÃO precisa informar nas classes de testes
		RestAssured.responseSpecification = resSpec;  // Com isso, NÃO precisa informar nas classes de testes
	}
	
	@Test
	public void deveTrabalharComXML() {
		given()
		.when()
			.get("/usersXML/3")
		.then()
			.statusCode(200)
			.body("user.name", is("Ana Julia"))
			.body("user.@id", is("3"))  // Como "id" é Atributo, precisa de "@"
			.body("user.filhos.name.size()", is(2))
			.body("user.filhos.name[0]", is("Zezinho"))
			.body("user.filhos.name[1]", is("Luizinho"))
			.body("user.filhos.name", hasItem("Luizinho"))
			.body("user.filhos.name", hasItems("Luizinho", "Zezinho"))
		;
	}
	
	@Test
	public void deveTrabalharComXML_2() {
		given()
		.when()
			.get("/usersXML/3")
		.then()
			.statusCode(200)
			
			.rootPath("user") // Caminho Raiz: Para Não precisar mais ficar colocando "user" nos "body"
			.body("name", is("Ana Julia"))
			.body("@id", is("3"))
			
			.rootPath("user.filhos") // Redefine "user.filhos" como Caminho Raiz
			.body("name.size()", is(2))
			
			.detachRootPath("filhos") // Retira o "filhos" do Caminho Raiz. Agora precisa especificar no caminho...
			.body("filhos.name[0]", is("Zezinho"))
			.body("filhos.name[1]", is("Luizinho"))
			
			.appendRootPath("filhos") // Adiciona o "filhos" dde volta. Agora NÃO precisa mais especificar no caminho...
			.body("name", hasItem("Luizinho"))
			.body("name", hasItems("Luizinho", "Zezinho"))
		;
	}
	
	@Test
	public void deveFazerPesquisasAvançadasComXML() {
		given()
		.when()
			.get("/usersXML")
		.then()
			.statusCode(200)
			.body("users.user.size()", is(3))
			.body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2))
			.body("users.user.@id", hasItems("1", "2", "3"))
			.body("users.user.find{it.age == 25}.name", is("Maria Joaquina"))
			.body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))
			.body("users.user.salary.find{it != null}", is("1234.5678"))
			.body("users.user.salary.find{it != null}.toDouble()", is(1234.5678d))
			.body("users.user.age.collect{it.toInteger() * 2}", hasItems(40, 50, 60))
			.body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
		;
	}
	
	@Test
	public void deveFazerPesquisasAvançadasComXMLeJava() {
		String nome = given()
			.when()
				.get("/usersXML")
			.then()
				.statusCode(200)
				.extract().path("users.user.name.findAll{it.toString().startsWith('Maria')}");
			;
		Assert.assertEquals("Maria Joaquina".toUpperCase(), nome.toUpperCase());
	}
	
	@Test
	public void deveFazerPesquisasAvançadasComXMLeJava_2() {
		ArrayList<NodeImpl> nomes = given()
			.when()
				.get("/usersXML")
			.then()
				.statusCode(200)
				.extract().path("users.user.name.findAll{it.toString().contains('n')}");
			;
		Assert.assertEquals(2, nomes.size());
		Assert.assertEquals("Maria Joaquina".toUpperCase(), nomes.get(0).toString().toUpperCase());
		Assert.assertTrue("ANA JULIA".equalsIgnoreCase(nomes.get(1).toString()));
	}
	
	@Test
	public void deveFazerPesquisasAvançadasComXPath() {
		given()
		.when()
			.get("/usersXML")
		.then()
			.statusCode(200)
			.body(hasXPath("count(/users/user)", is("3")))  // Qtdade de elementos
			.body(hasXPath("/users/user[@id = '1']"))  // Verifica se tem um usuario com id = 1
			.body(hasXPath("//user[@id = '2']"))  // Verifica se tem um usuario com id = 2
			.body(hasXPath("//name[text()='Luizinho']/../../name", is("Ana Julia")))  // Nome da mãe do Luizinho
			.body(hasXPath("//name[text()='Ana Julia']/following-sibling::filhos", allOf(containsString("Zezinho"), containsString("Luizinho"))))  // Filhos da Ana julia
			.body(hasXPath("/users/user/name", is("João da Silva")))  // Nome do primeiro registro que encontrar
			.body(hasXPath("//name", is("João da Silva")))  // Nome do primeiro registro que encontrar
			.body(hasXPath("/users/user[2]/name", is("Maria Joaquina")))  // Nome do segundo registro que encontrar
			.body(hasXPath("/users/user[last()]/name", is("Ana Julia")))  // Nome do último registro que encontrar
			.body(hasXPath("count(/users/user/name[contains(., 'n')])", is("2")))  // Qtas pessoas tem "n" no nome
			.body(hasXPath("//user[age < 24]/name", is("Ana Julia")))  // Nome da pessoa que tem < 24 anos
			.body(hasXPath("//user[age > 20 and age < 30]/name", is("Maria Joaquina")))  // Nome da pessoa que tem > 20 e < 30 anos
			.body(hasXPath("//user[age > 20][age < 30]/name", is("Maria Joaquina")))  // Nome da pessoa que tem > 20 e < 30 anos
		;
	}

}

