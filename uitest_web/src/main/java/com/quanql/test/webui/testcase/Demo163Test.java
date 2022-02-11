package com.quanql.test.webui.testcase;

import com.quanql.test.core.utils.AssertUtil;
import com.quanql.test.webui.base.WebBaseTest;
import com.quanql.test.webui.page.DemoBaiduPage;
import org.testng.annotations.Test;

public class Demo163Test extends WebBaseTest {

  @Test
  public void testSearchNetease() {
    DemoBaiduPage.openBaidu();
    DemoBaiduPage.typeInSearchEdt("网易");
    DemoBaiduPage.clickSearchBtn();
    AssertUtil.assertTrue(DemoBaiduPage.isNeteaseExisted(), "网易没有出现在结果第1位！");
  }
}
