package dev.mimi.trafiklabbuslines.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JourneyPatternPointOnLineResponse(@JsonProperty("ResponseData") ResponseData responseData) {
    public record ResponseData(@JsonProperty("Type") String type,
                               @JsonProperty("Result") List<JourneyPatternPointOnLine> result) {
        public record JourneyPatternPointOnLine(
                @JsonProperty("LineNumber") int lineNumber,
                @JsonProperty("JourneyPatternPointNumber") int journeyPatternPointNumber
        ) {}
    }
}
