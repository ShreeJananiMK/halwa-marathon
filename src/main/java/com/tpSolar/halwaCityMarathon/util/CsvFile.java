package com.tpSolar.halwaCityMarathon.util;
import com.tpSolar.halwaCityMarathon.model.RegistrationDetails;
import com.tpSolar.halwaCityMarathon.repository.RegistrationDetailsRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class CsvFile {

    @Autowired
    RegistrationDetailsRepository registrationDetailsRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern CONTACT_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern AADHAR_PATTERN = Pattern.compile("^\\d{12}$");

    private static final Logger logger = LoggerFactory.getLogger(CsvFile.class);

    public String uploadExcel(MultipartFile file) {
        boolean successFlag = false;
        StringBuilder resultMsg = new StringBuilder();
        try (InputStream inputStream = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

            XSSFSheet sheet = workbook.getSheetAt(0);

            // Map headers to column indices
            Map<String, Integer> headerMap = new HashMap<>();
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) throw new IllegalArgumentException("Header row missing");

            List<RegistrationDetails> registrationDetails = new ArrayList<>();

            for (Cell cell : headerRow) {
                headerMap.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
            }

            Set<String> aadhaarList = new HashSet<>();

            // Iterate rows and validate before insert
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                String name = getCellValue(row, headerMap.get("participant name"));
                String email = getCellValue(row, headerMap.get("email"));
                String aadharNumber = getCellValue(row, headerMap.get("aadhar number"));
                String contactNumber = getCellValue(row, headerMap.get("contact number"));
                String emergencyContactNumber = getCellValue(row, headerMap.get("emergency contact number"));

                if (!AADHAR_PATTERN.matcher(aadharNumber).matches()) {
                    String msg = " Invalid Aadhar in row " + i + ",";
                    resultMsg.append(msg);
                    logger.info("Invalid Aadhar in row ---{}", i);
                    continue;
                }

                if(aadhaarList.contains(aadharNumber)){
                    String msg = " Duplicate Aadhar in row " + i + ",";
                    resultMsg.append(msg);
                    logger.info("Duplicate Aadhar in row ---{}", i);
                    continue;
                }
                aadhaarList.add(aadharNumber);
                Long result = registrationDetailsRepository.alreadyRegisteredParticipant(aadharNumber);
                if(result >=1){
                    String msg =  " "+ aadharNumber + " already exists,";
                    resultMsg.append(msg);
                    logger.info("Aadhar already exists ---{}", aadharNumber);
                    continue;
                }

                if (!CONTACT_PATTERN.matcher(contactNumber).matches()) {
                    String msg = " Invalid contact number in row " + i + ",";
                    resultMsg.append(msg);
                    logger.info("Invalid contact number in row ---{}", i);
                    continue;
                }

                if (!CONTACT_PATTERN.matcher(emergencyContactNumber).matches()) {
                    String msg = " Invalid emergency contact number in row " + i + ",";
                    resultMsg.append(msg);
                    logger.info("Invalid emergency contact number in row ---{}", i);
                    continue;
                }

                if (!EMAIL_PATTERN.matcher(email).matches()) {
                    String msg = " Invalid email in row " + i + ",";
                    resultMsg.append(msg);
                    logger.info("Invalid email in row ---{}", i);
                    continue;
                }

                String age = getCellValue(row, headerMap.get("age"));
                String bloodGroup = getCellValue(row, headerMap.get("blood group"));
                LocalDate dob = parseDate(getCellValue(row, headerMap.get("dob")));
                String tsize = getCellValue(row, headerMap.get("tshirt size"));
                String eventName = getCellValue(row, headerMap.get("event name"));
                String gender = getCellValue(row, headerMap.get("gender"));

                RegistrationDetails reg = new RegistrationDetails();
                reg.setParticipantName(name);
                reg.setEmail(email);
                reg.setAadhar(aadharNumber);
                reg.setContactNumber(contactNumber);
                reg.setEmergencyContact(emergencyContactNumber);
                reg.setAge(age);
                reg.setBloodGroup(bloodGroup);
                reg.setDob(dob);
                reg.setTsize(tsize);
                reg.setEventName(eventName);
                reg.setGender(gender);
                registrationDetails.add(reg);
            }
            logger.info("The data to be inserted : ---{}",registrationDetails );
             for(RegistrationDetails regDetails : registrationDetails){
                 registrationDetailsRepository.save(regDetails);
                 successFlag = true;
             }
             if(successFlag){
            return "Excel uploaded and data saved successfully. " +  resultMsg ;}
             else{
            return " Data upload is unsuccessful. " +  resultMsg;}

        } catch (Exception e) {
            return "Error reading Excel file: " + e.getMessage();
        }
    }

    private String getCellValue(Row row, Integer colIndex) {
        if (colIndex == null) return "";
        Cell cell = row.getCell(colIndex);
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    double num = cell.getNumericCellValue();
                    if (num == Math.floor(num)) {
                        yield String.valueOf((long) num); // remove .0
                    } else {
                        yield BigDecimal.valueOf(num).toPlainString();
                    }
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell.getCellType() != CellType.BLANK && !getCellValue(row, cell.getColumnIndex()).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            return null;
        }
    }
}











































































