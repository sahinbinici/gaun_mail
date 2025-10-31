package com.mobilewebservices.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Custom JSON serializer that cleans HTML content before serialization
 */
public class HtmlCleaningSerializer extends JsonSerializer<String> {

    private static final HtmlUtils htmlUtils = new HtmlUtils();

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            String cleanedValue = htmlUtils.cleanTextForOutput(value);
            gen.writeString(cleanedValue);
        }
    }
}
