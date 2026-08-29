package com.algaworks.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.exception.CidadeNaoEncontradaException;
import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.model.Cidade;
import com.algaworks.algafood.domain.model.Estado;
import com.algaworks.algafood.domain.repository.CidadeRepository;

@Service
public class CadastroCidadeService {
	
	private static final String MSG_CIDADE_EM_USO 
		= "Cidade de código %d não pode ser removida, pois está em uso";

	private static final String MSG_CIDADE_NAO_ENCONTRADA 
		= "Não existe um cadastro de cidade com código %d";

	@Autowired
	private CidadeRepository cidadeRepository;
	
//	@Autowired
//	private EstadoRepository estadoRepository;
	
	@Autowired
	private CadastroEstadoService cadastroEstado;
	
	@Transactional
	public Cidade salvar(Cidade cidade) {
		Long estadoId = cidade.getEstado().getId();

		Estado estado = cadastroEstado.buscarOuFalhar(estadoId);
		
//		Estado estado = estadoRepository.findById(estadoId)
//			.orElseThrow(() -> new EntidadeNaoEncontradaException(
//					String.format("Não existe cadastro de estado com código %d", estadoId)));
		
		cidade.setEstado(estado);
		
		return cidadeRepository.save(cidade);
	}
	
	@Transactional
	public void excluir(Long cidadeId) {
		try {
			cidadeRepository.deleteById(cidadeId);
			cidadeRepository.flush(); // 11.21. Corrigindo bug de tratamento de exception de integridade de dados com flush do JPA
									  // Server pra solucionar: Quando se coloca @Transactional no metodo de serviço o Spring JPA só descarrega do commit do delete ao final do metodo e nao no momento 
									  // que roda a linha de delete. Com isso, ja passou pelo tratamento de exceções e mesmo que esteja implementado, nao vai pegar
									  // Se o metodo tiver uma consulta logo abaixo, nao precisa dessa linha pq ai JPA vai descarregar antes e depois executar o select

			
		} catch (EmptyResultDataAccessException e) {
			 throw new CidadeNaoEncontradaException(cidadeId);
//			throw new EntidadeNaoEncontradaException(
//				String.format(MSG_CIDADE_NAO_ENCONTRADA, cidadeId));
		
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
				String.format(MSG_CIDADE_EM_USO, cidadeId));
		}
	}
	
	public Cidade buscarOuFalhar(Long cidadeId) {
		
		return cidadeRepository.findById(cidadeId)
		        .orElseThrow(() -> new CidadeNaoEncontradaException(cidadeId));
//		return cidadeRepository.findById(cidadeId)
//			.orElseThrow(() -> new EntidadeNaoEncontradaException(
//					String.format(MSG_CIDADE_NAO_ENCONTRADA, cidadeId)));
	}
	
}