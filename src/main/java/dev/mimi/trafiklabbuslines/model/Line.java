package dev.mimi.trafiklabbuslines.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Line(
        int lineNumber) {
}
