package nl.andrewlalis.human_task_distributor;

import nl.andrewlalis.human_task_distributor.commands.DistributeTasks;
import nl.andrewlalis.human_task_distributor.commands.PrepareTasksList;
import org.apache.commons.cli.*;
import org.apache.commons.csv.CSVFormat;

public class HumanTaskDistributor {
	public static final CSVFormat CSV_FORMAT = CSVFormat.RFC4180;

	public static void main(String[] args) {
		final Options options = getOptions();
		CommandLineParser cmdParser = new DefaultParser();
		try {
			CommandLine cmd = cmdParser.parse(options, args);
			if (cmd.hasOption("ptl")) {
				new PrepareTasksList().execute(cmd);
			} else if (cmd.hasOption("hl") && cmd.hasOption("tl")) {
				new DistributeTasks().execute(cmd);
			}
			throw new IllegalArgumentException("Invalid command.");
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
