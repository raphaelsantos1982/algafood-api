package com.algaworks.algafood.domain.exception;

// anotação cmentada pq foi criado um metodo no a ApiExceptioonHandler pra tratar exceptions doo tipo EntidadeNaoEncontradaException @ResponseStatus(HttpStatus.NOT_FOUND) // Se essa exceção foi lançada e nao for tratada, o retorno vai ser o 404 NOT FOUND // Apenas com essa anotação nao da mais pra customizar o corpo da resposta, fica com timestamp/status/trace/message
//@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Entidade não encontrada") 
public class EntidadeNaoEncontradaException extends NegocioException {  // extends RuntimeException  { //ResponseStatusException{ // Extendendo a classe ResponseStatusException que é possivel customizar nao so a msg mas tb status

	private static final long serialVersionUID = 1L;
	
	
//	public EntidadeNaoEncontradaException(HttpStatus status, String mensagem) {
//		super(status, mensagem);
//	}

	public EntidadeNaoEncontradaException(String mensagem) {
		super(mensagem);
//		this(HttpStatus.NOT_FOUND, mensagem);
	}
}