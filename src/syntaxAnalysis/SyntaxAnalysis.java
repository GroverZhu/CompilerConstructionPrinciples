package syntaxAnalysis;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import util.Statement;
import util.TokenStream;
import util.TokenType;
import util.TreeNode;

/**
 * 语法分析程序 分析词法分析的结果，将词法分析的结果进行判断，采用递归下降法进行语法分析
 * 一个大的文法由若干个小的文法组成，采用递归下降的方法进行语法分析，每个文法都是一个函数
 * 每个函数的主要流程就是遇到总结符就采用match，不是总结符，调用相应的文法规则函数
 * 
 * @author groverzhu
 *
 */
public class SyntaxAnalysis {
	
	private String inputFile; // 词法分析结果文件，即输入文件
	
	private String outputFile; // 语法分析结果，即输出文件
	
	private String errorInfoFile; // 错误消息存储
	
	private ArrayList<String> tokenList; // 当前要分析的token序列
	
	private int index = 0; // 当前分析的单词的索引
	
	private TokenType currentToken; // 当前token类型
	
	private String currentValue; // 当前token的值
	
	private ArrayList<String> errorList; // 错误信息列表
	
	private StringBuilder treeStream = new StringBuilder(); // 存储抽象语法树的文本形式
	
	/**
	 * 构造函数
	 * @param inputFile 输入文件名
	 * @param outputFile 输出文件名
	 */
	public SyntaxAnalysis(String inputFile, String outputFile, String errorInfoFile) {
		this.inputFile = inputFile;
		this.outputFile =  outputFile;
		this.errorInfoFile = errorInfoFile;
		
		this.tokenList = new TokenStream(inputFile).getTokenList(); // 从TokenStream中获取到词法分析的结果
		
		this.errorList = new ArrayList<String>(); // 错误信息列表
	}
	
	/**
	 * 获得下一个token，并将该token类型与值存储到全局变量currentToken与currentValue
	 */
	private void nextToken() {
		if (index < tokenList.size()) {
			String temp = tokenList.get(index++);
			// 当前token是decimal,identifier的话，需要分开保留结果
			if (temp.startsWith("DECIMAL#")) {
				currentToken = TokenType.DECIMAL;
				currentValue = temp.substring("DECIMAL#".length());
			} else if (temp.startsWith("IDENTIFIER#")) {
				currentToken = TokenType.IDENTIFIER;
				currentValue = temp.substring("IDENTIFIER#".length());
			} else {
				currentToken = TokenType.valueOf(temp);
				currentValue = temp;
			}
		}
	}
	
	/**
	 * 判断为当前token是否为预期的token，如果是，读取下一个token
	 * 如果不是预期token，返回错误,终止该语法程序的运行
	 * 
	 * @param expected 期望当前的token的类型
	 * @return
	 */
	private boolean match(TokenType expected) {
		if (expected == currentToken) {
			nextToken();
			return true;
		} else {
			// 错误处理，找到一处错误直接结束该语法程序，将错误写入错误信息文件
			String error = "In row: " + index + " is expect " + expected  + ",  but is " + currentToken;
			System.err.println(error);
			errorList.add(error);

			// 退出程序，终止语法分析程序
			// System.exit(0);
			
			// 继续往下读取
			nextToken();
			
			/** 继续往下读一个
			if (currentToken == expected) {
				nextToken();
			} else {
				index--;
				nextToken();
			}
			*/
			return false;
		}
	}
	
