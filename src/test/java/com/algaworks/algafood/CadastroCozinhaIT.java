package com.algaworks.algafood;

import static org.hamcrest.CoreMatchers.equalTo;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.util.DatabaseCleaner;
import com.algaworks.algafood.util.ResourceUtils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // levanta um sevidor real possibilitar requisições e para que os testes possam ser executados, porta aleatoria, por isso usa @LocalServerPort
@TestPropertySource("/application-test.properties") // segue usando as propriedades do application-test.properties, porem as que estiverem application-test.properties substituem
class CadastroCozinhaIT {

	private static final int COZINHA_ID_INEXISTENTE = 100;

	@LocalServerPort
	private int port;
	
//	@Autowired
//	private Flyway flyway; // comentado na aula 10.14. Limpando e populando o banco de dados de teste
	@Autowired
	private DatabaseCleaner databaseCleaner;
	
	@Autowired
	private CozinhaRepository cozinhaRepository;
	
	private Cozinha cozinhaAmericana;
	private int quantidadeCozinhasCadastradas;
	private String jsonCorretoCozinhaChinesa;
	
	@BeforeEach// @ beforeEach -> será executado antes de cada teste, para que possa redefinir as condições para o próximo / @beforeAll -> será executado uma vez
	public void setUp() {
		
		// RestAssured -> Biblioteca pra teste e validação de RestApi
		// Se falhar, habilita o log da requisição(exibe request e response)
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		
		RestAssured.port = port;
		RestAssured.basePath = "/cozinhas";
		
		jsonCorretoCozinhaChinesa = ResourceUtils.getContentFromResource(
				"/json/correto/cozinha-chinesa.json");
		
//		flyway.migrate(); // vai executar o arquivo afterMigrate.sql // comentado na aula 10.14. Limpando e populando o banco de dados de teste
		databaseCleaner.clearTables(); // vai rodar e limpar todos os dados do banco de dados de teste
		prepararDados(); // insere uma massa de dados necessaria para os testes rodarem / antes de cada teste, limpa o banco(databaseCleaner.clearTables()) e insere a massa
		

	}
	
	@Test
	public void deveRetornarStatus200_QuandoConsultarCozinhas() {
		
		// dado que o path for /cozinhas com a porta e o retorno for json, quando s a requisição for get, entao o codigo de status tem que ser 200
		RestAssured.given()
//			.basePath("/cozinhas")
//			.port(port)
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.OK.value());
	}
	
	@Test
	public void deveRetornarQuantidadeCorretaDeCozinhas_QuandoConsultarCozinhas() {
		RestAssured.given()
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.body("", Matchers.hasSize(quantidadeCozinhasCadastradas));
	}
	
	@Test
	public void deveRetornarStatus201_QuandoCadastrarCozinha() {
		RestAssured.given()
			.body(jsonCorretoCozinhaChinesa)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.CREATED.value());
	}
	
	@Test
	public void deveRetornarRespostaEStatusCorretos_QuandoConsultarCozinhaExistente() {
		RestAssured.given()
			.pathParam("cozinhaId", cozinhaAmericana.getId())
			.accept(ContentType.JSON)
		.when()
			.get("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("nome", equalTo(cozinhaAmericana.getNome()));
	}
	
	@Test
	public void deveRetornarStatus404_QuandoConsultarCozinhaInexistente() {
		RestAssured.given()
			.pathParam("cozinhaId", COZINHA_ID_INEXISTENTE)
			.accept(ContentType.JSON)
		.when()
			.get("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	private void prepararDados() {
		Cozinha cozinhaTailandesa = new Cozinha();
		cozinhaTailandesa.setNome("Tailandesa");
		cozinhaRepository.save(cozinhaTailandesa);

		cozinhaAmericana = new Cozinha();
		cozinhaAmericana.setNome("Americana");
		cozinhaRepository.save(cozinhaAmericana);
		
		quantidadeCozinhasCadastradas = (int) cozinhaRepository.count();
	}
//	============================= TESTES DE INGRAÇÃO ===================================== INICIO
//	@Test
//	public void deveAtribuirId_QuandoCadastrarCozinhaComDadosCorretos() {
//		// cenário
//		Cozinha novaCozinha = new Cozinha();
//		novaCozinha.setNome("Chinesa");
//		
//		// ação
//		novaCozinha = cadastroCozinha.salvar(novaCozinha);
//		
//		// validação
//		assertThat(novaCozinha).isNotNull();
//		assertThat(novaCozinha.getId()).isNotNull();
//	}
//	
//	@Test
//	public void deveFalhar_QuandoCadastrarCozinhaSemNome() {
//	   Cozinha novaCozinha = new Cozinha();
//	   novaCozinha.setNome(null);
//	   
//	   ConstraintViolationException erroEsperado =
//	      Assertions.assertThrows(ConstraintViolationException.class, () -> {
//	         cadastroCozinha.salvar(novaCozinha);
//	      });
//	   assertThat(erroEsperado).isNotNull();
//	}
//	
//	@Test
//	public void deveFalhar_QuandoExcluirCozinhaEmUso() {
//
//		EntidadeEmUsoException erroEsperado =
//				Assertions.assertThrows(EntidadeEmUsoException.class, () -> {
//					cadastroCozinha.excluir(1L);
//				});
//		assertThat(erroEsperado).isNotNull();
//	}
//	
//	@Test
//	public void deveFalhar_QuandoExcluirCozinhaInexistente() {
//
//		CozinhaNaoEncontradaException erroEsperado =
//				Assertions.assertThrows(CozinhaNaoEncontradaException.class, () -> {
//					cadastroCozinha.excluir(100L);
//				});
//		assertThat(erroEsperado).isNotNull();
//	}
}
