package com.algaworks.algafood.api.controller;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.api.assembler.RestauranteInputDisassembler;
import com.algaworks.algafood.api.assembler.RestauranteModelAssembler;
import com.algaworks.algafood.api.model.RestauranteModel;
import com.algaworks.algafood.api.model.input.RestauranteInput;
import com.algaworks.algafood.api.model.view.RestauranteView;
import com.algaworks.algafood.core.validation.ValidacaoException;
import com.algaworks.algafood.domain.exception.CidadeNaoEncontradaException;
import com.algaworks.algafood.domain.exception.CozinhaNaoEncontradaException;
import com.algaworks.algafood.domain.exception.NegocioException;
import com.algaworks.algafood.domain.exception.RestauranteNaoEncontradoException;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.RestauranteRepository;
import com.algaworks.algafood.domain.service.CadastroRestauranteService;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

//@CrossOrigin(maxAge = 10)  // 16.4. Habilitando CORS para todos os controladores
@RestController
@RequestMapping(value = "/restaurantes")
public class RestauranteController {

	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private CadastroRestauranteService cadastroRestaurante;
	
	@Autowired
	private RestauranteModelAssembler restauranteModelAssembler;
	
	@Autowired
	private RestauranteInputDisassembler restauranteInputDisassembler;       
	
	
	@Autowired
	private SmartValidator validator;
	
	@JsonView(RestauranteView.Resumo.class)
	@GetMapping
	public List<RestauranteModel> listar() {
		return restauranteModelAssembler.toCollectionModel(restauranteRepository.findAll());
	}
	
	@JsonView(RestauranteView.ApenasNome.class)
	@GetMapping(params = "projecao=apenas-nome")
	public List<RestauranteModel> listarApenasNomes() {
		return listar();
	}
	
//	@GetMapping
//	public MappingJacksonValue listar(@RequestParam(required = false) String projecao) {
//		List<Restaurante> restaurantes = restauranteRepository.findAll();
//		List<RestauranteModel> restaurantesModel = restauranteModelAssembler.toCollectionModel(restaurantes);
//		
//		MappingJacksonValue restaurantesWrapper = new MappingJacksonValue(restaurantesModel);
//		
//		restaurantesWrapper.setSerializationView(RestauranteView.Resumo.class);
//		
//		if ("apenas-nome".equals(projecao)) {
//			restaurantesWrapper.setSerializationView(RestauranteView.ApenasNome.class);
//		} else if ("completo".equals(projecao)) {
//			restaurantesWrapper.setSerializationView(null);
//		}
//		
//		return restaurantesWrapper;
//	}
	
//	@GetMapping
//	public List<RestauranteModel> listar() {
//		return restauranteModelAssembler.toCollectionModel(restauranteRepository.findAll());
//	}
//	
//	@JsonView(RestauranteView.Resumo.class)
//	@GetMapping(params = "projecao=resumo")
//	public List<RestauranteModel> listarResumido() {
//		return listar();
//	}
//
//	@JsonView(RestauranteView.ApenasNome.class)
//	@GetMapping(params = "projecao=apenas-nome")
//	public List<RestauranteModel> listarApenasNomes() {
//		return listar();
//	}
	
	@GetMapping("/{restauranteId}")
	public RestauranteModel buscar(@PathVariable Long restauranteId) {
		
		Restaurante restaurante = cadastroRestaurante.buscarOuFalhar(restauranteId);
		
		return restauranteModelAssembler.toModel(restaurante);
	}
//	@GetMapping("/{restauranteId}")
//	public ResponseEntity<Restaurante> buscar(@PathVariable Long restauranteId) {
//		Optional<Restaurante> restaurante = restauranteRepository.findById(restauranteId);
//		
//		if (restaurante.isPresent()) {
//			return ResponseEntity.ok(restaurante.get());
//		}
//		
//		return ResponseEntity.notFound().build();
//	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RestauranteModel adicionar(@RequestBody @Valid RestauranteInput restauranteInput) {
		
		try {
			
			Restaurante restaurante = restauranteInputDisassembler.toDomainObject(restauranteInput);
			return restauranteModelAssembler.toModel(cadastroRestaurante.salvar(restaurante));
		} catch (CozinhaNaoEncontradaException | CidadeNaoEncontradaException e) {

			throw new NegocioException(e.getMessage());
		}
//	    try {
//	        return cadastroRestaurante.salvar(restaurante);
//	    } catch (EntidadeNaoEncontradaException e) {
//	        throw new NegocioException(e.getMessage());
//	    }
	}
//	@PostMapping
//	public ResponseEntity<?> adicionar(@RequestBody Restaurante restaurante) {
//		try {
//			restaurante = cadastroRestaurante.salvar(restaurante);
//			
//			return ResponseEntity.status(HttpStatus.CREATED)
//					.body(restaurante);
//		} catch (EntidadeNaoEncontradaException e) {
//			return ResponseEntity.badRequest()
//					.body(e.getMessage());
//		}
//	}
	
	@PutMapping("/{restauranteId}")
	public RestauranteModel atualizar(@PathVariable Long restauranteId, @RequestBody @Valid  RestauranteInput restauranteInput) {
		
		try {
			Restaurante restauranteAtual = cadastroRestaurante.buscarOuFalhar(restauranteId);
			
			restauranteInputDisassembler.copyToDomainObject(restauranteInput, restauranteAtual);
			
			return restauranteModelAssembler.toModel(cadastroRestaurante.salvar(restauranteAtual));
		} catch (CozinhaNaoEncontradaException | CidadeNaoEncontradaException e) {

			throw new NegocioException(e.getMessage());
		}
//	    Restaurante restauranteAtual = cadastroRestaurante.buscarOuFalhar(restauranteId);
//	    
//	    BeanUtils.copyProperties(restaurante, restauranteAtual, 
//	            "id", "formasPagamento", "endereco", "dataCadastro", "produtos"); // copia tudo menos parametro id, dataCadastro, forma de pagamento, endereco e produtos, evita copiar o array de forma de pagemtno vazio  e assim deletar o que tinha no banco
//	    
//	    try {
//	        return cadastroRestaurante.salvar(restauranteAtual);
//	    } catch (EntidadeNaoEncontradaException e) {
//	        throw new NegocioException(e.getMessage());
//	    }
	}
