package me.alek.serversecurity.utils;

import org.objectweb.asm.tree.ClassNode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    public static double arithmeticSecure(double d1, double d2) {
        try {
            return d1 / d2;
        } catch (ArithmeticException ex) {
            return 0;
        }
    }

    public static String percentage(double percentage) {
        DecimalFormat df = new DecimalFormat("0");
        double d = Math.ceil(percentage * 100);
        if (d > 100) d = 100;
        return df.format(d) + "%";
    }

    public static int mostOccuringChar(String str) {
        char[] tempArray = str.toCharArray();
        Arrays.sort(tempArray);
        String s = new String(tempArray);
        int n = s.length();
        int max_count = 0;
        int count = 1;
        for (int i = 1; i <= n; i++) {
            if ((i == n) || (s.charAt(i) != s.charAt(i - 1))) {
                if (max_count < count) {
                    max_count = count;
                }
                count = 1;
            } else {
                count++;
            }
        }
        return max_count;
    }

    public static List<String> getBigrams(String input) {
        List<String> bigramList = new ArrayList<String>();

        if(input == null || input.length() == 0) {
            return bigramList;
        }

        for(int idx=0; idx<input.length(); ++idx) {
            if(idx < input.length()-1) {
                bigramList.add("" + input.charAt(idx) + input.charAt(idx+1));
            }
        }
        return bigramList;
    }

    public static int frequencyOfWord(ClassNode classNode) {
        ArrayList<String> namesList = new ArrayList<>();
        classNode.methods.stream().map(method -> method.name).forEach(methodName -> {
            namesList.addAll(Arrays.asList(methodName.split("[ _-]")));
        });
        classNode.fields.stream().map(field -> field.name).forEach(fieldName -> {
            namesList.addAll(Arrays.asList(fieldName.split("[ _-]")));
        });

        int n = namesList.size();
        int freq = 0;

        for (int i = 0; i < n; i++) {
            int count = 0;
            for (int j = i + 1; j < n; j++) {
                if (namesList.get(j).equals(namesList.get(i))) {
                    count++;
                }
            }
            if (count >= freq) {
                freq = count;
            }
        }
        return freq;
    }

}
