package lukfor.nf.test.csv;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.util.Objects;

public class TableComparator {

    /**
     * Checks if two tables are equal.
     * Tables are considered equal if they have the same data,
     * with precision considered for number columns.
     * @param table1 the first table to compare
     * @param table2 the second table to compare
     * @param precision the minimum acceptable difference between numerical values
     * @return true if the tables are equal, false otherwise
     * @throws TableComparisonException if the tables are not equal, with details on why they are not equal
     */

    public static boolean equals(Table table1, Table table2, double precision) throws TableComparisonException {
        if (table1.rowCount() != table2.rowCount()) {
            throw new TableComparisonException("Row count mismatch: " + table1.rowCount() + " vs " + table2.rowCount());
        }
        if (table1.columnCount() != table2.columnCount()) {
            throw new TableComparisonException("Column count mismatch: " + table1.columnCount() + " vs " + table2.columnCount());
        }
        if (!table1.columnNames().equals(table2.columnNames())) {
            throw new TableComparisonException("Column names mismatch: " + table1.columnNames() + " vs " + table2.columnNames());
        }

        for (int i = 0; i < table1.rowCount(); i++) {
            for (Column<?> column : table1.columns()) {
                String columnName = column.name();
                Object thisValue = column.get(i);
                Object thatValue = table2.column(columnName).get(i);

                if (thisValue instanceof Number && thatValue instanceof Number) {
                    double thisDouble = ((Number) thisValue).doubleValue();
                    double thatDouble = ((Number) thatValue).doubleValue();
                    if (Math.abs(thisDouble - thatDouble) > precision) {
                        throw new TableComparisonException("Value mismatch in row " + i + " column " + columnName +
                                ": " + thisDouble + " vs " + thatDouble + " with precision " + precision);
                    }
                } else if (!Objects.equals(thisValue, thatValue)) {
                    throw new TableComparisonException("Value mismatch in row " + i + " column " + columnName +
                            ": " + thisValue + " vs " + thatValue);
                }
            }
        }

        return true;
    }
}