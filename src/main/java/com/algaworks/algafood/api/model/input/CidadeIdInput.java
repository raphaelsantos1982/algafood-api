package com.algaworks.algafood.api.model.input;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CidadeIdInput {

	@ApiModelProperty(example = "1", required = true)
	@NotNull
	private Long id;

	// SE DECOMENTAR, VAI TER QUE ADICIONAR OS CAMPOS 
		//org.springframework.web.bind.MethodArgumentNotValidException: Validation failed for 
		//argument [0] in public com.algaworks.algafood.api.model.PedidoModel 
		//com.algaworks.algafood.api.controller.PedidoController.adicionar(com.algaworks.algafood.api.model.input.PedidoInput): 
		//[Field error in object 'pedidoInput' on field 'enderecoEntrega.cidade.estado': rejected value [null]; 
		//codes [NotNull.pedidoInput.enderecoEntrega.cidade.estado,NotNull.enderecoEntrega.cidade.estado,NotNull.estado,NotNull.
		//       com.algaworks.algafood.api.model.input.EstadoIdInput,NotNull]; 
		//arguments [org.springframework.context.support.DefaultMessageSourceResolvable: 
		//	codes [pedidoInput.enderecoEntrega.cidade.estado,enderecoEntrega.cidade.estado]; arguments [];
		//	default message [enderecoEntrega.cidade.estado]]; default message [não deve ser nulo]] 
//	@Valid
//	@NotNull
//	private EstadoIdInput estado;
}