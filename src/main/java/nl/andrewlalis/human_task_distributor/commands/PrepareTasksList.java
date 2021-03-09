package nl.andrewlalis.human_task_distributor.commands;

import nl.andrewlalis.human_task_distributor.FileParser;
import nl.andrewlalis.human_task_distributor.FileWriter;
import nl.andrewlalis.human_task_distributor.Task;

import java.io.IOException;
import java.util.Set;

public class PrepareTasksList implements Command {
	@Override
	public void execute(String[] args) {
		if (args.length != 2) {
			throw new IllegalArgumentException("Expected exactly 2 parameters for ptl arg.");
		}
		String filePath = args[0].trim();
		String regex = args[1].trim();
		Set<Task> tasks = new FileParser().parseTaskList(filePath, regex);
		System.out.println("Read " + tasks.size() + " tasks from file.");
		String outFilePath = filePath.replaceFirst("\\..*", ".csv");
		try {
			new FileWriter().write(tasks, outFilePath);
			System.out.println("Wrote tasks to " + outFilePath);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Couldn't write output file.");
		}
	}
}
