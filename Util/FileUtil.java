package Util;

import Model.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class FileUtil {
    public static final char LEVEL_CHAR = '+';
    public static final String ONTOLOGY_FILE = "ontology.txt";

    public static String stringNormalize( String str) {
        return str.replace(LEVEL_CHAR+"","");
    }

    public static int symbolCount( String str) {
        char[] CharArray = str.toCharArray();
        int count = 0;
        for (int i = 0; i < CharArray.length; i++) {
            if (CharArray[i] == LEVEL_CHAR) count++;
            else break;
        }
        return  count;
    }

    public static ArrayList<Node> fetchData(String input) {
        ArrayList<Node> nodeList = new ArrayList<>();
        File file = new File(input);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;

            while ((st = br.readLine()) != null) {
                nodeList.add(new Node( stringNormalize(st), symbolCount(st)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nodeList;
    }
}
