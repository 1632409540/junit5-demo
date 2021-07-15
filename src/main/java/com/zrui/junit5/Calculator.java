package com.zrui.junit5;

public class Calculator {

    private Integer age;

    public int multiply(int a, int b){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return a * b;
    }

    public void close(){
        this.close();
    }

    public void setAge(String age1){
        try{
            age = Integer.parseInt(age1);
        }catch (IllegalArgumentException e){
            throw e;
        }

    }

    public void doBackup() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getStatus() {
        return 200;
    }
}
