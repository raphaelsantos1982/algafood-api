package com.algaworks.algafood.core.jackson;

import java.io.IOException;

import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.domain.Page;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

//Component Spring que fornece implementação de serializador/deserializador que deve ser registrado no jackson
// Com uma classe criamos  um serializador json para um tipico especifico(nesse caso Page, qaundo usa paginação), 
// com isso podemos customizar e deixar somente as informações(campos) necessarias
@JsonComponent 
public class PageJsonSerializer extends JsonSerializer<Page<?>> { // 13.10. Implementando JsonSerializer para 
																  // customizar representação de paginação


	@Override
	public void serialize(Page<?> page, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		
		gen.writeStartObject();
		
		gen.writeObjectField("content", page.getContent());
		gen.writeNumberField("size", page.getSize());
		gen.writeNumberField("totalElements", page.getTotalElements());
		gen.writeNumberField("totalPages", page.getTotalPages());
		gen.writeNumberField("number", page.getNumber());
		
		gen.writeEndObject();
	}

}