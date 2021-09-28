//Ali Khan and Owen Morris

package songlib.app;

public class Song {
	
	private String song;
	private String artist;
	private String album;
	private int year;
	
	public Song(String song, String artist) {
		this(song, artist, null, -1);
	}

	public Song(String song, String artist, String album) {
		this(song, artist, album, -1);
	}
	
	public Song(String song, String artist, String album, int year) {
		this.song = song;
		this.artist = artist;
		this.album = album;
		
		this.year = year;
	}
	
	public Song(String song, String artist, int year) {
		this(song, artist, null, year);
	}
	
	public String getSong() {
		return song;
	}
	
	public String getAlbum() {
		return album;
	}
	
	public int getYear() {
		return year;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public void setYear(String year) {
		this.year = Integer.parseInt(year);
	}
	
	public String toString() {
		return song + "  " + artist +"  " + album + "  " + year;
	}
}
