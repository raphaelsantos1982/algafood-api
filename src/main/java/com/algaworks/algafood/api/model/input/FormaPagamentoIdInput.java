package com.algaworks.algafood.api.model.input;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FormaPagamentoIdInput {

	@ApiModelProperty(example = "Cartão de crédito", required = true)
    @NotNull
    private Long id;            
} 
