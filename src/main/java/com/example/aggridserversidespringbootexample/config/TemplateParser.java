package com.example.aggridserversidespringbootexample.config;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.Locale;
import java.util.Map;

@Component
public class TemplateParser {
    /**
     * Количество знаков после запятой (14) при форматировании чисел в шаблоне
     */
    public static final String NUMBER_FORMAT = "0.##############";
  public static final String HQL_TEMPLATES_PATH = "/ftl";

  private final Configuration configuration;

    public TemplateParser(Configuration configuration) {
        this.configuration = configuration;
        this.configuration.setTemplateLoader(new ClassTemplateLoader(getClass(), HQL_TEMPLATES_PATH));
//        this.configuration.setOutputFormat(XMLOutputFormat.INSTANCE);
        this.configuration.setLocale(Locale.US);
        this.configuration.setNumberFormat(NUMBER_FORMAT);
    }

    @SneakyThrows
    public String prepareQuery(String templateName, Map<String, Object> params) {
        Template template = configuration.getTemplate(templateName);
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, params).trim();
    }
}
