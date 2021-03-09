package nl.andrewlalis.human_task_distributor;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Task implements Comparable<Task> {
	private final String name;

	public Task(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Task task = (Task) o;
		return getName().equals(task.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName());
	}

	@Override
	public int compareTo(Task o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}
}
