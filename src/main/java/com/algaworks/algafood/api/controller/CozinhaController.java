package com.algaworks.algafood.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.algaworks.algafood.api.assembler.CozinhaInputDisassembler;
import com.algaworks.algafood.api.assembler.CozinhaModelAssembler;
import com.algaworks.algafood.api.model.CozinhaModel;
import com.algaworks.algafood.api.model.input.CozinhaInput;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.domain.service.CadastroCozinhaService;


@RestController
@RequestMapping(value = "/cozinhas")
public class CozinhaController {

	@Autowired
	private CozinhaRepository cozinhaRepository;
	
	@Autowired
	private CadastroCozinhaService cadastroCozinha;
	
	@Autowired
	private CozinhaModelAssembler cozinhaModelAssembler;

	@Autowired
	private CozinhaInputDisassembler cozinhaInputDisassembler;    
	
	@GetMapping // 13.8. Implementando paginação e ordenação em recursos de coleção da API // Padrao de elementos retornardo é 20
	public Page<CozinhaModel> listar(@PageableDefault(size = 10) Pageable pageable) { 
		
		Page<Cozinha> cozinhasPage = cozinhaRepository.findAll(pageable); 
		
		List<CozinhaModel> cozinhasModel = cozinhaModelAssembler
				.toCollectionModel(cozinhasPage.getContent());
		
		// Para exibir o total de intens, teve que alterar o retorno do metodo de List<CozinhaModel> para Page<CozinhaModel>
		// e acrescentar essa alteração
		Page<CozinhaModel> cozinhasModelPage = new PageImpl<>(cozinhasModel, pageable, 
				cozinhasPage.getTotalElements());
		
		return cozinhasModelPage;
	}
//	@GetMapping
//	public List<CozinhaModel> listar() {
//	    List<Cozinha> todasCozinhas = cozinhaRepository.findAll();
//	    
//	    return cozinhaModelAssembler.toCollectionModel(todasCozinhas);
//	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CozinhaModel adicionar(@RequestBody @Valid CozinhaInput cozinhaInput) {
	    Cozinha cozinha = cozinhaInputDisassembler.toDomainObject(cozinhaInput);
	    cozinha = cadastroCozinha.salvar(cozinha);
	    
	    return cozinhaModelAssembler.toModel(cozinha);
	}

	@PutMapping("/{cozinhaId}")
	public CozinhaModel atualizar(@PathVariable Long cozinhaId, @RequestBody @Valid CozinhaInput cozinhaInput) {
	    Cozinha cozinhaAtual = cadastroCozinha.buscarOuFalhar(cozinhaId);
	    cozinhaInputDisassembler.copyToDomainObject(cozinhaInput, cozinhaAtual);
	    cozinhaAtual = cadastroCozinha.salvar(cozinhaAtual);
	    
	    return cozinhaModelAssembler.toModel(cozinhaAtual);
	}	
//	@PutMapping("/{cozinhaId}")
//	public Cozinha atualizar(@PathVariable Long cozinhaId,	@RequestBody @Valid Cozinha cozinha) {
////	public ResponseEntity<Cozinha> atualizar(@PathVariable Long cozinhaId,	@RequestBody Cozinha cozinha) {
//		
//		Cozinha cozinhaAtual = cadastroCozinha.buscarOuFalhar(cozinhaId);
//		BeanUtils.copyProperties(cozinha, cozinhaAtual, "id");
//		return cadastroCozinha.salvar(cozinhaAtual);
////		Optional<Cozinha> cozinhaAtual = cozinhaRepository.findById(cozinhaId);
////		
////		if (cozinhaAtual.isPresent()) {
////			BeanUtils.copyProperties(cozinha, cozinhaAtual.get(), "id"); // terceiro "id" parametro é ignorado na copia
////			
////			Cozinha cozinhaSalva = cadastroCozinha.salvar(cozinhaAtual.get());
////			return ResponseEntity.ok(cozinhaSalva);
////		}
////		return ResponseEntity.notFound().build();
//	}
	
	@DeleteMapping("/{cozinhaId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long cozinhaId) {
		cadastroCozinha.excluir(cozinhaId);
	}
//	@DeleteMapping("/{cozinhaId}")
//	@ResponseStatus(HttpStatus.NO_CONTENT)
//	public void remover(@PathVariable Long cozinhaId) {
//		try {
//			cadastroCozinha.excluir(cozinhaId);
//		} catch (EntidadeNaoEncontradaException e) {
//			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage()); // Quando nao quer criar novas  exceptions, ainda nao consegue customizar o corpo da resposta
////			throw new ServerWebInputException(e.getMessage());
//		}
//	}
//	@DeleteMapping("/{cozinhaId}")
//	public ResponseEntity<?> remover(@PathVariable Long cozinhaId) {
//		try {
//			cadastroCozinha.excluir(cozinhaId);
//			return ResponseEntity.noContent().build();
//
//		} catch (EntidadeNaoEncontradaException e) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//
//		} catch (EntidadeEmUsoException e) {
//			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
//		}
//	}	
	
	@GetMapping("/{cozinhaId}")
	public CozinhaModel buscar(@PathVariable Long cozinhaId) {
	    Cozinha cozinha = cadastroCozinha.buscarOuFalhar(cozinhaId);
	    
	    return cozinhaModelAssembler.toModel(cozinha);
	}
//	@GetMapping("/{cozinhaId}")
//	public Cozinha buscar(@PathVariable Long cozinhaId) {
////	public ResponseEntity<Cozinha> buscar(@PathVariable Long cozinhaId) {
//		
//		return cadastroCozinha.buscarOuFalhar(cozinhaId);
////		Optional<Cozinha> cozinha = cozinhaRepository.findById(cozinhaId);
////		
////		if (cozinha.isPresent()) {
////			return ResponseEntity.ok(cozinha.get());
////		}
////		
////		return ResponseEntity.notFound().build();
//	}
//	@GetMapping("/{cozinhaId}")
//	public ResponseEntity<Cozinha> buscar(@PathVariable Long cozinhaId) {
//		Cozinha cozinha = cozinhaRepository.buscar(cozinhaId);
//		
////		return ResponseEntity.status(HttpStatus.OK).body(cozinha);
////		return ResponseEntity.ok(cozinha);
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.add(HttpHeaders.LOCATION, "http://localhost:8080/cozinhas");
//		
//		return ResponseEntity
//				.status(HttpStatus.FOUND)
//				.headers(headers)
//				.build();
//	}
	
}
