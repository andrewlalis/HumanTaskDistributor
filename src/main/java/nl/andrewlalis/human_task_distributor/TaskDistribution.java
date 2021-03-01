package nl.andrewlalis.human_task_distributor;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class TaskDistribution {
	private final Human human;
	private final float weight;
	private final Set<Task> tasks;

	public TaskDistribution(Human human, float weight, Set<Task> tasks) {
		this.human = human;
		this.weight = weight;
		this.tasks = tasks;
	}

	public TaskDistribution(Human human, float weight) {
		this(human, weight, new HashSet<>());
	}

	@Override
	public String toString() {
		return "TaskDistribution{" +
				"human=" + human +
				", weight=" + weight +
				", tasks=" + tasks +
				'}';
	}
}
