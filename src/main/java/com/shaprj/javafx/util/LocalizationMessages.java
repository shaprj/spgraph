package com.shaprj.javafx.util;
/*
 * Created by O.Shalaevsky on 04.03.2020
 */

import java.util.ResourceBundle;

public class LocalizationMessages {

    private static ResourceBundle res = ResourceBundle.getBundle("messages");

    public static String getString(String key){
        return res != null ? res.getString(key) : key;
    }

}
