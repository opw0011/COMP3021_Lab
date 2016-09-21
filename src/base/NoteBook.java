package base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoteBook {
	
	private ArrayList<Folder> folders;
	public NoteBook() {
		folders = new ArrayList<Folder>();
	}
	
	public boolean createTextNote(String folderName, String title) {
		TextNote note = new TextNote(title);
		return insertNote(folderName, note);
	}
	
	public boolean createTextNote(String folderName, String title, String content) {
		TextNote note = new TextNote(title, content);
		return insertNote(folderName, note);
	}
	
	public boolean createImageNote(String folderName, String title) {
		ImageNote note = new ImageNote(title);
		return insertNote(folderName, note); 
	}

	public ArrayList<Folder> getFolders() {
		return folders;
	}
	
	private boolean insertNote(String folderName, Note note) {
		Folder f = null;
		for(Folder f1 : folders) {
			if(f1.getName().equals(folderName)) {
				f = f1;
				break;
			}			
		}
		
		if(f == null) {
			f = new Folder(folderName);
			folders.add(f);
		}
		
		for(Note n : f.getNotes()) {
			if(n.equals(note)) {
				System.out.println("Creating note " + note.getTitle() + " under fodler " + folderName + " failed");
				return false;
			}
		}
		
		f.addNote(note);		
		return true;		
	}
	
	public void sortFolders() {
		// sort all the notes of each folder
		for(Folder f : folders) {
			f.sortNotes();
		}
		
		// sort all the folder of the note book
		Collections.sort(folders);
	}

	public List<Note> searchNotes(String string) {
		List<Note> notes = new ArrayList<Note>();
		for(Folder f : folders) {
			// insert to notes if found
			if(f.searchNotes(string) != null) {
				notes.addAll(f.searchNotes(string));
			}				
		}
		return notes;
	}
	
	
}
