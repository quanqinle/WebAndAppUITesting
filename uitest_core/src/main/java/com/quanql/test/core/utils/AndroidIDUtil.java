package com.quanql.test.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理混淆后的Android id
 *
 * @author quanqinle
 */
public class AndroidIDUtil {

  private static String packageName = Constant.APP_PACKAGE_NAME;
  protected static String mappingFileProp =
      ConfigUtil.getInstance().getProperty("resource.mapping.file", "");
  protected static String mappingFileFullPath =
      String.join(File.separator, System.getProperty("user.dir"), mappingFileProp);

  /** APP原始id与混淆后id的映射 */
  protected static final Map<String, String> APP_ID_MAPS =
      new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;

        {
          if (!mappingFileProp.isEmpty()) {
            long ts = System.currentTimeMillis();
            String resIdStr = packageName + ".R.id.";
            int startIndex = resIdStr.length();
            File file = new File(mappingFileFullPath);
            BufferedReader reader = null;
            try {
              String tempStr = null;
              String[] tempArr = {"", ""};

              reader = new BufferedReader(new FileReader(file));
              while ((tempStr = reader.readLine()) != null) {
                // 形如: ${pakagename}.R.id.about_desc -> ${pakagename}.R.id.b
                if (tempStr.contains(resIdStr)) {
                  tempArr = tempStr.split("->");
                  put(
                      tempArr[0].trim().substring(startIndex),
                      tempArr[1].trim().substring(startIndex));
                }
              }
              reader.close();
            } catch (IOException e) {
              LogUtil.info("fail to read: " + file);
              e.printStackTrace();
            } finally {
              if (reader != null) {
                try {
                  reader.close();
                } catch (IOException ignored) {
                }
              }
            }
            long te = System.currentTimeMillis();
            LogUtil.info("解析mapping耗时ms: " + (te - ts));
          }
        }
      };

  /**
   * 通过原始id获取混淆后的id<br>
   * 找不到映射的关系时，则直接用原始id<br>
   * 注意：<br>
   * 1.该方法只对android项目有效，非android项目会直接原样返回传入的参数，即original_id<br>
   *
   * @author quanqinle
   * @param original_id 即${pakagename}.R.id.original_id中的original_id
   * @return 适用于appium的id串，形如${pakagename}:id/obfuscated_id
   */
  public static String getObfuscatedID(String original_id) {
    if ("Android".equalsIgnoreCase(Constant.DRIVER_TYPE)) {
      String appiumAndroidIdPrefix = packageName + ":id/";
      if (!mappingFileProp.isEmpty()) {
        return appiumAndroidIdPrefix + APP_ID_MAPS.getOrDefault(original_id, original_id);
      }
      return appiumAndroidIdPrefix + original_id;
    } else {
      return original_id;
    }
  }
}
