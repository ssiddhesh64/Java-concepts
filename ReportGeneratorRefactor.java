/**
 * CONCEPT TAUGHT: Stateless Exporter & Decoupling
 * 
 * WHY THIS WAS WRITTEN:
 * - Refactors report exporting into a stateless service pattern using records.
 * 
 * KEY LESSONS:
 * - Separate state (records) from behaviors (stateless service classes).
 * - Method arguments should pass dynamic state; constructors should inject stateless dependencies.
 */
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class ReportGeneratorOld {
    public void generateReport(List<TransactionOld> txs, String outputPath) {
        if (txs != null && txs.size() > 0) {
            double totalSales = 0;
            List<TransactionOld> highValueTxs = new ArrayList<>();

            for (TransactionOld tx : txs) {
                if (tx != null) {
                    if (tx.getStatus().equals("COMPLETED")) {
                        totalSales += tx.getAmount();
                        if (tx.getAmount() > 1000) {
                            highValueTxs.add(tx);
                        }
                    }
                }
            }

            System.out.println("Summary:");
            System.out.println("Total Sales: " + totalSales);
            System.out.println("High Value Count: " + highValueTxs.size());

            // Format to CSV and write to file (Legacy resource management)

        } else {
            System.out.println("No transactions to process");
        }
    }
}

class TransactionOld {
    private String id;
    private double amount;
    private String status;

    public TransactionOld(String id, double amount, String status) {
        this.id = id;
        this.amount = amount;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}

record Transaction(String id, double amount, String status) {
}

interface WriteFile {
    void write(String outputPath, List<Transaction> highValueTxs);
}

class CsvWriteFile implements WriteFile {

    @Override
    public void write(String outputPath, List<Transaction> highValueTxs) {

        try (var writer = new FileWriter(outputPath)) {
            writer.write("ID,Amount,Status\n");
            for (Transaction tx : highValueTxs) {
                writer.write(tx.id() + "," + tx.amount() + "," + tx.status() + "\n");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to write CSV report to: " + outputPath, ex);
        }
    }
}

public class ReportGeneratorRefactor {

    private final WriteFile fileWriter;

    ReportGeneratorRefactor(WriteFile writer) {
        this.fileWriter = writer;
    }

    public void generateReport(List<Transaction> txs, String outputPath) {
        if (txs == null || txs.isEmpty()) {
            System.out.println("No transactions to process");
            return;
        }

        List<Transaction> alltxns = txs.stream().filter(Objects::nonNull)
                .filter(tx -> tx.status().equalsIgnoreCase("COMPLETED"))
                .toList();

        System.out.println("Summary:");
        System.out.println("Total Sales: " + alltxns.stream().mapToDouble(Transaction::amount).sum());
        System.out.println("High Value Count: " + alltxns.stream().filter(txn -> txn.amount() > 1000).count());

        // Format to CSV and write to file (Legacy resource management)
        fileWriter.write(outputPath, alltxns);
    }
}
