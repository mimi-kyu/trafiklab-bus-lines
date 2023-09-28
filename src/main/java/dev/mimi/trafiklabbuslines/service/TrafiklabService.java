package dev.mimi.trafiklabbuslines.service;

import dev.mimi.trafiklabbuslines.model.JourneyPatternPointOnLineResponse;
import dev.mimi.trafiklabbuslines.model.StopPointResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.exit;

@Service
public class TrafiklabService {
    private final WebClient trafiklabWebClient;
    private static final Logger log = LoggerFactory
            .getLogger(TrafiklabService.class);

    private final String trafiklabKey;
    private final String transportModeCode;

    public TrafiklabService(WebClient trafiklabWebClient,
                            @Value("${application.trafiklab.endpoint.key}") String trafiklabKey,
                            @Value("${application.trafiklab.endpoint.transport}") String transportModeCode) {
        this.trafiklabWebClient = trafiklabWebClient;
        this.trafiklabKey = trafiklabKey;
        this.transportModeCode = transportModeCode;
    }

    public List<Map.Entry<Integer, List<Integer>>> getTopTenLongestLines() {
        return findTopTenLongestLines(retrieveJourneyPatternPointOnLines());
    }

    public List<Map.Entry<Integer, List<Integer>>> findTopTenLongestLines(Map<Integer, List<Integer>> lineStops) {
        List<Map.Entry<Integer, List<Integer>>> lineStopsMapEntriesArrayList = new ArrayList<>(lineStops.entrySet());
        lineStopsMapEntriesArrayList.sort((o1, o2) -> Integer.compare(o2.getValue().size(), o1.getValue().size()));

        return lineStopsMapEntriesArrayList.subList(0, Math.min(lineStopsMapEntriesArrayList.size(), 10));
    }

    public Map<Integer, List<Integer>> retrieveJourneyPatternPointOnLines() {
        Map<Integer, List<Integer>> journeyPatternPointOnLineHashMap = null;
        ResponseEntity<JourneyPatternPointOnLineResponse> journeyPatternPointOnLineListResponse = trafiklabWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("key", trafiklabKey)
                        .queryParam("model", "jour")
                        .queryParam("DefaultTransportModeCode", transportModeCode)
                        .build())
                .retrieve()
                .toEntity(JourneyPatternPointOnLineResponse.class)
                .block();
        HttpStatusCode statusCode = journeyPatternPointOnLineListResponse.getStatusCode();
        if(statusCode.is2xxSuccessful()) {
            log.info("journeyPatternPointOnLineListResponse.getBody().size() = {}", journeyPatternPointOnLineListResponse
                    .getBody().responseData().result().size());
            journeyPatternPointOnLineHashMap = journeyPatternPointOnLineListResponse
                    .getBody().responseData().result()
                    .stream()
                    .collect(Collectors.toMap(entity -> entity.lineNumber(),
                            entity -> Collections.singletonList(entity.journeyPatternPointNumber()),
                            (existingList, newList) -> {
                                List<Integer> mergedList = new ArrayList<>(existingList);
                                mergedList.addAll(newList);
                                return mergedList;
                            }));
        } else {
            log.error("Journey pattern points on line could not be retrieved. Status code: {}, Error message: {}",
                    journeyPatternPointOnLineListResponse.getStatusCode(),
                    journeyPatternPointOnLineListResponse.getBody());
            exit(1);
        }
        log.info("journeyPatternPointOnLineHashMap.size() = {}", journeyPatternPointOnLineHashMap.size());
        return journeyPatternPointOnLineHashMap;
    }

    public Map<Integer, String> retrieveStopPoints() {
        Map<Integer, String> stopPointHashMap = null;
        ResponseEntity<StopPointResponse> stopPointListResponse = trafiklabWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("key", trafiklabKey)
                        .queryParam("model", "StopPoint")
                        .build())
                .retrieve()
                .toEntity(StopPointResponse.class)
                .block();
        HttpStatusCode statusCode = stopPointListResponse.getStatusCode();
        if(statusCode.is2xxSuccessful()) {
            stopPointHashMap = stopPointListResponse
                    .getBody().responseData().result()
                    .stream()
                    .collect(Collectors.toMap(entity -> entity.stopPointNumber(), entity -> entity.stopPointName()));
        } else {
            log.error("Stop points could not be retrieved. Status code: {}, Error message: {}",
                    stopPointListResponse.getStatusCode(),
                    stopPointListResponse.getBody());
            exit(1);
        }
        return stopPointHashMap;
    }
}
