package com.quanql.test.core.listener;

import com.quanql.test.core.utils.ConfigUtil;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import org.testng.*;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.log4testng.Logger;
import org.testng.xml.XmlSuite;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Reported designed to render self-contained HTML top down view of a testing suite.
 *
 * @author Paul Mendelson
 * @since 5.2
 * @version $Revision: 719 $
 */
public class PowerEmailableReporter implements IReporter {
  private static final Logger L = Logger.getLogger(PowerEmailableReporter.class);

  // ~ Instance fields ------------------------------------------------------

  private PrintWriter m_out;

  private int m_row;

  private Integer m_testIndex;

  private Set<Integer> testIds = new HashSet<Integer>();
  private List<Integer> allRunTestIds = new ArrayList<Integer>();
  private JavaDocBuilder builder = new JavaDocBuilder();

  // ~ Methods --------------------------------------------------------------

  /** Creates summary of the run */
  @Override
  public void generateReport(List<XmlSuite> xml, List<ISuite> suites, String outdir) {
    try {
      m_out = createWriter(outdir);
    } catch (IOException e) {
      L.error("output file", e);
      return;
    }
    ConfigUtil cr = ConfigUtil.getInstance();
    if (cr.getSrouceCodeEncoding() != null) {
      builder.setEncoding(cr.getSrouceCodeEncoding());
    } else {
      builder.setEncoding("UTF-8");
    }

    builder.addSourceTree(new File(cr.getSourceCodeDir()));
    startHtml(m_out);
    generateSuiteSummaryReport(suites);
    testIds.clear();
    generateMethodSummaryReport(suites);
    testIds.clear();
    generateMethodDetailReport(suites);
    testIds.clear();
    endHtml(m_out);
    m_out.flush();
    m_out.close();
  }

  protected PrintWriter createWriter(String outdir) throws IOException {
    new File(outdir).mkdirs();
    return new PrintWriter(
        new BufferedWriter(new FileWriter(new File(outdir, "power-emailable-report.html"))));
  }

  /** Creates a table showing the highlights of each test method with links to the method details */
  protected void generateMethodSummaryReport(List<ISuite> suites) {
    startResultSummaryTable("methodOverview");
    int testIndex = 1;
    for (ISuite suite : suites) {
      if (suites.size() > 1) {
        titleRow(suite.getName(), 5);
      }
      Map<String, ISuiteResult> r = suite.getResults();
      for (ISuiteResult r2 : r.values()) {
        ITestContext testContext = r2.getTestContext();
        String testName = testContext.getName();
        m_testIndex = testIndex;

        // resultSummary(suite, testContext.getSkippedConfigurations(),
        // testName, "skipped",
        // " (configuration methods)");
        resultSummary(suite, testContext.getSkippedTests(), testName, "skipped", "");
        // resultSummary(suite, testContext.getFailedConfigurations(),
        // testName, "failed",
        // " (configuration methods)");
        resultSummary(suite, testContext.getFailedTests(), testName, "failed", "");
        resultSummary(suite, testContext.getPassedTests(), testName, "passed", "");

        testIndex++;
      }
    }
    m_out.println("</table>");
  }

  /** Creates a section showing known results for each method */
  protected void generateMethodDetailReport(List<ISuite> suites) {
    for (ISuite suite : suites) {
      Map<String, ISuiteResult> r = suite.getResults();
      for (ISuiteResult r2 : r.values()) {
        ITestContext testContext = r2.getTestContext();
        if (r.values().size() > 0) {
          m_out.println("<h1>" + testContext.getName() + "</h1>");
        }
        resultDetail(testContext.getFailedConfigurations());
        resultDetail(testContext.getFailedTests());
        resultDetail(testContext.getSkippedConfigurations());
        resultDetail(testContext.getSkippedTests());
        resultDetail(testContext.getPassedTests());
      }
    }
  }

