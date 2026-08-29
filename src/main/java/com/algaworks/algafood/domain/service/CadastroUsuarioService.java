package com.algaworks.algafood.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.exception.NegocioException;
import com.algaworks.algafood.domain.exception.UsuarioNaoEncontradoException;
import com.algaworks.algafood.domain.model.Grupo;
import com.algaworks.algafood.domain.model.Usuario;
import com.algaworks.algafood.domain.repository.UsuarioRepository;

@Service
public class CadastroUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private CadastroGrupoService cadastroGrupo;
    
    @Transactional
    public Usuario salvar(Usuario usuario) {
    	
    	// Antes de chamar a consulta findByEmail(usuario.getEmail()), o JPA sincrozina, logo ele faz um update 
    	// com isso se eu passar um usuario novo com um email que ja existe em outro usuario, ja da erro na consulta pq 
    	// o metodo de consulta findByEmail(usuario.getEmail() esta esperando retornar 1 registro, mas acaba tendo 2.
    	// com isso, nem chega no tratamento de erro feito, estoura erro na execução da consulta
    	// pra resover, executa detach(usuario) pra tirar a instancia do objeto usuario passado do contexto de persistencia do JPA, ou seja, deixa de ser gerenciado
    	usuarioRepository.detach(usuario); // desconectar a instancia do moedlo de persistencia 
		Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(usuario.getEmail());
		
		// Como o metodo salvar esta sendo usando pra update tambem, tem que validar se alem de existir um email igual cadastrado, 
		// o usuario retornado na busca nao é o mesmo passado no objeto. Se nao for o mesmo, é insert e estaria inserindo novo usuario 
		// com o email ja cadastrado
		if (usuarioExistente.isPresent() && !usuarioExistente.get().equals(usuario)) {
			throw new NegocioException(
					String.format("Já existe um usuário cadastrado com o e-mail %s", usuario.getEmail()));
		}
		
		return usuarioRepository.save(usuario);
    }
    
    @Transactional
    public void alterarSenha(Long usuarioId, String senhaAtual, String novaSenha) {
        Usuario usuario = buscarOuFalhar(usuarioId);
        
        if (usuario.senhaNaoCoincideCom(senhaAtual)) {
            throw new NegocioException("Senha atual informada não coincide com a senha do usuário.");
        }
        
        usuario.setSenha(novaSenha);
    }

    public Usuario buscarOuFalhar(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));
    }    
    @Transactional
    public void desassociarGrupo(Long usuarioId, Long grupoId) {
        Usuario usuario = buscarOuFalhar(usuarioId);
        Grupo grupo = cadastroGrupo.buscarOuFalhar(grupoId);
        
        usuario.removerGrupo(grupo);
    }

    @Transactional
    public void associarGrupo(Long usuarioId, Long grupoId) {
        Usuario usuario = buscarOuFalhar(usuarioId);
        Grupo grupo = cadastroGrupo.buscarOuFalhar(grupoId);
        
        usuario.adicionarGrupo(grupo);
    }
} 