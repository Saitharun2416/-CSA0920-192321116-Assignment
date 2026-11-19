package q5;

import java.util.*;

/** Q5 - tie-heavy data, faulty comparators, wildcards/bounded generics, and the alternatives. */
public class Q5Tests {

    /** Bounded generic helper: works for any T that is Comparable to itself (or a supertype). */
    static <T extends Comparable<? super T>> List<T> rankNatural(Collection<? extends T> in) {
        List<T> copy = new ArrayList<>(in); Collections.sort(copy); return copy;
    }
    /** Wildcard: accepts List<Team>, List<SubTeam>... as a read-only producer (PECS: Producer Extends). */
    static <T> void addAll(Leaderboard<T> board, List<? extends T> source) { for (T t : source) board.add(t); }

    static class CompTeam extends Team implements Comparable<CompTeam> {
        CompTeam(String n, int p, int g) { super(n, p, g); }
        @Override public int compareTo(CompTeam o) {            // Comparable = ONE natural order only
            int c = Integer.compare(o.points, points); if (c != 0) return c;
            c = Integer.compare(o.goalDiff, goalDiff); if (c != 0) return c;
            return name.compareTo(o.name);
        }
    }

    public static void main(String[] args) {
        Comparator<Team> byRank = Comparator.comparingInt((Team t) -> t.points).reversed()
                .thenComparing(Comparator.comparingInt((Team t) -> t.goalDiff).reversed())
                .thenComparing(t -> t.name);

        // ---- data set 1: several ties at every level ----
        List<Team> data = List.of(new Team("Delta", 9, 4), new Team("Alpha", 9, 4), new Team("Cobras", 9, 6),
                new Team("Bison", 6, 2), new Team("Eagle", 6, 2), new Team("Falcon", 6, 5), new Team("Gnu", 3, 0), new Team("Hawk", 3, 0));
        Leaderboard<Team> lb = new Leaderboard<>(byRank); addAll(lb, data);
        System.out.println("Ranking (ties at points, goal diff, and name decides): " + lb.ranking());

        // ---- data set 2: designed to break a faulty comparator ----
        Team big = new Team("Big", Integer.MAX_VALUE, 0), neg = new Team("Neg", -5, 0);
        Comparator<Team> bySubtraction = (a, b) -> b.points - a.points;      // FAULTY: overflow
        System.out.println("Faulty subtraction compare(Big, Neg) = " + bySubtraction.compare(big, neg)
                + "  (should be NEGATIVE: Big ranks first; overflow gives a POSITIVE value)");
        System.out.println("Correct Integer.compare            = " + Comparator.comparingInt((Team t) -> t.points).reversed().compare(big, neg));

        Comparator<Team> ignoresTie = Comparator.comparingInt((Team t) -> t.points).reversed();   // FAULTY: no tie-breakers
        List<Team> ab = new ArrayList<>(List.of(new Team("Zed", 5, 1), new Team("Amy", 5, 9)));
        List<Team> ba = new ArrayList<>(List.of(new Team("Amy", 5, 9), new Team("Zed", 5, 1)));
        ab.sort(ignoresTie); ba.sort(ignoresTie);
        System.out.println("Ignoring tie-breakers: input order A,B -> " + ab + " | input order B,A -> " + ba + "   (result depends on INPUT order = inconsistent ranking)");
        ab.sort(byRank); ba.sort(byRank);
        System.out.println("Full comparator      : " + ab + " | " + ba + "   (same either way)");

        // ---- alternatives ----
        List<CompTeam> ct = new ArrayList<>(List.of(new CompTeam("Panthers", 9, 4), new CompTeam("Eagles", 9, 6), new CompTeam("Cobras", 6, 1), new CompTeam("Bisons", 9, 4)));
        System.out.println("Comparable version : " + rankNatural(ct));

        List<Team> three = new ArrayList<>(List.of(new Team("Panthers", 9, 4), new Team("Eagles", 9, 6), new Team("Cobras", 6, 1), new Team("Bisons", 9, 4)));
        three.sort(Comparator.comparing((Team t) -> t.name));                                     // sort 1 (least significant key)
        three.sort(Comparator.comparingInt((Team t) -> t.goalDiff).reversed());                   // sort 2
        three.sort(Comparator.comparingInt((Team t) -> t.points).reversed());                     // sort 3 (most significant key)
        System.out.println("Three stable sorts : " + three + "   (3 x O(n log n), relies on sort STABILITY)");
        System.out.println("Comparator chain: 1 sort O(n log n), extra space O(n) copy (merge buffer); Comparable: 1 sort but only ONE order built into Team;");
        System.out.println("  three sorts: 3 x O(n log n) time, same O(n) space. Comparator chain is easiest to reuse for players/colleges: pass a different Comparator, Leaderboard<T> unchanged.");
        System.out.println("Why Bisons is above Panthers: points equal (9), goal diff equal (4), name decides: 'Bisons' < 'Panthers' alphabetically.");
    }
}
