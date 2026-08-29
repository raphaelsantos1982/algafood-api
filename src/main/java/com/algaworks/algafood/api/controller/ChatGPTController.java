package com.algaworks.algafood.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.algaworks.algafood.domain.service.ChatGPTService;

@RestController
@RequestMapping("/api/chat")
public class ChatGPTController {
	
	private static final Logger logger = LoggerFactory.getLogger(ChatGPTController.class);
	
	//NECESSARIO TER CREDITOS NA CONTA DO PERFIL DO OPEN AI PARA FUNCIONAR https://platform.openai.com/settings/organization/billing/overview
	// SE TIVER CREDITOS, TEM QUE GERAR NOVA CHAVE DE API https://platform.openai.com/settings/organization/api-keys
	@Autowired
	private ChatGPTService chatGPTService;

	@PostMapping("/ask")
	public String askChatGPT(@RequestBody String prompt) {
	    logger.info("ChatGPT request received with prompt: {}", prompt);
	    return chatGPTService.getChatGPTResponse(prompt);
	}

}