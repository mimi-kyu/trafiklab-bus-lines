package dev.mimi.trafiklabbuslines.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JourneyPatternPointOnLineResponse(ResponseData ResponseData) {
    public record ResponseData(String Type, List<JourneyPatternPointOnLine> Result) {
        public record JourneyPatternPointOnLine(
                int LineNumber,
                int JourneyPatternPointNumber
        ) {}
    }
}
