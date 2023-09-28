package dev.mimi.trafiklabbuslines.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StopPointResponse(
        @JsonProperty("ResponseData")
        ResponseData responseData
        ) {
        public record ResponseData(@JsonProperty("Result") List<StopPoint> result) {
            public record StopPoint(
                    @JsonProperty("StopPointNumber")
                    int stopPointNumber,
                    @JsonProperty("StopPointName")
                    String stopPointName) { }
    }
}
