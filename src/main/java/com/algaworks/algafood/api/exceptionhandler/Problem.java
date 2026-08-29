package com.algaworks.algafood.api.exceptionhandler;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Getter;

@JsonInclude(Include.NON_NULL) // So vai incluir na representação json se o campo NAO for null
@Getter
@Builder
public class Problem {

	// propriedades da especificacao RFC 7807
	private Integer status;
	private String type;
	private String title;
	private String detail;
	
	private String userMessage;
	private OffsetDateTime timestamp;
	
	private List<Object> objects;
	
	@Getter
	@Builder
	public static class Object {
		
		private String name;
		private String userMessage;
		
	}

	
}