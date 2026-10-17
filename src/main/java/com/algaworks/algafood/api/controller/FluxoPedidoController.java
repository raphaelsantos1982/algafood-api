package com.algaworks.algafood.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.api.openapi.controller.FluxoPedidoControllerOpenApi;
import com.algaworks.algafood.domain.service.FluxoPedidoService;

@RestController
@RequestMapping(value = "/pedidos/{codigoPedido}") // /pedidos/{pedidoId}") -> 12.25. Usando IDs vs UUIDs nas URIs de recursos
public class FluxoPedidoController implements FluxoPedidoControllerOpenApi {

	@Autowired
	private FluxoPedidoService fluxoPedido;
	
	@PutMapping("/confirmacao") // 12.25. Usando IDs vs UUIDs nas URIs de recursos
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> confirmar(@PathVariable String codigoPedido) {
		fluxoPedido.confirmar(codigoPedido);
		
		return ResponseEntity.noContent().build();
	}
//	@PutMapping("/confirmacao")
//	@ResponseStatus(HttpStatus.NO_CONTENT)
//	public void confirmar(@PathVariable Long pedidoId) {
//		fluxoPedido.confirmar(pedidoId);
//	}
	
	@PutMapping("/cancelamento")  // 12.25. Usando IDs vs UUIDs nas URIs de recursos
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> cancelar(@PathVariable String codigoPedido) {
		fluxoPedido.cancelar(codigoPedido);
		
		return ResponseEntity.noContent().build();
	}
//	@PutMapping("/cancelamento")
//	@ResponseStatus(HttpStatus.NO_CONTENT)
//	public void cancelar(@PathVariable Long pedidoId) {
//	    fluxoPedido.cancelar(pedidoId);
//	}

	@PutMapping("/entrega") // 12.25. Usando IDs vs UUIDs nas URIs de recursos
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> entregar(@PathVariable String codigoPedido) {
		fluxoPedido.entregar(codigoPedido);
		
		return ResponseEntity.noContent().build();
	}
//	@PutMapping("/entrega")
//	@ResponseStatus(HttpStatus.NO_CONTENT)
//	public void entregar(@PathVariable Long pedidoId) {
//	    fluxoPedido.entregar(pedidoId);
//	}
	
}