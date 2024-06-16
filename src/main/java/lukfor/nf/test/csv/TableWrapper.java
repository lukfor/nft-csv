package lukfor.nf.test.csv;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.Column;

import java.util.*;
import java.util.stream.Collectors;

public class TableWrapper {

    private Table table;

    public TableWrapper(Table table) {
        this.table = table;
    }

    /**
     * Returns the number of rows in the table.
     * @return the number of rows
     */
    public int getRowCount() {
        return table.rowCount();
    }

    /**
     * Returns the number of columns in the table.
     * @return the number of columns
     */
    public int getColumnCount() {
        return table.columnCount();
    }

    /**
     * Returns the names of the columns in the table.
     * @return a list of column names
     */
    public List<String> getColumnNames() {
        return table.columnNames();
    }

    /**
     * Returns the rows of the table as a list of maps.
     * Each map represents a row with column names as keys.
     */
    public List<Map<String, Object>> getRows() {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = 0; i < table.rowCount(); i++) {
            Map<String, Object> row = new HashMap<>();
            for (Column<?> column : table.columns()) {
                row.put(column.name(), column.get(i));
            }
            rows.add(row);
        }
        return rows;
    }

    /**
     * Returns the columns of the table as a map of lists.
     * Each entry in the map represents a column with the column name as the key.
     */
    public Map<String, List<Object>> getColumns() {
        Map<String, List<Object>> columns = new HashMap<>();
        for (Column<?> column : table.columns()) {
            List<Object> columnData = new ArrayList<>();
            for (int i = 0; i < column.size(); i++) {
                columnData.add(column.get(i));
            }
            columns.put(column.name(), columnData);
        }
        return columns;
    }

    /**
     * Sorts the rows of the table based on the values in the specified column.
     * @param columnName the column to sort by
     * @param ascending true for ascending order, false for descending
     */
    public TableWrapper sortRows(String columnName, boolean ascending) {
        if (ascending) {
            table.sortAscendingOn(columnName);
        } else {
            table.sortDescendingOn(columnName);
        }
        return this;
    }

    /**
     * Sorts the rows of the table based on the values in the specified column.
     * @param columnName the column to sort by
     */
    public TableWrapper sortRows(String columnName) {
        return sortRows(columnName, true);
    }

    /**
     * Sorts the rows of the table based on all columns in their natural order.
     */
    public TableWrapper sortRows() {
        List<Column<?>> columns = table.columns();
        String[] columnNames = columns.stream().map(Column::name).toArray(String[]::new);
        table.sortOn(columnNames);
        return this;
    }

    /**
     * Sorts the columns of the table alphabetically by their names.
     * @param ascending true for ascending order, false for descending
     */
    public TableWrapper sortColumns(boolean ascending) {
        List<Column<?>> sortedColumns = table.columns().stream()
                .sorted(Comparator.comparing(Column::name))
                .collect(Collectors.toList());

        if (!ascending) {
            Collections.reverse(sortedColumns);
        }

        Table sortedTable = Table.create(table.name());
        for (Column<?> column : sortedColumns) {
            sortedTable.addColumns(column);
        }

        // Replace the current table with the sorted table
        this.table = sortedTable;
        return this;
    }

    /**
     * Sorts the columns of the table alphabetically by their names in ascending order.
     */
    public TableWrapper sortColumns() {
        sortColumns(true);
        return this;
    }

    /**
     * Sorts the columns of the table alphabetically by their names in ascending order and all rows.
     */
    public TableWrapper sort() {
        return sortColumns(true).sortRows();
    }

    /**
     * Returns the raw Table object.
     * @return the raw Table object
     */
    public Table getTable() {
        return table;
    }

    public TableWrapper view() {
        System.out.println();
        System.out.println(table.structure()+ "\n");
        System.out.println("Size: " + getColumnCount() + " columns, " + getRowCount() + "rows.");
        System.out.println();
        return this;
    }

}
