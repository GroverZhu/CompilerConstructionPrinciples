package lexicalAnalysis;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import util.*;

/**
 * 词法分析程序 代码主体框架： 从文本文件中读取出文本(按行读取)
 * 从读取的每一行开始判断，从开始状态进行，由有穷状态机可得，总共有12个状态，其中包含1个起始状态，1个终止状态，为每一个状态申明一个唯一的标识符
 * 状态机的读取是按行读取，只有读到一行的结束或者该行出现第一个错误才返回结果。
 * 
 * 对于一个状态机，输入为一行源代码及该行源代码的所在行数，输出为最后的分析结果。
 * 采用双层switch-case嵌套进行判断，第一层switch-case为判断当前状态，第二层switch-case为判断输入的字符，对字符的判断申明相应的函数进行判断，在该判断中进行转换状态，如果是错误的输入直接报错抛出错误
 * 状态机是按行读取的，当到达结束状态，判断当前当字符为何种类型，根据不同的类型做出不同的输出
 * 在达到结束状态有2种可能，第一种是正确到达该字符，这时候只需要将这些字符保存下来。
 * 第二种是因为读取到other才导致的结束，在这种情况下，还要考虑不同的情况
 * 如果是在状态与最后的结束状态连接的这一环中，因为读到其他字符，可以将该字符当成分隔符，下一次读取依然从该字符开始
 * 如果不是在只与结束状态连接读到其他字符，直接报错。
 * 
 * @author groverzhu
 *
 */
public class LexicalAnalysis {

	private String inputFileName; // 源代码文件

	private String outputFileName; // 输出token或错误信息的文件路径

	private State currentState; // 起始状态

	private TokenType currentTokenType; // 当前状态
	
	private String endSymbol = "\n"; // 用于标记一个字符的分割

	public LexicalAnalysis() {
	}

	/**
	 * 初始化输入输出文件
	 * 
	 * @param inputFileName 输入文件名
	 * @param outputFileName 输出文件名
	 * 
	 */
	public LexicalAnalysis(String inputFileName, String outputFileName) {
		this.inputFileName = inputFileName;
		this.outputFileName = outputFileName;
	}