  /** @param tests */
  private void resultSummary(
      ISuite suite, IResultMap tests, String testname, String style, String details) {
    if (tests.getAllResults().size() > 0) {
      StringBuffer buff = new StringBuffer();
      String lastClassName = "";
      int mq = 0;
      int cq = 0;
      Map<String, Integer> methods = new HashMap<String, Integer>();
      Set<String> setMethods = new HashSet<String>();
      for (ITestNGMethod method : getMethodSet(tests, suite)) {
        m_row += 1;

        ITestClass testClass = method.getTestClass();
        String className = testClass.getName();
        if (mq == 0) {
          String id = (m_testIndex == null ? null : "t" + Integer.toString(m_testIndex));
          titleRow(testname + " &#8212; " + style + details, 5, id);
          m_testIndex = null;
        }
        if (!className.equalsIgnoreCase(lastClassName)) {
          if (mq > 0) {
            cq += 1;
            m_out.print("<tr class=\"" + style + (cq % 2 == 0 ? "even" : "odd") + "\">" + "<td");
            if (mq > 1) {
              m_out.print(" rowspan=\"" + mq + "\"");
            }
            m_out.println(">" + lastClassName + "</td>" + buff);
          }
          mq = 0;
          buff.setLength(0);
          lastClassName = className;
        }
        Set<ITestResult> resultSet = tests.getResults(method);
        long end = Long.MIN_VALUE;
        long start = Long.MAX_VALUE;
        for (ITestResult testResult : tests.getResults(method)) {
          if (testResult.getEndMillis() > end) {
            end = testResult.getEndMillis();
          }
          if (testResult.getStartMillis() < start) {
            start = testResult.getStartMillis();
          }
        }
        mq += 1;
        if (mq > 1) {
          buff.append("<tr class=\"" + style + (cq % 2 == 0 ? "odd" : "even") + "\">");
        }
        String description = method.getDescription();
        String testInstanceName = resultSet.toArray(new ITestResult[] {})[0].getTestName();
        // Calculate each test run times, the result shown in the html
        // report.
        ITestResult[] results = resultSet.toArray(new ITestResult[] {});
        String methodName = method.getMethodName();
        if (setMethods.contains(methodName)) {
          methods.put(methodName, methods.get(methodName) + 1);
        } else {
          setMethods.add(methodName);
          methods.put(methodName, 0);
        }
        String parameterString = "";
        int count = 0;

        ITestResult result = null;
        if (results.length > methods.get(methodName)) {
          result = results[methods.get(methodName)];
          int testId = getId(result);

          for (Integer id : allRunTestIds) {
            if (id.intValue() == testId) count++;
          }
          Object[] parameters = result.getParameters();

          boolean hasParameters = parameters != null && parameters.length > 0;
          if (hasParameters) {
            for (Object p : parameters) {
              parameterString = parameterString + Utils.escapeHtml(p.toString()) + " ";
            }
          }
        }

        int methodId = method.getTestClass().getName().hashCode();
        methodId = methodId + method.getMethodName().hashCode();
        if (result != null)
          methodId =
              methodId
                  + (result.getParameters() != null ? Arrays.hashCode(result.getParameters()) : 0);

        buff.append(
            "<td><a href=\"#m"
                + methodId
                + "\">"
                + qualifiedName(method)
                + " "
                + (description != null && description.length() > 0
                    ? "(\"" + description + "\")"
                    : "")
                + "</a>"
                + (null == testInstanceName ? "" : "<br>(" + testInstanceName + ")")
                + "</td><td>"
                + this.getAuthors(className, method)
                + "</td><td class=\"numi\">"
                + resultSet.size()
                + "</td>"
                + "<td>"
                + (count == 0 ? "" : count)
                + "</td>"
                + "<td>"
                + parameterString
                + "</td>"
                + "<td>"
                + convertDate(start)
                + "</td>"
                + "<td class=\"numi\">"
                + (end - start)
                + "</td>"
                + "</tr>");
      }
      if (mq > 0) {
        cq += 1;
        m_out.print("<tr class=\"" + style + (cq % 2 == 0 ? "even" : "odd") + "\">" + "<td");
        if (mq > 1) {
          m_out.print(" rowspan=\"" + mq + "\"");
        }
        m_out.println(">" + lastClassName + "</td>" + buff);
      }
    }
  }

  /** Starts and defines columns result summary table */
  private void startResultSummaryTable(String style) {
    tableStart(style, "summary");
    m_out.println(
        "<tr><th>Class</th><th>Method</th><th>Authors</th><th># of<br/>Scenarios</th><th>Running Counts</th>"
            + "<th>Parameters</th><th>Start</th><th>Time<br/>(ms)</th></tr>");
    m_row = 0;
  }

  private String qualifiedName(ITestNGMethod method) {
    StringBuilder addon = new StringBuilder();
    String[] groups = method.getGroups();
    int length = groups.length;
    if (length > 0 && !"basic".equalsIgnoreCase(groups[0])) {
      addon.append("(");
      for (int i = 0; i < length; i++) {
        if (i > 0) {
          addon.append(", ");
        }
        addon.append(groups[i]);
      }
      addon.append(")");
    }

    return "<b>" + method.getMethodName() + "</b> " + addon;
  }

