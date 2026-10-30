package com.algaworks.algafood.api.v1.assembler;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.algaworks.algafood.api.v1.AlgaLinks;
import com.algaworks.algafood.api.v1.controller.CozinhaController;
import com.algaworks.algafood.api.v1.model.CozinhaModel;
import com.algaworks.algafood.domain.model.Cozinha;

@Component
public class CozinhaModelAssembler
		extends RepresentationModelAssemblerSupport<Cozinha, CozinhaModel> { // RepresentationModelAssemblerSupport serve para criar links HATEOAS para os recursos representados pelos modelos

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private AlgaLinks algaLinks;
	
	public CozinhaModelAssembler() {
		super(CozinhaController.class, CozinhaModel.class);
	}
	
	@Override
	public CozinhaModel toModel(Cozinha cozinha) {
		
		CozinhaModel cozinhaModel = createModelWithId(cozinha.getId(), cozinha); // createModelWithId cria um link para o recurso representado pelo modelo, usando o ID do recurso e a classe do controlador que lida com ele
		modelMapper.map(cozinha, cozinhaModel);
		    
	    cozinhaModel.add(algaLinks.linkToCozinhas("cozinhas"));
		    
	    return cozinhaModel;
//		return modelMapper.map(cozinha, CozinhaModel.class);
	}
	
//	public List<CozinhaModel> toCollectionModel(List<Cozinha> cozinhas) {
//		return cozinhas.stream()
//				.map(cozinha -> toModel(cozinha))
//				.collect(Collectors.toList());
//	}
	
}