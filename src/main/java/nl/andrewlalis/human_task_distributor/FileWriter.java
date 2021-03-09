package nl.andrewlalis.human_task_distributor;

import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static nl.andrewlalis.human_task_distributor.HumanTaskDistributor.CSV_FORMAT;

public class FileWriter {

	public void write(Map<Human, Set<Task>> taskDistributions, String filePath) throws IOException {
		CSVPrinter printer = new CSVPrinter(Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8), CSV_FORMAT);
		for (Map.Entry<Human, Set<Task>> entry : taskDistributions.entrySet()) {
			Human human = entry.getKey();
			Set<Task> assignedTasks = entry.getValue();
			List<Task> sortedTasks = assignedTasks.stream().sorted(Comparator.comparing(Task::getName)).collect(Collectors.toList());
			for (Task task : sortedTasks) {
				printer.printRecord(human.getName(), task.getName());
			}
		}
		printer.close(true);
	}

	public void write(Set<Task> tasks, String filePath) throws IOException {
		CSVPrinter printer = new CSVPrinter(Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8), CSV_FORMAT);
		List<Task> orderedTasks = new ArrayList<>(tasks);
		orderedTasks.sort(Task::compareTo);
		for (Task t : orderedTasks) {
			printer.printRecord(t.getName());
		}
		printer.close(true);
	}
}
