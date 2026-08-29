package com.algaworks.algafood.domain.exception;

// anotação comentada pq foi criado um metodo no a ApiExceptionHandler pra tratar exceptions doo tipo NegocioException E@ResponseStatus(HttpStatus.BAD_REQUEST) // Se essa exceção foi lançada e nao for tratada, o retorno vai ser o 404 NOT FOUND // Apenas com essa anotação nao da mais pra customizar o corpo da resposta, fica com timestamp/status/trace/message
//@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Entidade não encontrada") 
public class NegocioException extends RuntimeException  { //ResponseStatusException{ // Extendendo a classe ResponseStatusException que é possivel customizar nao so a msg mas tb status

	private static final long serialVersionUID = 1L;
	
	
//	public EntidadeNaoEncontradaException(HttpStatus status, String mensagem) {
//		super(status, mensagem);
//	}

	public NegocioException(String mensagem) {
		super(mensagem);
//		this(HttpStatus.NOT_FOUND, mensagem);
	}
	
	public NegocioException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
}