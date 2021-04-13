package com.example.captcha.Entity;

public class CaptchaResult {
    private Captcha actualCaptcha;
    private Captcha resultCaptcha;
    private int solverCount;
    private Boolean solverResult;
    private String fileName;
    private String base64;

    public Captcha getActualCaptcha() {
        return actualCaptcha;
    }

    public void setActualCaptcha(Captcha actualCaptcha) {
        this.actualCaptcha = actualCaptcha;
    }

    public Captcha getResultCaptcha() {
        return resultCaptcha;
    }

    public void setResultCaptcha(Captcha resultCaptcha) {
        this.resultCaptcha = resultCaptcha;
    }

    public int getSolverCount() {
        return solverCount;
    }

    public void setSolverCount(int solverCount) {
        this.solverCount = solverCount;
    }

    public Boolean getSolverResult() {
        return solverResult;
    }

    public void setSolverResult(Boolean solverResult) {
        this.solverResult = solverResult;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }
}
