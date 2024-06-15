package org.example.arissoft;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelExporter  {

    public static <T> void exportToExcel(TableView<T> tableView, String filePath) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet 1");

        // Write header row
        Row headerRow = sheet.createRow(0);
        ObservableList<TableColumn<T, ?>> columns = tableView.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            TableColumn<T, ?> column = columns.get(i);
            headerRow.createCell(i).setCellValue(column.getText());
        }

        // Write data rows
        ObservableList<T> items = tableView.getItems();
        for (int i = 0; i < items.size(); i++) {
            Row dataRow = sheet.createRow(i + 1);
            T item = items.get(i);
            for (int j = 0; j < columns.size(); j++) {
                TableColumn<T, ?> column = columns.get(j);
                Object cellValue = column.getCellData(item);
                dataRow.createCell(j).setCellValue(cellValue != null ? cellValue.toString() : "");
            }
        }

        // Write the output to a file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
