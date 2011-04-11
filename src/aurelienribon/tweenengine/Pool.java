package aurelienribon.tweenengine;

import java.util.ArrayList;

/**
 * A light pool of objects that can be resused to avoid allocation.
 * Based on Nathan Sweet pool implementation
 */
public abstract class Pool<T> {
	private final ArrayList<T> objects;

	public Pool (int initCapacity) {
		objects = new ArrayList<T>(initCapacity);
	}

	protected abstract T getNew();

	public T get() {
		return objects.isEmpty() ? getNew() : objects.remove(objects.size()-1);
	}

	public void free(T obj) {
		objects.add(obj);
	}

	public void clear() {
		objects.clear();
	}
}