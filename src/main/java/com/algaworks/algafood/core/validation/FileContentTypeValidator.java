package com.algaworks.algafood.core.validation;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

public class FileContentTypeValidator implements ConstraintValidator<FileContentType, MultipartFile> {

	private List<String> allowedContentTypes;
	
	@Override
	public void initialize(FileContentType constraint) { // Método chamado quando a anotação é inicializada, recebendo a instância da anotação
		this.allowedContentTypes = Arrays.asList(constraint.allowed()); // Converte o array de tipos de conteúdo permitidos em uma lista
	}
	
	@Override
	public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
		return multipartFile == null 
				|| this.allowedContentTypes.contains(multipartFile.getContentType());
	}

}