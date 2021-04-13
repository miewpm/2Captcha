package com.example.captcha.Entity;

import java.util.List;

public class Captcha {
    private List<String> charCaptcha;
    private String stringCaptcha;

    public List<String> getCharCaptcha() {
        return charCaptcha;
    }

    public void setCharCaptcha(List<String> charCaptcha) {
        this.charCaptcha = charCaptcha;
    }

    public String getStringCaptcha() {
        return stringCaptcha;
    }

    public void setStringCaptcha(String stringCaptcha) {
        this.stringCaptcha = stringCaptcha;
    }
}
