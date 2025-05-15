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

    public String uploadExcel(MultipartFile file) {
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

                            /*if (imageColIndex != null && colNum == imageColIndex) {
                                byte[] imageBytes = picture.getPictureData().getData();

                                // Check if image size is <= 300KB
                                if (isImageSizeWithinLimit(imageBytes, 300)) {
                                    rowImageMap.put(rowNum, imageBytes);
                                } else {
                                    System.out.println("Image in row " + rowNum + " exceeds 300KB limit.");
                                    // Optionally: add to a rejected list or skip storing
                                }
                            }*/

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
    }

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
                    BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
                    yield bd.toPlainString(); // avoids scientific notation
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula(); // optional, handle as needed
            default -> "";
        };
    }


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
