package com.algaworks.algafood.api.controller;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import com.algaworks.algafood.api.assembler.FormaPagamentoInputDisassembler;
import com.algaworks.algafood.api.assembler.FormaPagamentoModelAssembler;
import com.algaworks.algafood.api.model.FormaPagamentoModel;
import com.algaworks.algafood.api.model.input.FormaPagamentoInput;
import com.algaworks.algafood.domain.model.FormaPagamento;
import com.algaworks.algafood.domain.repository.FormaPagamentoRepository;
import com.algaworks.algafood.domain.service.CadastroFormaPagamentoService;

@RestController
@RequestMapping("/formas-pagamento")
public class FormaPagamentoController {

	@Autowired
	private FormaPagamentoRepository formaPagamentoRepository;
	
	@Autowired
	private CadastroFormaPagamentoService cadastroFormaPagamento;
	
	@Autowired
	private FormaPagamentoModelAssembler formaPagamentoModelAssembler;
	
	@Autowired
	private FormaPagamentoInputDisassembler formaPagamentoInputDisassembler;
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FormaPagamentoModel>> listar(ServletWebRequest request) { // ResponseEntity quando precisa alterar o cabeçalho da resposta HTTP: status code, headers e body.
		
		ShallowEtagHeaderFilter.disableContentCaching(request.getRequest()); // Desabilita o cache(ShallowEtagHeaderFilter configurado no WebConfig.java) de conteúdo para a requisição atual.
		
		String eTag = "0";
		
		OffsetDateTime dataUltimaAtualizacao = formaPagamentoRepository.getDataUltimaAtualizacao();
		
		if (dataUltimaAtualizacao != null) {
			eTag = String.valueOf(dataUltimaAtualizacao.toEpochSecond());
		}
		
		// Se iguais, recurso não foi modificado, retorna um status 304 (Not Modified) para o cliente, evitando o envio desnecessário de dados.
		if (request.checkNotModified(eTag)) { // Pega o if-none-match do cabeçalho HTTP da requisição e compara com o eTag gerado.
			return null;
//			checkNotModified()
//		       │
//		       ├── compara If-None-Match com ETag
//		       │
//		       ├── percebe que são iguais
//		       │
//		       ├── configura HTTP 304
//		       │
//		       └── informa ao Spring que a requisição
//		           já foi tratada
//		           
//		return null
//		       │
//		       └── não existe body para montar
		}
		
		List<FormaPagamento> todasFormasPagamentos = formaPagamentoRepository.findAll();
		
		List<FormaPagamentoModel> formasPagamentosModel = formaPagamentoModelAssembler
				.toCollectionModel(todasFormasPagamentos);
		
		return ResponseEntity.ok()
				.cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS)) // CacheControl.maxAge(10, TimeUnit.SECONDS) define o tempo de vida do cache em 10 segundos. Nao funciona no Postman, mas funciona no navegador.
				.eTag(eTag) // Define o valor do cabeçalho ETag na resposta HTTP. O ETag é um identificador único para a versão atual do recurso. Se o cliente fizer uma solicitação subsequente para o mesmo recurso, ele pode enviar o ETag no cabeçalho "If-None-Match". Se o ETag corresponder ao recurso no servidor, o servidor pode responder com um status 304 (Not Modified), indicando que o recurso não foi alterado desde a última solicitação.
				//.header("ETag", eTag) // outra forma de definir o cabeçalho ETag na resposta HTTP.
				.body(formasPagamentosModel);
	}
//	@GetMapping
//	public List<FormaPagamentoModel> listar() {
//		List<FormaPagamento> todasFormasPagamentos = formaPagamentoRepository.findAll();
//		
//		return formaPagamentoModelAssembler.toCollectionModel(todasFormasPagamentos);
//	}
	
	@GetMapping(value = "/{formaPagamentoId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FormaPagamentoModel> buscar(@PathVariable Long formaPagamentoId, ServletWebRequest request) {
		
		ShallowEtagHeaderFilter.disableContentCaching(request.getRequest());
	    
	    String eTag = "0";
	    
	    OffsetDateTime dataAtualizacao = formaPagamentoRepository.getDataAtualizacaoById(formaPagamentoId);
	    
	    if (dataAtualizacao != null) {
	        eTag = String.valueOf(dataAtualizacao.toEpochSecond());
	    }
	    if (request.checkNotModified(eTag)) {
	        return null;
	    }
	    
	  FormaPagamento formaPagamento = cadastroFormaPagamento.buscarOuFalhar(formaPagamentoId);
	  
	  FormaPagamentoModel formaPagamentoModel =  formaPagamentoModelAssembler.toModel(formaPagamento);
	  
	  return ResponseEntity.ok()
	      .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS)) // define o tempo de vida do cache em 10 segundos. Nao funciona no Postman, mas funciona no navegador.
//			.cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS).cachePrivate())
//			.cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS).cachePublic())
//			.cacheControl(CacheControl.noCache()) // CacheControl.noCache() -> indica que a resposta não deve ser armazenada em cache pelo cliente ou por proxies intermediários. Isso significa que o cliente sempre precisará fazer uma nova solicitação ao servidor para obter a resposta, mesmo que a mesma solicitação tenha sido feita anteriormente. Isso é útil quando a resposta pode mudar com frequência e não deve ser armazenada em cache.
			.cacheControl(CacheControl.noStore()) // CacheControl.noStore() -> Desativa completamente o armazenamento em cache da resposta, tanto no cliente quanto em proxies intermediários. Isso significa que a resposta não será armazenada em cache de forma alguma, e o cliente sempre precisará fazer uma nova solicitação ao servidor para obter a resposta. Isso é útil quando a resposta contém informações sensíveis ou críticas que não devem ser armazenadas em cache.
			.eTag(eTag)
			.body(formaPagamentoModel);
	}
//	@GetMapping("/{formaPagamentoId}")
//	public FormaPagamentoModel buscar(@PathVariable Long formaPagamentoId) {
//		FormaPagamento formaPagamento = cadastroFormaPagamento.buscarOuFalhar(formaPagamentoId);
//		
//		return formaPagamentoModelAssembler.toModel(formaPagamento);
//	}
	
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public FormaPagamentoModel adicionar(@RequestBody @Valid FormaPagamentoInput formaPagamentoInput) {
		FormaPagamento formaPagamento = formaPagamentoInputDisassembler.toDomainObject(formaPagamentoInput);
		
		formaPagamento = cadastroFormaPagamento.salvar(formaPagamento);
		
		return formaPagamentoModelAssembler.toModel(formaPagamento);
	}
	
	@PutMapping(value = "/{formaPagamentoId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public FormaPagamentoModel atualizar(@PathVariable Long formaPagamentoId,
			@RequestBody @Valid FormaPagamentoInput formaPagamentoInput) {
		FormaPagamento formaPagamentoAtual = cadastroFormaPagamento.buscarOuFalhar(formaPagamentoId);
		
		formaPagamentoInputDisassembler.copyToDomainObject(formaPagamentoInput, formaPagamentoAtual);
		
		formaPagamentoAtual = cadastroFormaPagamento.salvar(formaPagamentoAtual);
		
		return formaPagamentoModelAssembler.toModel(formaPagamentoAtual);
	}
	
	@DeleteMapping("/{formaPagamentoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long formaPagamentoId) {
		cadastroFormaPagamento.excluir(formaPagamentoId);	
	}
	
}