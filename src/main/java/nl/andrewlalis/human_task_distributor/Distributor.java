package nl.andrewlalis.human_task_distributor;

import java.util.*;
import java.util.stream.Collectors;

public class Distributor {

	public Map<Human, Set<Task>> generateDistribution(
			Map<Human, Float> weightedHumans,
			Set<Task> tasks,
			List<Map<Human, Set<Task>>> previousDistributions
	) {
		if (weightedHumans.isEmpty() || tasks.isEmpty()) {
			return Map.of();
		}

		float unweightedAverageTasksPerHuman = (float) tasks.size() / weightedHumans.size();

		// Initialize distribution data sets for each human.
		Map<Human, Set<Task>> taskDistributions = new HashMap<>(weightedHumans.size());
		weightedHumans.forEach((h, w) -> taskDistributions.put(h, new HashSet<>()));
		final float totalWeight = weightedHumans.values().stream().reduce(Float::sum).orElse(0.0f);
		final float averageTasksPerHuman = unweightedAverageTasksPerHuman / (totalWeight / taskDistributions.size());

		// Precompute the theoretical maximum number of tasks that each person should be assigned.
		Map<Human, Float> maxTasksPerHuman = new HashMap<>();
		weightedHumans.forEach((h, w) -> maxTasksPerHuman.put(h, w * averageTasksPerHuman));

		// Prepare a stack of tasks we can gradually take from.
		Stack<Task> taskStack = new Stack<>();
		taskStack.addAll(tasks);
		Collections.shuffle(taskStack);

		// Iteratively pop and assign each task to a person.
		while (!taskStack.empty()) {
			Task t = taskStack.pop();
			Human h = this.chooseHumanForNextTask(
					taskDistributions,
					maxTasksPerHuman,
					t,
					previousDistributions
			);
			taskDistributions.get(h).add(t);
		}

		return taskDistributions;
	}

	/**
	 * Chooses the next person for a task, using a ranked choice based on two
	 * criteria:
	 * <ol>
	 *     <li>Whether the person has been given this exact task before.</li>
	 *     <li>The difference between the tasks the person has, and how many they can have at most.</li>
	 * </ol>
	 * @param tasksPerHuman A set of tasks that each person is already assigned to.
	 * @param maxTasksPerHuman For each person, a floating point maximum number
	 *                         of tasks that they may be assigned to.
	 * @param task The task being assigned.
	 * @param previousDistributions A list of distributions done previously, in
	 *                              order to avoid assigning people to the same
	 *                              tasks.
	 * @return The human to use for the task.
	 */
	private Human chooseHumanForNextTask(
			Map<Human, Set<Task>> tasksPerHuman,
			Map<Human, Float> maxTasksPerHuman,
			Task task,
			List<Map<Human, Set<Task>>> previousDistributions
	) {
		List<Human> rankedHumans = maxTasksPerHuman.keySet().stream()
				.sorted((h1, h2) -> {
					// Sort first so people who haven't had the task are sorted first.
					boolean h1NeverHadTask = previousDistributions.stream().noneMatch(map -> map.getOrDefault(h1, Set.of()).contains(task));
					boolean h2NeverHadTask = previousDistributions.stream().noneMatch(map -> map.getOrDefault(h2, Set.of()).contains(task));
					int previousTaskCompare = Boolean.compare(h1NeverHadTask, h2NeverHadTask);
					if (previousTaskCompare != 0) {
						return previousTaskCompare;
					}
					final float diff1 = maxTasksPerHuman.get(h1) - tasksPerHuman.get(h1).size();
					final float diff2 = maxTasksPerHuman.get(h2) - tasksPerHuman.get(h2).size();
					return Float.compare(diff2, diff1); // Reverse sorting direction so largest diffs appear first.
				})
				.collect(Collectors.toList());
		return rankedHumans.get(0);
	}
}
