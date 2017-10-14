package com.jamieswhiteshirt.clothesline.api.util;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SortedIntShiftMap<T> {
    public static class Entry<T> {
        private final int key;
        private T value;

        public Entry(int key, T value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        private void setValue(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
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

    public static <T> SortedIntShiftMap<T> build(Map<Integer, T> map, int maxKey) {
        return new SortedIntShiftMap<>(new ArrayList<>(map.entrySet().stream().map(
                entry -> new Entry<>(entry.getKey(), entry.getValue())
        ).collect(Collectors.toList())), maxKey);
    }

    public static <T> SortedIntShiftMap<T> empty(int maxKey) {
        return new SortedIntShiftMap<>(new ArrayList<>(), maxKey);
    }

    public static <T> SortedIntShiftMap<T> concatenate(List<SortedIntShiftMap<T>> subMaps) {
        ArrayList<Entry<T>> entries = new ArrayList<>(subMaps.stream().mapToInt(SortedIntShiftMap::size).sum());
        int maxKey = 0;
        for (SortedIntShiftMap<T> subMap : subMaps) {
            for (Entry<T> entry : subMap.entries) {
                entries.add(new Entry<>(entry.key + maxKey, entry.value));
            }
            maxKey += subMap.maxKey;
        }
        return new SortedIntShiftMap<>(entries, maxKey);
    }

    private final ArrayList<Entry<T>> entries;
    private final int maxKey;

    public SortedIntShiftMap(ArrayList<Entry<T>> entries, int maxKey) {
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
        if (keyIndex <= entries.size() && entries.get(keyIndex).key != key) {
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

    public SortedIntShiftMap<T> subMap(int minKey, int maxKey) {
        int minIndex = findKeyIndex(minKey);
        int maxIndex = findKeyIndex(maxKey);
        return new SortedIntShiftMap<>(new ArrayList<>(entries.subList(minIndex, maxIndex).stream().map(
                entry -> new SortedIntShiftMap.Entry<>(entry.getKey() - minKey, entry.getValue())
        ).collect(Collectors.toList())), maxKey - minKey);
    }
}
