package com.algaworks.algafood.domain.exception;

// anotação comentada pq foi criado um metodo no a ApiExceptioonHandler pra tratar exceptions doo tipo EntidadeEmUsoException @ResponseStatus(HttpStatus.CONFLICT)
public class EntidadeEmUsoException extends NegocioException{ //extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public EntidadeEmUsoException(String mensagem) {
		super(mensagem);
	}
}