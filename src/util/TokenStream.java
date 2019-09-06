package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 将词法分析的结果存储在一个list里面
 * 
 * @author groverzhu
 *
 */

public class TokenStream {

	private ArrayList<String> tokenList;

	public TokenStream(String fileName) {
		File file = new File(fileName);
		tokenList = new ArrayList<String>();
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(file));
			String tmp = null;
			while ((tmp = fileReader.readLine()) != null) {
				tokenList.add(tmp);
			}

			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> getTokenList() {
		return this.tokenList;
	}

	public void setTokenList(ArrayList<String> tokenList) {
		this.tokenList = tokenList;
	}
}
