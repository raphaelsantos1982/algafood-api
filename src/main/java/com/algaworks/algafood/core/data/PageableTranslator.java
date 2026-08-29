package com.algaworks.algafood.core.data;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// Classe criada em -> 13.11. Implementando um conversor de propriedades de ordenação
// facilita o usuario passar os nomes de campos mais que ele prefere e o conversor mapeia para o campo corretamente
public class PageableTranslator {

	public static Pageable translate(Pageable pageable, Map<String, String> fieldsMapping) {
		var orders = pageable.getSort().stream()
			.filter(order -> fieldsMapping.containsKey(order.getProperty())) // retorna true ou false. true segue no stream, false sai do stream
			.map(order -> new Sort.Order(order.getDirection(), 
					fieldsMapping.get(order.getProperty()))) // order.getProperty() = client.nome
			.collect(Collectors.toList());
							
		return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by(orders));
	}
	
}