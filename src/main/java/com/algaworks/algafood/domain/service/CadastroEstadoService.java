package com.algaworks.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.EstadoNaoEncontradoException;
import com.algaworks.algafood.domain.model.Estado;
import com.algaworks.algafood.domain.repository.EstadoRepository;

@Service
public class CadastroEstadoService {

	private static final String MSG_ESTADO_EM_USO 
		= "Estado de código %d não pode ser removido, pois está em uso";

//	private static final String MSG_ESTADO_NAO_ENCONTRADO  // Conteúdo da constante foi para a a exception EstadoNaoEncontradoException, no seu construtor que recebe somente id 
//		= "Não existe um cadastro de estado com código %d";

	@Autowired
	private EstadoRepository estadoRepository;
	
	@Transactional
	public Estado salvar(Estado estado) {
		return estadoRepository.save(estado);
	}
	
	@Transactional
	public void excluir(Long estadoId) {
		try {
			estadoRepository.deleteById(estadoId);
			estadoRepository.flush(); //	11.21. Corrigindo bug de tratamento de exception de integridade de dados com flush do JPA
									   // Server pra solucionar: Quando se coloca @Transactional no metodo de serviço o Spring só descarrega do commit do delete ao final do metodo e nao no momento 
									  // que roda a linha de delete. Com isso, ja passou pelo tratamento de exceções e mesmo que esteja implementado, nao vai pegar
									 // Se o metodo tiver uma consulta logo abaixo, nao precisa dessa linha pq ai JPA vai descarregar antes e depois executar o select
			
		} catch (EmptyResultDataAccessException e) {
			throw new EstadoNaoEncontradoException(estadoId);
//			throw new EntidadeNaoEncontradaException(
//				String.format(MSG_ESTADO_NAO_ENCONTRADO, estadoId));
		
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
				String.format(MSG_ESTADO_EM_USO, estadoId));
		}
	}
	
	public Estado buscarOuFalhar(Long estadoId) {
		return estadoRepository.findById(estadoId)
				.orElseThrow(() -> new EstadoNaoEncontradoException(estadoId));
//			.orElseThrow(() -> new EntidadeNaoEncontradaException(
//					String.format(MSG_ESTADO_NAO_ENCONTRADO, estadoId)));
	}
	
}