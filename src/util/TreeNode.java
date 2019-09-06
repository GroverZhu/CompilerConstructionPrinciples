package util;

import java.util.ArrayList;

/**
 * 定义树的节点
 * 树的节点应包含一个当前文法的类型类型(statement)，token类型(token)，token类型的值(value)，子女节点(children)，
 * 
 * @author groverzhu
 *
 */

public class TreeNode {
	
	public Statement statement; // 当前文法的类型
	public TokenType token; // 当前单词类型
	public String value; // 当前单词的值，主要针对TokenType是identifier和decimal，如果是其他类型的话，那么value与TokenType一致
	public ArrayList<TreeNode> children; // 非叶节点的子女节点
	
	/**
	 * 默认构造函数，对于不明确的树节点，后续补全节点属性
	 */
	public TreeNode() {
	}
	
	/**
	 * 初始设置节点的文法类型，token类型，token的值，并初始化子女节点
	 * @param statement 文法类型
	 * @param token token类型
	 * @param value token值
	 */
	public TreeNode(Statement statement, TokenType token, String value) {
		this.statement = statement;
		this.token = token;
		this.value = value;
		this.children = new ArrayList<TreeNode>();
	}
	
	/**
	 * 仅设置文法的初始值，并初始化子女节点
	 * @param statement 文法类型
	 */
	public TreeNode(Statement statement) {
		this.statement = statement;
		this.children = new ArrayList<TreeNode>();
	}
	
	/**
	 * 返回相应的属性，主要由statement, token, value组成，只打印出具有的属性
	 */
	public String toString() {
		String string;
		string = "statement: " + statement.toString();
		if (token != null) {
			string = string + ", " + "token: " + token.toString();
		}
		if (value != null) {
			string = string + ", " + "value: " + value;
		}
		return string;
	}
	
	/**
	 * 前序遍历当前建立好的抽象语法树
	 * 
	 * @param root 抽象语法树的跟节点
	 * @param prefix 打印出来的语法树的前缀
	 * @param builder 最终语法树的文本表示
	 * @return 语法树的文法表示
	 */
	public static StringBuilder preOrder(TreeNode root, String prefix, StringBuilder builder) {
		if (root != null) {
			builder.append(prefix + root.toString() + "\n");
			if (root.children != null && root.children.size() != 0) {
				for (TreeNode node : root.children) {
					// 到子树就往后缩紧一个制表符
					preOrder(node, "\t" + prefix, builder);
				}
			}
		}
		return builder;

	}

}
