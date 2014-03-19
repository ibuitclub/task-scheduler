package org.infobip.taskscheduler.service;

import java.util.List;

/**
 * Service for basic data operations.
 * 
 * @param <K> Element identifier type.
 * @param <E> Element type.
 */
interface DataService<K, E> {

	/** Default selection offset. */
	public static final int DEFAULT_OFFSET = 0;

	/** Default selection limit. */
	public static final int DEFAULT_LIMIT = 100;

	/** Insert element of type {@code E}. */
	E insert(E e);

	/** Update element of type {@code E}. */
	E update(E e);

	/** Delete element of type {@code E}, by key of type {@code K}. */
	E delete(K k);

	/** Select element of type {@code E}, by key of type {@code K}. */
	E select(K k);

	/** Select multiple elements of type {@code E}, by {@code offset} and {@code limit}. */
	List<E> select(int offset, int limit);

	/** Select multiple elements of type {@code E}. */
	List<E> select();

	/** Count elements of type {@code E}. */
	int count();

}