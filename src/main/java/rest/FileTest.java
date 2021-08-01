package rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.io.File;

import org.junit.Test;

public class FileTest {
	
	@Test
	public void deveObrigarEnvioArquivo() {
		given()
			.log().all()
		.when()
			.post("https://restapi.wcaquino.me/upload")
		.then()
			.log().all()
			.statusCode(404)  // 404 Not Found
			.body("error", is("Arquivo não enviado"))
		;
	}
	
	@Test
	public void deveFazerUploadDoArquivo() {
		given()
			.log().all()
			.multiPart("arquivo", new File("src/main/resources/users.pdf"))
		.when()
			.post("https://restapi.wcaquino.me/upload")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("users.pdf"))
		;
	}
	
	@Test
	public void naoDeveFazerUploadArquivoGrande() { // Obs: Arquivo deve ter pouco mais de 1 MB
		given()
			.log().all()
			.multiPart("arquivo", new File("src/main/resources/arquivo_1.16mb.jpeg"))
		.when()
			.post("https://restapi.wcaquino.me/upload")
		.then()
			.log().all()
			//.time(lessThan(4000L))   // 4000ms = 4s  --> Para limitar o tempo máx de Resposta
			.statusCode(413)  // 413: Request Entity Too Large
		;
	}

}

