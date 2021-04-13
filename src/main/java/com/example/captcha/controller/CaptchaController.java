package com.example.captcha.controller;

import com.example.captcha.Entity.CaptchaResult;
import com.example.captcha.service.CaptchaService;
import com.twocaptcha.TwoCaptcha;
import com.twocaptcha.captcha.Normal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class CaptchaController {

    @Autowired
    CaptchaService captchaService;

    @RequestMapping("/solver")
    public String solverCaptcha() throws IOException {
        TwoCaptcha solver = new TwoCaptcha("");

        byte[] bytes = Files.readAllBytes(Paths.get("src/main/resources/captcha/0114.jpg"));
        String base64EncodedImage = Base64.getEncoder().encodeToString(bytes);

        Normal captcha = new Normal();
        captcha.setBase64(base64EncodedImage);

        try {
            solver.solve(captcha);
            System.out.println("Captcha solved: " + captcha.getCode());

            return captcha.getCode();
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());

            return null;
        }
    }


    @RequestMapping("/test")
    public void test(){
//        String test = captchaService.writeFile(null );
    }

    @RequestMapping("/captcha-solve")
    public void readResult() throws IOException {
//        List<CaptchaResult> resultsList =  captchaService.captchaResultsBuilder();
//        String results = captchaService.writeFile(resultsList);

        for(int i = 16 ; i<= 36; i++){
            String actualPath = "src/main/resources/captcha/actualSet" + i +  ".xlsx";
            String captchaPath  = "src/main/resources/captcha/" + i;
            String resultPath  = "resolve-set"+ i + "-file.xlsx";
            List<CaptchaResult> resultsList =  captchaService.captchaResultsBuilder(actualPath, captchaPath);
            String results = captchaService.writeFile(resultsList, resultPath);
        }

    }


}
