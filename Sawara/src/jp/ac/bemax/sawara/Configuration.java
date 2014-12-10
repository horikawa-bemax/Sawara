package jp.ac.bemax.sawara;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Configuration {
	static File confFile = null;
	
	/**
	 * configファイルから値を取り出す。
	 * @param valName 変数名
	 * @return 変数の値
	 */
	static String getConfig(String valName){
		String result = null;
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(confFile));
			String readData = null;
			
			breakPoint:
			while((readData = br.readLine()) != null){
				String[] str = readData.split(";");
				for(String s: str){
					String[] ss = s.split("=");
					if(ss[0].equals(valName)){
						result = ss[1];
						break breakPoint;
					}
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				br.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}

		return result;
	}
	
	/**
	 * configファイルに値を設定する
	 * @param name 変数名
	 * @param val 値
	 */
	static void setConfig(String name, String val){
		StringBuffer buffer = new StringBuffer("");
		FileReader fr = null;
		BufferedReader br = null;
		FileWriter fw = null;
		
		try{
			br = new BufferedReader(new FileReader(confFile));
			String readData = null;
			while((readData = br.readLine()) != null){
				String[] str = readData.split(";");
				for(String s: str){
					String writeStr = readData;
					String[] ss = readData.split("=");
					if(ss[0].equals(name)){
						writeStr = name + "=" + val + ";";
					}
					buffer.append(writeStr);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try{
			fw = new FileWriter(confFile);
			fw.write(buffer.toString());
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * configファイルを作成する
	 */
	static void makeConfFile(){
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new FileWriter(confFile));
			bw.write("theme=DefaultTheme;");
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				bw.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}