/*
package com.tpSolar.halwaCityMarathon.util;
import com.tpSolar.halwaCityMarathon.model.RegistrationDetails;
import com.tpSolar.halwaCityMarathon.repository.RegistrationDetailsRepository;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;

import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CsvFile {

    @Autowired
    RegistrationDetailsRepository registrationDetailsRepository;

        try (InputStream inputStream = file.getInputStream();
    Workbook workbook = new XSSFWorkbook(inputStream)) {

        XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

        // Map headers to column indices
        Map<String, Integer> headerMap = new HashMap<>();
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) throw new IllegalArgumentException("Header row missing");

        for (Cell cell : headerRow) {
            headerMap.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
        }

        // Get the column index for the "image" header
        Integer imageColIndex = headerMap.get("image");

        // Extract image data
        Map<Integer, byte[]> rowImageMap = new HashMap<>();
        for (POIXMLDocumentPart dr : sheet.getRelations()) {
            if (dr instanceof XSSFDrawing drawing) {
                for (XSSFShape shape : drawing.getShapes()) {
                    if (shape instanceof XSSFPicture picture) {
                        XSSFClientAnchor anchor = (XSSFClientAnchor) picture.getAnchor();
                        int rowNum = anchor.getRow1();
                        int colNum = anchor.getCol1();

                        // Only associate the image if it is in the "image" column
                        if (imageColIndex != null && colNum == imageColIndex) {
                            byte[] imageBytes = picture.getPictureData().getData();
                            rowImageMap.put(rowNum, imageBytes);
                        }

                        if (imageColIndex != null && colNum == imageColIndex) {
                            byte[] imageBytes = picture.getPictureData().getData();

                               */
