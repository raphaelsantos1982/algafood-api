package com.algaworks.algafood.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.algaworks.algafood.core.validation.ValorZeroIncluiDescricao;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

// Verifica se taxaFrete = 0, se for, verifica se nome possui no conteudo [Frete Grátis]
@ValorZeroIncluiDescricao(valorField = "taxaFrete", 	// "taxaFrete" nome do campo dessa classe que eu quero checar o valor
	descricaoField = "nome", descricaoObrigatoria = "Frete Grátis") // "nome" nome do campo dessa classe que eu vou verificar dependendo do valor de "taxaFrete"
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Restaurante {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
//	@NotBlank
	@Column(nullable = false)
	private String nome;
	
//	@NotNull
//	@TaxaFrete // APENAS DIDATICO
//	@Multiplo(numero = 5)  // APENAS DIDATICO
//	@PositiveOrZero
	@Column(name = "taxa_frete", nullable = false)
	private BigDecimal taxaFrete;
	
//	@JsonIgnoreProperties(value = "nome", allowGetters = true) // na hora de serializar ou deserializar Restaurante, ignora atributo nome da Cozinha. com allowGetters = true ignora apenas na deserializacao(json para objeto)
//	@JsonIgnoreProperties("hibernateLazyInitializer") // Ignora propriedades da instancia atribuida da variavel cozinha
//	@ManyToOne(fetch = FetchType.LAZY) // pra usar sem comentar (fetch = FetchType.LAZY) tem que descomentar JsonIgnoreProperties("hibernateLazyInitializer") 
//	@JsonIgnore
//	@Valid
//	@ConvertGroup(from = Default.class, to = Groups.CozinhaId.class) // na hora de validar a cozinha converta grupo para Groups.CozinhaId. 
//	@NotNull
	@ManyToOne //(fetch = FetchType.LAZY) = pra usar sem precisar do @JsonIgnoreProperties("hibernateLazyInitializer")
	@JoinColumn(name = "cozinha_id", nullable = false)
	private Cozinha cozinha;
	
	@Embedded // não é uma coluna, é incorporada, essa classe é uma parte de restaurante
	private Endereco endereco;
	
	private Boolean ativo = Boolean.TRUE;
	
	private Boolean aberto = Boolean.FALSE;
	
	@CreationTimestamp
	@Column(nullable = false, columnDefinition = "datetime") // columnDefinition = "datetime" -> Cria sem a precisão de milisegundos
	private OffsetDateTime dataCadastro;
	
	@UpdateTimestamp
	@Column(nullable = false, columnDefinition = "datetime") // columnDefinition = "datetime" -> Cria sem a precisão de milisegundos
	private OffsetDateTime dataAtualizacao;
	
	@ManyToMany
	@JoinTable(name = "restaurante_forma_pagamento",                    // Customizando nome da tabela intermediaria e colunas
	joinColumns = @JoinColumn(name = "restaurante_id"),                // @JoinColumn - Define qual nome da coluna da tabela intermediaria que associa a tabela restaurante(no caso prorpia classe que esta mapeando)
	inverseJoinColumns = @JoinColumn(name = "forma_pagamento_id"))  
	private Set<FormaPagamento> formasPagamento = new HashSet<>(); // ALterado pra Set pq né um conjunto que nao aceito dados duplicados, logo, se tentar associar uma forma de pagamento que ja esta associado, nao da erro de chave duplicada: Duplicate entry '2-1' for key 'PRIMARY'
	
	@ManyToMany
	@JoinTable(name = "restaurante_usuario_responsavel",
	        joinColumns = @JoinColumn(name = "restaurante_id"),
	        inverseJoinColumns = @JoinColumn(name = "usuario_id"))
	private Set<Usuario> responsaveis = new HashSet<>();      
	
	@OneToMany(mappedBy = "restaurante")
	private List<Produto> produtos = new ArrayList<>(); 
	
	public void ativar() {
		setAtivo(true);
	}
	
	public void inativar() {
		setAtivo(false);
	}
	
	public void abrir() {
	    setAberto(true);
	}

	public void fechar() {
	    setAberto(false);
	}  
	
	public boolean removerResponsavel(Usuario usuario) {
	    return getResponsaveis().remove(usuario);
	}

	public boolean adicionarResponsavel(Usuario usuario) {
	    return getResponsaveis().add(usuario);
	}
	
	public boolean aceitaFormaPagamento(FormaPagamento formaPagamento) {
	    return getFormasPagamento().contains(formaPagamento);
	}

	public boolean naoAceitaFormaPagamento(FormaPagamento formaPagamento) {
	    return !aceitaFormaPagamento(formaPagamento);
	}
	
	public boolean removerFormaPagamento(FormaPagamento formaPagamento) {
		return getFormasPagamento().remove(formaPagamento);
	}
	
	public boolean adicionarFormaPagamento(FormaPagamento formaPagamento) {
		return getFormasPagamento().add(formaPagamento);
	}
}