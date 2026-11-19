# CSA09 - Programming in Java - Assignment 5 (CO5)

Name: ______________   Register No: ______________   Java version: ______

## How to open in IntelliJ IDEA
1. Unzip the folder.  **File > Open** > select the folder containing `pom.xml` > *Open as Project*.
2. Wait for Maven to finish downloading (MySQL + H2 drivers). Set **Project SDK = JDK 11 or newer**
   (if your JDK is older than 17, change `maven.compiler.source/target` in `pom.xml`).
3. Open any class listed below, click the green arrow next to `main` > **Run**.

## Database setting (one line to change) - `src/main/java/common/DbConfig.java`
* `USE_H2 = true`  : in-memory H2 database, nothing to install. Good for first runs.
* `USE_H2 = false` : your local MySQL. Set `MYSQL_USER` / `MYSQL_PASSWORD`. The database `busdb`
  and all tables are created automatically (or run `sql/schema.sql`).
  **Use MySQL for the final run of B3/B4 - the assignment asks for MySQL measurements.**

## Which file answers which task
| Task | Run this class | What it shows |
|------|----------------|---------------|
| A1 (layouts, alt. concept) | `task_a1.BinaryPassFile`, `task_a1.FixedRecordFile` | DataOutputStream layout; RandomAccessFile O(1) seek vs O(n) scan, padding waste |
| A2 (buffering) | `task_a2.A2Demo` (uses `LineCounters`) | predicted calls, unbuffered vs buffered vs byte[] vs readAllLines |
| A3 (DAO) | `task_a3.DaoDemo` (`Dao`, `Student`, `StudentDao`) | generic Dao<T,K>, object-table mapping |
| A4 (serialization) | `task_a4.TransientDemo`, `HostelSave` -> `HostelLoad`, `SerializationVsCsv` | transient, object graph, versioning steps (see comments in `Resident.java`), CSV comparison |
| A5 (transactions) | `task_a5.TransferDemo` | auto-commit loses money; transaction rolls back |
| B1 | `task_b1.PassStore` (completed TODO 1), `PassStoreTest`, `BufferingBenchmark`, `GrowthBenchmark` | tests B1.1, buffering B1.3 (1/10/50 MB), growth B1.4, HashMap alternative |
| B2 | `task_b2.LoginInjectionDemo`, `DamagedFileDemo` | injection vs PreparedStatement, escaping/regex weakness, damaged file |
| B3 | `task_b3.SmallDataCheck`, `JdbcBenchmark`, `TransactionFailureDemo` (`PassLoader` = completed TODO 1-3) | B3.1, B3.2, B3.3 |
| B4 | `task_b4.MigrationTool`, `MetadataReport`, `ConcurrentCountersDemo` | migration + failure report, DatabaseMetaData, concurrent counters |

## Notes
* `BufferingBenchmark`: the unbuffered 50 MB run makes ~52 million read calls - it can take a minute or more.
* `JdbcBenchmark` on MySQL: Statement / PreparedStatement at 100,000 rows can take minutes. That is the point of B3.2.
* Part A answers must be **pseudocode in the SIMATS standard** (Java is not accepted there). The `task_a*`
  programs are only supporting experiments for your reasoning.
* Take screenshots of the console output of each run, plus your own measured numbers, for the report.
