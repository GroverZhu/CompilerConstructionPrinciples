package util;

/**
 * 定义文法类型
 * 
 * @author groverzhu
 *
 */
public enum Statement {
	Thread_SPEC,
	FEATURE_SPEC,
	PORT_SPEC,
	PARAMETER_SPEC,
	PORT_TYPE,
	IOTYPE,
	FLOW_SPEC,
	FLOW_SOURCE_SPEC,
	FLOW_SINK_SPEC,
	FLOW_PATH_SPEC,
	ASSOCIATION,
	SPLITTER,
	REFERENCE,
	//外增的文法用来存储decimal
	DECIMAL
	
}
