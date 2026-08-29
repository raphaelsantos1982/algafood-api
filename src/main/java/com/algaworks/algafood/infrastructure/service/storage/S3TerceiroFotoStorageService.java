package com.algaworks.algafood.infrastructure.service.storage;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.algaworks.algafood.core.storage.StorageProperties;
import com.algaworks.algafood.domain.service.FotoStorageService;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3TerceiroFotoStorageService implements FotoStorageService {

    @Autowired
    private StorageProperties storageProperties;

    @Autowired
    @Qualifier("amazonS3Terceiro") // Injeta o bean do AmazonS3 configurado para o S3 novo
    private AmazonS3 amazonS3;

    @Override
    public void armazenar(NovaFoto novaFoto) {
        try {
            String caminhoArquivo = getCaminhoArquivo(
                    novaFoto.getNomeAquivo());

            var objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(
                    novaFoto.getContentType());

            var putObjectRequest = new PutObjectRequest(
                    storageProperties.getS3Terceiro().getBucket(),
                    caminhoArquivo,
                    novaFoto.getInputStream(),
                    objectMetadata);
//                    .withCannedAcl(
//                            CannedAccessControlList.PublicRead);

            amazonS3.putObject(putObjectRequest);

        } catch (Exception e) {
            throw new StorageException(
                    "Não foi possível enviar arquivo para o novo Amazon S3.", e);
        }
    }

    @Override
    public void remover(String nomeArquivo) {
        try {
            String caminhoArquivo = getCaminhoArquivo(nomeArquivo);

            var deleteObjectRequest = new DeleteObjectRequest(
                    storageProperties.getS3Terceiro().getBucket(),
                    caminhoArquivo);

            amazonS3.deleteObject(deleteObjectRequest);

        } catch (Exception e) {
            throw new StorageException(
                    "Não foi possível excluir arquivo no novo Amazon S3.", e);
        }
    }

    @Override
    public FotoRecuperada recuperar(String nomeArquivo) {

        String caminhoArquivo = getCaminhoArquivo(nomeArquivo);

        URL url = amazonS3.getUrl(
                storageProperties.getS3Terceiro().getBucket(),
                caminhoArquivo);

        return FotoRecuperada.builder()
                .url(url.toString())
                .build();
    }

    private String getCaminhoArquivo(String nomeArquivo) {
        return String.format(
                "%s/%s",
                storageProperties.getS3Terceiro().getDiretorioFotos(),
                nomeArquivo);
    }

}