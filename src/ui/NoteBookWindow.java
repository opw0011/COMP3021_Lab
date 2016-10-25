package ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import base.Folder;
import base.Note;
import base.NoteBook;
import base.TextNote;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 
 * NoteBook GUI with JAVAFX
 * 
 * COMP 3021
 * 
 * 
 * @author valerio
 *
 */
public class NoteBookWindow extends Application {

	/**
	 * TextArea containing the note
	 */
	final TextArea textAreaNote = new TextArea("");
	/**
	 * list view showing the titles of the current folder
	 */
	final ListView<String> titleslistView = new ListView<String>();
	/**
	 * 
	 * Combobox for selecting the folder
	 * 
	 */
	final ComboBox<String> foldersComboBox = new ComboBox<String>();
	/**
	 * This is our Notebook object
	 */
	NoteBook noteBook = null;
	/**
	 * current folder selected by the user
	 */
	String currentFolder = "";
	/**
	 * current search string
	 */
	String currentSearch = "";
	/**
	 * current stage
	 */
	Stage stage;

	public static void main(String[] args) {
		launch(NoteBookWindow.class, args);
	}

	@Override
	public void start(Stage stage) {
		loadNoteBook();
		// Use a border pane as the root for scene
		BorderPane border = new BorderPane();
		// add top, left and center
		border.setTop(addHBox());
		border.setLeft(addVBox());
		border.setCenter(addGridPane());

		Scene scene = new Scene(border);
		stage.setScene(scene);
		stage.setTitle("NoteBook COMP 3021");
		stage.show();
		
		this.stage = stage;
	}

	/**
	 * This create the top section
	 * 
	 * @return
	 */
	private HBox addHBox() {

		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10); // Gap between nodes

		// Load Button
		Button buttonLoad = new Button("Load");
		buttonLoad.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// load file
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Please choose a file which contains a Notebook object!");
				
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Serialized Object File (*.ser)", "*.ser");
				fileChooser.getExtensionFilters().add(extFilter);
				 	
				File file = fileChooser.showOpenDialog(stage);	// ensure the window(stage) is blocked/locked by the pop-up windows
				if(file != null) {
					loadNoteBook(file);						
				}
			}			
		});
		buttonLoad.setPrefSize(100, 20);

		// Save Button
		Button buttonSave = new Button("Save");
		buttonSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// select file to be saved
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Please choose a file which you want to save to!");
				
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Serialized Object File (*.ser)", "*.ser");
				fileChooser.getExtensionFilters().add(extFilter);
				
				File file = fileChooser.showOpenDialog(stage);
				
				if(file != null && noteBook.save(file.getPath())) {
					// show alert box
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Successfully saved");
					alert.setContentText("You file has been saved to file " + file.getName());
					alert.showAndWait().ifPresent(rs -> {
						if (rs == ButtonType.OK) {
							System.out.println("Pressed OK.");
						}
					});
				}
			}			
		});
		buttonSave.setPrefSize(100, 20);
		
		Label searchLable = new Label("Search : ");
		searchLable.setPrefSize(50, 20);
		
		TextField textSearch = new TextField();
		textSearch.setPrefSize(200, 20);
		
		Button buttonSearch= new Button("Search");
		buttonSearch.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				currentSearch = textSearch.getText();
				textSearch.setText("");
				for(Folder folder : noteBook.getFolders()) {
					// get the current folder
					if(folder.getName().equals(currentFolder)) {
						// get all the note titles
						List<Note> notes = folder.searchNotes(currentSearch);
						updateListView(notes);
						break;
					}			
				}
			}			
		});
		buttonSearch.setPrefSize(100, 20);
		
		Button buttonClearSearch = new Button("Clear Search");
		buttonClearSearch.setPrefSize(100, 20);
		buttonClearSearch.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				currentSearch = "";
				textSearch.setText("");
				textAreaNote.setText("");
				for(Folder folder : noteBook.getFolders()) {
					// get the current folder
					if(folder.getName().equals(currentFolder)) {
						updateListView(folder.getNotes());
						break;
					}			
				}
			}			
		});
		
		hbox.getChildren().addAll(buttonLoad, buttonSave);
		hbox.getChildren().add(searchLable);
		hbox.getChildren().add(textSearch);
		hbox.getChildren().add(buttonSearch);
		hbox.getChildren().add(buttonClearSearch);

		return hbox;
	}

	/**
	 * this create the section on the left
	 * 
	 * @return
	 */
	private VBox addVBox() {

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10)); // Set all sides to 10
		vbox.setSpacing(8); // Gap between nodes
		
		
