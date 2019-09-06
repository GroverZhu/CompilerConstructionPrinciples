package run;


import syntaxAnalysis.SyntaxAnalysis;

public class SyntaxAnalysisTest {
	public static void main(String[] args) {
		String prefix = "--";
		
		// 测试第一个tokenOut文件
		System.out.println("Analysising tokenOut1.txt");
		String inputFile1 = "./testcase/tokenOut1.txt";
		String outputFile1 = "./testcase/syntaxOut1.txt";
		String errorFile1 = "./testcase/errorInfo1.txt";
		SyntaxAnalysis test1 = new SyntaxAnalysis(inputFile1, outputFile1, errorFile1);
		test1.syntaxAnalysis(prefix);
		System.out.println("Done!\n");
	
		// 测试第四个tokenOut文件
		System.out.println("Analysising tokenOut2.txt");
		String inputFile2 = "./testcase/tokenOut4.txt";
		String outputFile2 = "./testcase/syntaxOut2.txt";
		String errorFile2 = "./testcase/errorInfo2.txt";
		SyntaxAnalysis test2 = new SyntaxAnalysis(inputFile2, outputFile2, errorFile2);
		test2.syntaxAnalysis(prefix);
		System.out.println("Done!\n");
		
		// 测试第五个tokenOut文件
		System.out.println("Analysising tokenOut5.txt");
		String inputFile3 = "./testcase/tokenOut5.txt";
		String outputFile3 = "./testcase/syntaxOut3.txt";
		String errorFile3 = "./testcase/errorInfo3.txt";
		SyntaxAnalysis test3 = new SyntaxAnalysis(inputFile3, outputFile3, errorFile3);
		test3.syntaxAnalysis(prefix);
		System.out.println("DONE!\n");
	}

}
