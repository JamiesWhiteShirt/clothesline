package com.jamieswhiteshirt.clothesline.api.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class MutableSortedIntMapTest {
    @Test
    void getIsNullForUnmappedValue() {
        MutableSortedIntMap<Object> map = MutableSortedIntMap.empty(10);
        map.put(1, new Object());
        Assertions.assertNull(map.get(0));
        Assertions.assertNull(map.get(2));
    }

    @Test
    void removeUnmapsValue() {
        MutableSortedIntMap<Object> map = MutableSortedIntMap.empty(10);
        map.put(1, new Object());
        Assertions.assertNotNull(map.get(1));
        map.remove(1);
        Assertions.assertNull(map.get(1));
    }

    @Test
    void keysAreStrictlyOrdered() {
        MutableSortedIntMap<Integer> map = MutableSortedIntMap.empty(10);
        map.put(5, 5);
        map.put(4, 4);
        map.put(7, 7);
        map.put(1, 1);
        List<MutableSortedIntMap.Entry<Integer>> entries = map.entries();
        for (int i = 1; i < entries.size(); i++) {
            Assertions.assertTrue(entries.get(i - 1).getKey() < entries.get(i).getKey());
        }
    }

    @Test
    void overridesMappings() {
        MutableSortedIntMap<Integer> map = MutableSortedIntMap.empty(10);
        map.put(4, 4);
        map.put(5, 5);
        map.put(4, 5);
        Assertions.assertEquals(map.get(4), Integer.valueOf(5));
    }

    @Test
    void concatenationSumsLength() {
        MutableSortedIntMap<Object> map1 = MutableSortedIntMap.empty(5);
        MutableSortedIntMap<Object> map2 = MutableSortedIntMap.empty(10);
        MutableSortedIntMap<Object> concatenatedMap = MutableSortedIntMap.concatenate(Arrays.asList(map1, map2));
        Assertions.assertEquals(concatenatedMap.getMaxKey(), map1.getMaxKey() + map2.getMaxKey());
    }

    @Test
    void concatenationInheritsShiftedEntries() {
        MutableSortedIntMap<Object> map1 = MutableSortedIntMap.empty(5);
        MutableSortedIntMap<Object> map2 = MutableSortedIntMap.empty(10);
        map2.put(5, new Object());
        MutableSortedIntMap<Object> concatenatedMap = MutableSortedIntMap.concatenate(Arrays.asList(map1, map2));
        Assertions.assertEquals(concatenatedMap.get(10), map2.get(5));
    }
}
