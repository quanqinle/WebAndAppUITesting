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
 * Reported designed to render self-contained HTML top-down view of a testing suite.
 *
 * @author Paul Mendelson
 * @since 5.2
 * @version $Revision: 719 $
 */
public class PowerEmailableReporter implements IReporter {
  private static final Logger L = Logger.getLogger(PowerEmailableReporter.class);

  // ~ Instance fields ------------------------------------------------------

  private PrintWriter mOut;

  private int mRow;

  private Integer mTestIndex;

  private final Set<Integer> testIds = new HashSet<>();
  private final List<Integer> allRunTestIds = new ArrayList<>();
  private final JavaDocBuilder builder = new JavaDocBuilder();

  // ~ Methods --------------------------------------------------------------

  /** Creates summary of the run */
  @Override
  public void generateReport(List<XmlSuite> xml, List<ISuite> suites, String outdir) {
    try {
      mOut = createWriter(outdir);
    } catch (IOException e) {
      L.error("output file", e);
      return;
    }
    ConfigUtil cr = ConfigUtil.getInstance();
    if (cr.getSourceCodeEncoding() != null) {
      builder.setEncoding(cr.getSourceCodeEncoding());
    } else {
      builder.setEncoding("UTF-8");
    }

    builder.addSourceTree(new File(cr.getSourceCodeDir()));
    startHtml(mOut);
    generateSuiteSummaryReport(suites);
    testIds.clear();
    generateMethodSummaryReport(suites);
    testIds.clear();
    generateMethodDetailReport(suites);
    testIds.clear();
    endHtml(mOut);
    mOut.flush();
    mOut.close();
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
        mTestIndex = testIndex;

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
    mOut.println("</table>");
  }

  /** Creates a section showing known results for each method */
  protected void generateMethodDetailReport(List<ISuite> suites) {
    for (ISuite suite : suites) {
      Map<String, ISuiteResult> r = suite.getResults();
      for (ISuiteResult r2 : r.values()) {
        ITestContext testContext = r2.getTestContext();
        if (r.values().size() > 0) {
          mOut.println("<h1>" + testContext.getName() + "</h1>");
        }
        resultDetail(testContext.getFailedConfigurations());
        resultDetail(testContext.getFailedTests());
        resultDetail(testContext.getSkippedConfigurations());
        resultDetail(testContext.getSkippedTests());
        resultDetail(testContext.getPassedTests());
      }
    }
  }

