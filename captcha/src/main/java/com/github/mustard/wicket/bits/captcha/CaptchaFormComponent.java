package com.github.mustard.wicket.bits.captcha;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.fluent.Content;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.apache.http.client.fluent.Form.form;
import static org.apache.http.client.fluent.Request.Post;

public class CaptchaFormComponent extends FormComponent<String> {

    private static final Logger LOG = LoggerFactory.getLogger(CaptchaFormComponent.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String siteKey;
    private final String secretKey;

    public CaptchaFormComponent(String id, String siteKey, String secretKey) {
        super(id, new Model<>());
        this.siteKey = siteKey;
        this.secretKey = secretKey;
        // TODO Doesn't work adding the validator here, have to override validate on the component. investigate?
        // add(reCaptchaValidator(checkCaptcha));
    }

    @Override
    public void validate() {
        if (captchaCorrect()) {
            success(getString("recaptcha.validation.success"));
        } else {
            error(getString("recaptcha.validation.error"));
        }
    }

    public IValidator<String> reCaptchaValidator(final boolean checkCaptcha) {
        return new IValidator<String>() {
            @Override
            public void validate(IValidatable<String> validatable) {
                if (checkCaptcha) {
                    if (captchaCorrect()) {
                        success(getString("recaptcha.validation.success"));
                    } else {
                        error(getString("recaptcha.validation.error"));
//                        error(new ValidationError().addKey("recaptcha.validation.error"));
                        validatable.error(new ValidationError().addKey("recaptcha.validation.error"));
                    }
                }
            }
        };
    }

    public boolean captchaCorrect() {

        final HttpServletRequest servletRequest = (HttpServletRequest) getRequest().getContainerRequest();
        final String remoteAddress = servletRequest.getRemoteAddr();
        final String response = servletRequest.getParameter("g-recaptcha-response");

        try {
            return validate(remoteAddress, response);
        } catch (RuntimeException rEx) {
            LOG.error("An error occurred validating captcha", rEx);
            return false;
        }

    }

    @Override
    public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        replaceComponentTagBody(markupStream, openTag, "" +
                "<script src='https://www.google.com/recaptcha/api.js'></script>" +
                "<div class='g-recaptcha' data-sitekey='" + siteKey + "'></div>"
        );
    }

    protected boolean validate(String remoteAddress, String response) {
        try {
            Content content = Post("https://www.google.com/recaptcha/api/siteverify")
                    .bodyForm(form()
                            .add("secret", secretKey)
                            .add("response", response)
                            .add("remoteip", remoteAddress).build()
                    ).execute().returnContent();

            JsonNode json = OBJECT_MAPPER.readTree(content.asStream());
            LOG.info("ReCaptcha Response '{}'", json);
            return json.get("success").asBoolean();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
