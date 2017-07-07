package smartvip.mainStage.center.leftTopControl.maker.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileCheck4CG901A {
    public static List<List<Object>> listExcel = new LinkedList<>();
    public static String verSoft = "";
    public static String verInner = "";
    public static String verHard = "";
    public static String AbsPath;
    
    public final static int DateIndex = 2;

    public static String parseFile(File file, String model, String step, String operator) {
        AbsPath = file.getAbsolutePath();
        
        String fileName = file.getName();
        String extension = (fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1)).toLowerCase(Locale.CHINA);
        if (extension.equals("xlsx")) {
            try {
                return parseContent(file, model, step, operator);
            } catch (IOException ex) {
                return "error_openFile";
            }
        } else {
            return "error_extension";
        }
    }

    private static String parseContent(File file, String model, String step, String operator) throws IOException {
        XSSFWorkbook xwb = new XSSFWorkbook(new FileInputStream(file)); // 构造 XSSFWorkbook 对象，strPath 传入文件路径  
        XSSFSheet sheet = xwb.getSheetAt(0);  // read sheet0 

        Object value = null;
        XSSFRow row = null;
        XSSFCell cell = null;

        int rowCounter = 0;
        int validRows = sheet.getPhysicalNumberOfRows();

        if (validRows < 4) {
            return "error_noData";
        } else {
            listExcel.clear();
            
            for (int i = sheet.getFirstRowNum(); rowCounter < validRows; i++) {
                row = sheet.getRow(i);
                if (row != null) {  // ignore blank line
                    switch (rowCounter++) {
                        case 0:
                            cell = row.getCell(2);
                            if (cell == null) {
                                return "error_cellModelNull";
                            } else if (!cell.getStringCellValue().equals(model)) {
                                return "error_model";
                            } else {
                                continue;
                            }
                        case 1:
                            if(row.getCell(3) == null || row.getCell(4) == null || row.getCell(5) == null) {
                                return "error_cellVersionNull";
                            } else {
                                verSoft = (row.getCell(3)).toString();
                                verInner = (row.getCell(4)).toString();
                                verHard = (row.getCell(5)).toString();
                                
                                cell = row.getCell(2);
                                if (cell == null) {
                                    return "error_cellStepNull";
                                } else if (!cell.getStringCellValue().equals(step)) {
                                    return "error_step";
                                } else {
                                    continue;
                                }                                
                            }
                        case 2:
                            continue;
                        default:
                            break;
                    }

                    List<Object> listRow = new LinkedList<>();
                    for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                        cell = row.getCell(j);
                        if (cell != null) {
                            DecimalFormat df = new DecimalFormat("0");// 格式化 number String 字符  
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");// 格式化日期字符串  
                            DecimalFormat nf = new DecimalFormat("0.00");// 格式化数字  
                            switch (cell.getCellType()) {
                                case XSSFCell.CELL_TYPE_STRING:
                                    value = cell.getStringCellValue();
                                    break;
                                case XSSFCell.CELL_TYPE_NUMERIC:
                                    if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                                        value = df.format(cell.getNumericCellValue());
                                    } else if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                                        value = nf.format(cell.getNumericCellValue());
                                    } else {
                                        value = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                                    }
                                    break;
                                case XSSFCell.CELL_TYPE_BOOLEAN:
                                    value = cell.getBooleanCellValue();
                                    break;
                                case XSSFCell.CELL_TYPE_BLANK:
                                    value = "";
                                    break;
                                default:
                                    value = cell.toString();
                            }
                            listRow.add(value);
                        } else if (cell == null && j < DateIndex) {
                            listRow.add("");
                        }
                    }

                    listExcel.add(listRow);
                }
            }
            return "ok";
        }
    }

    public static List<List<Object>> readExcel(File file) throws IOException {
        String fileName = file.getName();
        String extension = (fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1)).toLowerCase(Locale.CHINA);
        if (extension.equals("xlsx")) {
            return read2007Excel(file);
        } else {
            return null;
        }
    }

    private static List<List<Object>> read2007Excel(File file) throws IOException {
        List<List<Object>> list = new LinkedList<>();
        XSSFWorkbook xwb = new XSSFWorkbook(new FileInputStream(file)); // 构造 XSSFWorkbook 对象，strPath 传入文件路径  
        XSSFSheet sheet = xwb.getSheetAt(0);  // read sheet0 

        Object value = null;
        XSSFRow row = null;
        XSSFCell cell = null;

        int rowCounter = 0;
        int validRows = sheet.getPhysicalNumberOfRows();
        for (int i = sheet.getFirstRowNum(); rowCounter < validRows; i++) {
            row = sheet.getRow(i);
            if (row != null) {  // ignore blank line
                rowCounter++;

                List<Object> linked = new LinkedList<>();
                for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    if (cell != null) {
                        DecimalFormat df = new DecimalFormat("0");// 格式化 number String 字符  
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化日期字符串  
                        DecimalFormat nf = new DecimalFormat("0.00");// 格式化数字  
                        switch (cell.getCellType()) {
                            case XSSFCell.CELL_TYPE_STRING:
                                System.out.println(i + "行" + j + " 列 is String type");
                                value = cell.getStringCellValue();
                                break;
                            case XSSFCell.CELL_TYPE_NUMERIC:
                                System.out.println(i + "行" + j + " 列 is Number type ; DateFormt:" + cell.getCellStyle().getDataFormatString());
                                if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                                    value = df.format(cell.getNumericCellValue());
                                } else if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                                    value = nf.format(cell.getNumericCellValue());
                                } else {
                                    value = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                                }
                                break;
                            case XSSFCell.CELL_TYPE_BOOLEAN:
                                System.out.println(i + "行" + j + " 列 is Boolean type");
                                value = cell.getBooleanCellValue();
                                break;
                            case XSSFCell.CELL_TYPE_BLANK:
                                System.out.println(i + "行" + j + " 列 is Blank type");
                                value = "";
                                break;
                            default:
                                System.out.println(i + "行" + j + " 列 is default type");
                                value = cell.toString();
                        }
                        if (value == null || "".equals(value)) {
                            continue;
                        }
                        linked.add(value);
                    }
                }
                list.add(linked);
            }
        }
        return list;
    }
    
    public static String changeRowCell(File excelFile, int sheetIndex, int startRow, int startColoum, String[] values) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(excelFile));
            XSSFSheet sheet = workbook.getSheetAt(sheetIndex);

            XSSFRow xssfRow = sheet.getRow((short) startRow);
            if (null == xssfRow) {
                return "error_xssfRowNull";
            } else {
                for(int i=0; i<values.length; i++) {
                    XSSFCell xssfCell = xssfRow.getCell(startColoum + i);
                    if (null == xssfCell) {
                        return "error_xssfCellNull";
                    } else {
                        xssfCell.setCellValue(values[i]);
                    }                    
                }
            }

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(excelFile);
                workbook.write(out);
                return "ok";
            } catch (IOException e) {
                return "error_xssfIOExcep_1";
            } finally {
                try {
                    if(out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    return "error_xssfIOExcep_2";
                }
            }
        } catch (IOException e) {
            return "error_xssfExcep_1";
        }
    }

}
