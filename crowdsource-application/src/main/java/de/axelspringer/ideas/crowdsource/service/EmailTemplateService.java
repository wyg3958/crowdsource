package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.exceptions.ResourceNotFoundException;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

@Service
public class EmailTemplateService {

    private final ExpressionParser parser = new SpelExpressionParser();
    private final TemplateParserContext templateParserContext = new TemplateParserContext();


    public String format(final String templatePath, final Object context) {

        try {
            Resource resource = new ClassPathResource(templatePath);
            InputStream inputStream = resource.getInputStream();
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer, "UTF-8");

            return parser.parseExpression(writer.toString(), templateParserContext).getValue(context, String.class);

        } catch (IOException e) {
            throw new ResourceNotFoundException();
        }
    }
}

