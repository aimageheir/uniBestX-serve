package com.unibestx.serve.config;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonConfigTest {

    private final JsonMapper mapper = buildMapper();

    @Test
    void serializesLocalDateTimeWithConfiguredPattern() throws Exception {
        LocalDateTime time = LocalDateTime.of(2026, 11, 19, 16, 30, 5);

        String json = mapper.writeValueAsString(Map.of("time", time));

        assertThat(json).contains("\"2026-11-19 16:30:05\"");
    }

    @Test
    void deserializesLocalDateTimeWithConfiguredPattern() throws Exception {
        LocalDateTime time = mapper.readValue("{\"time\":\"2026-11-19 16:30:05\"}", Holder.class).time;

        assertThat(time).isEqualTo(LocalDateTime.of(2026, 11, 19, 16, 30, 5));
    }

    private JsonMapper buildMapper() {
        var builder = JsonMapper.builder();
        new JacksonConfig().javaTimeCustomizer().customize(builder);
        return builder.build();
    }

    record Holder(LocalDateTime time) {
    }
}
