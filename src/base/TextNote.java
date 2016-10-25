package base;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

public class TextNote extends Note {
	private String content;
	
	public TextNote(String title) {
		super(title);
	}
	
	/**
	 * Load a TextNote from a file
	 * note title is the filename
	 * note content is the file content
	 * 
	 * @param file
	 */
	public TextNote(File file) {
		super(file.getName());
		this.content = getTextFromFile(file.getAbsolutePath());
	}

	public TextNote(String title, String content) {
		this(title);
		this.content = content;
	}

	@Override
	public boolean containsKeyword(String keyword) {
		return super.containsKeyword(keyword) || content.toUpperCase().contains(keyword.toUpperCase());
	}
	

	private String getTextFromFile(String absolutePath) {
		String txt = "";
		try {
			txt = new String(Files.readAllBytes(Paths.get(absolutePath)), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return txt;
	}
	
	public void exportTextToFile(String pathFolder) {
		String fileName = getTitle().replace(" ", "_");
		String fullFilePath = pathFolder.isEmpty() ? fileName + ".txt" : pathFolder+ File.separator + fileName + ".txt";
		System.out.println( fullFilePath );		
		File file = new File(fullFilePath);
		try {
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(this.content);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	//Lab6, buggy
	public Character countLetters(){
		HashMap<Character,Integer> count = new HashMap<Character,Integer>();
		String a = this.getTitle() + this.getContent();
		int b = 0;
		Character r = ' ';
		for (int i = 0; i < a.length(); i++) {
			Character c = a.charAt(i);
			if (c <= 'Z' && c >= 'A' || c <= 'z' && c >= 'a') {
				if (!count.containsKey(c)) {
					count.put(c, 1);
					if(b == 0) {
						b = count.get(c);
						r = c;
					}
				} else {
					count.put(c, count.get(c) + 1);
					if (count.get(c) > b) {
						b = count.get(c);
						r = c;
					}
				}
			}
		}
		return r;
	}
	
	
	

}
