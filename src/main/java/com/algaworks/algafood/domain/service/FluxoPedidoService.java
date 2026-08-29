package com.algaworks.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.model.Pedido;
import com.algaworks.algafood.domain.repository.PedidoRepository;

@Service
public class FluxoPedidoService {

	@Autowired
	private EmissaoPedidoService emissaoPedido;
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Transactional
	public void confirmar(String codigoPedido) {  // 12.25. Usando IDs vs UUIDs nas URIs de recursos
		Pedido pedido = emissaoPedido.buscarOuFalhar(codigoPedido);
		pedido.confirmar();
		
		pedidoRepository.save(pedido);
	}
//	@Transactional
//	public void confirmar(Long pedidoId) {
//		Pedido pedido = emissaoPedido.buscarOuFalhar(pedidoId);
//		pedido.confirmar();
//	}
	
	@Transactional
	public void cancelar(String codigoPedido) {  // 12.25. Usando IDs vs UUIDs nas URIs de recursos
		Pedido pedido = emissaoPedido.buscarOuFalhar(codigoPedido);
		pedido.cancelar();
		
		pedidoRepository.save(pedido);
	}
//	@Transactional
//	public void cancelar(Long pedidoId) {
//		Pedido pedido = emissaoPedido.buscarOuFalhar(pedidoId);
//		pedido.cancelar();
//	}
	
	@Transactional
	public void entregar(String codigoPedido) {  // 12.25. Usando IDs vs UUIDs nas URIs de recursos
		Pedido pedido = emissaoPedido.buscarOuFalhar(codigoPedido);
		pedido.entregar();
	}
//	@Transactional
//	public void entregar(Long pedidoId) {
//		Pedido pedido = emissaoPedido.buscarOuFalhar(pedidoId);
//		pedido.entregar();
//	}
	
}