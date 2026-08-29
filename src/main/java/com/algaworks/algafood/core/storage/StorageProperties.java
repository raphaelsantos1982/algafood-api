package com.algaworks.algafood.core.storage;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.amazonaws.regions.Regions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties("algafood.storage")
public class StorageProperties {

	private Local local = new Local();
	private S3 s3 = new S3();
	private S3Novo s3Novo = new S3Novo();
	private S3Terceiro s3Terceiro = new S3Terceiro();

	private TipoStorage tipo = TipoStorage.LOCAL; //define o tipo de storage a ser utilizado, por padrão será o local
	
	public enum TipoStorage {
		LOCAL, S3, S3_NOVO, S3_TERCEIRO
	}

	@Getter
	@Setter
	public class Local {
		
		private Path diretorioFotos;
		
	}
	
	@Getter
	@Setter
	public class S3 {
		
		private String idChaveAcesso;
		private String chaveAcessoSecreta;
		private String bucket;
		private Regions regiao; //utilizando a classe Regions do SDK da AWS para definir a região do bucket
		private String diretorioFotos;
		
	}
	
	@Getter
	@Setter
	public class S3Novo {
	    private String idChaveAcesso;
	    private String chaveAcessoSecreta;
	    private String bucket;
	    private Regions regiao;
	    private String diretorioFotos;
	}
	
	@Getter
	@Setter
	public class S3Terceiro {
	    private String idChaveAcesso;
	    private String chaveAcessoSecreta;
	    private String bucket;
	    private Regions regiao;
	    private String diretorioFotos;
	}
	
}