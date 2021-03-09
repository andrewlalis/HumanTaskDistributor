package nl.andrewlalis.human_task_distributor;

import nl.andrewlalis.human_task_distributor.commands.PrepareTasksList;
import org.apache.commons.cli.*;
import org.apache.commons.csv.CSVFormat;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HumanTaskDistributor {
	public static final CSVFormat CSV_FORMAT = CSVFormat.RFC4180;

	public static void main(String[] args) {
		final Options options = getOptions();
		CommandLineParser cmdParser = new DefaultParser();
		try {

			FileParser fileParser = new FileParser();
			FileWriter fileWriter = new FileWriter();
			CommandLine cmd = cmdParser.parse(options, args);
			if (cmd.hasOption("ptl")) {
				String[] values = cmd.getOptionValues("ptl");
				new PrepareTasksList().execute(values);
				return;
			}

			if (!cmd.hasOption("hl") || !cmd.hasOption("tl")) {
				throw new IllegalArgumentException("When not preparing a tasks-list, hl and tl are required.");
			}

			Map<Human, Float> nameWeightMap = fileParser.parseHumanList(cmd.getOptionValue("hl"));
			Set<Task> tasks = fileParser.parseTaskList(cmd.getOptionValue("tl"));
			String[] previousDistributionPaths = cmd.getOptionValues("prev");
			if (previousDistributionPaths == null) previousDistributionPaths = new String[0];
			List<Map<Human, Set<Task>>> previousDistributions = fileParser.parsePreviousTaskDistributions(previousDistributionPaths);

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
			final String filePath = cmd.hasOption("o") ? cmd.getOptionValue("o") : "distribution.csv";
			fileWriter.write(taskDistributions, filePath);
			System.out.println("Wrote task distribution data to " + filePath);

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
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
				.required(false)
				.numberOfArgs(1)
				.type(String.class)
				.build()
		);
		options.addOption(Option.builder("ptl")
				.longOpt("prepare-tasks-list")
				.desc("Prepares a tasks-list CSV from a TXT file with one task for each item that matches the given Regex.")
				.required(false)
				.numberOfArgs(2)
				.valueSeparator(',')
				.type(String.class)
				.build()
		);
		options.addOption(Option.builder("tl")
				.longOpt("tasks-list")
				.hasArg(true)
				.desc("Path to a CSV file containing list of tasks that can be distributed to humans. First column should be unique task name.")
				.required(false)
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
