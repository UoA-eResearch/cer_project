package nz.ac.auckland.cer.common.util;

import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

/**
 * Send e-mail from a template. The template can be either provided as a string,
 * or as a resource.
 * 
 */
public class TemplateEmail {

    @Autowired private Email email;
    @Autowired TemplateUtil templateUtil;

    public void send(
            String from,
            String to,
            String cc,
            String replyto,
            String subject,
            String body,
            Map<String, String> templateParams) throws Exception {

        this.email.send(from, to, cc, replyto, subject, templateUtil.substituteParameters(body, templateParams));
    }

    public void sendFromResource(
            String from,
            String to,
            String cc,
            String replyto,
            String subject,
            Resource body,
            Map<String, String> templateParams) throws Exception {

        String bodyString = null;
        if (body != null && body.exists()) {
            bodyString = IOUtils.toString(body.getInputStream());
            if (bodyString == null) {
                throw new Exception("Failed to read " + body.getFilename());
            }
        } else {
            throw new Exception("resource for email body must not be null");
        }
        this.send(from, to, cc, replyto, subject, bodyString, templateParams);
    }

    public void setEmail(
            Email email) {

        this.email = email;
    }
}
