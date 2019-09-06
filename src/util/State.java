package util;

/**
 * 使用菜单存储确定行有穷状态机里的状态
 * 
 * 在该状态机下总共有12个状态，其中一个起始状态，一个终止状态
 * @author groverzhu
 *
 */
public enum State {
	START, // 开始状态
	IDLETE, // 读到一个identifier_letter或者重复读取identifier_letter,digit的状态
	UDLN, // 读到下划线的状态
	DGT, // 读到一个数字后的状态
	DOT, // 读到小数点后的状态
	NUM, // decimal结束的状态
	MINU, // 读到减号后的状态
	ADDS, // 读到加号的状态
	EQ, // 读到等号的状态
	CLN, // 读到冒号的状态
	RTAG, // 读到右尖括号的状态
	DONE // 结束状态

}
