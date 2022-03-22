/***********************************************************************
 *
 *   FileUtil.java
 *
 *   @creator       kevinkong@corp.netease.com
 *   @create-time   2011-8-25 下午03:59:03
 ***********************************************************************/
package com.quanql.test.core.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作
 *
 * @author quanqinle
 */
public class FileUtil {
  public static BufferedWriter bw;
  public static FileOutputStream out;
  public static OutputStreamWriter osw;

  /** for debug */
  public static void main(String[] args) {
    createFolder("perfreport" + File.separator + "debug");
  }

  /**
   * 创建文件夹
   *
   * @param folderName 文件夹名称
   */
  public static void createFolder(String folderName) {
    LogUtil.debug(folderName);
    File dirFile = new File(folderName);
    // if (!dirFile.isDirectory()) {
    // dirFile.mkdirs(); // 不存在则新建目录
    // }
    boolean bFile = dirFile.exists();
    if (bFile == false) {
      bFile = dirFile.mkdirs();
    }
    if (bFile == true) {
      LogUtil.debug("Create folder successfully!");
    } else {
      LogUtil.info("Create folder error!");
    }
  }

  private static String getPerfReportFolder() {
    // 存放目录
    return "perfreport" + File.separator + TimeUtil.getCurrentDateTime(2);
  }

  /**
   * 写文件<br>
   * 性能测试使用此函数记录结果<br>
   * 文件已存在时，追加内容<br>
   * 文件存储路径，形如 /perfreport/20160808/
   *
   * @author 权芹乐
   * @param fileName 文件名，会被处理自动加上时间和后缀，形如 fileName.csv
   * @param content 写入内容，需调用者自行用半角逗号分隔
   */
  public static void write2Csv(String fileName, String content) {

    write2Csv(
        getPerfReportFolder(), /* CommonUtil.getCurrentDateTime(3) + */ fileName, content, false);
  }

  /**
   * 写文件<br>
   * 文件已存在时，追加内容
   *
   * @param folderName 以项目目录为根目录
   * @param fileName 文件名（后缀默认csv，不含加后缀）
   * @param content 写入内容，自行用半角逗号分隔
   * @isDelete 是否删除已有文件
   */
  public static void write2Csv(
      String folderName, String fileName, String content, boolean isDelete) {

    try {
      createFolder(folderName);
      String resultFilePath = folderName + File.separator + fileName + ".csv";
      File resultFile = new File(resultFilePath);

      // 为了便于jenkins文件发送，数据分析时需要先删除已有文件，重新生成，做兼容
      if (isDelete && resultFile.exists()) {
        resultFile.delete();
      }
      resultFile.createNewFile();

      // 文末追加
      out = new FileOutputStream(resultFile, true);
      osw = new OutputStreamWriter(out, "UTF-8");
      bw = new BufferedWriter(osw);
      bw.write(content);
    } catch (IOException e) {
      LogUtil.error("creat csv file false!\n" + e.getMessage());
    } finally {
      closeOpenedStream();
    }
  }

  /**
   * 从csv中读取测试数据
   *
   * @author quanqinle
   * @param filePath
   * @param startline 从第n行开始读数据（n从1开始），超出[1，maxline]时，使用默认值1
   * @return
   */
  public static Object[][] readFromCsv(String filePath, int startline) {
    List<String> lines = FileUtil.readFileByLines(filePath);
    int linecnt = lines.size();
    if (0 >= startline || startline > linecnt) {
      startline = 1;
    }

    int lineInList = startline - 1;
    Object[][] result = new Object[lines.size() - lineInList][];

    // 第1行是字段名，不用存放到数据对象中
    for (int i = lineInList; i < linecnt; i++) {
      String line = lines.get(i);
      LogUtil.info(line);
      result[i - lineInList] = line.split(",");
    }

    return result;
  }

  /** close all opened stream. */
  public static void closeOpenedStream() {
    try {
      if (bw != null) {
        bw.close();
      }
      if (osw != null) {
        osw.close();
      }
      if (out != null) {
        out.close();
      }
    } catch (Exception e) {
      LogUtil.error(e.getMessage());
    }
  }

