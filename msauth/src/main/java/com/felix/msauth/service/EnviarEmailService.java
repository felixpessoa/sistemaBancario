package com.felix.msauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.felix.msauth.dto.auth.request.SignupRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EnviarEmailService {

	@Autowired
	private JavaMailSender enviarEmail;
	@Autowired
	private TemplateEngine templateEngine;
	
	public void enviar(String para, String titulo, String conteudo) {
		log.info("Enviando email para redefinir senha...");
		
		var mensagem = new SimpleMailMessage();
		
		mensagem.setTo(para);
		mensagem.setSubject(titulo);
		mensagem.setText(conteudo);
		enviarEmail.send(mensagem);
		log.info("Email enviado com sucesso!");
	}
	
	public void enviarEmailComAnexo(SignupRequest obj) throws Exception {
		log.info("Enviando Email com anexo.");
		
		var mensagem = enviarEmail.createMimeMessage();
		var helper = new MimeMessageHelper(mensagem, true); //html definido
		
		helper.setTo(obj.getEmail());
		helper.setSubject("Redefição de senha.");
		helper.setText(htmlFromTemplateRecuperar(obj), true);
		
//		helper.addAttachment(arquivo, null);
		
		enviarEmail.send(mensagem);
		log.info("Email eviado.");
	}

	
	public String htmlFromTemplateRecuperar(SignupRequest obj) throws Exception {
		Context context = new Context();
		context.setVariable("reset", obj);
		return templateEngine.process("email/recuperaSenha/index", context);
	}
	
	
}
