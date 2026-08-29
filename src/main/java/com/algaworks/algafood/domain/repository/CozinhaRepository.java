package com.algaworks.algafood.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.algaworks.algafood.domain.model.Cozinha;

@Repository
public interface CozinhaRepository  extends JpaRepository<Cozinha, Long>{

	List<Cozinha> findByNome(String nome);
//	List<Cozinha> listar();
//	Cozinha buscar(Long id);
//	Cozinha salvar(Cozinha cozinha);
//	void remover(Long id);
	
}
