package q3;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/** Q3 - three designed tests with PREDICTIONS printed before each result. */
public class HashContractTests {

    /** The junior developer's class: equals() overridden, hashCode() NOT. */
    static class BadStudent {
        final String regNo; final String name;
        BadStudent(String r, String n) { regNo = r; name = n; }
        @Override public boolean equals(Object o) { return o instanceof BadStudent b && regNo.equals(b.regNo); }
    }
    /** Corrected class. */
    static class GoodStudent {
        final String regNo; final String name;
        GoodStudent(String r, String n) { regNo = r; name = n; }
        @Override public boolean equals(Object o) { return o instanceof GoodStudent g && regNo.equals(g.regNo); }
        @Override public int hashCode() { return Objects.hash(regNo); }
    }
    /** Mutable key: regNo can change after insertion. */
    static class MutableStudent {
        String regNo; MutableStudent(String r) { regNo = r; }
        @Override public boolean equals(Object o) { return o instanceof MutableStudent m && regNo.equals(m.regNo); }
        @Override public int hashCode() { return regNo.hashCode(); }
    }
    record Named(String regNo, String name) { }

    public static void main(String[] args) {
        // ---- Test 1: appears to work (the SAME object reference is scanned twice) ----
        BadStudent same = new BadStudent("101", "Arun");
        Set<BadStudent> s1 = new HashSet<>();
        s1.add(same); s1.add(new BadStudent("102", "Divya")); s1.add(same);
        System.out.println("TEST 1 predict 2 (same reference => identical identity hash). Actual: " + s1.size());

        // ---- Test 2: exposes duplicates (two DIFFERENT objects, equal regNo) ----
        Set<BadStudent> s2 = new HashSet<>();
        s2.add(new BadStudent("101", "Arun")); s2.add(new BadStudent("102", "Divya")); s2.add(new BadStudent("101", "Arun"));
        System.out.println("TEST 2 predict 3 (equal objects get different identity hashCodes => different buckets, equals never called). Actual: " + s2.size());

        Set<GoodStudent> s2b = new HashSet<>();
        s2b.add(new GoodStudent("101", "Arun")); s2b.add(new GoodStudent("102", "Divya")); s2b.add(new GoodStudent("101", "Arun"));
        System.out.println("TEST 2b (after fix) predict 2. Actual: " + s2b.size());

        // ---- Test 3: mutating a field used in hashCode() after insertion ----
        Set<MutableStudent> s3 = new HashSet<>();
        MutableStudent m = new MutableStudent("101");
        s3.add(m);
        m.regNo = "999";
        System.out.println("TEST 3 predict contains(m)=false and size 1 (element stranded in the old bucket).");
        System.out.println("        contains(m) = " + s3.contains(m) + ", size = " + s3.size()
                + ", can add equal again: " + s3.add(new MutableStudent("999")) + " -> size " + s3.size());

        // ---- Test 4: TreeSet whose comparator is inconsistent with equals ----
        Set<Named> byName = new TreeSet<>(Comparator.comparing(Named::name));
        byName.add(new Named("101", "Arun")); byName.add(new Named("205", "Arun"));
        System.out.println("TEST 4 predict 1 (TreeSet uses compare()==0 as 'equal', two different students share a name). Actual: " + byName.size());

        // ---- Alternatives: HashMap keyed by regNo, TreeSet with comparator on regNo ----
        Map<String, GoodStudent> map = new HashMap<>();
        map.putIfAbsent("101", new GoodStudent("101", "Arun")); map.putIfAbsent("101", new GoodStudent("101", "Arun"));
        Set<Named> byReg = new TreeSet<>(Comparator.comparing(Named::regNo));
        byReg.add(new Named("101", "Arun")); byReg.add(new Named("101", "Arun"));
        System.out.println("HashMap<String,Student> size = " + map.size() + "; TreeSet by regNo size = " + byReg.size());

        // ---- small timing: n scans ----
        int n = 500_000;
        long t0 = System.nanoTime(); Set<GoodStudent> hs = new HashSet<>();
        for (int i = 0; i < n; i++) hs.add(new GoodStudent("R" + (i % 100_000), "x"));
        long a = System.nanoTime() - t0; t0 = System.nanoTime();
        Map<String, GoodStudent> hm = new HashMap<>();
        for (int i = 0; i < n; i++) hm.putIfAbsent("R" + (i % 100_000), new GoodStudent("R" + (i % 100_000), "x"));
        long b = System.nanoTime() - t0; t0 = System.nanoTime();
        Set<Named> ts = new TreeSet<>(Comparator.comparing(Named::regNo));
        for (int i = 0; i < n; i++) ts.add(new Named("R" + (i % 100_000), "x"));
        long c = System.nanoTime() - t0;
        System.out.printf("%,d scans, %,d unique: HashSet %d ms (O(1) avg) | HashMap %d ms (O(1) avg) | TreeSet %d ms (O(log n))%n",
                n, hs.size(), a / 1_000_000, b / 1_000_000, c / 1_000_000);
        System.out.println("Space for n unique: HashSet/HashMap = n entries + table (HashSet is backed by a HashMap); TreeSet = n tree nodes (3 refs + colour). All O(n).");
    }
}
