package aurelienribon.tweenengine.utils;

import java.util.ArrayList;

/**
 * A light pool of objects that can be resused to avoid allocation.
 * Based on Nathan Sweet pool implementation
 */
public abstract class Pool<T> {
	private final ArrayList<T> objects;
	private final Callback<T> callback;

	public Pool (int initCapacity, Callback<T> callback) {
		this.objects = new ArrayList<T>(initCapacity);
		this.callback = callback;
	}

	protected abstract T getNew();

	public T get() {
		T obj = objects.isEmpty() ? getNew() : objects.remove(objects.size()-1);
		if (callback != null)
			callback.act(obj);
		return obj;
	}

	public void free(T obj) {
		if (!objects.contains(obj))
			objects.add(obj);
	}

	public void clear() {
		objects.clear();
	}

	public int size() {
		return objects.size();
	}

	public interface Callback<T> {
		public void act(T obj);
	}
}