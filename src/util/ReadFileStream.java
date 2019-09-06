package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadFileStream {

	// 将文件里的文本按行读取，存储在该ArrayList里
	private ArrayList<String> stringList;

	public ReadFileStream(String fileName) {
		File file = new File(fileName);
		stringList = new ArrayList<String>();
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(file));
			String tmp = null;
			while ((tmp = fileReader.readLine()) != null) {
				tmp += "\n";
				stringList.add(tmp);

			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 返回ArrayList
	public ArrayList<String> getStringList() {
		return this.stringList;
	}
}
