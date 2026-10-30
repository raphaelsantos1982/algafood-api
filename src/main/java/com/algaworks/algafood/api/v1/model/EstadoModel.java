package com.algaworks.algafood.api.v1.model;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Relation(collectionRelation = "estados")
@Setter
@Getter
public class EstadoModel extends RepresentationModel<CidadeModel> { // RepresentationModel é uma classe do Spring HATEOAS que permite adicionar links de hipertexto aos recursos representados por essa classe. Isso é útil para criar APIs RESTful que seguem o princípio HATEOAS (Hypermedia as the Engine of Application State), permitindo que os clientes naveguem pelos recursos da API de forma mais intuitiva.

	@ApiModelProperty(example = "1")
	private Long id;
	
	@ApiModelProperty(example = "Minas Gerais")
	private String nome;
	
}