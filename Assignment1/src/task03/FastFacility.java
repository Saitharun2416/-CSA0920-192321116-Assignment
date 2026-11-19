package task03;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * TASK 3 - Alternative concept: free-count per floor + stack of free slots per floor.
 * allocate: O(F) (find a floor with freeCount>0, then pop O(1))   release: O(1)
 * extra space: O(F) counts + O(F*S) stack entries.
 * (The scanning version: allocate O(F*S) worst case, release O(1), extra space O(1).)
 */
public class FastFacility {
    private final String[][] slots;
    private final int[] freeCount;
    private final Deque<Integer>[] freeStack;

    @SuppressWarnings("unchecked")
    public FastFacility(int[] slotsPerFloor) {
        slots = new String[slotsPerFloor.length][];
        freeCount = new int[slotsPerFloor.length];
        freeStack = new Deque[slotsPerFloor.length];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            slots[f] = new String[slotsPerFloor[f]];
            freeCount[f] = slotsPerFloor[f];
            freeStack[f] = new ArrayDeque<>();
            for (int s = slotsPerFloor[f] - 1; s >= 0; s--) freeStack[f].push(s); // lowest slot on top
        }
    }

    public int[] allocate(String vehicleNo) {
        for (int f = 0; f < slots.length; f++) {
            if (freeCount[f] == 0) continue;            // skip full floors without scanning slots
            int s = freeStack[f].pop();
            freeCount[f]--;
            slots[f][s] = vehicleNo;
            return new int[]{f, s};
        }
        return null;
    }

    public boolean release(int floor, int slot) {
        if (floor < 0 || floor >= slots.length || slot < 0 || slot >= slots[floor].length) return false;
        if (slots[floor][slot] == null) return false;
        slots[floor][slot] = null;
        freeStack[floor].push(slot);
        freeCount[floor]++;
        return true;
    }

    public static void main(String[] args) {
        FastFacility p = new FastFacility(new int[]{2, 1});
        System.out.println(Arrays.toString(p.allocate("A")) + Arrays.toString(p.allocate("B"))
                + Arrays.toString(p.allocate("C")) + Arrays.toString(p.allocate("D")));
        System.out.println(p.release(0, 1) + " " + p.release(0, 1) + " " + p.release(5, 0));
        System.out.println(Arrays.toString(p.allocate("D")));
    }
}
