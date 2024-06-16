# nft-csv

nf-test plugin to provide support for CSV files based on [tablesaw](https://jtablesaw.github.io/tablesaw/).

## Requirements

- nf-test version 0.7.0 or higher

## Setup

To use this plugin you need to activate the `nft-csv` plugin in your `nf-test.config` file:

```
config {
  plugins {
    load "nft-csv@0.1.0"
  }
}
```

## Usage

nft-csv extends `path` by a `csv` property that can be used to parse csv files. The csv property could be configured with different options.

Example:

```groovy
def csvFile = path("file.csv").csv

def tabFile = path("file.tab").csv(sep: "\t")

def CsvFile = path("file.csv").csv(sep: "\t", quote: '"')

def gzCsvFile = path("file.csv.gz").csv(decompress: true)

def csvFile = csv("https://raw.githubusercontent.com/jtablesaw/tablesaw/master/data/bush.csv")

```

Available options:

- `header`: When true, the first line is used as the columns names. Otherwise, the columns are named "C0", "C1", ... (default: `true`).
- `sep`: The character used to separate values (default: `,`)
- `quote`: The character used to quote values (default:  `""`).
- `decompress`: When true, decompress the content using the GZIP format before processing it (default: `false`). Files with the `.gz` extension are **NOT YET** decompressed automatically.

The result is an object of class `TableWrapper` that contains the following properties anf methods:

#### `rowCount`

Returns the number of rows in the table.

Examples:

```groovy
assert path("file.csv").csv.rowCount == 4

//or
with( path("file.tab").csv(sep: "\t")) {
    assert rowCount == 4
}
```


#### `columCount`

Returns the number of columns in the table.

Examples:

```groovy
assert path("file.csv").csv.columCount == 3

//or
with( path("file.csv").csv) {
    assert columCount == 3
}
```


#### `columnNames`

Returns the names of the columns in the table.

Examples:

```groovy
def csvFile = path("file.csv").csv
assert pcsvFile.columnNames == ["col_a", "col_b", "col_c"]
assert "col_b" in csvFile.columnNames
assert "lukas" !in csvFile.csv.columnNames

//or
with( path("file.csv").csv) {
    assert columnNames == ["col_a", "col_b", "col_c"]
    assert "col_b" in columnNames
    assert "lukas" !in columnNames
}
```


#### `rows`

Returns the rows of the table as a list of maps. Each map represents a row with column names as keys.

Examples:

```groovy
with (path("file.csv").csv) {
    assert rows[1] == ["col_a": 4, "col_b": 5, "col_c": 6]
    assert rows[1] == ["col_c": 6, "col_a": 4, "col_b": 5]
}
```


#### `columns`

Returns the columns of the table as a map of lists. Each entry in the map represents a column with the column name as the key.

Examples:

```groovy
assert columns["col_b"] == [2, 5 ,8, 11]
assert columns["col_b"] != [5, 2 ,8, 11]
```


#### `sort()`

Sorts the columns and rows of the table based on their natural order. This makes it possible to compare tables with a different order of columns and rows with expected table data.
Examples:

```groovy
def sortedTable1 = path("file1.csv").csv.sort()
```


#### `sortRows()`

Sorts the rows of the table based on all columns in their natural order.

Examples:

```groovy
def sortedTable1 = path("file1.csv").csv.sortRows()
```


#### `sortRows(String columnName, boolean ascending)`

Sorts the rows of the table based on the values in the specified column.
- `columnName`: The column to sort by.
- `ascending`: `true` for ascending order, `false` for descending. (optional)

Examples:

```groovy
def sortedTable1 = path("file1.csv").csv.sortRows("col_b").sortRows("col_a", false)
```


#### `sortColumns()`

Sorts the columns of the table alphabetically by their names in ascending order.


```groovy
def csvFile = path("file.csv").csv
assert csvFile.columnNames == ["col_c", "col_a", "col_b"]

def csvFile2 = path("file.csv").csv.sortColumns()
assert csvFile2.columnNames == ["col_a", "col_b", "col_c"]
```

#### `sortColumns(boolean ascending)`

Sorts the columns of the table alphabetically by their names.
- `ascending`: `true` for ascending order, `false` for descending.

Examples:

```groovy
def csvFile = path("file.csv").csv
assert csvFile.columnNames == ["col_c", "col_a", "col_b"]

def csvFile2 = path("file.csv").csv.sortColumns(false)
assert csvFile2.columnNames == ["col_c", "col_b", "col_a"]

with(path("file.csv").csv.sortColumns(false)) {
    assert columnNames == ["col_c", "col_b", "col_a"]
}
```


#### `view()`

Prints the strucuture and number of rows and columns.

```groovy
path("file.csv").csv.view()
```

#### `table`

Returns the [tablesaw](https://jtablesaw.github.io/tablesaw/userguide/tables.html) table instance and allows you to use all the methods to shape, merge and filter your data.

Examples:

```groovy
//print table structure
print path(filename).csv.table.structure()
```

Most tablesaw operations create a new table object. You could use the `csv` function to create a `TableWrapper` object to use its methods.

Examples:
```groovy
 def filename = process.out.csv.get(0)
with(path(filename).csv){
    assert columnNames == ["col_a", "col_b", "col_c"]
    assert rowCount == 4
    assert columnCount == 3
}
def table = csv(path(filename).csv.table.select("col_c", "col_a"))

with(table){
    assert columnNames == ["col_c", "col_a"]
    assert rowCount == 4
    assert columnCount == 2
}
```

### Assertions

This plugin provides two assertions to simplify the comparison of tables containing numbers, using a specified precision.

#### `assertTableEquals(array1, array2, double precision)`

This could be used to compare tables where columns with numbers use the default precision of 0.00001.

```groovy
then {
    def expected = csv("tests/data/input/chr20-unphased/scores.expected.txt")
    def actual = csv("${outputDir}/scores.txt")
    assertTableEquals actual, expected
}
```

A user defined precision can be provided:

```groovy
then {
    assertTableEquals actual, expected, 0.000000001
}
```

The order of rows and columns has to be same between the tables. However, you could combine it with `sortColumns()`, `sortRows()` or `sort()` to normalize the files:

```groovy
then {
    def expected = csv("tests/data/input/chr20-unphased/scores.expected.txt").sort()
    def actual = csv("${outputDir}/scores.txt").sort()
    assertTableEquals actual, expected, 0.00001
}
```

#### `assertArrayEquals(array1, array2, double precision)`

This could be used to compare arrays containing numbers with precision.

Examples:

```groovy
then {
    def expected = csv("tests/data/input/chr20-unphased/scores.expected.txt")

    def actual = csv("${outputDir}/scores.txt")
    with(actual) {
        assert columnNames == ["sample", "PGS000027"]
        assert columns["sample"] == expected.columns["sample"]
        assertArrayEquals columns["PGS000027"], expected.columns["PGS000027"]
        // or with user defined precision
        assertArrayEquals columns["PGS000027"], expected.columns["PGS000027"], 0.0000001
    }
}
```

## Contact

Lukas Forer (@lukfor)
