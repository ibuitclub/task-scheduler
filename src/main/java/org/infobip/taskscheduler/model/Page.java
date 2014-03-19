package org.infobip.taskscheduler.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

/**
 * Helper model for items pagination.
 * 
 * @param <T> Page item of type {@code T}.
 */
public final class Page<T> {

	private final int count;
	private final List<T> items;

	/**
	 * @param count Total count of all items.
	 * @param items Subset(page) of items.
	 */
	public Page(int count, List<T> items) {
		Assert.notNull(items, "'List<T> items' cannot be null");
		this.count = count;
		this.items = items;
	}

	public int getCount() {
		return count;
	}

	public List<T> getItems() {
		return new ArrayList<T>(items);
	}

	@Override
	public String toString() {
		return "Page [count=" + count + ", items=" + items + "]";
	}

}