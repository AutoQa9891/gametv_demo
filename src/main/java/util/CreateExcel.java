package util;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CreateExcel {

	public void excel() {
		@SuppressWarnings("resource")
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Staff Report");

		Map<String, Object[]> data = new TreeMap<String, Object[]>();
		data.put("1", new Object[] {1, "Manager Name", "Eamil", "Store", "Total Orders", "Refund Amount", "Total Round Off Sales" });
		data.put("2", new Object[] {2, "Manager Name", "Eamil", "Store", "Total Orders", "Refund Amount", "Total Round Off Sales" });
		data.put("3", new Object[] {3, "Manager Name", "Eamil", "Store", "Total Orders", "Refund Amount", "Total Round Off Sales" });
		data.put("4", new Object[] { 4, "Manager Name", "Eamil", "Store", "Total Orders", "Refund Amount", "Total Round Off Sales" });
		
		Set<String> keyset = data.keySet();
		int rownum = 0;
		for (String key : keyset) {
			Row row = sheet.createRow(rownum++);
			Object[] objArr = data.get(key);
			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);
				if (obj instanceof String)
					cell.setCellValue((String) obj);
				else if (obj instanceof Integer)
					cell.setCellValue((Integer) obj);
			}
		}
		try {
			FileOutputStream out = new FileOutputStream(new File("./RuntimeDataExport/staff.xlsx"));
			workbook.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
