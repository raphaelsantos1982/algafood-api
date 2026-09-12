package com.algaworks.algafood.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.api.assembler.CidadeInputDisassembler;
import com.algaworks.algafood.api.assembler.CidadeModelAssembler;
import com.algaworks.algafood.api.controller.openapi.CidadeControllerOpenApi;
import com.algaworks.algafood.api.model.CidadeModel;
import com.algaworks.algafood.api.model.input.CidadeInput;
import com.algaworks.algafood.domain.exception.EstadoNaoEncontradoException;
import com.algaworks.algafood.domain.exception.NegocioException;
import com.algaworks.algafood.domain.model.Cidade;
import com.algaworks.algafood.domain.repository.CidadeRepository;
import com.algaworks.algafood.domain.service.CadastroCidadeService;


@RestController
@RequestMapping(value = "/cidades")
public class CidadeController implements CidadeControllerOpenApi {

	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
	private CadastroCidadeService cadastroCidade;
	
	@Autowired
	private CidadeModelAssembler cidadeModelAssembler;

	@Autowired
	private CidadeInputDisassembler cidadeInputDisassembler; 
		
	@GetMapping
	public List<CidadeModel> listar() {
	    List<Cidade> todasCidades = cidadeRepository.findAll();
	    
	    return cidadeModelAssembler.toCollectionModel(todasCidades);
	}
	
	
	/*
	 * IMPORTANTE !!!
	 * Inconformidade com cenario do curso. -> "name = "corpo", value = "Representação de uma cidade com os novos dados"" no metodo  atualizar nao estao aparecendo no swagger
	 * 
	 * Diagnóstico
	 * 
	 * Não é um erro no seu código — é uma limitação conhecida do Springfox. Vi
	 * confirmação em issues abertas no próprio repositório do Springfox (ex:
	 * springfox#2248, springfox#1882).
	 * 
	 * O motivo: @ApiParam funciona bem para parâmetros individuais
	 * como @PathVariable e @RequestParam — eles aparecem como linhas na tabela
	 * Parameters, como você viu no cidadeId ("ID de uma cidade - teste [atualizar]"
	 * apareceu certinho).
	 * 
	 * Mas para @RequestBody, o Swagger UI (na versão empacotada pelo Springfox) não
	 * renderiza um parâmetro individual — ele mostra a seção Request body genérica,
	 * baseada apenas no schema do objeto (via Example Value / Schema). O name/value
	 * do @ApiParam colocado no parâmetro do método simplesmente não é usado para
	 * gerar essa descrição — na especificação OpenAPI 2 (que o Springfox gera), o
	 * "name" de um parâmetro body é fixo, e o value (descrição) não é propagado
	 * pelo swagger-ui nessa seção.
	 * 
	 * Solução prática
	 * 
	 * A forma correta e confiável de documentar o corpo da requisição é anotar a
	 * classe do DTO (CidadeInput), não o parâmetro do método:
	 */
	
	@GetMapping("/{cidadeId}")
	public CidadeModel buscar(@PathVariable Long cidadeId) {
	    Cidade cidade = cadastroCidade.buscarOuFalhar(cidadeId);
	    
	    return cidadeModelAssembler.toModel(cidade);
	}
//	@GetMapping("/{cidadeId}")
//	public ResponseEntity<Cidade> buscar(@PathVariable Long cidadeId) {
//		Optional<Cidade> cidade = cidadeRepository.findById(cidadeId);
//		
//		if (cidade.isPresent()) {
//			return ResponseEntity.ok(cidade.get());
//		}
//		
//		return ResponseEntity.notFound().build();
//	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CidadeModel adicionar(@RequestBody @Valid CidadeInput cidadeInput) {
		try {
			
			Cidade cidade = cidadeInputDisassembler.toDomainObject(cidadeInput);
		        
			cidade = cadastroCidade.salvar(cidade);
		        
			return cidadeModelAssembler.toModel(cidade);
		} catch (EstadoNaoEncontradoException e) {
//		} catch (EntidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}
//	@PostMapping
//	public ResponseEntity<?> adicionar(@RequestBody Cidade cidade) {
//		try {
//			cidade = cadastroCidade.salvar(cidade);
//			
//			return ResponseEntity.status(HttpStatus.CREATED)
//					.body(cidade);
//		} catch (EntidadeNaoEncontradaException e) {
//			return ResponseEntity.badRequest()
//					.body(e.getMessage());
//		}
//	}
	
	
	@PutMapping("/{cidadeId}")
	public CidadeModel atualizar(@PathVariable Long cidadeId,
								 @RequestBody @Valid CidadeInput cidadeInput) {
	    try {
	        Cidade cidadeAtual = cadastroCidade.buscarOuFalhar(cidadeId);
	        
	        cidadeInputDisassembler.copyToDomainObject(cidadeInput, cidadeAtual);
	        
	        cidadeAtual = cadastroCidade.salvar(cidadeAtual);
	        
	        return cidadeModelAssembler.toModel(cidadeAtual);
	    } catch (EstadoNaoEncontradoException e) {
	        throw new NegocioException(e.getMessage(), e);
	    }
	}
//	@PutMapping("/{cidadeId}")
//	public ResponseEntity<?> atualizar(@PathVariable Long cidadeId,
//			@RequestBody Cidade cidade) {
//		try {
//			// Podemos usar o orElse(null) também, que retorna a instância de cidade
//			// dentro do Optional, ou null, caso ele esteja vazio,
//			// mas nesse caso, temos a responsabilidade de tomar cuidado com NullPointerException
//			Cidade cidadeAtual = cidadeRepository.findById(cidadeId).orElse(null);
//			
//			if (cidadeAtual != null) {
//				BeanUtils.copyProperties(cidade, cidadeAtual, "id");
//				
//				cidadeAtual = cadastroCidade.salvar(cidadeAtual);
//				return ResponseEntity.ok(cidadeAtual);
//			}
//			
//			return ResponseEntity.notFound().build();
//		
//		} catch (EntidadeNaoEncontradaException e) {
//			return ResponseEntity.badRequest()
//					.body(e.getMessage()); 
//		}
//	}
	
	
	@DeleteMapping("/{cidadeId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long cidadeId) {
	    cadastroCidade.excluir(cidadeId);	
	}
//	@DeleteMapping("/{cidadeId}")
//	public ResponseEntity<Cidade> remover(@PathVariable Long cidadeId) {
//		try {
//			cadastroCidade.excluir(cidadeId);	
//			return ResponseEntity.noContent().build();
//			
//		} catch (EntidadeNaoEncontradaException e) {
//			return ResponseEntity.notFound().build();
//			
//		} catch (EntidadeEmUsoException e) {
//			return ResponseEntity.status(HttpStatus.CONFLICT).build();
//		}
//	}
}