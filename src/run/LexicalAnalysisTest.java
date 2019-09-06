package run;

import java.util.ArrayList;

import lexicalAnalysis.LexicalAnalysis;
import util.*;

public class LexicalAnalysisTest {
	public static void main(String[] args) {
		
		System.out.println("Analysis the files...");

		// 运行test1
		String inputFileName1 = "./testcase/test1.txt";
		String outputFileName1 = "./testcase/tokenOut1.txt";
		LexicalAnalysis lexicalAnalysis = new LexicalAnalysis(inputFileName1, outputFileName1);
		lexicalAnalysis.analysisFile();

		// 运行test2
		String inputFileName2 = "./testcase/test2.txt";
		String outputFileName2 = "./testcase/tokenOut2.txt";
		lexicalAnalysis.setInputFileName(inputFileName2);
		lexicalAnalysis.setOutputFileName(outputFileName2);
		lexicalAnalysis.analysisFile();

		// 运行test3
		String inputFileName3 = "./testcase/test3.txt";
		String outputFileName3 = "./testcase/tokenOut3.txt";
		lexicalAnalysis.setInputFileName(inputFileName3);
		lexicalAnalysis.setOutputFileName(outputFileName3);
		lexicalAnalysis.analysisFile();
		
		// 运行test4
		String inputFileName7 = "./testcase/test4.txt";
		String outputFileName7 = "./testcase/tokenOut4.txt";
		lexicalAnalysis.setInputFileName(inputFileName7);
		lexicalAnalysis.setOutputFileName(outputFileName7);
		lexicalAnalysis.analysisFile();
		
		// 运行test5
		String inputFileName8 = "./testcase/test5.txt";
		String outputFileName8 = "./testcase/tokenOut5.txt";
		lexicalAnalysis.setInputFileName(inputFileName8);
		lexicalAnalysis.setOutputFileName(outputFileName8);
		lexicalAnalysis.analysisFile();


		System.out.println("Done!");

	}
}
