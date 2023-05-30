package com.log.elasticsearch.slack;

import com.log.elasticsearch.entity.Entidad;
import com.log.elasticsearch.entity.Logjboss;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SlackMessageSenderLog {

    private static final String SLACK_TOKEN = "xoxb-5011203934215-5249328362928-b0tZK3b4e1vmplcTjDMjMMuR";
    private static final String SLACK_CHANNEL = "#errores-logs";

    public void sendMessageLog(List<Logjboss> logjboss) throws Exception {
        // Crea una instancia del cliente de Slack

        System.setProperty("javax.net.ssl.trustStore", "D:\\fv\\ELK_LOG\\slack\\keystoreslack");
        System.setProperty("javax.net.ssl.trustStorePassword", "Password1234");

        Slack slack = Slack.getInstance();
        MethodsClient methods = slack.methods(SLACK_TOKEN);

        for (Logjboss logjbos : logjboss) {
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(SLACK_CHANNEL)
                    .text(logjbos.getMessage())
                    .build();
            ChatPostMessageResponse response = methods.chatPostMessage(request);

            if (response.isOk()) {
                System.out.println("Mensaje enviado con éxito!");
            } else {
                System.out.println("Error al enviar mensaje: " + response.getError());
            }
        }

    }

    public void sendMessageLogEntidad(List<Entidad> entidads) throws Exception {

        System.setProperty("javax.net.ssl.trustStore", "D:\\fv\\ELK_LOG\\slack\\keystoreslack");
        System.setProperty("javax.net.ssl.trustStorePassword", "Password1234");

        Slack slack = Slack.getInstance();
        MethodsClient methods = slack.methods(SLACK_TOKEN);

        for (Entidad entidad : entidads) {
            if (entidad.getProcesado().equals("SinProcesar")) {

                ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                        .channel(SLACK_CHANNEL)
                        .text(entidad.getMessage())
                        .build();
                ChatPostMessageResponse response = methods.chatPostMessage(request);
                if (response.isOk()) {
                    System.out.println("Mensaje enviado con éxito!");
                } else {
                    System.out.println("Error al enviar mensaje: " + response.getError());
                }
            }
        }
    }
}
