package com.algaworks.algafood.core.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.algaworks.algafood.core.storage.StorageProperties.TipoStorage;
import com.algaworks.algafood.domain.service.FotoStorageService;
import com.algaworks.algafood.infrastructure.service.storage.LocalFotoStorageService;
import com.algaworks.algafood.infrastructure.service.storage.S3FotoStorageService;
import com.algaworks.algafood.infrastructure.service.storage.S3NovoFotoStorageService;
import com.algaworks.algafood.infrastructure.service.storage.S3TerceiroFotoStorageService;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class StorageConfig {

	@Autowired
	private StorageProperties storageProperties;
	
	@Bean
	public AmazonS3 amazonS3() { //cria um bean do tipo AmazonS3 para ser injetado em outras classes
		var credentials = new BasicAWSCredentials(
				storageProperties.getS3().getIdChaveAcesso(), 
				storageProperties.getS3().getChaveAcessoSecreta());
		
		return AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(storageProperties.getS3().getRegiao())
				.build(); //cria um cliente do AmazonS3 com as credenciais e região configuradas
	}
	
	@Bean
    public AmazonS3 amazonS3Novo() {

        var credentials = new BasicAWSCredentials(
                storageProperties.getS3Novo().getIdChaveAcesso(),
                storageProperties.getS3Novo().getChaveAcessoSecreta());

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(storageProperties.getS3Novo().getRegiao())
                .build();
    }
	
	@Bean
    public AmazonS3 amazonS3Terceiro() {

        var credentials = new BasicAWSCredentials(
                storageProperties.getS3Terceiro().getIdChaveAcesso(),
                storageProperties.getS3Terceiro().getChaveAcessoSecreta());

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(storageProperties.getS3Terceiro().getRegiao())
                .build(); //cria um cliente do AmazonS3 com as credenciais e região configuradas para o S3 novo
    }
	
	// Bean com nome explícito para o S3 padrão
	@Bean(name = "s3FotoStorageService")
	public FotoStorageService s3FotoStorageService() {
		return new S3FotoStorageService();
	}
	
	// Bean com nome explícito para o S3 novo
	@Bean(name = "s3NovoFotoStorageService")
	public FotoStorageService s3NovoFotoStorageService() {
		return new S3NovoFotoStorageService();
	}
	
	// Bean com nome explícito para o S3 terceiro
	@Bean(name = "s3TerceiroFotoStorageService")
	public FotoStorageService s3TerceiroFotoStorageService() {
		return new S3TerceiroFotoStorageService();
	}
	
	// Bean para Local Storage (opcional, mantém compatibilidade)
	@Bean(name = "localFotoStorageService")
	public FotoStorageService localFotoStorageService() {
		return new LocalFotoStorageService();
	}
	
	// Bean padrão baseado na propriedade (mantém a lógica do curso funcional)
	@Bean
	@Primary  // ← ISSO RESOLVE O ERRO!
	public FotoStorageService fotoStorageService() {
		if (TipoStorage.S3.equals(storageProperties.getTipo())) {
			return new S3FotoStorageService();
		}
		if (TipoStorage.S3_NOVO.equals(storageProperties.getTipo())) {
			return new S3NovoFotoStorageService();
		}
		if (TipoStorage.S3_TERCEIRO.equals(storageProperties.getTipo())) {
		    return new S3TerceiroFotoStorageService();
		}
		return new LocalFotoStorageService();
	}
}