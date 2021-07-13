package com.example.mvvmfirebase.utils;

import com.example.mvvmfirebase.model.ContactUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class Util {
    //generate a random digit.........
    public static String randomDigit() {
        char[] chars= "1234567890".toCharArray();
        StringBuilder stringBuilder= new StringBuilder();
        Random random= new Random();
        for(int i=0;i<4;i++){
            char c= chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }
    public static String getPackageName(){
        return getPackageName();
    }
    public static Map<String,String> processContactToHashMap(ContactUser contactUser){
        Map<String,String> uploadMap = new HashMap<>();
        uploadMap.put("id",contactUser.getContactId());
        uploadMap.put("name",contactUser.getContactName());
        uploadMap.put("image",contactUser.getContactImage());
        uploadMap.put("number",contactUser.getContactPhone());
        uploadMap.put("email",contactUser.getContactEmail());
        return uploadMap;
    }
}
