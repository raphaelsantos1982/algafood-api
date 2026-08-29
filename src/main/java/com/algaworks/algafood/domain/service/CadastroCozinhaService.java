package com.algaworks.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.exception.CozinhaNaoEncontradaException;
import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.repository.CozinhaRepository;

@Service
public class CadastroCozinhaService {

	@Autowired
	private CozinhaRepository cozinhaRepository;
	
	private static final String MSG_COZINHA_EM_USO 
	= "Cozinha de código %d não pode ser removida, pois está em uso";

	private static final String MSG_COZINHA_NAO_ENCONTRADA 
	= "Não existe um cadastro de cozinha com código %d";
	
	@Transactional
	public Cozinha salvar(Cozinha cozinha) {
		return cozinhaRepository.save(cozinha);
	}
	
	@Transactional
	public void excluir(Long cozinhaId) {
		try {
			cozinhaRepository.deleteById(cozinhaId);
			cozinhaRepository.flush(); //	11.21. Corrigindo bug de tratamento de exception de integridade de dados com flush do JPA
									   // Server pra solucionar: Quando se coloca @Transactional no metodo de serviço o Spring só descarrega do commit do delete ao final do metodo e nao no momento 
									// que roda a linha de delete. Com isso, ja passou pelo tratamento de exceções e mesmo que esteja implementado, nao vai pegar
									// Se o metodo tiver uma consulta logo abaixo, nao precisa dessa linha pq ai JPA vai descarregar antes e depois executar o select
			
		} catch (EmptyResultDataAccessException e) {
			 throw new CozinhaNaoEncontradaException(cozinhaId);
//			throw new EntidadeNaoEncontradaException(
//				String.format(MSG_COZINHA_NAO_ENCONTRADA, cozinhaId));
		
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
				String.format(MSG_COZINHA_EM_USO, cozinhaId));
		}
	}
	
	public Cozinha buscarOuFalhar(Long cozinhaId) {
		
		return cozinhaRepository.findById(cozinhaId)
		        .orElseThrow(() -> new CozinhaNaoEncontradaException(cozinhaId));
//		return cozinhaRepository.findById(cozinhaId)
//			.orElseThrow(() -> new EntidadeNaoEncontradaException(
//					String.format(MSG_COZINHA_NAO_ENCONTRADA, cozinhaId)));
	}
}