package org.carlosmorenoin.micro.vertxrxjava.task;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;

public class TaskService {

	public Maybe<Integer> executeComplexTask(final Integer number) {
		if (number == null) {
			throw new IllegalArgumentException("The task requires a valid entry number");
		}
		return Maybe.just(number)
				.filter(Objects::nonNull)
				.map(n -> n * 1000);

	}
}
