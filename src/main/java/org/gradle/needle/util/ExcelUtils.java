package org.gradle.needle.util;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.gradle.needle.config.GlobalSettings;

import net.sf.jxls.util.Util;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public class ExcelUtils {
	private static String CaseFile;
	private static InputStream CaseInputStream;
	private static Sheet ExcelWorkSheet;
	private static Workbook ExcelWorkBook;
	private static Logger logger = Logger.getLogger(ExcelUtils.class.getName());

	// ���弸���г������������ݶ�ȡ��д��
	static int responsecol = Integer.parseInt(GlobalSettings.getProperty("response"));
	static int expectcol = Integer.parseInt(GlobalSettings.getProperty("expect"));
	static int E_keycol = Integer.parseInt(GlobalSettings.getProperty("E_key"));
	static int E_valuecol = Integer.parseInt(GlobalSettings.getProperty("E_value"));
	static int A_keycol = Integer.parseInt(GlobalSettings.getProperty("A_key"));
	static int A_valuecol = Integer.parseInt(GlobalSettings.getProperty("A_value"));
	static int Resultcol = Integer.parseInt(GlobalSettings.getProperty("Result"));

	public ExcelUtils(String CaseFile) {
		ExcelUtils.CaseFile = CaseFile;
		try {
			CaseInputStream = new FileInputStream(CaseFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public ExcelUtils(InputStream CaseInputStream) {
		ExcelUtils.CaseInputStream = CaseInputStream;
	}

	/**
	 * �Ի���expect�ͷ���response��ѭ���Աȣ����ԱȽ��д��comparison�����
	 * 
	 * @param testid
	 * @param bsheet
	 * @param response
	 * @param csheet
	 */
	public static void ParserAndCompare(String testid, String bsheet, String response, String csheet) {

		Map<String, String> actualmap = CustomUtils.parser(response);
		try {
			// ��bsheet�ж�ȡexpect�ַ���
			ExcelUtils.setWorkSheet(bsheet);
			int brownum = ExcelUtils.getRowNumberOnTestid(testid);
			String expectstr = (String) ExcelUtils.getCellData(brownum, expectcol);

			// �л���csheet,д��ԱȺ���
			ExcelUtils.setWorkSheet(csheet);
			int crownum = ExcelUtils.getRowNumberOnTestid(testid);
			Map<String, String> expectmap = CustomUtils.parser(expectstr);
			Iterator<String> iter = expectmap.keySet().iterator();

			while (iter.hasNext()) {
				String ekey = iter.next();
				String evalue = expectmap.get(ekey);
				ExcelUtils.setCellData(ekey, crownum, E_keycol);
				ExcelUtils.setBorder(crownum, E_keycol);
				ExcelUtils.setCellData(evalue, crownum, E_valuecol);
				ExcelUtils.setBorder(crownum, E_valuecol);

				String avalue = actualmap.get(ekey);
				if (avalue != null) {
					ExcelUtils.setCellData(ekey, crownum, A_keycol);
					ExcelUtils.setBorder(crownum, A_keycol);
					ExcelUtils.setCellData(avalue, crownum, A_valuecol);
					ExcelUtils.setBorder(crownum, A_valuecol);
					if (avalue.equals(evalue)) {
						ExcelUtils.setCellData("passed", crownum, Resultcol);
						ExcelUtils.setBorder(crownum, Resultcol);
						ExcelUtils.setCellColor("GREEN", crownum, Resultcol);
					} else {
						ExcelUtils.setCellData("failed", crownum, Resultcol);
						ExcelUtils.setBorder(crownum, Resultcol);
						ExcelUtils.setCellColor("YELLOW", crownum, Resultcol);
					}
				} else {
					ExcelUtils.setCellData("--", crownum, A_keycol);
					ExcelUtils.setCellData("--", crownum, A_valuecol);
					ExcelUtils.setCellData("failed", crownum, Resultcol);
					ExcelUtils.setBorder(crownum, Resultcol);
					ExcelUtils.setCellColor("YELLOW", crownum, Resultcol);
					logger.info(testid + ":  " + ekey + "��ͳ�����ڷ��ؽ���в�����");
				}

				// ������testid���������һ�����У�׼��д����һ��key-value
				int totalrows = ExcelUtils.getWorkSheet().getLastRowNum(); // ��ȡ������
				ExcelUtils.insertRow(crownum, totalrows, 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���ӿڵķ��ؽ��д��output��
	 * 
	 * @param osheet
	 * @param testid
	 * @param response
	 */
	public static void saveResponse(String osheet, String testid, String response) {
		try {
			ExcelUtils.setWorkSheet(osheet);
			int rownumber = ExcelUtils.getRowNumberOnTestid(testid);
			ExcelUtils.setCellData(response, rownumber, responsecol);
			ExcelUtils.setBorder(rownumber, responsecol);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * ��ʼ��Excel������
	 * 
	 * @param SheetName
	 * @throws Exception
	 */
	public static void setWorkSheet(String SheetName) {
		try {
			ExcelWorkBook = WorkbookFactory.create(CaseInputStream);
			ExcelWorkSheet = ExcelWorkBook.getSheet(SheetName);
			// logger.info("���Թ����� " + SheetName + " �ɹ���ʼ��");
		} catch (Exception e) {
			logger.error("���Թ����� " + SheetName + " ��ʼ��ʧ�ܣ�");
			e.printStackTrace();
		}
	}

	public static Sheet getWorkSheet() {
		return ExcelWorkSheet;
	}

	/**
	 * @param TestSet
	 *            ָ��TestSet
	 * @return map���͵Ĳ������ݼ���keyΪ�б��⣬valueΪ��ֵ column �ӵ�i�п�ʼ��ȡ����
	 * @throws Exception
	 */
	public Iterator<Map<String, String>> getCaseSet(String TestSet) throws Exception {
		int numberOfColumns = countNonEmptyColumns(ExcelWorkSheet);
		int i = 0;
		List<Map<String, String>> requestlist = new ArrayList<Map<String, String>>();
		Map<String, String> m;
		for (Row row : ExcelWorkSheet) {
			if (isEmpty(row)) {
				break;
			} else {
				if (row.getRowNum() != 0 & verRowBelong(TestSet, row)) {
					/*
					 * ����map<key,value>�У�key��Ψһ�ģ����m��ÿ��ѭ����ֵ֮ǰ�����ʼ��һ�Σ�
					 * ������ͬ��key��Ӧ��value�ᱻ��һ�鸲�ǡ�
					 */
					m = new HashMap<String, String>();
					for (int column = i; column < numberOfColumns; column++) {
						Object key = getCellData(0, column);
						Cell cell = row.getCell(column);
						m.put(key.toString(), objectFrom(ExcelWorkBook, cell).toString());
					}
				} else {
					// logger.info("�Ǳ��β������ݼ����� " + row.getRowNum() + "
					// �в������ݽ�����");
					continue;
				}
			}
			requestlist.add(m);
			// logger.info("��ȷƥ�䣬�� " + row.getRowNum() + " �в�������ѹ�����ݼ�");
		}
		Iterator<Map<String, String>> s = requestlist.iterator();
		// logger.info("���в���������ʼ�����" + "\r\n");
		return (s);
	}

	/**
	 * ��֤�Ƿ�����TestSet�������� TestSet������ڹ�����ĵڶ���
	 * 
	 * @param TestSet
	 * @param colNum
	 * @return
	 * @throws Exception
	 */
	public static boolean verRowBelong(String TestSet, Row row) throws Exception {
		int colNum = 1;
		int rowNum = row.getRowNum();
		Object Value = getCellData(rowNum, colNum);
		boolean a = ((String) Value).equalsIgnoreCase(TestSet);
		return a;
	}

	/**
	 * �Ӱ���package�ķ������н�ȡ������������������
	 */
	public static String getTestCaseName(String sTestCase) throws Exception {
		String value = sTestCase;
		try {
			int posi = value.indexOf("@");
			value = value.substring(0, posi);
			posi = value.lastIndexOf(".");
			value = value.substring(posi + 1);
			return value;
		} catch (Exception e) {
			throw (e);
		}
	}

	/**
	 * ����testid��ȡ���е�rownumber return rownumber
	 */
	public static int getRowNumberOnTestid(String testid) {
		int rownumber = 0;
		for (Row row : ExcelWorkSheet) {
			int rownum = row.getRowNum();
			if (isEmpty(row)) {
				break;
			} else {
				try {
					String ca = (String) getCellData(rownum, 0);
					if (ca.equals(testid)) {
						rownumber = rownum;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return rownumber;
	}

	/**
	 * ��ȡ��Ԫ��������� Excel��Ԫ�������id���Ǵ�0��ʼ getRow(0)��ʾ������
	 * 
	 * @param RowNum
	 * @param ColNum
	 * @return
	 * @throws Exception
	 */
	public static Object getCellData(int RowNum, int ColNum) throws Exception {
		Object CellData;
		try {
			Cell cell = ExcelWorkSheet.getRow(RowNum).getCell(ColNum);
			if (isEmpty(ExcelWorkSheet.getRow(RowNum))) {
				CellData = null;
				logger.info("��  " + RowNum + " ��" + ColNum + "��û��ֵ");
			} else {
				CellData = objectFrom(ExcelWorkBook, cell);
				// logger.info("�ѻ�ȡ��" + RowNum + " ��" + ColNum + "�е�ֵ��"+
				// CellData);
			}
			return CellData;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * ��鵱ǰ�е�һ�е�ֵ�Ƿ�Ϊ�ջ�NULL
	 * 
	 * @param row
	 * @return
	 */
	private static boolean isEmpty(final Row row) {
		Cell firstCell = row.getCell(0);
		boolean rowIsEmpty = (firstCell == null) || (firstCell.getCellType() == Cell.CELL_TYPE_BLANK);
		return rowIsEmpty;
	}

	/**
	 * �����һ�е�����
	 */
	private static int countNonEmptyColumns(final Sheet sheet) {
		Row firstRow = sheet.getRow(0);
		return firstEmptyCellPosition(firstRow);
	}

	private static int firstEmptyCellPosition(final Row cells) {
		int columnCount = 0;
		for (Cell cell : cells) {
			if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
				break;
			}
			columnCount++;
		}
		return columnCount;
	}

	/**
	 * ����cell���������ͻ�ȡcellvalue
	 * 
	 * @param workbook
	 * @param cell
	 * @return
	 */
	private static Object objectFrom(final Workbook workbook, final Cell cell) {
		Object cellValue = null;

		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			cellValue = cell.getRichStringCellValue().getString();
		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			cellValue = getNumericCellValue(cell);
		} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			cellValue = cell.getBooleanCellValue();
		} else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
			cellValue = evaluateCellFormula(workbook, cell);
		}

		return cellValue;

	}

	/**
	 * �ж���ֵ�����Ƿ�Ϊ������
	 * 
	 * @param cell
	 * @return
	 */
	private static Object getNumericCellValue(final Cell cell) {
		Object cellValue;
		if (DateUtil.isCellDateFormatted(cell)) {
			cellValue = new Date(cell.getDateCellValue().getTime());
		} else {
			cellValue = cell.getNumericCellValue();
		}
		return cellValue;
	}

	private static Object evaluateCellFormula(final Workbook workbook, final Cell cell) {
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		CellValue cellValue = evaluator.evaluate(cell);
		Object result = null;

		if (cellValue.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			result = cellValue.getBooleanValue();
		} else if (cellValue.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			result = cellValue.getNumberValue();
		} else if (cellValue.getCellType() == Cell.CELL_TYPE_STRING) {
			result = cellValue.getStringValue();
		}

		return result;
	}

	/**
	 * ��Ԫ��д������
	 * 
	 * @param Value
	 * @param RowNum
	 * @param ColNum
	 * @throws Exception
	 */
	public static void setCellData(String Value, int RowNum, int ColNum) throws Exception {
		try {
			Row row = ExcelWorkSheet.getRow(RowNum);
			Cell cell = row.getCell(ColNum, Row.RETURN_BLANK_AS_NULL);
			if (cell == null) {
				cell = row.createCell(ColNum);
				cell.setCellValue(Value);
			} else {
				cell.setCellValue(Value);
			}
			FileOutputStream fileOut = new FileOutputStream(CaseFile);
			ExcelWorkBook.write(fileOut);
			fileOut.flush();
			fileOut.close();

		} catch (Exception e) {
			throw (e);
		}
	}

	/**
	 * ��Ԫ�����ñ���ɫ
	 * 
	 * @param Value
	 * @param RowNum
	 * @param ColNum
	 * @throws Exception
	 */
	public static void setCellColor(String color, int RowNum, int ColNum) {
		try {
			Cell cell = ExcelWorkSheet.getRow(RowNum).getCell(ColNum);
			CellStyle style = ExcelWorkBook.createCellStyle();
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);
			switch (color) {
			case "YELLOW":
				style.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
				style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
				break;
			case "GREEN":
				style.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
				style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
				break;
			case "RED":
				style.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
				style.setFillForegroundColor(IndexedColors.RED.getIndex());
				break;
			case "BLUE":
				style.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
				style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
				break;
			}
			cell.setCellStyle(style);
			FileOutputStream fileOut = new FileOutputStream(CaseFile);
			ExcelWorkBook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��Ԫ�����ñ߿� �����Զ�����
	 * 
	 * @param crownum
	 * @param e_keycol
	 */
	public static void setBorder(int RowNum, int ColNum) {
		try {
			Cell cell = ExcelWorkSheet.getRow(RowNum).getCell(ColNum);
			CellStyle style = ExcelWorkBook.createCellStyle();
			style.setBorderRight(CellStyle.BORDER_HAIR);
			style.setBorderLeft(CellStyle.BORDER_HAIR);
			style.setBorderBottom(CellStyle.BORDER_HAIR);
			style.setBorderTop(CellStyle.BORDER_HAIR);
			style.setWrapText(true);
			cell.setCellStyle(style);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ʵ��Excel������
	 * 
	 * @param startrow
	 *            ��ʼ��
	 * @param totalrows
	 *            ������
	 * @param rows
	 *            ���������
	 */
	public static void insertRow(int startrow, int totalrows, int rows) {

		ExcelWorkSheet.shiftRows(startrow + 1, totalrows, rows, true, false);
		for (int i = 0; i < rows; i++) {
			Row sourceRow = null;
			Row targetRow = null;
			sourceRow = ExcelWorkSheet.getRow(startrow);
			targetRow = ExcelWorkSheet.createRow(++startrow);
			Util.copyRow(ExcelWorkSheet, sourceRow, targetRow);
		}
	}
}
