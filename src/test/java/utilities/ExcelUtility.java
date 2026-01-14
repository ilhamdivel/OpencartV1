package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Utility class for reading and writing Excel files (.xlsx)
 * Uses Apache POI library for Excel operations
 */
public class ExcelUtility {

	private static final Logger logger = LogManager.getLogger(ExcelUtility.class);
	private final String path;

	public ExcelUtility(String path) {
		this.path = path;
	}

	/**
	 * Get total row count in a sheet (excluding header)
	 */
	public int getRowCount(String sheetName) throws IOException {
		try (FileInputStream fi = new FileInputStream(path);
				XSSFWorkbook workbook = new XSSFWorkbook(fi)) {

			XSSFSheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				logger.warn("Sheet '{}' not found in file: {}", sheetName, path);
				return 0;
			}
			return sheet.getLastRowNum();
		}
	}

	/**
	 * Get total cell count in a specific row
	 */
	public int getCellCount(String sheetName, int rowNum) throws IOException {
		try (FileInputStream fi = new FileInputStream(path);
				XSSFWorkbook workbook = new XSSFWorkbook(fi)) {

			XSSFSheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				logger.warn("Sheet '{}' not found", sheetName);
				return 0;
			}

			XSSFRow row = sheet.getRow(rowNum);
			if (row == null) {
				logger.warn("Row {} not found in sheet '{}'", rowNum, sheetName);
				return 0;
			}
			return row.getLastCellNum();
		}
	}

	/**
	 * Get cell data as String from specific row and column
	 */
	public String getCellData(String sheetName, int rowNum, int colNum) throws IOException {
		try (FileInputStream fi = new FileInputStream(path);
				XSSFWorkbook workbook = new XSSFWorkbook(fi)) {

			XSSFSheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				logger.warn("Sheet '{}' not found", sheetName);
				return "";
			}

			XSSFRow row = sheet.getRow(rowNum);
			if (row == null) {
				logger.debug("Row {} is empty", rowNum);
				return "";
			}

			XSSFCell cell = row.getCell(colNum);
			if (cell == null) {
				logger.debug("Cell at row {}, col {} is empty", rowNum, colNum);
				return "";
			}

			DataFormatter formatter = new DataFormatter();
			return formatter.formatCellValue(cell);
		}
	}

	/**
	 * Set cell data at specific row and column
	 */
	public void setCellData(String sheetName, int rowNum, int colNum, String data) throws IOException {
		File xlFile = new File(path);

		// Create new file if it doesn't exist
		if (!xlFile.exists()) {
			try (XSSFWorkbook workbook = new XSSFWorkbook();
					FileOutputStream fo = new FileOutputStream(path)) {
				workbook.write(fo);
				logger.info("Created new Excel file: {}", path);
			}
		}

		try (FileInputStream fi = new FileInputStream(path);
				XSSFWorkbook workbook = new XSSFWorkbook(fi)) {

			// Create sheet if it doesn't exist
			XSSFSheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				sheet = workbook.createSheet(sheetName);
				logger.info("Created new sheet: {}", sheetName);
			}

			// Create row if it doesn't exist
			XSSFRow row = sheet.getRow(rowNum);
			if (row == null) {
				row = sheet.createRow(rowNum);
			}

			// Create cell and set value
			XSSFCell cell = row.createCell(colNum);
			cell.setCellValue(data);

			// Write changes to file
			try (FileOutputStream fo = new FileOutputStream(path)) {
				workbook.write(fo);
			}
			logger.debug("Set cell data at [{},{}]: {}", rowNum, colNum, data);
		}
	}

	/**
	 * Fill cell with green color (for PASS status)
	 */
	public void fillGreenColor(String sheetName, int rowNum, int colNum) throws IOException {
		fillCellColor(sheetName, rowNum, colNum, IndexedColors.GREEN);
	}

	/**
	 * Fill cell with red color (for FAIL status)
	 */
	public void fillRedColor(String sheetName, int rowNum, int colNum) throws IOException {
		fillCellColor(sheetName, rowNum, colNum, IndexedColors.RED);
	}

	/**
	 * Helper method to fill cell with specified color
	 */
	private void fillCellColor(String sheetName, int rowNum, int colNum, IndexedColors color) throws IOException {
		try (FileInputStream fi = new FileInputStream(path);
				XSSFWorkbook workbook = new XSSFWorkbook(fi)) {

			XSSFSheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				logger.warn("Sheet '{}' not found", sheetName);
				return;
			}

			XSSFRow row = sheet.getRow(rowNum);
			if (row == null) {
				logger.warn("Row {} not found", rowNum);
				return;
			}

			XSSFCell cell = row.getCell(colNum);
			if (cell == null) {
				logger.warn("Cell at [{},{}] not found", rowNum, colNum);
				return;
			}

			CellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(color.getIndex());
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cell.setCellStyle(style);

			try (FileOutputStream fo = new FileOutputStream(path)) {
				workbook.write(fo);
			}
			logger.debug("Applied {} color to cell [{},{}]", color.name(), rowNum, colNum);
		}
	}

	/**
	 * Get all data from sheet as 2D array (for TestNG DataProvider)
	 * Skips header row (row 0)
	 */
	public Object[][] getSheetData(String sheetName) throws IOException {
		int rowCount = getRowCount(sheetName);
		if (rowCount == 0) {
			return new Object[0][0];
		}

		int colCount = getCellCount(sheetName, 0);
		Object[][] data = new Object[rowCount][colCount];

		for (int i = 1; i <= rowCount; i++) {
			for (int j = 0; j < colCount; j++) {
				data[i - 1][j] = getCellData(sheetName, i, j);
			}
		}

		logger.info("Loaded {} rows from sheet '{}'", rowCount, sheetName);
		return data;
	}
}
