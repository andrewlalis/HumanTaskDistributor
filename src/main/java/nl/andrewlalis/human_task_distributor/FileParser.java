package nl.andrewlalis.human_task_distributor;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

import static nl.andrewlalis.human_task_distributor.HumanTaskDistributor.CSV_FORMAT;

public class FileParser {

	public Map<Human, Float> parseHumanList(String path) {
		Map<Human, Float> humanNameWeightMap = new HashMap<>();
		try (CSVParser csvParser = CSVParser.parse(Paths.get(path), StandardCharsets.UTF_8, CSV_FORMAT)) {
			for (CSVRecord record : csvParser) {
				if (record.size() > 0) {
					String name = record.get(0).trim();
					float weight = 1.0f;
					if (record.size() > 1) {
						try {
							weight = Float.parseFloat(record.get(1));
						} catch (NumberFormatException e) {
							// Do nothing here, simply skip a custom weight.
						}
					}
					humanNameWeightMap.put(new Human(name), weight);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return humanNameWeightMap;
	}

	public Set<Task> parseTaskList(String path) {
		Set<Task> taskSet = new HashSet<>();
		try (CSVParser csvParser = CSVParser.parse(Paths.get(path), StandardCharsets.UTF_8, CSV_FORMAT)) {
			for (CSVRecord record : csvParser) {
				if (record.size() > 0) {
					String taskName = record.get(0);
					if (!taskName.isBlank()) {
						taskSet.add(new Task(taskName.trim()));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return taskSet;
	}

	public List<Map<Human, Set<Task>>> parsePreviousTaskDistributions(String[] paths) {
		List<Map<Human, Set<Task>>> previousDistributions = new ArrayList<>();
		for (String path : paths) {
			Map<Human, Set<Task>> distribution = new HashMap<>();
			try (CSVParser csvParser = CSVParser.parse(Paths.get(path), StandardCharsets.UTF_8, CSV_FORMAT)) {
				for (CSVRecord record : csvParser) {
					if (record.size() > 1) {
						Human h = new Human(record.get(0).trim());
						Task t = new Task(record.get(1).trim());
						if (!distribution.containsKey(h)) {
							distribution.put(h, new HashSet<>());
						}
						distribution.get(h).add(t);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			previousDistributions.add(distribution);
		}
		return previousDistributions;
	}
}
