package com.algaworks.algafood.infrastructure.service.email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.algaworks.algafood.core.email.EmailProperties;
import com.algaworks.algafood.domain.service.EnvioEmailService;

import freemarker.template.Configuration;
import freemarker.template.Template;

//@Service
public class SmtpEnvioEmailService implements EnvioEmailService {

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private EmailProperties emailProperties;
	
	
	@Autowired
	private Configuration freemarkerConfig;
	
	@Override
	public void enviar(Mensagem mensagem) {
		try {
			MimeMessage mimeMessage = criarMimeMessage(mensagem);
	        
	        mailSender.send(mimeMessage);
		} catch (Exception e) {
			throw new EmailException("Não foi possível enviar e-mail", e);
		}
	}
	
	protected MimeMessage criarMimeMessage(Mensagem mensagem) throws MessagingException {
	    String corpo = processarTemplate(mensagem);
	    
	    MimeMessage mimeMessage = mailSender.createMimeMessage();
	    
	    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8"); // Cria o helper para facilitar a configuração da mensagem
	    helper.setFrom(emailProperties.getRemetente());
	    helper.setTo(mensagem.getDestinatarios().toArray(new String[0]));
	    helper.setSubject(mensagem.getAssunto());
	    helper.setText(corpo, true);
	    
	    return mimeMessage;
	}
	
	protected String processarTemplate(Mensagem mensagem) {
		try {
			Template template = freemarkerConfig.getTemplate(mensagem.getCorpo()); // Carrega o template do FreeMarker com base no nome do arquivo especificado na mensagem
			
			return FreeMarkerTemplateUtils.processTemplateIntoString(
					template, mensagem.getVariaveis()); // Processa o template com as variáveis fornecidas na mensagem e retorna o conteúdo resultante como uma string
		} catch (Exception e) {
			throw new EmailException("Não foi possível montar o template do e-mail", e);
		}
	}


}