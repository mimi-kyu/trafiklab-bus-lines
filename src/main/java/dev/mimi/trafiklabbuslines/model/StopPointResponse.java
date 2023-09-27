package dev.mimi.trafiklabbuslines.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StopPointResponse(
        ResponseData ResponseData
        ) {
        public record ResponseData(List<StopPoint> Result) {
            public record StopPoint(
                   int StopPointNumber,
                    String StopPointName) { }
    }
}
