package com.example.inventory.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Serialises every {@link BigDecimal} as a two-decimal string.
 *
 * <p>Spring Boot 4 renames {@code @JsonComponent} to {@code @JacksonComponent}, and
 * Jackson 3 renames {@code JsonSerializer} to {@code ValueSerializer} and
 * {@code SerializerProvider} to {@code SerializationContext}.
 */
@JsonComponent
public class MoneyJsonComponent {

    public static class Serializer extends JsonSerializer<BigDecimal> {

        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            gen.writeString(value.setScale(2, RoundingMode.HALF_UP).toPlainString());
        }
    }
}