/* // Check if image size is <= 300KB
                                if (isImageSizeWithinLimit(imageBytes, 300)) {
                                    rowImageMap.put(rowNum, imageBytes);
                                } else {
                                    System.out.println("Image in row " + rowNum + " exceeds 300KB limit.");
                                    // Optionally: add to a rejected list or skip storing
                                }*//*

                        }

                    }
                }
            }
        }


        // Process each row
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String name = getCellValue(row, headerMap.get("participant name"));
            String email = getCellValue(row, headerMap.get("email"));
            String aadharNumber = getCellValue(row, headerMap.get("aadhar number"));
            String contactNumber = getCellValue(row, headerMap.get("contact number"));
            String emergencyContactNumber = getCellValue(row, headerMap.get("emergency contact number"));
            String age = getCellValue(row, headerMap.get("age"));
            String bloodGroup = getCellValue(row, headerMap.get("blood group"));
            LocalDate dob = parseDate(getCellValue(row, headerMap.get("dob")));
            String tsize = getCellValue(row, headerMap.get("tshirt size"));
            String eventName = getCellValue(row, headerMap.get("event name"));
            String gender = getCellValue(row, headerMap.get("gender"));
            byte[] imageBytes = rowImageMap.get(i);

            RegistrationDetails reg = new RegistrationDetails();
            reg.setParticipantName(name);
            reg.setEmail(email);
            reg.setAadhar(aadharNumber);
            reg.setContactNumber(contactNumber);
            reg.setEmergencyContact(emergencyContactNumber);
            reg.setAge(age);
            reg.setBloodGroup(bloodGroup);
            reg.setDob(dob);
            reg.setTsize(tsize);
            reg.setEventName(eventName);
            reg.setGender(gender);
            reg.setImage(imageBytes); // Assuming image field is a byte[] in entity

            registrationDetailsRepository.save(reg);
        }

        return "Excel uploaded and data saved successfully";

    } catch (Exception e) {
        return "Error reading Excel file: " + e.getMessage();
    }
}public String uploadExcel(MultipartFile file) {


    private String getCellValue(Row row, Integer colIndex) {
        if (colIndex == null) return "";
        Cell cell = row.getCell(colIndex);
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    // for dates
                    yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    // use BigDecimal to preserve full number
                    BigDecimal bd = BigDecimal.valueOf(cell.getNumericCellValue());
                    yield bd.toPlainString(); // avoids scientific notation
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula(); // optional, handle as needed
            default -> "";
        };
    }
   */
/* public String uploadExcel(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

            // Map headers to column indices
            Map<String, Integer> headerMap = new HashMap<>();
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) throw new IllegalArgumentException("Header row missing");

            for (Cell cell : headerRow) {
                headerMap.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
            }

            // Get the column index for the "image" header
            Integer imageColIndex = headerMap.get("image");

            // Extract image data
            Map<Integer, byte[]> rowImageMap = new HashMap<>();
            for (POIXMLDocumentPart dr : sheet.getRelations()) {
                if (dr instanceof XSSFDrawing drawing) {
                    for (XSSFShape shape : drawing.getShapes()) {
                        if (shape instanceof XSSFPicture picture) {
                            XSSFClientAnchor anchor = (XSSFClientAnchor) picture.getAnchor();
                            int rowNum = anchor.getRow1();
                            int colNum = anchor.getCol1();

                            if (imageColIndex != null && colNum == imageColIndex) {
                                byte[] imageBytes = picture.getPictureData().getData();
                                rowImageMap.put(rowNum, imageBytes);
                            }
                        }
                    }
                }
            }

            // Iterate only through rows with actual content
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                RegistrationDetails reg = new RegistrationDetails();

                reg.setParticipantName(getCellValue(row, headerMap.get("participant name")));
                reg.setEmail(getCellValue(row, headerMap.get("email")));
                reg.setAadhar(getCellValue(row, headerMap.get("aadhar number")));
                reg.setContactNumber(getCellValue(row, headerMap.get("contact number")));
                reg.setEmergencyContact(getCellValue(row, headerMap.get("emergency contact number")));
                reg.setAge(getCellValue(row, headerMap.get("age")));
                reg.setBloodGroup(getCellValue(row, headerMap.get("blood group")));
                reg.setDob(parseDate(getCellValue(row, headerMap.get("dob"))));
                reg.setTsize(getCellValue(row, headerMap.get("tshirt size")));
                reg.setEventName(getCellValue(row, headerMap.get("event name")));
                reg.setGender(getCellValue(row, headerMap.get("gender")));
                reg.setImage(rowImageMap.get(i));

                registrationDetailsRepository.save(reg);
            }

            return "Excel uploaded and data saved successfully";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error reading Excel file: " + e.getMessage();
        }
    }*//*



    public LocalDate parseDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // or your format
        try {
            return LocalDate.parse(dateStr.trim(), formatter);
        } catch (DateTimeParseException e) {
            // Handle invalid or missing date
            return null;
        }
    }

    public void generateCsv(){}
}
*/
