package dev.mimi.trafiklabbuslines;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import dev.mimi.trafiklabbuslines.model.JourneyPatternPointOnLineResponse;
import dev.mimi.trafiklabbuslines.model.StopPointResponse;
import dev.mimi.trafiklabbuslines.service.TrafiklabService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest(httpPort = 8088)
@SpringBootTest(properties = { "application.trafiklab.endpoint.url=http://localhost:8088/api2/linedata.json" })
class TrafiklabBusLinesApplicationTests {
	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private TrafiklabService underTest;
	@MockBean
	private TrafiklabBusLinesApplication trafiklabBusLinesApplication;

	@Test
	void givenSuccessfulResponseTrafiklabApiReturnsExpectedResult() throws JsonProcessingException {
		stubFor(get(anyUrl())
				.withQueryParam("model", equalTo("jour"))
				.willReturn(aResponse()
						.withBody(generateJourneyPatternPointOnLineResponse())
						.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

		stubFor(get(anyUrl())
				.withQueryParam("model", equalTo("StopPoint"))
				.willReturn(aResponse()
						.withBody(generateStopPointResponse())
						.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
		var journeyPatternPointOnLines = underTest.findTopTenLongestLines();
		assertEquals(10, journeyPatternPointOnLines.size());
		assertEquals(20, journeyPatternPointOnLines.get(0).getValue().size());
		assertEquals(11, journeyPatternPointOnLines.get(9).getValue().size());
	}

	private String generateJourneyPatternPointOnLineResponse() throws JsonProcessingException {
		List<JourneyPatternPointOnLineResponse.ResponseData.JourneyPatternPointOnLine> jourResult = new ArrayList<>();
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < i + 1; j++) {
				JourneyPatternPointOnLineResponse.ResponseData.JourneyPatternPointOnLine journeyPatternPointOnLine =
						new JourneyPatternPointOnLineResponse.ResponseData.JourneyPatternPointOnLine(i, j + 1);
				jourResult.add(journeyPatternPointOnLine);
			}
		}

		JourneyPatternPointOnLineResponse.ResponseData jourResponseData =
				new JourneyPatternPointOnLineResponse.ResponseData("JourneyPatternPointOnLine", jourResult);
		JourneyPatternPointOnLineResponse journeyPatternPointOnLineResponse = new JourneyPatternPointOnLineResponse(jourResponseData);
		return objectMapper.writeValueAsString(journeyPatternPointOnLineResponse);
	}

	private String generateStopPointResponse() throws JsonProcessingException {
		List<StopPointResponse.ResponseData.StopPoint> stopPointResult = new ArrayList<>();
		for(int i = 0; i < 20; i++) {
			StopPointResponse.ResponseData.StopPoint stopPoint =
					new StopPointResponse.ResponseData.StopPoint(i + 1, Integer.toString(i + 1) + " name");
			stopPointResult.add(stopPoint);
		}
		StopPointResponse.ResponseData stopPointResponseData = new StopPointResponse.ResponseData(stopPointResult);
		StopPointResponse stopPointResponse = new StopPointResponse(stopPointResponseData);
		return objectMapper.writeValueAsString(stopPointResponse);
	}
}
