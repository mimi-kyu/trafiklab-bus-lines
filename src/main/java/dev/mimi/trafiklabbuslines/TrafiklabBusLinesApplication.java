package dev.mimi.trafiklabbuslines;

import dev.mimi.trafiklabbuslines.service.TrafiklabService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class TrafiklabBusLinesApplication
		implements CommandLineRunner {
	private static Logger log = LoggerFactory
			.getLogger(TrafiklabBusLinesApplication.class);

	private final TrafiklabService trafiklabService;

	public TrafiklabBusLinesApplication(TrafiklabService trafiklabService) {
		this.trafiklabService = trafiklabService;
	}

	public static void main(String[] args) {
		SpringApplication.run(TrafiklabBusLinesApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info("EXECUTING : command line runner");

		for (int i = 0; i < args.length; ++i) {
			log.info("args[{}]: {}", i, args[i]);
		}

		List<Map.Entry<Integer, List<Integer>>> top10Lines = trafiklabService.getTopTenLongestLines();
		log.info("Top 1 -> Line number: " + top10Lines.get(0).getKey().toString() + " -> " + printStopPointNames(top10Lines.get(0).getValue()));
		for(int i = 1; i < top10Lines.size(); i++) {
			log.info("Top " + (i + 1) + " -> Line number: " + top10Lines.get(i).getKey().toString() + " - > Number of stops: " + top10Lines.get(i).getValue().size());
		}
	}

	public String printStopPointNames(List<Integer> stopPointNumbersList) {
		StringBuilder output = new StringBuilder("[Bus stops: ");
		Map<Integer, String> stopPointsMap = trafiklabService.retrieveStopPoints();
		for(Integer number: stopPointNumbersList) {
			output.append(stopPointsMap.get(number) + ", ");
		}
		return output.replace(output.length() - 3, output.length(), "]").toString();
	}
}