  private void resultDetail(IResultMap tests) {
    for (ITestResult result : tests.getAllResults()) {
      ITestNGMethod method = result.getMethod();

      int methodId = getId(result);

      String cname = method.getTestClass().getName();
      m_out.println(
          "<h2 id=\"m"
              + methodId
              + "\" name=\"m"
              + methodId
              + "\" >"
              + cname
              + ":"
              + method.getMethodName()
              + "</h2>");
      Set<ITestResult> resultSet = tests.getResults(method);
      generateForResult(result, method, resultSet.size());
      m_out.println("<p class=\"totop\"><a href=\"#summary\">back to summary</a></p>");
    }
  }

  private void generateForResult(ITestResult ans, ITestNGMethod method, int resultSetSize) {
    Object[] parameters = ans.getParameters();
    boolean hasParameters = parameters != null && parameters.length > 0;
    if (hasParameters) {
      tableStart("result", null);
      m_out.print("<tr class=\"param\">");
      for (int x = 1; x <= parameters.length; x++) {
        m_out.print("<th>Parameter #" + x + "</th>");
      }
      m_out.println("</tr>");
      m_out.print("<tr class=\"param stripe\">");
      for (Object p : parameters) {
        m_out.println("<td>" + Utils.escapeHtml(p.toString()) + "</td>");
      }
      m_out.println("</tr>");
    }
    List<String> msgs = Reporter.getOutput(ans);
    boolean hasReporterOutput = msgs.size() > 0;
    Throwable exception = ans.getThrowable();
    boolean hasThrowable = exception != null;
    if (hasReporterOutput || hasThrowable) {
      if (hasParameters) {
        m_out.print("<tr><td");
        if (parameters.length > 1) {
          m_out.print(" colspan=\"" + parameters.length + "\"");
        }
        m_out.println(">");
      } else {
        m_out.println("<div>");
      }
      if (hasReporterOutput) {
        if (hasThrowable) {
          m_out.println("<h3>Test Messages</h3>");
        }
        for (String line : msgs) {
          m_out.println(line + "<br/>");
        }
      }
      if (hasThrowable) {
        boolean wantsMinimalOutput = ans.getStatus() == ITestResult.SUCCESS;
        if (hasReporterOutput) {
          m_out.println("<h3>" + (wantsMinimalOutput ? "Expected Exception" : "Failure") + "</h3>");
        }
        generateExceptionReport(exception, method);
      }
      if (hasParameters) {
        m_out.println("</td></tr>");
      } else {
        m_out.println("</div>");
      }
    }
    if (hasParameters) {
      m_out.println("</table>");
    }
  }

  protected void generateExceptionReport(Throwable exception, ITestNGMethod method) {
    m_out.print("<div class=\"stacktrace\">");
    m_out.print(Utils.stackTrace(exception, true)[0]);
    m_out.println("</div>");
  }

  /**
   * Since the methods will be sorted chronologically, we want to return the ITestNGMethod from the
   * invoked methods.
   */
  private Collection<ITestNGMethod> getMethodSet(IResultMap tests, ISuite suite) {
    List<IInvokedMethod> r = Lists.newArrayList();
    List<IInvokedMethod> invokedMethods = suite.getAllInvokedMethods();

    // Eliminate the repeat retry methods
    for (IInvokedMethod im : invokedMethods) {
      if (tests.getAllMethods().contains(im.getTestMethod())) {
        int testId = getId(im.getTestResult());
        if (!testIds.contains(testId)) {
          testIds.add(testId);
          r.add(im);
        }
      }
    }
    Arrays.sort(r.toArray(new IInvokedMethod[r.size()]), new TestSorter());
    List<ITestNGMethod> result = Lists.newArrayList();

    // Add all the invoked methods
    for (IInvokedMethod m : r) {
      result.add(m.getTestMethod());
    }

    // Add all the methods that weren't invoked (e.g. skipped) that we
    // haven't added yet
    // for (ITestNGMethod m : tests.getAllMethods()) {
    // if (!result.contains(m)) {
    // result.add(m);
    // }
    // }

    for (ITestResult allResult : tests.getAllResults()) {
      int testId = getId(allResult);
      if (!testIds.contains(testId)) {
        result.add(allResult.getMethod());
      }
    }

    return result;
  }

