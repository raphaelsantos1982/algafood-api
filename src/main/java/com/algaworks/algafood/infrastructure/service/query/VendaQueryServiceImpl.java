package com.algaworks.algafood.infrastructure.service.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;

import org.springframework.stereotype.Repository;

import com.algaworks.algafood.domain.filter.VendaDiariaFilter;
import com.algaworks.algafood.domain.model.Pedido;
import com.algaworks.algafood.domain.model.StatusPedido;
import com.algaworks.algafood.domain.model.dto.VendaDiaria;
import com.algaworks.algafood.domain.service.VendaQueryService;

@Repository
public class VendaQueryServiceImpl implements VendaQueryService {

	@PersistenceContext
	private EntityManager manager;
	
	@Override
	public List<VendaDiaria> consultarVendasDiarias(VendaDiariaFilter filtro, String timeOffset) {
		
		// 13.14. Implementando consulta com dados agregados de vendas diárias
		//		select date(convert_tz(p.data_criacao, '+00:00', '-03:00')) as data_criacao,
		//		   count(p.id) as total_vendas,
		//		   sum(p.valor_total) as total_faturado
		//		from algafood.pedido p
		//		where p.status in ('CONFIRMADO','ENTREGUE')
		//		group by date(convert_tz(p.data_criacao, '+00:00', '-03:00'));
		// MONTANDO A QUERY ACIMA USANDO Criteria
		var builder = manager.getCriteriaBuilder();
		var query = builder.createQuery(VendaDiaria.class); // seta o tipo que o  metodo retorna
		var root = query.from(Pedido.class);
		var predicates = new ArrayList<Predicate>();
		
		// "convert_tz" é a função convert_tz da query do mySql: 
		var functionConvertTzDataCriacao = builder.function(
				"convert_tz",  // função do mysql
				Date.class, // tipo de rotorno
				root.get("dataCriacao"), builder.literal("+00:00"), builder.literal(timeOffset)); // parametros da função
		
		// "date" é a função date(date(p.data_criacao)) da query do mySql: 
		var functionDateDataCriacao = builder.function("date", Date.class, functionConvertTzDataCriacao);
		
		var selection = builder.construct(VendaDiaria.class, // informa que o resultado da pesquisa corresponde a um construtor de uma classe(VendaDiaria)
				functionDateDataCriacao,
				builder.count(root.get("id")), // root foi setado sendo o Pedido, logo root.get("id")
				builder.sum(root.get("valorTotal")));
		
		if (filtro.getRestauranteId() != null) {
			predicates.add(builder.equal(root.get("restaurante"), filtro.getRestauranteId()));
		}
	      
		if (filtro.getDataCriacaoInicio() != null) {
			predicates.add(builder.greaterThanOrEqualTo(root.get("dataCriacao"), filtro.getDataCriacaoInicio()));
		}

		if (filtro.getDataCriacaoFim() != null) {
			predicates.add(builder.lessThanOrEqualTo(root.get("dataCriacao"), filtro.getDataCriacaoFim()));
		}
	      
		predicates.add(root.get("status").in(
				StatusPedido.CONFIRMADO, StatusPedido.ENTREGUE));
		
		query.select(selection);
		query.where(predicates.toArray(new Predicate[0]));
		query.groupBy(functionDateDataCriacao);
		
		return manager.createQuery(query).getResultList();
	}

}