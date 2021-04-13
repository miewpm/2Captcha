package com.example.captcha.service;

import com.example.captcha.Entity.Captcha;
import com.example.captcha.Entity.CaptchaResult;
import com.twocaptcha.TwoCaptcha;
import com.twocaptcha.captcha.Normal;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CaptchaService {
    private static final String CSV_FILE_LOCATION = "src/main/resources/captcha/actualSet3.xlsx";
    private static final String CAPTCHA_DIRECTORY_LOCATION  = "src/main/resources/captcha/3";
    private static final String TWO_CAPTCHA_API_KEY = "KEY";
    private static final String RESULT_FILE  = "resolve-set2-file.xlsx";


    public List<CaptchaResult> captchaResultsBuilder(String actualFile, String captchaFile) throws IOException {
        Workbook workbook = null;
        List<CaptchaResult> captchaResultList = new ArrayList<>();
        try {
            workbook = WorkbookFactory.create(new File(actualFile));
            DataFormatter dataFormatter = new DataFormatter();


            workbook.forEach(sheet -> {

                List<String> captchaPathList = getListPath(captchaFile);
                //loop through all rows and columns and create Course object
                int index = 0;
                for(Row row : sheet) {
                    List<String> actualCharList = new ArrayList<>();

                    CaptchaResult captchaResult = new CaptchaResult();
                    Captcha actualCaptcha = new Captcha();
                    Captcha solverCaptcha = new Captcha();

                    actualCaptcha.setStringCaptcha(dataFormatter.formatCellValue(row.getCell(1)));
                    int count = actualCaptcha.getStringCaptcha().length();
                    for (int i = 0 ; i< count ; i++){
                        actualCharList.add(String.valueOf(actualCaptcha.getStringCaptcha().charAt(i)));
                    }
                    actualCaptcha.setCharCaptcha(actualCharList);


                    System.out.println("Actual : " + actualCaptcha.getStringCaptcha());

                    List<String> solverCharList = new ArrayList<>();
                    String solverResult = null;
                    if(index < captchaPathList.size()){
                        solverResult = solverCaptcha(encodeImage(captchaPathList.get(index)), count);
                    }
                    solverCaptcha.setStringCaptcha(solverResult);
                    if(solverResult != null){
                        for (int i = 0 ; i< solverResult.length() ; i++){
                            solverCharList.add(String.valueOf(solverResult.charAt(i)));
                        }
                    }else {
                        solverCharList.add("");
                    }
                    solverCaptcha.setCharCaptcha(solverCharList);

                    captchaResult.setActualCaptcha(actualCaptcha);
                    captchaResult.setResultCaptcha(solverCaptcha);
                    captchaResult.setFileName(captchaPathList.get(index));
                    captchaResult.setSolverResult(compareString(actualCaptcha.getStringCaptcha(), solverResult));
                    captchaResult.setSolverCount(compareCharacter(actualCharList, solverCharList));

                    System.out.println("path : " + captchaResult.getFileName());
                    System.out.println("result : " + captchaResult.getSolverResult().toString());
                    captchaResultList.add(index, captchaResult);
                    index = index + 1;
                }
            });


        } catch (EncryptedDocumentException  | IOException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }finally {
            try {
                if(workbook != null) workbook.close();
            } catch (IOException e) {
                System.out.println("Error occurred: " + e.getMessage());
            }
        }
        return captchaResultList;
    }

    public String encodeImage(String path)  {
        byte[] bytes = new byte[0];
        String result = "";
        try {
            bytes = Files.readAllBytes(Paths.get(path));
            result = Base64.getEncoder().encodeToString(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<String> getListPath(String path){
        try (Stream<Path> walk = Files.walk(Paths.get(path))) {

            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());
            Collections.sort(result);


            Collections.sort(result);
            result.forEach(System.out::println);
            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String solverCaptcha(String base64, int len) {
        TwoCaptcha solver = new TwoCaptcha(TWO_CAPTCHA_API_KEY);

        Normal captcha = new Normal();
        captcha.setBase64(base64);
        String result = "";

        try {
            solver.solve(captcha);
            System.out.println("Captcha solved: " + captcha.getCode());

            result = captcha.getCode();
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());

            for(int i = 0 ; i< len ; i++){
                result = result + "-";
            }

        }
        return result;
    }

    public Boolean compareString(String actual, String result) {
        Boolean compareResult = false;
        if(result != null && actual != null ){
            if (actual.equals(result)){
                compareResult =  true;
            }
        }
        return compareResult;
    }

    public int compareCharacter(List<String> actual , List<String> result){
        int count = 0;

        if(result.size() > 0 && actual.size() > 0) {
            for (int i = 0; i < actual.size() &&  i < result.size(); i++) {
                if(result.get(i) != null && actual.get(i) != null){
                    if (actual.get(i).equals(result.get(i))) {
                        count = count + 1;
                    }
                }
            }
        }
        return count;
    }

    public String writeFile(List<CaptchaResult> captchaResults, String resultFile){
        // Create a Workbook
        Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

        // Create a Sheet
        Sheet sheet = workbook.createSheet("Sheet1");

        Row heardRow = sheet.createRow(0);
        heardRow.createCell(1).setCellValue("ActualCaptcha");
        heardRow.createCell(2).setCellValue("SolverResult");
        heardRow.createCell(3).setCellValue("Solver_1");
        heardRow.createCell(4).setCellValue("Solver_2");
        heardRow.createCell(5).setCellValue("Solver_3");
        heardRow.createCell(6).setCellValue("Solver_4");
        heardRow.createCell(7).setCellValue("Solver_5");
        heardRow.createCell(8).setCellValue("Solver_6");
        heardRow.createCell(9).setCellValue("Solver_7");
        heardRow.createCell(10).setCellValue("Solver_8");
        heardRow.createCell(11).setCellValue("Solver_9");
        heardRow.createCell(12).setCellValue("Solver_10");
        heardRow.createCell(13).setCellValue("CollectCount");
        heardRow.createCell(14).setCellValue("SolverResult");
        heardRow.createCell(15).setCellValue("FileName");


        // Create Other rows and cells with employees data
        int rowNum = 1;
        for(int r = 0; r < captchaResults.size(); r++) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0)
                    .setCellValue(String.valueOf(rowNum -1));
            row.createCell(1)
                    .setCellValue(captchaResults.get(r).getActualCaptcha().getStringCaptcha());

            System.out.println(String.valueOf(rowNum -1)+ ": " + captchaResults.get(r).getResultCaptcha().getStringCaptcha());

            row.createCell(2)
                    .setCellValue(captchaResults.get(r).getResultCaptcha().getStringCaptcha());

            int col = 3;
            List<String> charResult = captchaResults.get(r).getResultCaptcha().getCharCaptcha();
            if(charResult != null){
                for(int c = 0; c < charResult.size(); c++){
                    row.createCell(col)
                            .setCellValue(charResult.get(c));
                    col = col + 1;
                }
            }


            row.createCell(13)
                    .setCellValue(String.valueOf(captchaResults.get(r).getSolverCount()));

            if(captchaResults.get(r).getSolverResult() != null){
                row.createCell(14)
                        .setCellValue(captchaResults.get(r).getSolverResult().toString());
            }else {
                row.createCell(14)
                        .setCellValue("");
            }

            row.createCell(15)
                    .setCellValue(captchaResults.get(r).getFileName());
        }

        // Write the output to a file
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(resultFile);

            workbook.write(fileOut);
            fileOut.close();
            // Closing the workbook
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
}