	/**
	 * 总分析程序
	 */
	public void syntaxAnalysis(String prefix){
		this.nextToken();
		while (index < tokenList.size()) {
			treeStream = TreeNode.preOrder(this.threadSpec(), prefix, treeStream).append("\n"); // 每个thread加入一个分行
			
		}
		// 将语法树文本形式写入文件
		try {
			PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
			writer.write(treeStream.toString());
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			PrintWriter writer = new PrintWriter(errorInfoFile, "UTF-8");
			for (String s : errorList) {
				writer.write(s + "\n");
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	// 文法规则函数
	
	/**
	 * ThreadSpec→thread identifier 
	 * 				[features featureSpec]
	 * 				[flows flowSpec]
	 * 				[properties association;] 
	 * 				end identifier;
	 * 树的表示
	 * 			identifier
	 * 		   /	|	   \ 
	 * 		  /		|	    \
	 * featureSpec flowSpec association
	 * @return
	 */
	private TreeNode threadSpec() {
		TreeNode root = new TreeNode(Statement.Thread_SPEC);
		String temp;
		match(TokenType.thread);
		if (currentToken == TokenType.IDENTIFIER) {
			root.token = currentToken;
			root.value = currentValue;
			temp = currentValue;
			match(TokenType.IDENTIFIER);
			if (currentToken == TokenType.features) {
				match(TokenType.features);
				TreeNode featureSpec = this.featureSpec();
				root.children.add(featureSpec);
			}
			if (currentToken == TokenType.flows) {
				match(TokenType.flows);
				TreeNode flowSpec = this.flowSpec();
				root.children.add(flowSpec);
			}
			if (currentToken == TokenType.properties) {
				match(TokenType.properties);
				TreeNode association = this.association();
				root.children.add(association);
				match(TokenType.SEMICOLON);
			}
			match(TokenType.end);
			// 判断起始identifier与结束identifier相同
			if (currentToken == TokenType.IDENTIFIER) {
				if (currentValue.equals(temp)) {
					match(TokenType.IDENTIFIER);
					match(TokenType.SEMICOLON);
				} else {
					String error = "In row: " + index + ". the begin identifier: " + temp + " is not equal to the end identifier: " + currentValue;
					errorList.add(error);
					System.err.println(error);
					// 遇到错误结束程序
					// System.exit(0);
					match(TokenType.IDENTIFIER);
					match(TokenType.SEMICOLON);
					
				}
			}
		}
		return root;
	}
	
	/**
	 * featureSpec → identifier : IOtype partSpec|none;
	 * 树的表示
	 * 				identifier
	 * 			   /		\
	 * 			  /			 \
	 *        IOtype        partSpec
	 * @return
	 */
	private TreeNode featureSpec() {
		TreeNode root = new TreeNode(Statement.FEATURE_SPEC);
		if (currentToken == TokenType.IDENTIFIER) {
			root.token = currentToken;
			root.value = currentValue;
			match(TokenType.IDENTIFIER);
			match(TokenType.COLON);
			if (currentToken == TokenType.in || currentToken == TokenType.out) {
				TreeNode iotype = this.iotype();
				root.children.add(iotype);
				if (currentToken == TokenType.data || currentToken == TokenType.event) {
					TreeNode portSpec = this.portSpec();
					root.children.add(portSpec);
				}
				if (currentToken == TokenType.parameter) {
					TreeNode parameterSpec = this.parameterSpec();
					root.children.add(parameterSpec);
				}
			}
		}
		if (currentToken == TokenType.none) {
			root.token = currentToken;
			root.value = currentValue;
			match(TokenType.none);
			match(TokenType.SEMICOLON);
		}
		return root;
	}
	
	/**
	 * portSpec → portType[{{association}}];
	 * 树的表示
	 * 				portSpec
	 * 			   /		\
	 * 			  / 		 \
	 * 			portType	association ...
	 * @return
	 */
	private TreeNode portSpec() {
		TreeNode root = new TreeNode(Statement.PORT_SPEC);
		if (currentToken == TokenType.data || currentToken == TokenType.event) {
			TreeNode portType = this.portType();
			root.children.add(portType);
			if (currentToken == TokenType.LEFTBRACE) {
				match(TokenType.LEFTBRACE);
				while (currentToken == TokenType.IDENTIFIER) {
					TreeNode association = this.association();
					root.children.add(association);
				}
				match(TokenType.RIGHTBRACE);
			}
			match(TokenType.SEMICOLON);
		}
		
		return root;
	}
	
	/**
	 * ParameterSpec → parameter[reference][{{association}}];
	 * 				ParameterSpec
	 * 				/	     \
	 * 			   /	      \
	 * 		reference	   association ...
	 * @return
	 */
	private TreeNode parameterSpec() {
		TreeNode root = new TreeNode(Statement.PARAMETER_SPEC);
		match(TokenType.parameter);
		if (currentToken == TokenType.IDENTIFIER) {
			TreeNode reference = this.reference();
			root.children.add(reference);
			if (currentToken == TokenType.LEFTBRACE) {
				match(TokenType.LEFTBRACE);
				while (currentToken == TokenType.IDENTIFIER) {
					TreeNode association = this.association();
					root.children.add(association);
				}
				match(TokenType.RIGHTBRACE);
			}
		}
		match(TokenType.SEMICOLON);
		return root;
	}
	
	/**
	 * portType → data port[reference]|event data port[reference]|event port
	 * 树的表示
	 * 						data port/
	 * 						event data port/
	 * 						event port
	 * 							|
	 * 							|	
	 * 						 reference
	 * 
	 * @return
	 */
	private TreeNode portType() {
		TreeNode root = new TreeNode(Statement.PORT_TYPE);
		if (currentToken == TokenType.data) {
			match(TokenType.data);
			if (currentToken == TokenType.port) {
				root.token = TokenType.DATA_PORT;
				root.value = TokenType.DATA_PORT.toString();
				match(TokenType.port);
				if (currentToken == TokenType.IDENTIFIER) {
					TreeNode reference = this.reference();
					root.children.add(reference);
				}
			}
		}
		if (currentToken == TokenType.event) {
			match(TokenType.event);
			if (currentToken == TokenType.port) {
				root.token = TokenType.EVENT_PORT;
				root.value = TokenType.EVENT_PORT.toString();
				match(TokenType.port);
			}
			if (currentToken == TokenType.data) {
				match(TokenType.data);
				if(currentToken == TokenType.port) {
					root.token = TokenType.EVENT_DATA_PORT;
					root.value = TokenType.EVENT_DATA_PORT.toString();
					match(TokenType.port);
					if (currentToken == TokenType.IDENTIFIER) {
						TreeNode reference = this.reference();
						root.children.add(reference);
					}
				}
			}
		}
		
		return root;
	}
	
	/**
	 * IOtype → in|out|in out
	 * 树的表示
	 * 			in/out/in out
	 * @return
	 */
	private TreeNode iotype() {
		TreeNode root = new TreeNode(Statement.IOTYPE);
		if (currentToken == TokenType.out) {
			root.token = currentToken;
			root.value = currentValue;
			match(TokenType.out);
		}
		if (currentToken == TokenType.in) {
			root.token = currentToken;
			root.value = currentValue;
			match(TokenType.in);
			if (currentToken == TokenType.out) {
				root.token = TokenType.INOUT;
				root.value = TokenType.INOUT.toString();
				match(TokenType.out);
			}
		}
		return root;
	}
	
	/**
	 * 
	 * flowSpec → identifier : flow typeSpec|none;
	 * typeSpec → flowSourceSpec | flowSinkSpec | flowPathSpec
	 * 树的表示
	 * 				identifier
	 * 					|	
	 * 					|
	 * 				  typeSpec
	 * 
	 * @return
	 */
	private TreeNode flowSpec() {
		TreeNode root = new TreeNode(Statement.FLOW_SPEC);
		if (currentToken == TokenType.IDENTIFIER) {
			root.token = currentToken;
			root.value = currentValue;
			match(TokenType.IDENTIFIER);
			match(TokenType.COLON);
			match(TokenType.flow);
			if (currentToken == TokenType.source) {
				TreeNode source = this.flowSourceSpec();
				root.children.add(source);
			}
			if (currentToken == TokenType.sink) {
				TreeNode sink = this.flowSinkSpec();
				root.children.add(sink);
			}
			if (currentToken == TokenType.path) {
				TreeNode path = this.flowPathSpec();
				root.children.add(path);
			}
		}
		if (currentToken == TokenType.none) {
			root.token = currentToken;
			root.value = currentValue;
			match(TokenType.none);
			match(TokenType.SEMICOLON);
		}
		return root;
	}
	
	/**
	 * flowSourceSpec → source identifier[{{association}}];
	 * 树的表示
	 * 				identifier
	 * 					|
	 * 					|
	 * 				association ...
	 * 
	 * @return
	 */
	private TreeNode flowSourceSpec() {
		TreeNode root = new TreeNode(Statement.FLOW_SOURCE_SPEC);
		match(TokenType.source);
		if (currentToken == TokenType.IDENTIFIER) {
			root.token = currentToken;
			root.value = currentValue;
			match(TokenType.IDENTIFIER);
			if (currentToken == TokenType.LEFTBRACE) {
				match(TokenType.LEFTBRACE);
				while (currentToken == TokenType.IDENTIFIER) {
					TreeNode association = this.association();
					root.children.add(association);
				}
				match(TokenType.RIGHTBRACE);
			}
			match(TokenType.SEMICOLON);
		}
		return root;
	}
	
	/**
	 * flowSinkSpec → sink identifier[{{association}}];
	 * 树的表示
	 * 				identifier
	 * 					|
	 * 					|
	 * 				association ...
	 * 			
	 * @return
	 */
	private TreeNode flowSinkSpec() {
		TreeNode root = new TreeNode(Statement.FLOW_SINK_SPEC);
		match(TokenType.sink);
		if (currentToken == TokenType.IDENTIFIER) {
			root.token = currentToken;
			root.value = currentValue;
			match(TokenType.IDENTIFIER);
			if (currentToken == TokenType.LEFTBRACE) {
				match(TokenType.LEFTBRACE);
				while (currentToken == TokenType.IDENTIFIER) {
					TreeNode association = this.association();
					root.children.add(association);
				}
				match(TokenType.RIGHTBRACE);
			}
			match(TokenType.SEMICOLON);
		}
		return root;
	}
	
	/**
	 * flowPathSpec → path identifier− > identifier;
	 * 树的表示
	 * 			identifier1,identifier2
	 * @return
	 */
	private TreeNode flowPathSpec() {
		TreeNode root = new TreeNode(Statement.FLOW_PATH_SPEC);
		match(TokenType.path);
		if (currentToken == TokenType.IDENTIFIER) {
			root.token = currentToken;
			root.value = currentValue;
			match(TokenType.IDENTIFIER);
			match(TokenType.MINUSRIGHTANGLEBRACKET);
			if (currentToken == TokenType.IDENTIFIER) {
				root.value = root.value + "\t" + currentValue;
				match(TokenType.IDENTIFIER);
				match(TokenType.SEMICOLON);
			}
		}
		return root;
	}
	
	/**
	 * association → [identifier ::]identifier splitter[constant]access decimal|none
	 * 树的表示
	 * 				identifier1,identifier2
	 * 			    	/    			\
	 * 			       /				 \
	 * 		    	splitter			decimal
	 * @return
	 */
	private TreeNode association() {
		TreeNode root = new TreeNode(Statement.ASSOCIATION);
		if (currentToken == TokenType.IDENTIFIER) {
			root.token = currentToken;
			root.value = currentValue;
			match(TokenType.IDENTIFIER);
			if (currentToken == TokenType.DOUBLECOLON) {
				match(TokenType.DOUBLECOLON);
				if (currentToken == TokenType.IDENTIFIER) {
					root.value = root.value + "\t" + currentValue;
					match(TokenType.IDENTIFIER);
				}
			}
			TreeNode splitter = this.splitter();
			root.children.add(splitter);
			if (currentToken == TokenType.constant) {
				match(TokenType.constant);
			}
			match(TokenType.access);
			if (currentToken == TokenType.DECIMAL) {
				TreeNode decimal = new TreeNode(Statement.DECIMAL);
				decimal.token = currentToken;
				decimal.value = currentValue;
				root.children.add(decimal);
				match(TokenType.DECIMAL);
			}
		}
		if (currentToken == TokenType.none) {
			root.token = currentToken;
			root.value = currentValue;
			match(TokenType.none);
		}
		return root;
	}
	
	/**
	 * splitter →=> |+ =>
	 * 树的表示
	 * 		=>/+=>
	 * @return
	 */
	private TreeNode splitter() {
		TreeNode root = new TreeNode(Statement.SPLITTER);
		if (currentToken == TokenType.EQUALRIGHTANGLEBRACKET) {
			root.token = currentToken;
			root.value = currentValue;
			match(TokenType.EQUALRIGHTANGLEBRACKET);
		}
		if (currentToken == TokenType.PLUSEQUALANGLEBRACKET) {
			root.token = currentToken;
			root.value = currentValue;
			match(TokenType.PLUSEQUALANGLEBRACKET);
		}
		return root;
		
	}
	
	/**
	 * reference → [{identifier ::}]identifier
	 * 树的表示
	 * 			identifier1,identifier2,...
	 * @return
	 */
	private TreeNode reference() {
		TreeNode root = new TreeNode(Statement.REFERENCE);
		if (currentToken == TokenType.IDENTIFIER) {
			root.token = currentToken;
			root.value = currentValue;
			match(TokenType.IDENTIFIER);
			if (currentToken == TokenType.DOUBLECOLON) {
				match(TokenType.DOUBLECOLON);
				while (currentToken == TokenType.IDENTIFIER) {
					root.value = root.value + "\t" + currentValue;
					match(TokenType.IDENTIFIER);
					if (currentToken == TokenType.DOUBLECOLON) {
						match(TokenType.DOUBLECOLON);
					}
				}
			}
		}
		return root;
	}
	
	/**
	 * 获取错误信息列表
	 * @return
	 */
	public ArrayList<String> getErrorList() {
		return this.errorList;
	}
}