  public void generateSuiteSummaryReport(List<ISuite> suites) {
    tableStart("testOverview", null);
    m_out.print("<tr>");
    tableColumnStart("Test");
    tableColumnStart("Methods<br/>Passed");
    tableColumnStart("Scenarios<br/>Passed");
    tableColumnStart("# skipped");
    tableColumnStart("# failed");
    tableColumnStart("Total<br/>Time");
    tableColumnStart("Included<br/>Groups");
    tableColumnStart("Excluded<br/>Groups");
    m_out.println("</tr>");
    NumberFormat formatter = new DecimalFormat("#,##0.0");
    int qty_tests = 0;
    int qty_pass_m = 0;
    int qty_pass_s = 0;
    int qty_skip = 0;
    int qty_fail = 0;
    long time_start = Long.MAX_VALUE;
    long time_end = Long.MIN_VALUE;
    m_testIndex = 1;
    for (ISuite suite : suites) {
      if (suites.size() > 1) {
        titleRow(suite.getName(), 8);
      }
      Map<String, ISuiteResult> tests = suite.getResults();
      for (ISuiteResult r : tests.values()) {
        qty_tests += 1;
        ITestContext overview = r.getTestContext();
        startSummaryRow(overview.getName());

        getAllTestIds(overview, suite);
        int q = getMethodSet(overview.getPassedTests(), suite).size();
        qty_pass_m += q;
        summaryCell(q, Integer.MAX_VALUE);
        q = overview.getPassedTests().size();
        qty_pass_s += q;
        summaryCell(q, Integer.MAX_VALUE);

        q = getMethodSet(overview.getSkippedTests(), suite).size();
        qty_skip += q;
        summaryCell(q, 0);

        q = getMethodSet(overview.getFailedTests(), suite).size();
        qty_fail += q;
        summaryCell(q, 0);

        time_start = Math.min(overview.getStartDate().getTime(), time_start);
        time_end = Math.max(overview.getEndDate().getTime(), time_end);
        summaryCell(
            formatter.format(
                    (overview.getEndDate().getTime() - overview.getStartDate().getTime()) / 1000.)
                + " seconds",
            true);
        summaryCell(overview.getIncludedGroups());
        summaryCell(overview.getExcludedGroups());
        m_out.println("</tr>");
        m_testIndex++;
      }
    }
    if (qty_tests > 1) {
      m_out.println("<tr class=\"total\"><td>Total</td>");
      summaryCell(qty_pass_m, Integer.MAX_VALUE);
      summaryCell(qty_pass_s, Integer.MAX_VALUE);
      summaryCell(qty_skip, 0);
      summaryCell(qty_fail, 0);
      summaryCell(formatter.format((time_end - time_start) / 1000.) + " seconds", true);
      m_out.println("<td colspan=\"2\">&nbsp;</td></tr>");
    }
    m_out.println("</table>");
  }

  private void summaryCell(String[] val) {
    StringBuffer b = new StringBuffer();
    for (String v : val) {
      b.append(v + " ");
    }
    summaryCell(b.toString(), true);
  }

  private void summaryCell(String v, boolean isgood) {
    m_out.print("<td class=\"numi" + (isgood ? "" : "_attn") + "\">" + v + "</td>");
  }

  private void startSummaryRow(String label) {
    m_row += 1;
    m_out.print(
        "<tr"
            + (m_row % 2 == 0 ? " class=\"stripe\"" : "")
            + "><td style=\"text-align:left;padding-right:2em\"><a href=\"#t"
            + m_testIndex
            + "\">"
            + label
            + "</a>"
            + "</td>");
  }

  private void summaryCell(int v, int maxexpected) {
    summaryCell(String.valueOf(v), v <= maxexpected);
  }

  private void tableStart(String cssclass, String id) {
    m_out.println(
        "<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\""
            + (cssclass != null ? " class=\"" + cssclass + "\"" : " style=\"padding-bottom:2em\"")
            + (id != null ? " id=\"" + id + "\"" : "")
            + ">");
    m_row = 0;
  }

  private void tableColumnStart(String label) {
    m_out.print("<th>" + label + "</th>");
  }

  private void titleRow(String label, int cq) {
    titleRow(label, cq, null);
  }

  private void titleRow(String label, int cq, String id) {
    m_out.print("<tr");
    if (id != null) {
      m_out.print(" id=\"" + id + "\"");
    }
    m_out.println("><th colspan=\"" + cq + "\">" + label + "</th></tr>");
    m_row = 0;
  }

