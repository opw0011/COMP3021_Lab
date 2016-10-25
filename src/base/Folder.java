package base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Folder implements Comparable<Folder>, Serializable{
	private ArrayList<Note> notes;
	private String name;
	private static final long serialVersionUID = 1L;
	
	public Folder(String name) {
		this.name = name;
		notes = new ArrayList<Note>();
	}
	
	public void addNote(Note note) {
		notes.add(note);
	}

	public ArrayList<Note> getNotes() {
		return notes;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Folder other = (Folder) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		int nText = 0;
		int nImage = 0;
		
		// Loop through the notes and count num text node and image note
		for(Note note : notes) {
			if (note instanceof TextNote) {
				nText++;
			}
			else {
				nImage++;
			}
		}
		
		return name + ":" + nText + ":" + nImage;
	}

	@Override
	public int compareTo(Folder o) {
		return this.name.compareTo(o.name);
	}
	
	public void sortNotes() {
		Collections.sort(notes);
	}
	
	public List<Note> searchNotes(String keywords) {
		String [] words = keywords.split(" ");
		ArrayList<String> andKeywords = new ArrayList<String>();
		ArrayList<Set<String>> orKeywords = new ArrayList<Set<String>>();

		// parse the query to 'or' and 'and' list
		int i = 0;
		while(i < words.length) {
			if(words[i].toUpperCase().equals("OR")) {
				Set<String> orSet = new HashSet<String>();
				andKeywords.remove(words[i-1]);
				orSet.add(words[i-1]);
				orSet.add(words[i+1]);
//				System.out.print("add > " + words[i-1] + " " + words[i+1] + " ");
				// a or b or c
				i+=2;
				while(i < words.length && words[i].toUpperCase().equals("OR")) {
//					System.out.print(words[i+1] + " ");
					orSet.add(words[i+1]);
					i += 2;
				}
				
//				System.out.println();
				// add to array list
				orKeywords.add(orSet);				
			}
			else {
				andKeywords.add(words[i]);
				i++;
			}			
		}
		
//		System.out.println("AND: " + andKeywords);
//		System.out.println("OR: " + orKeywords);
		
		List<Note> matchedNotes= new ArrayList<Note>();
		
		for(Note note: notes) {
			boolean match = true;
			// check or
			for(Set<String> orKeyword : orKeywords) {
				boolean orMatch = false;
				// either one , a or b or c
				for(String keyword : orKeyword) {
//					System.out.println(keyword);
					orMatch = orMatch || note.containsKeyword(keyword);
				}				
				match = match && orMatch;
			}		
			
			// check and 
			for(String andKeyword : andKeywords) {
				if (!note.containsKeyword(andKeyword))
				{
					match = false;
					break;
				}
			}
			if(match) matchedNotes.add(note);
		}
		
		return matchedNotes;
	}
	
	/**
	 * Get the note by the title
	 * @param title note's title
	 * @return Note if found, otherwise return null
	 */
	public Note getNote(String title) {
		Note matchedNote = null;		
		for(Note note : notes) {
			if(note.getTitle().equals(title)) {
				matchedNote = note;
			}
		}
		return matchedNote;
	}
	
	/**
	 * Remove the note from the folder
	 * @param title
	 * @return
	 */
	public boolean removeNote(String title) {
		Note noteToBeRemoved = getNote(title);
		if(noteToBeRemoved != null) {
			notes.remove(noteToBeRemoved);
			return true;
		}
		return false;
		
	}

}
