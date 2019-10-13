package com.example.finalyearclient;

public class Pateint {
    private String name;
    private String password;
    private String age;
    private String weight;
    private String height;
    private String sex;
    private String bloodPressure;
    private String bloodOxygen;
    private String respirationRate;
    private String heartRate;


    Pateint(String Name,String Password,String Age,String Weight, String Height, String Sex, String BloodPressure,
            String BloodOxygen,String RespirationRate,String HeartRate ){
        name = Name;
        password = Password;
        age = Age;
        weight = Weight;
        height = Height;
        sex = Sex;
        bloodPressure = BloodPressure;
        bloodOxygen = BloodOxygen;
        respirationRate = RespirationRate;
        heartRate = HeartRate;
    }

    public String getName(){
        return name;
    }

    public String getPassword(){
        return password;
    }

    public String getAge(){
        return age;
    }

    public String getWeight(){
        return weight;
    }

    public String getHeight(){
        return height;
    }

    public String getGender() {
        return sex;
    }

    public String getBloodPressure(){
        return bloodPressure;
    }

    public String getBloodOxygen(){
        return bloodOxygen;
    }

    public String getRespirationRate(){
        return respirationRate;
    }

    public String getHeartRate(){
        return heartRate;
    }

    public String toString(){
        return name+","+password+","+age+","+weight+","+height+","+sex+","+bloodPressure+","+bloodOxygen+","+respirationRate+","+heartRate;
    }

    /*public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public void setBloodOxygen(String bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }

    public void setRespirationRate(String respirationRate) {
        this.respirationRate = respirationRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }*/


}
