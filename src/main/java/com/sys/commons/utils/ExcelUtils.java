package com.sys.commons.utils;

import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.piccolo.io.FileFormatException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author jiewai
 */
public class ExcelUtils {

    private static final String EXTENSION_XLS = "xls";
    private static final String EXTENSION_XLSX = "xlsx";

    static Logger log = Logger.getLogger(ExcelUtils.class);

    public static void writeToExcel(String outFilePath, String outFileName,
                                    String content) {
        File file = new File(outFilePath + outFileName);
        String[] contents = content.split("\\|");
        if (file.exists())
            addToExcels(outFilePath, outFileName, contents);
        else
            creatExcels(outFilePath, outFileName, contents, "短信");
    }

    /**
     * 创建新的Excel文件(写出 .xlsx 格式文件)
     *
     * @param outFilePath
     * @param outFileName
     * @param str
     * @param sheetName
     */
    public static void creatExcels(String outFilePath, String outFileName,
                                   String[] str, String sheetName) {
        // TODO Auto-generated constructor stub
        // 第一步，创建一个webbook，对应一个Excel文件
        XSSFWorkbook wb = new XSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        XSSFSheet sheet = wb.createSheet(sheetName);
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        for (int i = 0; i < 1; i++) {
            XSSFRow row = sheet.createRow(i);
            // 第五步，写入实体数据 实际应用中这些数据从数据库得到，
            for (int j = 0; j < str.length; j++) {
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(str[j]); // 设置值
            }
        }
        // 第六步，将文件存到指定位置
        try {
            FileUtils.creatFileOrPath(outFilePath, "");
            FileOutputStream fout = new FileOutputStream(outFilePath
                    + outFileName);
            wb.write(fout);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在原有文件中追加数据(写入.xlsx 格式文件)
     *
     * @param outFilePath
     * @param outFileName
     * @param str
     */
    public static void addToExcels(String outFilePath, String outFileName,
                                   String[] str) {
        FileInputStream fs;
        try {
            fs = new FileInputStream(outFilePath + outFileName);
            XSSFWorkbook wb = (XSSFWorkbook) WorkbookFactory.create(fs);
            XSSFSheet sheet = wb.getSheetAt(0); // 获取到工作表，因为一个excel可能有多个工作表
            XSSFRow row = sheet.getRow(0); // 获取第一行（excel中的行默认从0开始，所以这就是为什么，一个excel必须有字段列头），即，字段列头，便于赋值
            System.out.println(sheet.getLastRowNum() + " "
                    + row.getLastCellNum()); // 分别得到最后一行的行号，和一条记录的最后一个单元格

            FileOutputStream out = new FileOutputStream(outFilePath
                    + outFileName); // 向已存在文件中写数据
            row = sheet.createRow((short) (sheet.getLastRowNum() + 1)); // 在现有行号后追加数据
            for (int j = 0; j < str.length; j++) {
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(str[j]); // 设置值
            }
            out.flush();
            wb.write(out);
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (EncryptedDocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 读取全量数据封装到Map
     *
     * @param file     加载的文件
     * @param startH   表格的起始行
     * @param startL   表格的起始列
     * @param startKey 返回map的key的起始值
     * @param keyIndex 外层map的key值取值列标
     * @return
     */
    public static Map getAllExcelData(File file, int startH, int startL,
                                      int startKey, int keyIndex) {
        // 默认单元格内容为数字时格式
        DecimalFormat df = new DecimalFormat("#.############");
        // 默认单元格格式化日期字符串
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 格式化数字
        DecimalFormat nf = new DecimalFormat("0.00");
        // 检查
        preReadCheck(file.getPath());
        Workbook workbook = null;
        Map<String, Map<String, String>> maps = new HashMap<>();
        try {
            workbook = getWorkbook(file);
            if (workbook.getNumberOfSheets() <= 0)
                return null;
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {// 获取每个Sheet表
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet.getLastRowNum() <= 0)
                    return null;
                String idNo;
                for (int j = startH; j < sheet.getPhysicalNumberOfRows(); j++) {// 获取剩下的每行
                    if (j == startH) {
                        idNo = "HEAD";
                    } else {
                        idNo = "";
                    }
                    Row row = sheet.getRow(j);
                    Map<String, String> map = new HashMap<>();
                    String value = "";
                    for (int k = startL, m = 0; row != null
                            && k <= row.getLastCellNum(); k++) {// 获取每个单元格
                        String key = "a" + (m + startKey);
                        if (row != null) {
                            Cell cell = row.getCell(k);
                            if (cell != null) {
                                if (k == (startL + keyIndex) && j != startH) {
                                    cell.setCellType(cell.CELL_TYPE_STRING);
                                    idNo = cell.getStringCellValue();
                                }
                                if (row != null && row.getCell(k) != null) {
                                    switch (cell.getCellType()) {
                                        case XSSFCell.CELL_TYPE_STRING:
                                            value = cell.getStringCellValue();
                                            break;
                                        case XSSFCell.CELL_TYPE_NUMERIC:
                                            if ("@".equals(cell.getCellStyle()
                                                    .getDataFormatString())) {
                                                value = df.format(cell.getNumericCellValue());
                                            } else if ("General".equals(cell
                                                    .getCellStyle()
                                                    .getDataFormatString())) {
                                                value = df.format(cell.getNumericCellValue());
                                            } else {
                                                value = df.format(cell.getNumericCellValue());
                                            }
                                            //System.out.println(i + "行" + j + " 列 is Number type ; DateFormt:" + value.toString());
                                            break;
                                        case XSSFCell.CELL_TYPE_BOOLEAN:
                                            value = Boolean.valueOf(cell.getBooleanCellValue()).toString();
                                            break;
                                        case XSSFCell.CELL_TYPE_BLANK:
                                            value = "";
                                            break;
                                        default:
                                            value = cell.toString();
                                    }
                                }
                            }
                            map.put(key, value);
                            value = "";
                            m++;
                        }
                    }
                    if (idNo != null && !"".equals(idNo)) {
                        maps.put(idNo, map);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return maps;
    }

    public static List<Map<String, Object>> loadAllExcelData(File file) {
        // 检查
        preReadCheck(file.getPath());
        Workbook workbook;
        List<Map<String, Object>> source = null;
        try {
            workbook = getWorkbook(file);
            if (workbook.getNumberOfSheets() <= 0)
                return null;
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {// 获取每个Sheet表
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet.getLastRowNum() <= 0)
                    return null;
                source = loadSheet(sheet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return source;
    }

    private static List<Map<String, Object>> loadSheet(Sheet sheet) {
        Iterator<Row> rows = sheet.iterator();
        List<Map<String, Object>> source = new ArrayList<>();
        Row row = rows.next();
        Map<Integer, String> headers = new HashMap<>();
        int index = 0;
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            String value;
            if (cell != null) {
                value = cell.toString();
                headers.put(i, value);
            }
            index++;
        }
        while (rows.hasNext()) {
            Map<String, Object> lhhead = new HashMap<>();
            Row r = rows.next();
            for (int i = 0; i < index; i++) {
                Cell cell = r.getCell(i);
                Object value;
                String header = headers.get(i);
                if (cell != null) {
                    if (header != null && !"".equals(header)) {
                        DecimalFormat df = new DecimalFormat("#.#########");
                        switch (cell.getCellType()) {
                            case XSSFCell.CELL_TYPE_STRING:
                                value = cell.getRichStringCellValue().getString().trim();
                                break;
                            case XSSFCell.CELL_TYPE_NUMERIC:
                                value = df.format(cell.getNumericCellValue());
                                break;
                            case XSSFCell.CELL_TYPE_BOOLEAN:
                                value = String.valueOf(cell.getBooleanCellValue()).trim();
                                break;
                            case XSSFCell.CELL_TYPE_FORMULA:
                                value = cell.getCellFormula();
                                break;
                            default:
                                value = "";
                        }
                    } else {
                        value = null;// 当列为空
                    }
                    lhhead.put(headers.get(i), value);
                }
            }
            source.add(lhhead);
        }
        return source;
    }


    /**
     * 获取文件扩展名
     *
     * @param path
     * @return String
     * @author zhang 2015-08-17 23:26
     */
    private static String getExt(String path) {
        if (path == null || path.equals("") || !path.contains(".")) {
            return null;
        } else {
            return path.substring(path.lastIndexOf(".") + 1, path.length());
        }
    }

    /***
     * <pre>
     * 取得Workbook对象(xls和xlsx对象不同,不过都是Workbook的实现类)
     *   xls:HSSFWorkbook
     *   xlsx：XSSFWorkbook
     * @return
     * @throws IOException
     * </pre>
     */
    private static Workbook getWorkbook(File file) throws IOException {
        Workbook workbook = null;
        InputStream is = new FileInputStream(file);
        if (file.getPath().endsWith(EXTENSION_XLS)) {
            workbook = new HSSFWorkbook(is);
        } else if (file.getPath().endsWith(EXTENSION_XLSX)) {
            workbook = new XSSFWorkbook(is);
        }
        return workbook;
    }

    /**
     * 文件检查
     *
     * @param filePath
     * @throws FileNotFoundException
     * @throws FileFormatException
     */
    private static void preReadCheck(String filePath) {
        // 常规检查
        File file = new File(filePath);
        if (!file.exists()) {
            log.equals("");
        }
        if (!(filePath.endsWith(EXTENSION_XLS) || filePath
                .endsWith(EXTENSION_XLSX))) {
            log.info("文件不是excel" + filePath);
        }
    }


    /**
     * 批量写入表格数据 公共方法
     *
     * @param collectionCaseMap 表格数据
     * @param maps              表头列数据
     * @param outFilePath       输出路径
     * @param outFileName       输出文件名
     * @param sheetName         表格sheet名
     */
    public void writeExcelAll(Map collectionCaseMap, Map maps,
                              String outFilePath, String outFileName, String sheetName) {
        String[] contents = new String[maps.size()];
        // 写入Excel
        File file = new File(outFilePath + outFileName);
        if (!file.exists()) {
            // 设置表头,创建当日催收Excel表格文件
            for (int i = 0; i < contents.length; i++) {
                contents[i] = (String) maps.get("a" + i);
            }
            ExcelUtils.creatExcels(outFilePath, outFileName, contents,
                    sheetName);
        }
        Iterator iter = collectionCaseMap.keySet().iterator();
        FileInputStream fs;
        try {
            fs = new FileInputStream(outFilePath + outFileName);
            // 获取第一行（excel中的行默认从0开始，所以这就是为什么，一个excel必须有字段列头），即，字段列头，便于赋值
            XSSFWorkbook wb = (XSSFWorkbook) WorkbookFactory.create(fs);
            XSSFSheet sheet = wb.getSheetAt(0); // 获取到工作表，因为一个excel可能有多个工作表
            XSSFRow row = sheet.getRow(0); // 获取第一行（excel中的行默认从0开始，所以这就是为什么，一个excel必须有字段列头），即，字段列头，便于赋值

            System.out.println(sheet.getLastRowNum() + " "
                    + row.getLastCellNum()); // 分别得到最后一行的行号，和一条记录的最后一个单元格
            FileOutputStream out = new FileOutputStream(outFilePath
                    + outFileName); // 向已存在文件中写数据
            int i = (sheet.getLastRowNum() + 1);
            while (iter.hasNext()) {
                String key = (String) iter.next();
                if (key != null && !"HEAD".equals(key) && !"".equals(key)) {
                    Map map = (Map) collectionCaseMap.get(key);
                    row = sheet.createRow(i); // 在现有行号后追加数据
                    for (int j = 0; j < map.size(); j++) {
                        XSSFCell cell = row.createCell(j);
                        String value = map.get("a" + j) + "";
                        cell.setCellValue(value); // 设置值
                    }
                    i++;
                }
            }
            out.flush();
            wb.write(out);
            out.close();
            System.out.println(sheetName + "文件写出完成!");
        } catch (IOException | EncryptedDocumentException | InvalidFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Excel 文件写入并下载导出到浏览器
     *
     * @param fileName
     * @param wb
     * @param request
     * @param response
     */
    public static void writeFileToClient(String fileName, Workbook wb, HttpServletRequest request, HttpServletResponse response) {
        try {
            OutputStream fos;
            fos = response.getOutputStream();
            String userAgent = request.getHeader("USER-AGENT");
            if (org.apache.commons.lang.StringUtils.contains(userAgent, "Mozilla")) {
                fileName = new String(fileName.getBytes(), "ISO8859-1");
            } else {
                fileName = URLEncoder.encode(fileName, "utf8");
            }
            response.setCharacterEncoding("UTF-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "Attachment;Filename=" + fileName);
            wb.write(fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 输出Excel 到本地本地磁盘文件夹
     *
     * @param outFilePath
     * @param outFileName
     * @param wb
     */
    public static void printExcelFileToLocal(String outFilePath, String outFileName, Workbook wb) {
        try {
            FileUtils.creatFileOrPath(outFilePath, "");
            FileOutputStream fout = new FileOutputStream(outFilePath + File.separator + outFileName);
            wb.write(fout);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        DecimalFormat df;
        BigDecimal bd;
        double number = 4.239432e-12;
        String value;
        df = new DecimalFormat("#.######################");
        bd = new BigDecimal(number);
        value = bd.toString();
        System.out.println(df.format(number));
        System.out.println(value);
    }

}
