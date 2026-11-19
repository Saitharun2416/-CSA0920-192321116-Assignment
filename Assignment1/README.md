# CSA09 - Assignment 1 : Campus Smart-Parking System (Java)

Name: ____________  Register No: ____________  Java version: 17 or newer (tested on 21)

## Open in IntelliJ IDEA
1. File > Open > select this folder (`CSA09-Assignment1`).
2. If asked, set Project SDK (File > Project Structure > Project > SDK = JDK 17+). `src` is already the source root.
3. Open a file listed below and click the green arrow beside `main` (or right-click > Run).

## Run guide (one package per task - one commit per task)
| Task | Run this class | What it shows |
|------|----------------|---------------|
| 1 | `task01.Task01Analysis` | candidate classes, rules, objects vs parallel arrays search timing |
| 2 | `task02.Task02DataTypes` (add program argument `--interactive` to use Scanner) | data types, integer-division fee bug, jagged vs flat vs ArrayList |
| 3 | `task03.ParkingFacility` (Complete the Code; expected output matches the PDF) | allocate / release |
|   | `task03.FastFacility`, `task03.Task03Benchmark`, `task03.Task03Fee`, `task03/pseudocode.txt` | stack alternative, timing, fee slabs, pseudocode |
| 4 | `task04.Task04Demo` (class `ParkingTicket`) | constructors, chaining, copy ctor, setters/factory alternatives |
| 5 | `task05.Task05Static` | static counter, static block, two facilities, GC |
| 6 | `task06.Task06Demo` (+ `task06/UML.md`) | inheritance, constructor-order trace, composition alternative |
| 7 | `task07.Task07Demo` | overriding, final, equals/hashCode, switch alternative |
| 8 | `task08.FeeDemo` (Complete the Code) then `task08.Task08Extras` | expected output matches PDF; overloading, dispatch trace, instanceof alternative |
| 9 | `task09.Task09Demo` | abstract class vs interface, default/static methods |
| 10 | `task10.app.Task10Demo` (packages model/service/app) | encapsulation, validation, Integer == pitfall |
| 11 | `task11.Task11App` (27 tests) and `task11.Task11Benchmark` | full system, test table, array/ArrayList/HashMap |
| 12 | `task12.Task12App` | e-bike, delivery van, one-day pass, 5 uneven floors, regression of all Task 11 tests |

## Design-challenge result (Task 12)
Decision: RETAIN + EXTEND. 0 existing classes modified; 3 new classes (`EBike`, `DeliveryVan`, `OneDayPass`); 2 `VehicleFactory.register(...)` calls.
This works because Task 11 already had: a `Pass` interface, a `Vehicle.calculateFee(entryMinute, minutes)` hook, a registry factory, and a jagged slot array.

## Before you submit (important)
- The programs print the evidence; the REPORT (analysis, UML, pseudocode, complexity discussion, defence) must be written by you in your own words using these outputs.
- Run every program on your machine and take your own output screenshots. Timings differ by machine - use your numbers.
- Create the repo `CSA09-<RegisterNumber>-Assignment1`, one folder per task, commit after each task, add this README with your details.
- Be ready to explain / modify any line in the viva.
