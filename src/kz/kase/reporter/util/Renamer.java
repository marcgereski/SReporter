package kz.kase.reporter.util;


import java.io.File;

public class Renamer {

    public static void main(String[] args) {
        String suffixToRename = args[0];
        File dir = new File(args[1]);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File element : files) {
                    String path = element.getPath();
                    String fileName = element.getName();
                    fileName = suffixToRename + fileName;
                    path = path.substring(0, path.lastIndexOf(File.separator));
                    String newFile = path + File.separator + fileName;
                    element.renameTo(new File(newFile));
                }
            } else {
                System.exit(1);
            }
        } else {
            System.exit(1);
        }

    }


}
