package com.rntgroup.customparser.comparator;

import java.util.Comparator;
import java.util.Map.Entry;

public class TagCounterComparator implements Comparator<Entry<String, Integer>> {
    @Override
    public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
        return e2.getValue().compareTo(e1.getValue());
    }
}
