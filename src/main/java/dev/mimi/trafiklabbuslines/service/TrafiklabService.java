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

    public List<Map.Entry<Integer, List<Integer>>> findTopTenLongestLines() {
        log.info("Finding the top ten longest lines");
        Map<Integer, List<Integer>> lineStops = retrieveJourneyPatternPointOnLines();
        List<Map.Entry<Integer, List<Integer>>> lineStopsMapEntriesArrayList = new ArrayList<>(lineStops.entrySet());
        lineStopsMapEntriesArrayList.sort((o1, o2) -> Integer.compare(o2.getValue().size(), o1.getValue().size()));

        return lineStopsMapEntriesArrayList.subList(0, Math.min(lineStopsMapEntriesArrayList.size(), 10));
    }

    public Map<Integer, List<Integer>> retrieveJourneyPatternPointOnLines() {
        log.info("Retrieving JourneyPatternPointOnLines");
        Map<Integer, List<Integer>> journeyPatternPointOnLineHashMap = null;
        ResponseEntity<JourneyPatternPointOnLineResponse> journeyPatternPointOnLineResponse = trafiklabWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("key", trafiklabKey)
                        .queryParam("model", "jour")
                        .queryParam("DefaultTransportModeCode", transportModeCode)
                        .build())
                .retrieve()
                .toEntity(JourneyPatternPointOnLineResponse.class)
                .block();
        HttpStatusCode statusCode = journeyPatternPointOnLineResponse.getStatusCode();
        if(statusCode.is2xxSuccessful()) {
            log.info("JourneyPatternPointOnLines successfully retrieved");
            journeyPatternPointOnLineHashMap = journeyPatternPointOnLineResponse
                    .getBody().responseData().result()
                    .stream()
                    .collect(Collectors.toMap(entity -> entity.lineNumber(),
                            entity -> Collections.singletonList(entity.journeyPatternPointNumber()),
                            (existingList, newList) -> {
                                List<Integer> mergedList = new ArrayList<>(existingList);
                                mergedList.addAll(newList);
                                return mergedList;
                            }));
            log.debug("Number of lines retrieved: {}", journeyPatternPointOnLineHashMap.size());
        } else {
            log.error("Journey pattern points on line could not be retrieved. Status code: {}, Error message: {}",
                    journeyPatternPointOnLineResponse.getStatusCode(),
                    journeyPatternPointOnLineResponse.getBody());
            exit(1);
        }
        return journeyPatternPointOnLineHashMap;
    }

    public Map<Integer, String> retrieveStopPoints() {
        log.info("Retrieving StopPoints");
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
            log.info("StopPoints successfully retrieved");
            stopPointHashMap = stopPointListResponse
                    .getBody().responseData().result()
                    .stream()
                    .collect(Collectors.toMap(entity -> entity.stopPointNumber(), entity -> entity.stopPointName()));
            log.debug("Number of stops retrieved: {}", stopPointHashMap.size());
        } else {
            log.error("Stop points could not be retrieved. Status code: {}, Error message: {}",
                    stopPointListResponse.getStatusCode(),
                    stopPointListResponse.getBody());
            exit(1);
        }

        return stopPointHashMap;
    }
}
