package com.example.demo.Calc;

public class Calc {

    // преобразование размера файла
    public static String getFileSize (long fileSize) {
        int k;

        for (k = 0; fileSize > 1023; k++) {
            fileSize = fileSize / 1024;
        }

        switch (k) {
            case 0: {
                return fileSize + " байт";
            }
            case 1: {
                return fileSize + " КБайт";
            }
            case 2: {
                return fileSize + " МБайт";
            }
            case 3: {
                return fileSize + " ГБайт";
            }
        }

        return "";

    }

}