	/**
	 * 主控函数，分析词法并写入文件
	 */
	public void analysisFile() {
		ArrayList<String> sourceCodes = new ReadFileStream(inputFileName).getStringList();
		try {
			PrintWriter writer = new PrintWriter(outputFileName, "UTF-8");
			for (int i = 0; i < sourceCodes.size(); i++) {
				String result = lexicalAnalysis(sourceCodes.get(i), i + 1);
				if (result != null && !result.isEmpty() && !result.equals("\n")) {
					// 如果是合法的词组成的句子，一个token存放一行，方便后续语法分析使用
						String[] tokens = result.split("\n");
						for (String s : tokens) {
							writer.println(s);
					}
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 词法分析程序按行处理，每次处理一行，并返回一行处理的结果 如果在一行中有报错就结束该行的处理
	 * 
	 * @param resource   从源代码读取的一行
	 * @param lineNumber 当前读取到的代码在源文件的行号
	 * @return 词法分析的token序列或者是错误消息
	 */
	public String lexicalAnalysis(String resource, int lineNumber) {

		StringBuilder result = new StringBuilder(); // 存储当前行的分析结果

		currentState = State.START; // 起始状态初始化为开始状态

		int beginColumn = 0; // 记录当前token的起始列序号
		int endColumn; // 记录当前token的终止列序号
		int currentColumn = 0; // 记录当前的列序号
		int length = resource.length(); // 源代码的长度

		while (currentColumn < length) {
			endColumn = currentColumn;
			currentColumn++;
			char c = resource.charAt(endColumn);
			switch (currentState) {
			case START:
				if (isLetter(c)) {
					currentState = State.IDLETE;
					currentTokenType = TokenType.IDENTIFIER;
				} else if (isDigit(c)) {
					currentState = State.DGT;
					currentTokenType = TokenType.DIGIT;
				} else if (isMinus(c)) {
					currentState = State.MINU;
					currentTokenType = TokenType.MINUS;
				} else if (isPlus(c)) {
					currentState = State.ADDS;
					currentTokenType = TokenType.PLUS;
				} else if (isEqualSign(c)) {
					currentState = State.EQ;
					currentTokenType = TokenType.EQUAL;
				} else if (isColon(c)) {
					currentState = State.CLN;
					currentTokenType = TokenType.COLON;
				} else if (isSemicolon(c)) {
					currentState = State.DONE;
					currentTokenType = TokenType.SEMICOLON;
				} else if (isLeftBrace(c)) {
					currentState = State.DONE;
					currentTokenType = TokenType.LEFTBRACE;
				} else if (isRightBrace(c)) {
					currentState = State.DONE;
					currentTokenType = TokenType.RIGHTBRACE;
				} else if (isWhiteSpace(c)) {
					currentState = State.START;
					beginColumn = currentColumn;
				} else {
					// 在开始状态下出现为定义的词法，转到结束状态
					// 将错误结果返回
					currentState = State.DONE;
					currentTokenType = TokenType.NOTDEFINE;
					
					
					String error = "error, row: " + lineNumber + ", column: " + endColumn + ". \"" + c
							+ "\" is a undefined symbol in this lexical. ";
					/**
					String error = "ERROR";
					*/
					result.append(error);
					return result.toString();
				}
				break;
			case IDLETE:
				if (isLetter(c)) {
					currentState = State.IDLETE;
					currentTokenType = TokenType.IDENTIFIER;
				} else if (isDigit(c)) {
					currentState = State.IDLETE;
					currentTokenType = TokenType.IDENTIFIER;
				} else if (isUnderline(c)) {
					currentState = State.UDLN;
					currentTokenType = TokenType.IDENTIFIER;
				} else {
					// 当读取到其他的字符时，只能表示在identifier这边的路径完成，该字符当作一个分割符，需要将当前的字符返回
					currentState = State.DONE;
					currentTokenType = TokenType.IDENTIFIER;
					currentColumn--;
				}
				break;
			case UDLN:
				if (isDigit(c)) {
					currentState = State.IDLETE;
					currentTokenType = TokenType.IDENTIFIER;
				} else if (isLetter(c)) {
					currentState = State.IDLETE;
					currentTokenType = TokenType.IDENTIFIER;
				} else {
					currentState = State.DONE;
					currentTokenType = TokenType.IDENTIFIER;
					currentColumn--; // 回溯

				}
				break;
			case DGT:
				if (isDigit(c)) {
					currentState = State.DGT;
					currentTokenType = TokenType.DIGIT;
				} else if (isDot(c)) {
					currentState = State.DOT;
					currentTokenType = TokenType.DIGIT;
				} else {
					currentState = State.DONE;
					currentTokenType = TokenType.NOTDEFINE;
					
					String error = "error, row: " + lineNumber + " column: " + endColumn + ". \"" + c
							+ "\" is a undefined symbol in decimal lexical. ";
					/**
					String error = "ERROR";
					*/
					result.append(error);
					return result.toString();
				}
				break;
			case DOT:
				if (isDigit(c)) {
					currentState = State.NUM;
					currentTokenType = TokenType.DIGIT;
				} else {
					currentState = State.DONE;
					currentTokenType = TokenType.NOTDEFINE;
					
					String error = "error, row: " + lineNumber + " column: " + endColumn + ". \"" + c
							+ "\" is a not defined symbol in decimal lexical. ";
					/**
					String error = "ERROR";
					*/
					result.append(error);
					return result.toString();
				}
				break;
			case NUM:
				if (isDigit(c)) {
					currentState = State.NUM;
					currentTokenType = TokenType.DIGIT;
				} else {
					currentState = State.DONE;
					currentTokenType = TokenType.DIGIT;
					currentColumn--; // 回溯

				}
				break;
			case MINU:
				if (isDigit(c)) {
					currentState = State.DGT;
					currentTokenType = TokenType.DIGIT;
				} else if (isRightAngleBracket(c)) {
					currentState = State.RTAG;
					currentTokenType = TokenType.MINUSRIGHTANGLEBRACKET;
				} else {
					currentState = State.DONE;
					currentTokenType = TokenType.NOTDEFINE;
					
					String error = "error, row: " + lineNumber + " column: " + endColumn + ". \"" + c
							+ "\" is not a defined symbol. ";
					/**
					String error = "ERROR";
					*/
					result.append(error);
					return result.toString();
				}
				break;
			case ADDS:
				if (isEqualSign(c)) {
					currentState = State.EQ;
					currentTokenType = TokenType.PLUSEQUAL;
				} else if (isDigit(c)) {
					currentState = State.DGT;
					currentTokenType = TokenType.DIGIT;
				} else {
					currentState = State.DONE;
					currentTokenType = TokenType.NOTDEFINE;
					
					String error = "error, row: " + lineNumber + " column: " + endColumn + ". \"" + c
							+ "\" is  not a  defined symbol. ";
					/**
					String error = "ERROR";
					*/
					result.append(error);
					return result.toString();
				}
				break;
			case EQ:
				if (isRightAngleBracket(c)) {
					currentState = State.RTAG;

					if (currentTokenType == TokenType.EQUAL) {
						currentTokenType = TokenType.EQUALRIGHTANGLEBRACKET;
					} else if (currentTokenType == TokenType.PLUSEQUAL) {
						currentTokenType = TokenType.PLUSEQUALANGLEBRACKET;
					}
				} else {
					currentState = State.DONE;
					currentTokenType = TokenType.NOTDEFINE;
					
					String error = "error, row: " + lineNumber + " column: " + endColumn + ". \"" + c
							+ "\" is  not a  defined symbol. ";
					/**		
					String error = "ERROR";
					*/
					result.append(error);
					return result.toString();
				}
				break;
			case CLN:
				if (isColon(c)) {
					currentState = State.DONE;
					currentTokenType = TokenType.DOUBLECOLON;
				} else {
					currentState = State.DONE;
					currentTokenType = TokenType.COLON;
					currentColumn--;
				}
				break;
			case RTAG:
				currentState = State.DONE;
				currentColumn--; // 回溯
				break;
			case DONE:
				break;
			default:
				break;
			}

			if (currentState == State.DONE) {
				if (currentTokenType == TokenType.IDENTIFIER) {
					String s = resource.substring(beginColumn, endColumn);
					// 是保留关键字，直接保留
					if (isReservedWord(s) != null) {
						s = s + endSymbol;
						result.append(s);
					} else {
						String tmp = "IDENTIFIER#" + s + endSymbol;
						result.append(tmp);
					}
					beginColumn = currentColumn;
				} else if (currentTokenType == TokenType.DIGIT) {
					String decimal = resource.substring(beginColumn, endColumn) + endSymbol;
					String tmp = "DECIMAL#" + decimal;
					result.append(tmp);
					beginColumn = currentColumn;
				} else if (currentTokenType != TokenType.NOTDEFINE && currentTokenType != TokenType.IDENTIFIER
						&& currentTokenType != TokenType.DIGIT) {
					String tmp = currentTokenType.toString() + endSymbol;
					result.append(tmp);
					beginColumn = currentColumn;
				}

				currentState = State.START;
			}
		}
		return result.toString();
	}

	// 判断关键字
	private TokenType isReservedWord(String s) {
		switch (s) {
		case "thread":
			return TokenType.thread;
		case "features":
			return TokenType.features;
		case "flows":
			return TokenType.flows;
		case "properties":
			return TokenType.properties;
		case "end":
			return TokenType.end;
		case "none":
			return TokenType.none;
		case "in":
			return TokenType.in;
		case "out":
			return TokenType.out;
		case "data":
			return TokenType.data;
		case "port":
			return TokenType.port;
		case "event":
			return TokenType.event;
		case "parameter":
			return TokenType.parameter;
		case "flow":
			return TokenType.flow;
		case "source":
			return TokenType.source;
		case "sink":
			return TokenType.sink;
		case "path":
			return TokenType.path;
		case "constant":
			return TokenType.constant;
		case "access":
			return TokenType.access;
		default:
			return null;

		}

	}

	// 状态转化方程
	/**
	 * 判断当前字符是否为字母
	 * 
	 * @param c
	 * @return
	 */
	private boolean isLetter(char c) {
		if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
			return true;
		else
			return false;
	}

	/**
	 * 判断当前字符是否为数字
	 * 
	 * @param c
	 * @return
	 */
	private boolean isDigit(char c) {
		if (c >= '0' && c <= '9')
			return true;
		else
			return false;
	}

	/**
	 * 判断当前符号是否为下划线'_'
	 * 
	 * @param c
	 * @return
	 */
	private boolean isUnderline(char c) {
		return (c == '_') ? true : false;
	}

	/**
	 * 判断当前字符是否为':'
	 * 
	 * @param c
	 * @return
	 */
	private boolean isColon(char c) {
		return c == ':' ? true : false;
	}

	/**
	 * 判断当前符号是否为小数点'.'
	 * 
	 * @param c
	 * @return
	 */
	private boolean isDot(char c) {
		return (c == '.') ? true : false;
	}

	/**
	 * 判断当前符号是否为加号'+'
	 * 
	 * @param c
	 * @return
	 */
	private boolean isPlus(char c) {
		return (c == '+') ? true : false;
	}

	/**
	 * 判断当前符号是否为等号'='
	 * 
	 * @param c
	 * @return
	 */
	private boolean isEqualSign(char c) {
		return (c == '=') ? true : false;
	}

	/**
	 * 判断当前符号是否为右尖括号
	 * 
	 * @param c
	 * @return
	 */
	private boolean isRightAngleBracket(char c) {
		return (c == '>') ? true : false;
	}

	/**
	 * 判断当前符号是否为减号
	 * 
	 * @param c
	 * @return
	 */
	private boolean isMinus(char c) {
		return (c == '-') ? true : false;
	}

	/**
	 * 判断是否为‘;’
	 * 
	 * @param c
	 * @return
	 */
	private boolean isSemicolon(char c) {
		return (c == ';') ? true : false;
	}

	/**
	 * 判断当前符号是否为左大括号
	 * 
	 * @param c
	 * @return
	 */
	private boolean isLeftBrace(char c) {
		return (c == '{') ? true : false;

	}

	/**
	 * 判断当前字符是否为右大括号
	 * 
	 * @param c
	 * @return
	 */
	private boolean isRightBrace(char c) {
		return (c == '}') ? true : false;
	}

	/**
	 * 判断是否为空格，制表符或者换行符
	 * 
	 * @param c
	 * @return
	 */
	private boolean isWhiteSpace(char c) {
		return (c == '\t' || c == ' ' || c == '\n') ? true : false;
	}

	/**
	 * 设置代码源文件路径
	 * 
	 * @param inputFileName
	 */
	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}

	/**
	 * 设置分析结果输出文件路径
	 * 
	 * @param outputFileName
	 */
	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

}
