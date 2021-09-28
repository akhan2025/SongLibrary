//Ali Khan and Owen Morris

package songlib.view;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import songlib.app.Song;
import javafx.scene.control.ListView;
import java.io.BufferedReader;  
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SongLibController {

	@FXML Button editSong;
	@FXML Button deleteSong;
	@FXML Button addSong;
	@FXML TextField enterSong;
	@FXML TextField enterArtist;
	@FXML TextField enterYear;
	@FXML TextField enterAlbum;
	@FXML TextField showSong;
	@FXML TextField showArtist;
	@FXML TextField showYear;
	@FXML TextField showAlbum;
	@FXML ListView<String> listView;
	
	private ArrayList<Song> songinfo = new ArrayList<Song>();
	
	public void start(Stage mainStage) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader("src/songNames.csv"));  
		String line = "";
		
		
		while ((line = br.readLine()) != null) {
			if(line.isEmpty()) {
				break;
			}
			String[] lines = line.split("  ");
			if(lines.length == 2) {
				Song temp = new Song(lines[0], lines[1]);
				songinfo.add(temp);
			}
			else if(lines.length == 4){
				int temp_year = Integer.parseInt(lines[3]);
				Song temp = new Song(lines[0], lines[1], lines[2], temp_year);
				songinfo.add(temp);
			}
			else {
					if (lines[2].matches("-?\\d+")) {
						Song temp = new Song(lines[0], lines[1], Integer.parseInt(lines[2]));
						songinfo.add(temp);
					}
					else {
						Song temp = new Song(lines[0], lines[1], lines[2]);
						songinfo.add(temp);
					}
			}
		}
		
		br.close();
		
		ArrayList<String> tempList = new ArrayList<String>();
		for(int i = 0; i < songinfo.size();i++) {
			tempList.add(songinfo.get(i).getSong());
		}
		ObservableList<String> songList = FXCollections.observableArrayList(tempList);
		
		listView.setItems(songList);
		if(songList.size() > 0) {
			alphabetSort();
			listView.getSelectionModel().select(0);
			String song = songinfo.get(0).getSong();
			showSong.setText(song);
			showArtist.setText(songinfo.get(0).getArtist());
			showAlbum.setText(songinfo.get(0).getAlbum());
			if(songinfo.get(0).getYear() > 0) {
				showYear.setText(Integer.toString(songinfo.get(0).getYear()));
			}
			else {
				showYear.setText(null);
			}
		}
		
		listView.getSelectionModel().selectedIndexProperty().addListener(
				(obs, oldVal, newVal) -> 
				showSong());
	}
	
	
	public void alphabetSort() {
		System.out.println("Sorting");
		
		for ( int j=0; j < songinfo.size()-1; j++ )
	    {
	      int min = j;
	      for ( int k=j+1; k < songinfo.size(); k++ ) {
	        if ( songinfo.get(k).getSong().toLowerCase().compareTo( songinfo.get(min).getSong().toLowerCase() ) < 0 ) {
	        	min = k;  
	        }
	      }
	      Song temp = songinfo.get(j);
	      songinfo.set(j, songinfo.get(min));
	      songinfo.set(min, temp);
	    }
	      artistSort();
		
		ArrayList<String> tempList = new ArrayList<String>();
		for(int i = 0; i < songinfo.size();i++) {
			tempList.add(songinfo.get(i).getSong());
		}
		ObservableList<String> songList = FXCollections.observableArrayList(tempList);
		System.out.println("updating list");
		listView.setItems(songList);
		System.out.println("Sorted");
	}
		
	
	public void artistSort() {
		System.out.println("sorting artists");
		for ( int j=0; j < songinfo.size()-1; j++ )
	    {
	      int min = j;
	      for ( int k=j+1; k < songinfo.size(); k++ ) {
	        if ( songinfo.get(k).getSong().toLowerCase().compareTo( songinfo.get(min).getSong().toLowerCase() ) == 0 ) {
	        	if(songinfo.get(k).getArtist().toLowerCase().compareTo(songinfo.get(min).getArtist().toLowerCase()) < 0) {
	        		min=k;
	        	}
	        }
	    }
	      Song temp = songinfo.get(j);
	      songinfo.set(j, songinfo.get(min));
	      songinfo.set(min, temp);
		}
		System.out.println("sorted artists");
	}
	
	
	public boolean addSong(ActionEvent e) throws IOException {
		Button b = (Button)e.getSource();
		int index = listView.getSelectionModel().getSelectedIndex();
		if(b == addSong) {
			if(actionWarning() == false) {
				return false;
			}
			System.out.println("Adding");
			String song = enterSong.getText().trim();
			String artist = enterArtist.getText().trim();
			String album = enterAlbum.getText().trim();
			String temp_year = enterYear.getText();
			int year = -1;
			
			
			if(!temp_year.isEmpty()){
				try {
					year = Integer.parseInt(temp_year);
				}
				catch(NumberFormatException n) {
					incorrectInfoError();
					return false;
				}
			}
			
			
			if( song.isEmpty() || artist.isEmpty()) {
				missingInfoError();
				return false;
			}
			
			
			else {
				for(int i = 0; i < songinfo.size();i++) {
					if(songinfo.get(i).getSong().equals(song.strip()) && songinfo.get(i).getArtist().equals(artist.strip())) {
						duplicateSongError();
						return false;
					}
				}
				if(album == null && year < 0) {
					songinfo.add(new Song(song, artist));
				}
				else if(album != null && year < 0) {
					songinfo.add(new Song(song, artist, album));
				}
				else if(album == null && year > 0) {
					songinfo.add(new Song(song, artist, year));
				}
				else {
					songinfo.add(new Song(song, artist, album, year));
				}
				
			}
			alphabetSort();
			index = 0;
			for(int i = 0; i < songinfo.size();i++) {
				if(songinfo.get(i).getSong().equals(song) && songinfo.get(i).getArtist().equals(artist)) {
					index = i;
					break;
				}
			}
			
			enterSong.clear();
			enterArtist.clear();
			enterAlbum.clear();
			enterYear.clear();
			
			PrintWriter writer = new PrintWriter(new FileWriter("src/songNames.csv", true));
			writer.println(song + "  " + artist + "  " + album + "  " + year); 
			writer.close();
			}
		System.out.println("new index");
		listView.getSelectionModel().select(index);
		System.out.println("Added");
		return true;
	}
	
	public boolean editSong(ActionEvent e) throws IOException {
		Button b = (Button)e.getSource();
		if(b == editSong) {
			if(listView.getItems().size() > 0) {
				
				if(actionWarning() == false) {
					showSong();
					return false;
				}
				System.out.println("Editing");
				int index = listView.getSelectionModel().getSelectedIndex();
				String song = showSong.getText();
				String artist = showArtist.getText();
				String album = showAlbum.getText();
				String temp_year = showYear.getText();
				ArrayList<Song> tempList = new ArrayList<Song>(songinfo);
				tempList.remove(index);
				int year = -1;
				if(temp_year != null){
					try {
						year = Integer.parseInt(temp_year);
					}
					catch(NumberFormatException n) {
						incorrectInfoError();
						return false;
					}
				}
				
				if( song.isEmpty() || artist.isEmpty()) {
					missingInfoError();
					return false;
				}
				else {
					
					for(int i = 0; i < tempList.size();i++) {
						if(tempList.get(i).getSong().equals(song.strip()) && tempList.get(i).getArtist().equals(artist.strip())) {
							duplicateSongError();
							return false;
						}}
					song = song.strip();
					artist = artist.strip();
					album = album.strip();
					if(album == null && year < 0) {
						Song temp = new Song(song, artist);
						songinfo.set(index, temp);
						updateCSV();
					}
					else if(album != null && year < 0) {
						Song temp = new Song(song, artist, album);
						songinfo.set(index, temp);
						updateCSV();
					}
					else if(album == null && year > 0) {
						Song temp = new Song(song, artist, year);
						songinfo.set(index, temp);
						updateCSV();
					}
					else {
						Song temp = new Song(song, artist, album, year);
						songinfo.set(index, temp);
						updateCSV();
					}
					
				}
				alphabetSort();
				System.out.println("new index for old song");
				for(int i = 0; i < songinfo.size();i++) {
					if(songinfo.get(i).getSong().equals(song) && songinfo.get(i).getArtist().equals(artist)) {
						index = i;
						break;
					}
				}
				listView.getSelectionModel().select(index);
				System.out.println("Edited");
				return true;
			}
			
			else {
				nothingToEdit();
				return false;
			}
			}
		return false;
	}
	
	public boolean deleteSong(ActionEvent e) throws IOException {
		Button b = (Button)e.getSource();
		if(b == deleteSong) {
			System.out.println("Deleting Song");
			if(listView.getItems().size() > 0) {
				if(actionWarning() == false) {
					return false;
				}
			int index = listView.getSelectionModel().getSelectedIndex();
			
			showSong.clear();
			showArtist.clear();
			showAlbum.clear();
			showYear.clear();
			
			listView.getItems().remove(index);
			listView.getSelectionModel().select(index);
			songinfo.remove(index);
			updateCSV();
			System.out.println("Deleted Song");
			}
			else {
				System.out.println("Nothing to delete");
				nothingToDelete();
				return false;
			}
		}
		
		return true;
	}
	
	public void showSong() {
		System.out.println("Showing");
		int index = listView.getSelectionModel().getSelectedIndex();
		if(index>= 0) {
		showSong.setText(songinfo.get(index).getSong());
		showAlbum.setText(songinfo.get(index).getAlbum());
		if(songinfo.get(index).getYear() > 0) {
			showYear.setText(Integer.toString(songinfo.get(index).getYear()));
		}
		else {
			showYear.setText(null);
		}
		showArtist.setText(songinfo.get(index).getArtist());
		System.out.println("Shown");
		}
		else {
			System.out.println("FAILED TO SHOW");
			if(songinfo.size() == 0) {
				showSong.clear();
				showArtist.clear();
				showAlbum.clear();
				showYear.clear();
			}
		}
	}
	
	public void updateCSV() throws IOException {
		System.out.println("updating csv");
		PrintWriter writer = new PrintWriter(new FileWriter("src/songNames.csv"));
		for(int i = 0; i < songinfo.size();i++) {
			
			writer.println(songinfo.get(i).toString());
		}
		writer.close();
		System.out.println("updated csv");
	}
	
	public void missingInfoError() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Missing Information");
		alert.setContentText("You are trying to create a song without a name or artist");
		alert.show();
	}

	public void duplicateSongError() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("duplicate song");
		alert.setContentText("You are trying to create a song that already exists");
		alert.show();
	}
	
	public void incorrectInfoError() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Missing Information");
		alert.setContentText("You are trying to create a song with incorrect values");
		alert.show();
	}
	
	public void nothingToDelete() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Nothing to Delete");
		alert.setContentText("You are trying to delete from an empty library. Please add songs before trying to delete");
		alert.show();
	}
	
	public void nothingToEdit() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Nothing to Edit");
		alert.setContentText("You are trying to Edit from an empty library. Please add songs before trying to Edit");
		alert.show();
	}
	public boolean actionWarning() {
		Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Would You Like To Save Your Console Output?");
        alert.setContentText("Please choose an option.");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == yesButton)
        {
            return true;
        }
        else
        {
            return false;
        }
	}
}

