package com.algaworks.algafood.api.model;


import org.springframework.hateoas.RepresentationModel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CidadeModel extends RepresentationModel<CidadeModel> { // RepresentationModel é uma classe do Spring HATEOAS que permite adicionar links de hipertexto aos recursos representados por essa classe. Isso é útil para criar APIs RESTful que seguem o princípio HATEOAS (Hypermedia as the Engine of Application State), permitindo que os clientes naveguem pelos recursos da API de forma mais intuitiva.


//	@ApiModelProperty(value = "ID da cidade", example = "1")
	@ApiModelProperty(example = "1")
	private Long id;
	private String nome;
	private EstadoModel estado;
	
}