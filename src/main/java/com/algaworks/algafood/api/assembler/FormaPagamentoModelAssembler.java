package com.algaworks.algafood.api.assembler;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.algaworks.algafood.api.AlgaLinks;
import com.algaworks.algafood.api.controller.FormaPagamentoController;
import com.algaworks.algafood.api.model.FormaPagamentoModel;
import com.algaworks.algafood.domain.model.FormaPagamento;

@Component
public class FormaPagamentoModelAssembler 
			extends RepresentationModelAssemblerSupport<FormaPagamento, FormaPagamentoModel> { // RepresentationModelAssemblerSupport é uma classe abstrata do Spring HATEOAS que fornece métodos para criar modelos de representação (DTOs) a partir de entidades do domínio. Ela facilita a criação de links HATEOAS e a conversão de entidades em modelos de representação.

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
    private AlgaLinks algaLinks;
    
    public FormaPagamentoModelAssembler() {
        super(FormaPagamentoController.class, FormaPagamentoModel.class);
    }
	
    @Override
    public FormaPagamentoModel toModel(FormaPagamento formaPagamento) {
        FormaPagamentoModel formaPagamentoModel = 
                createModelWithId(formaPagamento.getId(), formaPagamento);
        
        modelMapper.map(formaPagamento, formaPagamentoModel);
        
        formaPagamentoModel.add(algaLinks.linkToFormasPagamento("formasPagamento"));
        
        return formaPagamentoModel;
    }
    
    @Override
    public CollectionModel<FormaPagamentoModel> toCollectionModel(Iterable<? extends FormaPagamento> entities) {
        return super.toCollectionModel(entities)
            .add(algaLinks.linkToFormasPagamento());
    }   
//	public List<FormaPagamentoModel> toCollectionModel(Collection<FormaPagamento> formasPagamentos) {
//		return formasPagamentos.stream()
//				.map(formaPagamento -> toModel(formaPagamento))
//				.collect(Collectors.toList());
//	}
	
}
