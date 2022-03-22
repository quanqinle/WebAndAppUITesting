/*
 * Tencent is pleased to support the open source community by making
 * Tencent GT (Version 2.4 and subsequent versions) available.
 *
 * Notwithstanding anything to the contrary herein, any previous version
 * of Tencent GT shall not be subject to the license hereunder.
 * All right, title, and interest, including all intellectual property rights,
 * in and to the previous version of Tencent GT (including any and all copies thereof)
 * shall be owned and retained by Tencent and subject to the license under the
 * Tencent GT End User License Agreement (http://gt.qq.com/wp-content/EULA_EN.html).
 *
 * Copyright (C) 2015 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.quanql.test.core.utils;

import java.math.BigDecimal;

/**
 * Double数据的操作
 *
 * @author quanqinle
 */
public class DoubleUtil {
  /**
   * double 乘法
   *
   * @param d1
   * @param d2
   * @return
   */
  public static double mul(double d1, double d2) {
    BigDecimal bd1 = new BigDecimal(Double.toString(d1));
    BigDecimal bd2 = new BigDecimal(Double.toString(d2));

    try {
      return bd1.multiply(bd2).doubleValue();
    } catch (Exception e) {
      // 根据bugly观测，在进入GTOpMulPerfActivity页时有极小概率crash，故加上异常保护
      // @see http://bugly.qq.com/detail?app=900010910&pid=1&ii=152#stack
      e.printStackTrace();
      return 0;
    }
  }

  /**
   * double 除法
   *
   * @param d1
   * @param d2
   * @param scale 四舍五入 小数点位数
   * @return
   */
  public static double div(double d1, double d2, int scale) {
    // 当然在此之前，你要判断分母是否为0，
    // 为0你可以根据实际需求做相应的处理

    BigDecimal bd1 = new BigDecimal(d1);
    BigDecimal bd2 = new BigDecimal(d2);
    // return bd1.divide(bd2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    // 直接向下取整，保持和UI展示一致
    try {
      return bd1.divide(bd2, scale, BigDecimal.ROUND_DOWN).doubleValue();
    } catch (Exception e) {
      // 根据bugly观测，在进入GTOpMulPerfActivity页时有极小概率crash，故加上异常保护
      // @see http://bugly.qq.com/detail?app=900010910&pid=1&ii=46#stack
      e.printStackTrace();
      return 0;
    }
  }
}