  /**
   * @param tests
   */
  private void resultSummary(
      ISuite suite, IResultMap tests, String testname, String style, String details) {
    if (tests.getAllResults().size() > 0) {
      StringBuilder buff = new StringBuilder();
      String lastClassName = "";
      int mq = 0;
      int cq = 0;
      Map<String, Integer> methods = new HashMap<>(10);
      Set<String> setMethods = new HashSet<>();
      for (ITestNGMethod method : getMethodSet(tests, suite)) {
        mRow += 1;

        ITestClass testClass = method.getTestClass();
        String className = testClass.getName();
        if (mq == 0) {
          String id = (mTestIndex == null ? null : "t" + mTestIndex);
          titleRow(testname + " &#8212; " + style + details, 5, id);
          mTestIndex = null;
        }
        if (!className.equalsIgnoreCase(lastClassName)) {
          if (mq > 0) {
            cq += 1;
            mOut.print("<tr class=\"" + style + (cq % 2 == 0 ? "even" : "odd") + "\">" + "<td");
            if (mq > 1) {
              mOut.print(" rowspan=\"" + mq + "\"");
            }
            mOut.println(">" + lastClassName + "</td>" + buff);
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
          buff.append("<tr class=\"")
              .append(style)
              .append(cq % 2 == 0 ? "odd" : "even")
              .append("\">");
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
        StringBuilder parameterString = new StringBuilder();
        int count = 0;

        ITestResult result = null;
        if (results.length > methods.get(methodName)) {
          result = results[methods.get(methodName)];
          int testId = getId(result);

          for (Integer id : allRunTestIds) {
            if (id == testId) {
              count++;
            }
          }
          Object[] parameters = result.getParameters();

          boolean hasParameters = parameters != null && parameters.length > 0;
          if (hasParameters) {
            for (Object p : parameters) {
              parameterString.append(Utils.escapeHtml(p.toString())).append(" ");
            }
          }
        }

        int methodId = method.getTestClass().getName().hashCode();
        methodId = methodId + method.getMethodName().hashCode();
        if (result != null) {
          methodId =
              methodId
                  + (result.getParameters() != null ? Arrays.hashCode(result.getParameters()) : 0);
        }

        buff.append("<td><a href=\"#m")
            .append(methodId)
            .append("\">")
            .append(qualifiedName(method))
            .append(" ")
            .append(
                description != null && description.length() > 0 ? "(\"" + description + "\")" : "")
            .append("</a>")
            .append(null == testInstanceName ? "" : "<br>(" + testInstanceName + ")")
            .append("</td><td>")
            .append(this.getAuthors(className, method))
            .append("</td><td class=\"numi\">")
            .append(resultSet.size())
            .append("</td>")
            .append("<td>")
            .append(count == 0 ? "" : count)
            .append("</td>")
            .append("<td>")
            .append(parameterString)
            .append("</td>")
            .append("<td>")
            .append(convertDate(start))
            .append("</td>")
            .append("<td class=\"numi\">")
            .append(end - start)
            .append("</td>")
            .append("</tr>");
      }
      if (mq > 0) {
        cq += 1;
        mOut.print("<tr class=\"" + style + (cq % 2 == 0 ? "even" : "odd") + "\">" + "<td");
        if (mq > 1) {
          mOut.print(" rowspan=\"" + mq + "\"");
        }
        mOut.println(">" + lastClassName + "</td>" + buff);
      }
    }
  }

  /** Starts and defines columns result summary table */
  private void startResultSummaryTable(String style) {
    tableStart(style, "summary");
    mOut.println(
        "<tr><th>Class</th><th>Method</th><th>Authors</th><th># of<br/>Scenarios</th><th>Running Counts</th>"
            + "<th>Parameters</th><th>Start</th><th>Time<br/>(ms)</th></tr>");
    mRow = 0;
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
      mOut.println(
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
      mOut.println("<p class=\"totop\"><a href=\"#summary\">back to summary</a></p>");
    }
  }

  private void generateForResult(ITestResult ans, ITestNGMethod method, int resultSetSize) {
    Object[] parameters = ans.getParameters();
    boolean hasParameters = parameters != null && parameters.length > 0;
    if (hasParameters) {
      tableStart("result", null);
      mOut.print("<tr class=\"param\">");
      for (int x = 1; x <= parameters.length; x++) {
        mOut.print("<th>Parameter #" + x + "</th>");
      }
      mOut.println("</tr>");
      mOut.print("<tr class=\"param stripe\">");
      for (Object p : parameters) {
        mOut.println("<td>" + Utils.escapeHtml(p.toString()) + "</td>");
      }
      mOut.println("</tr>");
    }
    List<String> msgs = Reporter.getOutput(ans);
    boolean hasReporterOutput = msgs.size() > 0;
    Throwable exception = ans.getThrowable();
    boolean hasThrowable = exception != null;
    if (hasReporterOutput || hasThrowable) {
      if (hasParameters) {
        mOut.print("<tr><td");
        if (parameters.length > 1) {
          mOut.print(" colspan=\"" + parameters.length + "\"");
        }
        mOut.println(">");
      } else {
        mOut.println("<div>");
      }
      if (hasReporterOutput) {
        if (hasThrowable) {
          mOut.println("<h3>Test Messages</h3>");
        }
        for (String line : msgs) {
          mOut.println(line + "<br/>");
        }
      }
      if (hasThrowable) {
        boolean wantsMinimalOutput = ans.getStatus() == ITestResult.SUCCESS;
        if (hasReporterOutput) {
          mOut.println("<h3>" + (wantsMinimalOutput ? "Expected Exception" : "Failure") + "</h3>");
        }
        generateExceptionReport(exception, method);
      }
      if (hasParameters) {
        mOut.println("</td></tr>");
      } else {
        mOut.println("</div>");
      }
    }
    if (hasParameters) {
      mOut.println("</table>");
    }
  }

  protected void generateExceptionReport(Throwable exception, ITestNGMethod method) {
    mOut.print("<div class=\"stacktrace\">");
    mOut.print(Utils.stackTrace(exception, true)[0]);
    mOut.println("</div>");
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
    mOut.print("<tr>");
    tableColumnStart("Test");
    tableColumnStart("Methods<br/>Passed");
    tableColumnStart("Scenarios<br/>Passed");
    tableColumnStart("# skipped");
    tableColumnStart("# failed");
    tableColumnStart("Total<br/>Time");
    tableColumnStart("Included<br/>Groups");
    tableColumnStart("Excluded<br/>Groups");
    mOut.println("</tr>");
    NumberFormat formatter = new DecimalFormat("#,##0.0");
    int qtyTests = 0;
    int qtyPassM = 0;
    int qtyPassS = 0;
    int qtySkip = 0;
    int qtyFail = 0;
    long timeStart = Long.MAX_VALUE;
    long timeEnd = Long.MIN_VALUE;
    mTestIndex = 1;
    for (ISuite suite : suites) {
      if (suites.size() > 1) {
        titleRow(suite.getName(), 8);
      }
      Map<String, ISuiteResult> tests = suite.getResults();
      for (ISuiteResult r : tests.values()) {
        qtyTests += 1;
        ITestContext overview = r.getTestContext();
        startSummaryRow(overview.getName());

        getAllTestIds(overview, suite);
        int q = getMethodSet(overview.getPassedTests(), suite).size();
        qtyPassM += q;
        summaryCell(q, Integer.MAX_VALUE);
        q = overview.getPassedTests().size();
        qtyPassS += q;
        summaryCell(q, Integer.MAX_VALUE);

        q = getMethodSet(overview.getSkippedTests(), suite).size();
        qtySkip += q;
        summaryCell(q, 0);

        q = getMethodSet(overview.getFailedTests(), suite).size();
        qtyFail += q;
        summaryCell(q, 0);

        timeStart = Math.min(overview.getStartDate().getTime(), timeStart);
        timeEnd = Math.max(overview.getEndDate().getTime(), timeEnd);
        summaryCell(
            formatter.format(
                    (overview.getEndDate().getTime() - overview.getStartDate().getTime()) / 1000.)
                + " seconds",
            true);
        summaryCell(overview.getIncludedGroups());
        summaryCell(overview.getExcludedGroups());
        mOut.println("</tr>");
        mTestIndex++;
      }
    }
    if (qtyTests > 1) {
      mOut.println("<tr class=\"total\"><td>Total</td>");
      summaryCell(qtyPassM, Integer.MAX_VALUE);
      summaryCell(qtyPassS, Integer.MAX_VALUE);
      summaryCell(qtySkip, 0);
      summaryCell(qtyFail, 0);
      summaryCell(formatter.format((timeEnd - timeStart) / 1000.) + " seconds", true);
      mOut.println("<td colspan=\"2\">&nbsp;</td></tr>");
    }
    mOut.println("</table>");
  }

  private void summaryCell(String[] val) {
    StringBuilder b = new StringBuilder();
    for (String v : val) {
      b.append(v).append(" ");
    }
    summaryCell(b.toString(), true);
  }

  private void summaryCell(String v, boolean isgood) {
    mOut.print("<td class=\"numi" + (isgood ? "" : "_attn") + "\">" + v + "</td>");
  }

  private void startSummaryRow(String label) {
    mRow += 1;
    mOut.print(
        "<tr"
            + (mRow % 2 == 0 ? " class=\"stripe\"" : "")
            + "><td style=\"text-align:left;padding-right:2em\"><a href=\"#t"
            + mTestIndex
            + "\">"
            + label
            + "</a>"
            + "</td>");
  }

  private void summaryCell(int v, int maxexpected) {
    summaryCell(String.valueOf(v), v <= maxexpected);
  }

  private void tableStart(String cssclass, String id) {
    mOut.println(
        "<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\""
            + (cssclass != null ? " class=\"" + cssclass + "\"" : " style=\"padding-bottom:2em\"")
            + (id != null ? " id=\"" + id + "\"" : "")
            + ">");
    mRow = 0;
  }

  private void tableColumnStart(String label) {
    mOut.print("<th>" + label + "</th>");
  }

  private void titleRow(String label, int cq) {
    titleRow(label, cq, null);
  }

  private void titleRow(String label, int cq, String id) {
    mOut.print("<tr");
    if (id != null) {
      mOut.print(" id=\"" + id + "\"");
    }
    mOut.println("><th colspan=\"" + cq + "\">" + label + "</th></tr>");
    mRow = 0;
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
  private static class TestSorter implements Comparator<IInvokedMethod> {
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
    StringBuilder allAuthors = new StringBuilder();
    if (authors.length == 0) {
      allAuthors = new StringBuilder("unknown");
    } else {
      for (DocletTag author : authors) {
        allAuthors.append(author.getValue()).append(" ");
      }
    }
    // get method author name
    JavaMethod[] mtds = cls.getMethods();
    for (JavaMethod mtd : mtds) {
      if (mtd.getName().equals(method.getMethodName())) {
        authors = mtd.getTagsByName("author");
        if (authors.length != 0) {
          allAuthors = new StringBuilder();
          for (DocletTag author : authors) {
            allAuthors.append(author.getValue()).append(" ");
          }
        }
        break;
      }
    }
    return allAuthors.toString().trim();
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
