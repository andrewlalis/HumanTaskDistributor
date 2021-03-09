package nl.andrewlalis.human_task_distributor.commands;

import nl.andrewlalis.human_task_distributor.*;
import org.apache.commons.cli.CommandLine;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DistributeTasks implements Command {
	@Override
	public void execute(CommandLine cmd) {
		final FileParser fileParser = new FileParser();
		final FileWriter fileWriter = new FileWriter();
		Map<Human, Float> nameWeightMap = fileParser.parseHumanList(cmd.getOptionValue("hl"));
		Set<Task> tasks = fileParser.parseTaskList(cmd.getOptionValue("tl"));
		String[] previousDistributionPaths = cmd.getOptionValues("prev");
		if (previousDistributionPaths == null) previousDistributionPaths = new String[0];
		List<Map<Human, Set<Task>>> previousDistributions = fileParser.parsePreviousTaskDistributions(previousDistributionPaths);

		long start = System.currentTimeMillis();
		Map<Human, Set<Task>> taskDistributions = new Distributor().generateDistribution(nameWeightMap, tasks, previousDistributions);
		long durationMillis = System.currentTimeMillis() - start;
		System.out.printf(
				"Created distribution of %d tasks to %d people in %d ms.%n",
				tasks.size(),
				taskDistributions.keySet().size(),
				durationMillis
		);

		// Write to a file.
		final String filePath = cmd.hasOption("o") ? cmd.getOptionValue("o") : "distribution.csv";
		try {
			fileWriter.write(taskDistributions, filePath);
			System.out.println("Wrote task distribution data to " + filePath);
		} catch (IOException e) {
			System.err.println("Couldn't write to file: " + e.getMessage());
		}
	}
}
