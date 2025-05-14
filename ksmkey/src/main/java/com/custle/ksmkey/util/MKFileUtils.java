//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.ksmkey.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MKFileUtils {
    public MKFileUtils() {
    }

    public static boolean deleteDirectory(String filePath) {
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }

        File dirFile = new File(filePath);
        if (dirFile.exists() && dirFile.isDirectory()) {
            boolean flag = true;
            File[] files = dirFile.listFiles();

            for(int i = 0; i < files.length; ++i) {
                if (files[i].isFile()) {
                    flag = deleteFile(files[i].getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                } else {
                    flag = deleteDirectory(files[i].getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                }
            }

            return !flag ? false : dirFile.delete();
        } else {
            return false;
        }
    }

    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        return file.isFile() && file.exists() ? file.delete() : false;
    }

    public static boolean copyFolder(String oldPath, String newPath) {
        try {
            File newFile = new File(newPath);
            if (!newFile.exists() && !newFile.mkdirs()) {
                return false;
            } else {
                File oldFile = new File(oldPath);
                String[] files = oldFile.list();
                String[] var6 = files;
                int var7 = files.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    String file = var6[var8];
                    File temp;
                    if (oldPath.endsWith(File.separator)) {
                        temp = new File(oldPath + file);
                    } else {
                        temp = new File(oldPath + File.separator + file);
                    }

                    if (temp.isDirectory()) {
                        copyFolder(oldPath + "/" + file, newPath + "/" + file);
                    } else if (temp.exists() && temp.isFile() && temp.canRead()) {
                        FileInputStream fileInputStream = new FileInputStream(temp);
                        FileOutputStream fileOutputStream = new FileOutputStream(newPath + "/" + temp.getName());
                        byte[] buffer = new byte[1024];

                        int byteRead;
                        while((byteRead = fileInputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, byteRead);
                        }

                        fileInputStream.close();
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                }

                return true;
            }
        } catch (Exception var14) {
            return false;
        }
    }
}
