package com.jamieswhiteshirt.clothesline.api.util;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class MutableSortedIntMap<T> {
    public static final class Entry<T> {
        private final int key;
        private final T value;

        public Entry(int key, T value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        public T getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry<?> entry = (Entry<?>) o;
            return key == entry.key &&
                Objects.equals(value, entry.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        @Override
        public String toString() {
            return "Entry{" +
                "key=" + key +
                ", value=" + value +
                '}';
        }
    }

    private int findKeyIndex(int key, int minIndex, int maxIndex) {
        if (minIndex != maxIndex) {
            int middleIndex = (minIndex + maxIndex) / 2;
            int comparison = Integer.compare(key, entries.get(middleIndex).getKey());
            if (comparison < 0) {
                return findKeyIndex(key, minIndex, middleIndex);
            } else if (comparison > 0) {
                return findKeyIndex(key, middleIndex + 1, maxIndex);
            } else {
                return middleIndex;
            }
        } else {
            return minIndex;
        }
    }

    private int findKeyIndex(int key) {
        return findKeyIndex(key, 0, entries.size());
    }

    public static <T> MutableSortedIntMap<T> build(Map<Integer, T> map, int maxKey) {
        return new MutableSortedIntMap<>(new ArrayList<>(map.entrySet().stream().map(
                entry -> new Entry<>(entry.getKey(), entry.getValue())
        ).collect(Collectors.toList())), maxKey);
    }

    public static <T> MutableSortedIntMap<T> empty(int maxKey) {
        return new MutableSortedIntMap<>(new ArrayList<>(), maxKey);
    }

    public static <T> MutableSortedIntMap<T> concatenate(List<MutableSortedIntMap<T>> subMaps) {
        ArrayList<Entry<T>> entries = new ArrayList<>(subMaps.stream().mapToInt(MutableSortedIntMap::size).sum());
        int maxKey = 0;
        for (MutableSortedIntMap<T> subMap : subMaps) {
            for (Entry<T> entry : subMap.entries) {
                entries.add(new Entry<>(entry.key + maxKey, entry.value));
            }
            maxKey += subMap.maxKey;
        }
        return new MutableSortedIntMap<>(entries, maxKey);
    }

    private final ArrayList<Entry<T>> entries;
    private final int maxKey;

    public MutableSortedIntMap(ArrayList<Entry<T>> entries, int maxKey) {
        this.entries = entries;
        this.maxKey = maxKey;
    }

    @Nullable
    public T get(int key) {
        int keyIndex = findKeyIndex(key);
        if (keyIndex >= entries.size() || entries.get(keyIndex).key != key) {
            return null;
        } else {
            return entries.get(keyIndex).value;
        }
    }

    public List<Entry<T>> getInRange(int minKey, int maxKey) {
        int minIndex = findKeyIndex(minKey);
        int maxIndex = findKeyIndex(maxKey);

        if (minKey <= maxKey) {
            return entries.subList(minIndex, maxIndex);
        } else {
            ArrayList<Entry<T>> entries = new ArrayList<>(minIndex + entries().size() - maxIndex);
            entries.addAll(this.entries.subList(minIndex, this.entries.size()));
            entries.addAll(this.entries.subList(0, maxIndex));
            return entries;
        }
    }

    public void put(int key, T value) {
        int keyIndex = findKeyIndex(key);
        Entry<T> entry = new Entry<>(key, value);
        if (keyIndex >= entries.size() || entries.get(keyIndex).key != key) {
            entries.add(keyIndex, entry);
        } else {
            entries.set(keyIndex, entry);
        }
    }

    public void remove(int key) {
        int keyIndex = findKeyIndex(key);
        if (keyIndex < entries.size() && entries.get(keyIndex).key == key) {
            entries.remove(keyIndex);
        }
    }

    public List<Entry<T>> entries() {
        return entries;
    }

    public int getMaxKey() {
        return maxKey;
    }

    public int size() {
        return entries.size();
    }

    public MutableSortedIntMap<T> shiftedSubMap(int minKey, int maxKey) {
        int minIndex = findKeyIndex(minKey);
        int maxIndex = findKeyIndex(maxKey);
        return new MutableSortedIntMap<>(new ArrayList<>(entries.subList(minIndex, maxIndex).stream().map(
                entry -> new MutableSortedIntMap.Entry<>(entry.getKey() - minKey, entry.getValue())
        ).collect(Collectors.toList())), maxKey - minKey);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableSortedIntMap<?> that = (MutableSortedIntMap<?>) o;
        return maxKey == that.maxKey &&
            Objects.equals(entries, that.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entries, maxKey);
    }

    @Override
    public String toString() {
        return "MutableSortedIntMap{" +
            "entries=" + entries +
            ", maxKey=" + maxKey +
            '}';
    }
}
