package com.example.inventory.json;

import org.springframework.boot.jackson.JacksonComponent;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Serialises every {@link BigDecimal} as a two-decimal string.
 */
@JacksonComponent
public class MoneyJsonComponent {

    public static class Serializer extends ValueSerializer<BigDecimal> {

        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializationContext serializers) {
            gen.writeString(value.setScale(2, RoundingMode.HALF_UP).toPlainString());
        }
    }
}
