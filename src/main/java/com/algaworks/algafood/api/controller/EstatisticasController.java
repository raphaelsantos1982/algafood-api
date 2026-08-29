package com.algaworks.algafood.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.domain.filter.VendaDiariaFilter;
import com.algaworks.algafood.domain.model.dto.VendaDiaria;
import com.algaworks.algafood.domain.service.VendaQueryService;
import com.algaworks.algafood.domain.service.VendaReportService;

// 13.14. Implementando consulta com dados agregados de vendas diárias
@RestController
@RequestMapping(path = "/estatisticas")
public class EstatisticasController {

	@Autowired
	private VendaQueryService vendaQueryService;
	
	@Autowired
	private VendaReportService vendaReportService;
	
	@GetMapping(path = "/vendas-diarias", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<VendaDiaria> consultarVendasDiarias(VendaDiariaFilter filtro,
			@RequestParam(required = false, defaultValue = "+00:00") String timeOffset) {
		return vendaQueryService.consultarVendasDiarias(filtro, timeOffset);
	}
	
	@GetMapping(path = "/vendas-diarias", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> consultarVendasDiariasPdf(VendaDiariaFilter filtro,
			@RequestParam(required = false, defaultValue = "+00:00") String timeOffset) {
		
		byte[] bytesPdf = vendaReportService.emitirVendasDiarias(filtro, timeOffset);
		
		// attachment 					-> indica que o conteudo(bytesPdf) retornado na resposta da requisição é pra ser baixado 
		//									pelo cliente e nao deve ser exibido inline
		// filename=vendas-diarias.pdf" -> nome do arquivo
		var headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vendas-diarias.pdf"); 
		
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_PDF)
				.headers(headers)
				.body(bytesPdf);
	}	
	
}