# CSA09 - Assignment 2 : Strings, Collections, Generics (Java)

Name: ____________  Register No: ____________  Java version: 17 or newer (tested on 21)

## Open in IntelliJ IDEA
File > Open > this folder. Set Project SDK to JDK 17+ if asked. `src` is the source root. Run any `main` with the green arrow.
Tip: for the benchmarks, use Run > Edit Configurations > VM options `-Xmx2g`, and run each 3 times; keep the raw numbers for your report.

## Run guide
| Q | Run this class | Notes |
|---|----------------|-------|
| 1 | `q1.Q1StringBench`, `q1.Q1ThreadSafety` | 3 workloads x 6 techniques; prediction is in the file header. The thread race is not guaranteed to show on every run |
| 2 | `q2.Q2ListBench` | ArrayList / LinkedList / ArrayDeque / plain array at 3 sizes + complexity table |
| 3 | `q3.Attendance` (Complete the Code, prints 2), `q3.HashContractTests` | For the "before" output: comment out `hashCode()` in `Attendance.java`, run (prints 3), restore it |
| 4 | `q4.Q4MapBench` | HashMap/TreeMap/LinkedHashMap at 1k, 100k, 1M + binarySearch alternative |
| 5 | `q5.LeaderboardDemo` (Complete the Code), `q5.Q5Tests` | expected `[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]`; faulty comparators; alternatives |
| 6 | `q6.Triage` (Complete the Code), `q6.Q6Experiments` | hand-verified 8-patient check, growth, starvation + aging, alternatives |
| 7 | `q7.Q7Redesign` | Design challenge: naive aging triage (re-score all, O(n)/call) -> time-invariant key + heap (O(log n)); verifies identical service order |

## Q7 summary (chosen: Q6 triage with the fairness rule)
- Diagnosis: aging needs `severity - waited/T`, which changes with time, so the naive version re-scores all n patients on every `next()` (O(n), O(n^2) per day).
- Insight: multiplying by T and adding the common `now` leaves ordering unchanged => key = `arrival + T*severity` is fixed at admission => normal heap.
- Evidence (this machine): comparisons 595,000,132 -> 1,477,786 at 100,000 ops, identical service order at every size.

## Before you submit
- Write the report yourself from your own runs: predictions BEFORE results, averaged repeated measurements, raw data tables/graphs, complexity answers, Q7 sections A-F.
- Screenshots of your output, a GitHub repo `CSA09-<RegisterNumber>-Assignment2` with one folder per Q and a commit per Q.
- Be ready to explain any line in the viva.
