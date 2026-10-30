package com.algaworks.algafood.api.v1.model;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Relation(collectionRelation = "usuarios") // Define o nome da coleção de recursos para a representação HATEOAS
@Setter
@Getter
public class UsuarioModel extends RepresentationModel<UsuarioModel> {

	@ApiModelProperty(example = "1")
    private Long id;
	
	@ApiModelProperty(example = "João da Silva")
    private String nome;
	
	@ApiModelProperty(example = "joao.ger@algafood.com.br")
    private String email;            
}