package com.tallerwebi.dominio.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.Usuario;

import jakarta.mail.*;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

/* 
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
*/
import java.util.Properties;

//Aplicando JavaMail
@Service
public class ServicioEmail {
    @Value("${email.usuario}")
    private String remitente;
    @Value("${email.clave}")
    private String clave;

    @Autowired
    private TemplateEngine templateEngine;

    public void enviarEmailActivacion(Usuario usuario) {
        String asunto = "";
        String cuerpo = "";

        if (usuario instanceof Cliente) {
            asunto = "Activa tu cuenta - Ventanet";
            Context context = new Context();

            context.setVariable("nombre", usuario.getNombre());

            context.setVariable("linkActivacion",
                    "http://localhost:8080/spring/verificar?token=" + usuario.getTokenVerificacion());
            cuerpo = templateEngine.process("verificacion-cuenta-email-cliente", context);
            enviarEmail(usuario.getEmail(), asunto, cuerpo, true);

        } else if (usuario instanceof Proveedor) {
            asunto = "Estado de su cuenta de proveedor - Ventanet";
            cuerpo = "Hola " + ((Proveedor) usuario).getRazonSocial()
                    + ". Su cuenta de proveedor ha sido creada exitosamente y está en proceso de verificación por nuestro equipo. "
                    + "Le notificaremos una vez que su cuenta haya sido aprobada y esté lista para usar. "
                    + "Gracias por unirse a Ventanet como proveedor.";
            enviarEmail(usuario.getEmail(), asunto, cuerpo, false);

        }
    }

    // Aplicamos en el constructor para leer email del remitente y su clave por
    // .properties

    /*
     * public ServicioEmail() {
     * try (InputStream input =
     * getClass().getClassLoader().getResourceAsStream("email.properties")) {
     * Properties properties = new Properties();
     * properties.load(input);
     * remitente = properties.getProperty("email.usuario");
     * clave = properties.getProperty("email.clave");
     * 
     * } catch (IOException exception) {
     * throw new
     * RuntimeException("Error al cargar la configuracion de las variables de email"
     * , exception);
     * }
     * }
     */

    public void enviarEmail(String destinatario, String asunto, String cuerpo, boolean esHtml) {
        System.out.println("DEBUG >> clave: '" + clave + "'");
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, clave);
            }
        });

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);

            if (esHtml) {
                MimeBodyPart bodyPart = new MimeBodyPart();
                bodyPart.setContent(cuerpo, "text/html; charset=utf-8");

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(bodyPart);

                message.setContent(multipart);

            } else
                message.setText(cuerpo);

            Transport.send(message);

        } catch (MessagingException exception) {
            exception.printStackTrace(); // TEMPORAL: para ver pq carajo no funca
            throw new RuntimeException("Error al enviar email: " + exception.getMessage(), exception);
        }

    }


}