//		foldersComboBox.getItems().addAll("FOLDER NAME 1", "FOLDER NAME 2", "FOLDER NAME 3");
		// load folder names
		updateFolderListView();

		foldersComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				if(t1 == null) return;	// to prevent null pointer exception when the list are clear
				currentFolder = t1.toString();	// this contains the name of the folder selected
				// TODO update listview
				for(Folder folder : noteBook.getFolders()) {
					// get the current folder
					if(folder.getName().equals(currentFolder)) {
						updateListView(folder.getNotes());
						break;
					}			
				}			
			}

		});

		titleslistView.setPrefHeight(100);

		titleslistView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				if (t1 == null)
					return;
				String title = t1.toString();
				// This is the selected title
				// TODO load the content of the selected note in
				// textAreNote
				String content = "";
				for(Folder folder : noteBook.getFolders()) {
					// get the current folder
					if(folder.getName().equals(currentFolder)) {
						// get all the note titles
						for(Note note : folder.getNotes()) {
							if(note instanceof TextNote && note.getTitle().equals(title)) {
								content = ((TextNote) note).getContent();
							}
						}
						break;
					}			
				}
				textAreaNote.setText(content);

			}
		});
		vbox.getChildren().add(new Label("Choose folder: "));
		vbox.getChildren().add(foldersComboBox);
		vbox.getChildren().add(new Label("Choose note title"));
		vbox.getChildren().add(titleslistView);

		return vbox;
	}

	/**
	 * Update the folders list shown on the drop-down menu
	 */
	private void updateFolderListView() {
		foldersComboBox.getItems().clear();
		ArrayList<Folder> folders = this.noteBook.getFolders();
		for(Folder folder : folders) {
			String name = folder.getName();
			foldersComboBox.getItems().add(name);
		}
		foldersComboBox.setValue("-----");	// set current selected item to nil
		updateListView(new ArrayList<Note>());	// clear all the item in the list view
	}

	private void updateListView(List<Note> notes) {
		ArrayList<String> list = new ArrayList<String>();

		// TODO populate the list object with all the TextNote titles of the
		// currentFolder
		for(Note note : notes) {
			list.add(note.getTitle());
		}
		ObservableList<String> combox2 = FXCollections.observableArrayList(list);
		titleslistView.setItems(combox2);
		textAreaNote.setText("");
	}

	/*
	 * Creates a grid for the center region with four columns and three rows
	 */
	private GridPane addGridPane() {

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		textAreaNote.setEditable(false);
		textAreaNote.setMaxSize(450, 400);
		textAreaNote.setWrapText(true);
		textAreaNote.setPrefWidth(450);
		textAreaNote.setPrefHeight(400);
		// 0 0 is the position in the grid
		grid.add(textAreaNote, 0, 0);

		return grid;
	}

	private void loadNoteBook() {
		NoteBook nb = new NoteBook();
		nb.createTextNote("COMP3021", "COMP3021 syllabus", "Be able to implement object-oriented concepts in Java.");
		nb.createTextNote("COMP3021", "course information",
				"Introduction to Java Programming. Fundamentals include language syntax, object-oriented programming, inheritance, interface, polymorphism, exception handling, multithreading and lambdas.");
		nb.createTextNote("COMP3021", "Lab requirement",
				"Each lab has 2 credits, 1 for attendence and the other is based the completeness of your lab.");

		nb.createTextNote("Books", "The Throwback Special: A Novel",
				"Here is the absorbing story of twenty-two men who gather every fall to painstakingly reenact what ESPN called “the most shocking play in NFL history” and the Washington Redskins dubbed the “Throwback Special”: the November 1985 play in which the Redskins’ Joe Theismann had his leg horribly broken by Lawrence Taylor of the New York Giants live on Monday Night Football. With wit and great empathy, Chris Bachelder introduces us to Charles, a psychologist whose expertise is in high demand; George, a garrulous public librarian; Fat Michael, envied and despised by the others for being exquisitely fit; Jeff, a recently divorced man who has become a theorist of marriage; and many more. Over the course of a weekend, the men reveal their secret hopes, fears, and passions as they choose roles, spend a long night of the soul preparing for the play, and finally enact their bizarre ritual for what may be the last time. Along the way, mishaps, misunderstandings, and grievances pile up, and the comforting traditions holding the group together threaten to give way. The Throwback Special is a moving and comic tale filled with pitch-perfect observations about manhood, marriage, middle age, and the rituals we all enact as part of being alive.");
		nb.createTextNote("Books", "Another Brooklyn: A Novel",
				"The acclaimed New York Times bestselling and National Book Award–winning author of Brown Girl Dreaming delivers her first adult novel in twenty years. Running into a long-ago friend sets memory from the 1970s in motion for August, transporting her to a time and a place where friendship was everything—until it wasn’t. For August and her girls, sharing confidences as they ambled through neighborhood streets, Brooklyn was a place where they believed that they were beautiful, talented, brilliant—a part of a future that belonged to them. But beneath the hopeful veneer, there was another Brooklyn, a dangerous place where grown men reached for innocent girls in dark hallways, where ghosts haunted the night, where mothers disappeared. A world where madness was just a sunset away and fathers found hope in religion. Like Louise Meriwether’s Daddy Was a Number Runner and Dorothy Allison’s Bastard Out of Carolina, Jacqueline Woodson’s Another Brooklyn heartbreakingly illuminates the formative time when childhood gives way to adulthood—the promise and peril of growing up—and exquisitely renders a powerful, indelible, and fleeting friendship that united four young lives.");

		nb.createTextNote("Holiday", "Vietnam",
				"What I should Bring? When I should go? Ask Romina if she wants to come");
		nb.createTextNote("Holiday", "Los Angeles", "Peter said he wants to go next Agugust");
		nb.createTextNote("Holiday", "Christmas", "Possible destinations : Home, New York or Rome");
		noteBook = nb;

	}
	
	private void loadNoteBook(File file) {
		String path = file.getPath();
//		System.out.println(path);
		noteBook = new NoteBook(path);
//		System.out.println(noteBook.getFolders());	
		updateFolderListView();
		
	}

}
