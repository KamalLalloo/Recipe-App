package com.example.homepage20;

//import javax.swing.*;
import java.sql.*;
import java.util.regex.Pattern;
import java.util.*;


public class Validation {
     static boolean isName(String value)   // validates if input is a name
    {
        boolean valid = true;
        if (value.equals(""))
        {
            valid = false;
            return valid;
        }
        for (int i = 0; i < value.length(); i++)
        {
            if (!(Character.isLetter(value.charAt(i))))
            {
                valid = false;
                return valid;
            }

        }
        return valid;
    }

    /*Sign up page */

    static int confirmPassWord(String pass, String cPass){

        if(pass==null || cPass==null){
            //JOptionPane.showMessageDialog(null,"Please enter a valid password","Error", JOptionPane.ERROR_MESSAGE);
            return 0;
        }
        if(pass.equals(cPass)){
            return 2;
        }
        else {
            return 1;
            //JOptionPane.showMessageDialog(null,"Password do not match", cPass, 0);
        }

    }
    static boolean isEmail(String value) // checks if input is a valid email
    {
        if(value.equals("")){
            return false;
        }else {
            String pattern = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"; // from https://www.baeldung.com/java-email-validation-regex
            return Pattern.compile(pattern).matcher(value).matches();
        }
    }

    static int validUsername(String value)  // checks if input meets username specifications
    {
        if(!(value.equals(""))){
        if (value.length() >= 6)
        {
            for (int i = 0; i < value.length(); i++)
            {
                if (Character.isLetter(value.charAt(i)) || Character.isDigit(value.charAt(i)) || value.charAt(i) == '_' || value.charAt(i) == '.' || value.charAt(i) == '-')
                {
                } else
                {
                    return 1; // "Only Letters, Numbers, -, _ And . Are Allowed!"
                }
            }
        } else
        {
            return 0; // "The User Name Is Too Short!"
        }
    }else{
            return 6;
        }
       return 2;

    }
    static int isPassword(String value) // checks if input meets password specifications
    {
        int counter = 0;
        if(!(value.equals(""))){
        if (value.length() >= 9)
        {
            if (Character.isUpperCase(value.charAt(0)))
            {
                for (int i = 0; i < value.length(); i++)
                {
                    if (!(Character.isLetter(value.charAt(i)) || Character.isDigit(value.charAt(i))))
                    {
                        return 2; // "Only Letters and Numbers are Allowed!"
                    }
                    if (Character.isDigit(value.charAt(i)))
                    {
                        counter++;
                    }
                }

            } else
            {
                return 1; // "First Character Must Be A Capital Letter!"
            }
        } else
        {
            return 0; // "Password Must be Longer Than 9 Characters!"
        }
    }else{
            return 6;
        }
        if (counter < 3)
        {
            return 3;
        } else
        {
            return 4;
        }

    }

    static public boolean isNum(String value){
        if (value.equals("")) {
            return false;
        }

        return value.matches("\\d+");
    }

    static public boolean isValidCookingTime(String value) {
        if (value.equals("")) {
            return false;
        }
        String lowerCaseInput = value.toLowerCase();
        return lowerCaseInput.contains("hour") || lowerCaseInput.contains("hours") ||
                lowerCaseInput.contains("minute") || lowerCaseInput.contains("minutes");
    }




}


