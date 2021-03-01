package nl.andrewlalis.human_task_distributor;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Human {
	private final String name;

	public Human(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Human human = (Human) o;
		return getName().equals(human.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName());
	}

	@Override
	public String toString() {
		return this.getName();
	}
}
