package util;
import java.io.File;
import java.util.Map;
import java.util.Set;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CreateExcel {

    public void createWorkBook(String filename, Map<String, Object[]> data){
        XSSFWorkbook excelWorkBook = new XSSFWorkbook();
        XSSFSheet sheet = excelWorkBook.createSheet("DemoResultData");

        Set<String> keyset = data.keySet();
        int rowCount = 0;
        for(String key : keyset){
            Row sheetrow = sheet.createRow(rowCount++);
            Object [] getRowObject = data.get(key);

            int cellCount = 0;
            for (Object obj : getRowObject){
               Cell cell = sheetrow.createCell(cellCount++);
                if(obj instanceof String){
                    cell.setCellValue((String)obj);
                }else if(obj instanceof Integer){
                    cell.setCellValue((Integer)obj);
                }
            }
        }

        try{
            String filepath = "./logs/";
            FileOutputStream out = new FileOutputStream(new File(filepath+filename+".xlsx"));
            excelWorkBook.write(out);
            out.close();
            excelWorkBook.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}