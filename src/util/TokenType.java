package util;

/**
 * 定义每个token的类型
 * 
 * @author groverzhu
 *
 */
public enum TokenType {
	// 标识符
	IDENTIFIER,
	// 数字
	DIGIT, // 用在词法分析中进行判断
	DECIMAL, // 语法分析中进行数字的判断

	// 专用符号
	EQUALRIGHTANGLEBRACKET, // =>
	PLUSEQUALANGLEBRACKET, // +=>
	MINUSRIGHTANGLEBRACKET, // ->
	SEMICOLON, // ;
	COLON, // :
	DOUBLECOLON, // ::
	LEFTBRACE, // {
	RIGHTBRACE, // }
	// 附加符号标识，便于编程
	EQUAL, // =
	MINUS, // -
	PLUS, // +
	PLUSEQUAL, // +=
	RIGHTANGLEBRACKET, // >(大概率用不到)
	
	// 保留关键字
	thread, 
	features, 
	flows, 
	properties, 
	end, 
	none, 
	in, 
	out, 
	data, 
	port, 
	event, 
	parameter, 
	flow, 
	source, 
	sink, 
	path,
	constant, 
	access,
	
	// 未定义的
	NOTDEFINE,
	
	// IOtype的类型
	INOUT,
	
	// portType的类型
	DATA_PORT,
	EVENT_DATA_PORT,
	EVENT_PORT

	
}
