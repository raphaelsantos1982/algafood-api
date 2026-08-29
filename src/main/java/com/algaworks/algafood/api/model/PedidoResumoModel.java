package com.algaworks.algafood.api.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;

//@JsonFilter("pedidoFilter") //13.2. Limitando os campos retornados pela API com @JsonFilter do Jackson // Indica que esta adicionando um filtro logico ->é usado para o usuario informar quais campos deseja que sejam retornados
@Setter
@Getter
public class PedidoResumoModel {

	private String codigo; // Long id;
	private BigDecimal subtotal;
	private BigDecimal taxaFrete;
	private BigDecimal valorTotal;
	private String status;
	private OffsetDateTime dataCriacao;
	private RestauranteResumoModel restaurante;
	private UsuarioModel cliente;
	
}