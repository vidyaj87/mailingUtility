package com.configure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class Listeners extends com.configure.Configure implements
		ISuiteListener, ITestListener {

	private static final String htmlFile = null;
	private int testRun = 0;
	private int testPassed = 0;
	private int testFailed = 0;
	private int testSkipped = 0;
	private String mailText;
	public static Map<Long, Boolean> passStatusMap = new HashMap<Long, Boolean>();
	public String filename;
	public String buildTime;
	public String buildDate;

	@Override
	public void onStart(ITestContext arg0) {
		intialiseNewHtmlFile();
		try {
			setProperty("Date", getCurrentDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
	}

	@Override
	public void onTestStart(ITestResult iTestResult) {
		String text1 = "Started " + iTestResult.getTestContext().getName()
				+ "->" + iTestResult.getName();
		System.out.println("Started " + iTestResult.getTestContext().getName()
				+ "->" + iTestResult.getName());
		testRun++;
		// appendHtlmFile(text1);
	}

	@Override
	public void onTestSuccess(ITestResult iTestResult) {
		String text2 = iTestResult.getTestContext().getName() + "->"
				+ iTestResult.getName() + "--" + " passed";
		System.out.println(iTestResult.getTestContext().getName() + "->"
				+ iTestResult.getName() + "--" + " passed");
		testPassed++;
		appendHtlmFileTestName(iTestResult.getName(), "passed");
		// appendHtlmFileTestResult("passed");
	}

	/**
	 * Method Name: onTestFailure() Description: This function will be called
	 * when a test case fails.
	 */
	@Override
	public void onTestFailure(ITestResult iTestResult) {
		String text3 = iTestResult.getTestContext().getName() + "->"
				+ iTestResult.getName() + "--" + " failed";
		System.out.println(iTestResult.getTestContext().getName() + "->"
				+ iTestResult.getName() + "--" + " failed");
		testFailed++;
		passStatusMap.put(Thread.currentThread().getId(), false);
		appendHtlmFileTestName(iTestResult.getName(), "failed");
		// appendHtlmFileTestResult("failed");
	}

	/**
	 * Method Name: onTestSkipped() Description: This function will be called
	 * when a test case execution is skipped.
	 */
	@Override
	public void onTestSkipped(ITestResult iTestResult) {
		testSkipped++;
		passStatusMap.put(Thread.currentThread().getId(), false);
		appendHtlmFileTestName(iTestResult.getName(), "skipped");
		// appendHtlmFileTestResult("skipped");
	}

	@Override
	public void onFinish(ISuite isuite) {
		appendHtlmFile("</tbody>");
		appendHtlmFile("</table>");
		appendHtlmFile("</html>");
	}

	/**
	 * Method Name: onFinish() Description: This function will be called after
	 * all the test cases are executed. This method will also send a mail
	 * regarding the execution status.
	 */
	@Override
	public void onFinish(ITestContext iTestContext) {
		String testsRun = "Total Tests run: " + testRun;
		String testsPassed = " Tests passed: " + testPassed;
		String testsFailed = " Tests failed: " + testFailed;
		String testsSkipped = " Tests skipped: " + testSkipped;
		mailText = testsRun + "\n" + testsPassed + "\n" + testsFailed + "\n"
				+ testsSkipped + "\n\n";
		appendHtlmFile("<tr><td align='center' colspan='5'; >" + mailText
				+ "</td></tr>");
		}

	@Override
	public void onStart(ISuite arg0) {
		// TODO Auto-generated method stub
		buildTime = getCurrentDate();
		buildDate = getCurrentTimeStamp();
	}

	public void appendHtlmFile(String text) {
		try {
			File file = new File(path());
			FileWriter fileWriter = new FileWriter(file, true);
			BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
			fileWriter.append(text);
			bufferFileWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void appendHtlmFileTestName(String text1, String text2) {
		try {
			File file = new File(path());
			FileWriter fileWriter = new FileWriter(file, true);
			BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
			if (text2.equals("failed")) {
				fileWriter.append("<tr class = 'red'><td>" + text1 + "</td>");
			} else if (text2.equals("passed")) {
				fileWriter.append("<tr class = 'green'><td>" + text1 + "</td>");
			} else if (text2.equals("skipped")) {
				fileWriter.append("<tr class = 'blue'><td>" + text1 + "</td>");
			}
			fileWriter.append("<td>" + text2 + "</td></tr>");
			bufferFileWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void appendHtlmFileTestResult(String text) {
		try {
			File file = new File(path());
			FileWriter fileWriter = new FileWriter(file, true);
			BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
			fileWriter.append("<td>" + text + "</td></tr>");
			bufferFileWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String htmlFileName() {
		try {
			String htmlFile = "Report" + getCurrentDate();
			setProperty("SummaryReport", htmlFile);
			return htmlFile;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return htmlFile;
	}

	public String path() {
		String path = System.getProperty("user.dir") + "\\TestSummary\\"
				+ htmlFileName() + ".html";
		return (path);
	}

	public void intialiseNewHtmlFile() {

		try {
			File file = new File(path());
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(file, true);
			// Use BufferedWriter instead of FileWriter for better performance
			BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
			fileWriter
					.append("<html><head><style>table{border-collapse: collapse;width: 600px;}table, td, th{border: 1px solid black;}.green{background-color: #3F3;}.red{background-color:red;}.blue{background-color: aliceblue;}</style></head><table><tbody><tr><td align='center' colspan='5' style = 'font-weight: bold;'>Automation test at time "
							+ buildDate + " on " + buildTime + "</td></tr>");
			bufferFileWriter.close();
			fileWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}