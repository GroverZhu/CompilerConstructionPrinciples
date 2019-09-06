# 编译原理实验课   
- [词法分析](#词法分析)   
- [语法分析](#语法分析)   

实验课内容对线程语法的词法分析和语法分析，实验项目文件结构如下所示。run文件夹下为程序运行入口，testcase文件下为测试用例   

```
├── src
│   ├── lexicalAnalysis
│   │   └── LexicalAnalysis.java
│   ├── run
│   │   ├── LexicalAnalysisTest.java
│   │   └── SyntaxAnalysisTest.java
│   ├── syntaxAnalysis
│   │   └── SyntaxAnalysis.java
│   └── util
│       ├── ReadFileStream.java
│       ├── State.java
│       ├── Statement.java
│       ├── TokenStream.java
│       ├── TokenType.java
│       └── TreeNode.java
└── testcase
```
## 词法分析   
根据有穷状态机进行词法分析，采用双层switch-case嵌套实现。[状态转化图](./docs/词法规则-状态转化图.tex)   

##  语法分析   
根据递归下降法进行语法分析[语法树定义](./docs/语法规则-语法树定义.tex)   
