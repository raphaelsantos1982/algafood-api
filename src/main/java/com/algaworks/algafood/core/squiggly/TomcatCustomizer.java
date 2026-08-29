//package com.algaworks.algafood.core.squiggly;
//
////Referências:
////- https://stackoverflow.com/a/53613678
////- https://tomcat.apache.org/tomcat-8.5-doc/config/http.html
////- https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-configure-webserver
//
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.boot.web.server.WebServerFactoryCustomizer;
//import org.springframework.stereotype.Component;
//
//
//@Component
//public class TomcatCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
//
// @Override
// public void customize(TomcatServletWebServerFactory factory) {
//	 
//	 // 13.3. Limitando os campos retornados pela API com Squiggly
//	 // Permite que o TomCat deixe passar colchete
//     factory.addConnectorCustomizers(connector -> connector.setAttribute("relaxedQueryChars", "[]"));
// }
// 
//}
