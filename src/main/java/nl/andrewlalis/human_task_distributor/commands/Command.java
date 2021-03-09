package nl.andrewlalis.human_task_distributor.commands;

import org.apache.commons.cli.CommandLine;

public interface Command {

	void execute(CommandLine cmd);
}