//	@PutMapping("/{restauranteId}")
//	public ResponseEntity<?> atualizar(@PathVariable Long restauranteId,
//			@RequestBody Restaurante restaurante) {
//		try {
//			Restaurante restauranteAtual = restauranteRepository
//					.findById(restauranteId).orElse(null);
//			
//			if (restauranteAtual != null) {
//				BeanUtils.copyProperties(restaurante, restauranteAtual, "id", "formasPagamento", "endereco", "dataCadastro", "produtos"); // copia tudo menos parametro id, dataCadastro, forma de pagamento, endereco e produtos, evita copiar o array de forma de pagemtno vazio  e assim deletar o que tinha no banco
//				
//				restauranteAtual = cadastroRestaurante.salvar(restauranteAtual);
//				return ResponseEntity.ok(restauranteAtual);
//			}
//			
//			return ResponseEntity.notFound().build();
//		
//		} catch (EntidadeNaoEncontradaException e) {
//			return ResponseEntity.badRequest()
//					.body(e.getMessage());
//		}
//	}
	
	@PatchMapping("/{restauranteId}")
	public RestauranteModel atualizarParcial(@PathVariable Long restauranteId,
			@RequestBody Map<String, Object> campos, HttpServletRequest request) {
		
	    Restaurante restauranteAtual = cadastroRestaurante.buscarOuFalhar(restauranteId);
	    
	    merge(campos, restauranteAtual, request);
	    
	    validate(restauranteAtual, "restaurante");
	    
	    
	    return null; // TODO VER DEPOIS atualizar(restauranteId, restauranteAtual);
	}
//	@PatchMapping("/{restauranteId}")
//	public ResponseEntity<?> atualizarParcial(@PathVariable Long restauranteId,
//			@RequestBody Map<String, Object> campos) {
//		Restaurante restauranteAtual = restauranteRepository
//				.findById(restauranteId).orElse(null);
//		
//		if (restauranteAtual == null) {
//			return ResponseEntity.notFound().build();
//		}
//		
//		merge(campos, restauranteAtual);
//		
//		return atualizar(restauranteId, restauranteAtual);
//	}
	
	private void validate(Restaurante restaurante, String objectName) {
		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(restaurante, objectName);
		validator.validate(restaurante, bindingResult);
		
		if (bindingResult.hasErrors()) {
			throw new ValidacaoException(bindingResult);
		}
	}

	private void merge(Map<String, Object> dadosOrigem, Restaurante restauranteDestino, HttpServletRequest request) {
		
		ServletServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(request);

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true); // Quero que falhe quando a propriedade esteja sendo ignorada(@JsonIgnore)
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true); // Quero que falhe quando a propriedade nao exista
			
			Restaurante restauranteOrigem = objectMapper.convertValue(dadosOrigem, Restaurante.class);
			
			dadosOrigem.forEach((nomePropriedade, valorPropriedade) -> {
				Field field = ReflectionUtils.findField(Restaurante.class, nomePropriedade);
				field.setAccessible(true);
				
				Object novoValor = ReflectionUtils.getField(field, restauranteOrigem);
				
	//			System.out.println(nomePropriedade + " = " + valorPropriedade + " = " + novoValor);
				
				ReflectionUtils.setField(field, restauranteDestino, novoValor);
			});
		} catch (IllegalArgumentException e) {
			Throwable rootCause = ExceptionUtils.getRootCause(e);
			throw new HttpMessageNotReadableException(e.getMessage(), rootCause, serverHttpRequest);
		}
	}
	
	@PutMapping("/{restauranteId}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void ativar(@PathVariable Long restauranteId) {
		cadastroRestaurante.ativar(restauranteId);
	}
	
	@DeleteMapping("/{restauranteId}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void inativar(@PathVariable Long restauranteId) {
		cadastroRestaurante.inativar(restauranteId);
	}
	
	@PutMapping("/ativacoes")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void ativarMultiplos(@RequestBody List<Long> restauranteIds) {
		try {
			cadastroRestaurante.ativar(restauranteIds);
		} catch (RestauranteNaoEncontradoException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}
	
	@DeleteMapping("/ativacoes")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void inativarMultiplos(@RequestBody List<Long> restauranteIds) {
		try {
			cadastroRestaurante.inativar(restauranteIds);
		} catch (RestauranteNaoEncontradoException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}
	
	@PutMapping("/{restauranteId}/abertura")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void abrir(@PathVariable Long restauranteId) {
	    cadastroRestaurante.abrir(restauranteId);
	}

	@PutMapping("/{restauranteId}/fechamento")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void fechar(@PathVariable Long restauranteId) {
	    cadastroRestaurante.fechar(restauranteId);
	}
	
}