  /** Starts HTML stream */
  protected void startHtml(PrintWriter out) {
    out.println(
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
    out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
    out.println("<head>");
    out.println("<title>TestNG Report</title>");
    out.println("<style type=\"text/css\">");
    out.println("table {margin-bottom:10px;border-collapse:collapse;empty-cells:show}");
    out.println("td,th {border:1px solid #009;padding:.25em .5em}");
    out.println(".result th {vertical-align:bottom}");
    out.println(".param th {padding-left:1em;padding-right:1em}");
    out.println(".param td {padding-left:.5em;padding-right:2em}");
    out.println(".stripe td,.stripe th {background-color: #E6EBF9}");
    out.println(".numi,.numi_attn {text-align:right}");
    out.println(".total td {font-weight:bold}");
    out.println(".passedodd td {background-color: #0A0}");
    out.println(".passedeven td {background-color: #3F3}");
    out.println(".skippedodd td {background-color: #CCC}");
    out.println(".skippedodd td {background-color: #DDD}");
    out.println(".failedodd td,.numi_attn {background-color: #CD919E}");
    out.println(".failedeven td,.stripe .numi_attn {background-color: #CD8162}");
    out.println(".stacktrace {white-space:pre;font-family:monospace}");
    out.println(".totop {font-size:85%;text-align:center;border-bottom:2px solid #000}");
    out.println("img {width:20%;height:20%}");
    out.println("</style>");
    out.print("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
    out.println("</head>");
    out.println("<body>");
  }

  /** Finishes HTML stream */
  protected void endHtml(PrintWriter out) {
    out.println("</body></html>");
  }

  // ~ Inner Classes --------------------------------------------------------
  /** Arranges methods by classname and method name */
  private class TestSorter implements Comparator<IInvokedMethod> {
    // ~ Methods
    // -------------------------------------------------------------

    /** Arranges methods by classname and method name */
    @Override
    public int compare(IInvokedMethod o1, IInvokedMethod o2) {
      // System.out.println("Comparing " + o1.getMethodName() + " " +
      // o1.getDate()
      // + " and " + o2.getMethodName() + " " + o2.getDate());
      return (int) (o1.getDate() - o2.getDate());
      // int r = ((T) o1).getTestClass().getName().compareTo(((T)
      // o2).getTestClass().getName());
      // if (r == 0) {
      // r = ((T) o1).getMethodName().compareTo(((T) o2).getMethodName());
      // }
      // return r;
    }
  }

  // ~ JavaDoc-specific Methods
  // --------------------------------------------------------
  /**
   * Get ITestNGMethod author(s) string, or class author(s) if no method author is present. Default
   * return value is "unknown".
   *
   * @param className
   * @param method
   * @return
   * @author hzjingcheng
   */
  private String getAuthors(String className, ITestNGMethod method) {
    JavaClass cls = builder.getClassByName(className);
    DocletTag[] authors = cls.getTagsByName("author");
    // get class authors as default author name
    String allAuthors = "";
    if (authors.length == 0) {
      allAuthors = "unknown";
    } else {
      for (DocletTag author : authors) {
        allAuthors += author.getValue() + " ";
      }
    }
    // get method author name
    JavaMethod[] mtds = cls.getMethods();
    for (JavaMethod mtd : mtds) {
      if (mtd.getName().equals(method.getMethodName())) {
        authors = mtd.getTagsByName("author");
        if (authors.length != 0) {
          allAuthors = "";
          for (DocletTag author : authors) {
            allAuthors += author.getValue() + " ";
          }
        }
        break;
      }
    }
    return allAuthors.trim();
  }

  /**
   * Get comment string of Java class.
   *
   * @param className
   * @return
   * @author hzjingcheng
   */
  @SuppressWarnings("unused")
  private String getClassComment(String className) {
    JavaClass cls = builder.getClassByName(className);
    return cls.getComment();
  }

  /**
   * Get ITestResult id by class + method + parameters hash code.
   *
   * @param result
   * @return
   * @author kevinkong
   */
  private int getId(ITestResult result) {
    int id = result.getTestClass().getName().hashCode();
    id = id + result.getMethod().getMethodName().hashCode();
    id = id + (result.getParameters() != null ? Arrays.hashCode(result.getParameters()) : 0);
    return id;
  }

  /**
   * Get All tests id by class + method + parameters hash code.
   *
   * @param context
   * @param suite
   */
  private void getAllTestIds(ITestContext context, ISuite suite) {
    IResultMap passTests = context.getPassedTests();
    IResultMap failTests = context.getFailedTests();
    List<IInvokedMethod> invokedMethods = suite.getAllInvokedMethods();
    for (IInvokedMethod im : invokedMethods) {
      if (passTests.getAllMethods().contains(im.getTestMethod())
          || failTests.getAllMethods().contains(im.getTestMethod())) {
        int testId = getId(im.getTestResult());
        // m_out.println("ALLtestid=" + testId);
        allRunTestIds.add(testId);
      }
    }
  }

  private String convertDate(long time) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Date date = new Date(time);
    return sdf.format(date);
  }
}
