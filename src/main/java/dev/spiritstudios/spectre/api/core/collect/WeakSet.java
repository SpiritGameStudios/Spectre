package dev.spiritstudios.spectre.api.core.collect;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

// this is so jank :3 - echo
public class WeakSet<E> extends AbstractSet<E> {
	private final Map<E, Void> ohGodWhatTheFuck = new WeakHashMap<>();

	@Override
	public @NotNull Iterator<E> iterator() {
		return ohGodWhatTheFuck.keySet().iterator();
	}

	@Override
	public int size() {
		return ohGodWhatTheFuck.size();
	}

	@Override
	public boolean add(E e) {
		ohGodWhatTheFuck.put(e, null);

		return false;
	}

	@Override
	public void clear() {
		ohGodWhatTheFuck.clear();
	}
}