  /**
   * 复制一个文件到另一个文件夹下
   *
   * @param oldPath 需要复制文件的路径
   * @param newPath 复制后的完整路径
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void copyFile(String oldPath, String newPath) throws IOException {
    int byteread = 0;
    File oldfile = new File(oldPath);
    File newFile = new File(newPath);
    if (newFile.exists()) {
      newFile.delete();
    }
    if (oldfile.exists()) {
      // 读入原文件
      InputStream inStream = new FileInputStream(oldPath);
      FileOutputStream fs = new FileOutputStream(newPath);
      byte[] buffer = new byte[1444];
      while ((byteread = inStream.read(buffer)) != -1) {
        fs.write(buffer, 0, byteread);
      }
      fs.close();
      inStream.close();
    } else {
      throw new FileNotFoundException("未找到" + oldPath + "文件");
    }
  }

  /**
   * 删除文件
   *
   * @param file 需删除的文件名，或目录名
   */
  public void deleteFile(File file) {
    LogUtil.info("Beginning Delete File!" + file.getName());
    try {
      if (file.exists()) {
        if (file.isFile()) {
          boolean de = file.delete();
          LogUtil.debug("filename=" + file.getName() + " " + de);
        } else if (file.isDirectory()) {
          File[] files = file.listFiles();
          assert files != null;
          for (File value : files) {
            this.deleteFile(value);
          }
        }
        file.delete();
      } else {
        LogUtil.info("所删除的文件不存在！" + '\n');
      }
    } catch (Exception e) {
      LogUtil.info(e.getStackTrace());
    }
    LogUtil.info("End Delete File!" + file.getName());
    return;
  }

  /** 创建文件 */
  public void createFile(String filePath) {
    File filename = new File(filePath);
    if (!filename.exists()) {
      try {
        filename.createNewFile();
      } catch (IOException e) {
        LogUtil.warn("Create file error!");
      }
    }
  }

  /** 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。 */
  public static void readFileByBytes(String fileName) {
    File file = new File(fileName);
    InputStream in;
    try {
      System.out.println("以字节为单位读取文件内容，一次读一个字节：");
      // 一次读一个字节
      in = new FileInputStream(file);
      int tempbyte;
      while ((tempbyte = in.read()) != -1) {
        System.out.write(tempbyte);
      }
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    try {
      System.out.println("以字节为单位读取文件内容，一次读多个字节：");
      // 一次读多个字节
      byte[] tempbytes = new byte[100];
      int byteread = 0;
      in = new FileInputStream(fileName);
      FileUtil.showAvailableBytes(in);
      // 读入多个字节到字节数组中，byteread为一次读入的字节数
      while ((byteread = in.read(tempbytes)) != -1) {
        System.out.write(tempbytes, 0, byteread);
      }
    } catch (Exception e1) {
      e1.printStackTrace();
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e1) {
        }
      }
    }
  }

  /** 以字符为单位读取文件，常用于读文本，数字等类型的文件 */
  public static void readFileByChars(String fileName) {
    File file = new File(fileName);
    Reader reader = null;
    try {
      System.out.println("以字符为单位读取文件内容，一次读一个字节：");
      // 一次读一个字符
      reader = new InputStreamReader(new FileInputStream(file));
      int tempchar;
      while ((tempchar = reader.read()) != -1) {
        // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
        // 但如果这两个字符分开显示时，会换两次行。
        // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
        if (((char) tempchar) != '\r') {
          System.out.print((char) tempchar);
        }
      }
      reader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      System.out.println("以字符为单位读取文件内容，一次读多个字节：");
      // 一次读多个字符
      char[] tempchars = new char[30];
      int charread = 0;
      reader = new InputStreamReader(new FileInputStream(fileName));
      // 读入多个字符到字符数组中，charread为一次读取字符数
      while ((charread = reader.read(tempchars)) != -1) {
        // 同样屏蔽掉\r不显示
        if ((charread == tempchars.length) && (tempchars[tempchars.length - 1] != '\r')) {
          System.out.print(tempchars);
        } else {
          for (int i = 0; i < charread; i++) {
            if (tempchars[i] == '\r') {
              continue;
            } else {
              System.out.print(tempchars[i]);
            }
          }
        }
      }

    } catch (Exception e1) {
      e1.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e1) {
        }
      }
    }
  }

  /** 以行为单位读取文件，常用于读面向行的格式化文件 */
  public static List<String> readFileByLines(String fileName) {
    File file = new File(fileName);
    List<String> result = new ArrayList<String>();

    BufferedReader reader = null;
    try {
      LogUtil.debug("以行为单位读取文件内容，一次读一整行：");
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

      String tempString = null;
      int line = 1;
      // 一次读入一行，直到读入null为文件结束
      while ((tempString = reader.readLine()) != null) {
        // 显示行号
        LogUtil.debug("line " + line + ": " + tempString);
        line++;
        result.add(tempString);
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e1) {
        }
      }
    }
    return result;
  }

  /** 显示输入流中还剩的字节数 */
  private static void showAvailableBytes(InputStream in) {
    try {
      System.out.println("当前字节输入流中的字节数为：" + in.available());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
