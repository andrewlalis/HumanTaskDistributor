package nl.andrewlalis.human_task_distributor;

import org.apache.commons.cli.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HumanTaskDistributor {
	public static final CSVFormat CSV_FORMAT = CSVFormat.RFC4180;

	public static void main(String[] args) {
		final Options options = getOptions();
		CommandLineParser cmdParser = new DefaultParser();
		try {
			CommandLine cmd = cmdParser.parse(options, args);
			FileParser fileParser = new FileParser();
			Map<Human, Float> nameWeightMap = fileParser.parseHumanList(cmd.getOptionValue("hl"));
			Set<Task> tasks = fileParser.parseTaskList(cmd.getOptionValue("tl"));
			List<Map<Human, Set<Task>>> previousDistributions = fileParser.parsePreviousTaskDistributions(cmd.getOptionValues("prev"));

			long start = System.currentTimeMillis();
			Map<Human, Set<Task>> taskDistributions = new Distributor().generateDistribution(nameWeightMap, tasks, previousDistributions);
			long durationMillis = System.currentTimeMillis() - start;
			System.out.printf(
					"Completed distribution of %d tasks to %d people in %d ms.%n",
					tasks.size(),
					taskDistributions.keySet().size(),
					durationMillis
			);

			// Write to a file.
			String filePath = cmd.hasOption("o") ? cmd.getOptionValue("o") : "distribution.csv";
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

			System.out.println("Wrote task distribution data to " + filePath);

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp("HumanTaskDistributor", options);
			System.exit(1);
		}
	}

	private static Options getOptions() {
		Options options = new Options();
		options.addOption(Option.builder("hl")
				.longOpt("humans-list")
				.hasArg(true)
				.desc("Path to a CSV file containing list of humans to distribute tasks to. First column should be the name of the person, and second column can be empty, or contain a floating-point weight.")
				.required(true)
				.numberOfArgs(1)
				.type(String.class)
				.build()
		);
		options.addOption(Option.builder("tl")
				.longOpt("tasks-list")
				.hasArg(true)
				.desc("Path to a CSV file containing list of tasks that can be distributed to humans. First column should be unique task name.")
				.required(true)
				.numberOfArgs(1)
				.type(String.class)
				.build()
		);
		options.addOption(Option.builder("prev")
				.longOpt("previous-distributions")
				.desc("One or more CSV files containing previous task distribution results, to aid in balancing distribution over multiple iterations. Each should be of the form: person name, task name")
				.numberOfArgs(Option.UNLIMITED_VALUES)
				.hasArg(true)
				.required(false)
				.valueSeparator(',')
				.build()
		);
		options.addOption(Option.builder("o")
				.longOpt("output")
				.desc("Output file to write CSV distribution data to.")
				.hasArg(true)
				.numberOfArgs(1)
				.required(false)
				.type(String.class)
				.build()
		);
		return options;
	}
}
