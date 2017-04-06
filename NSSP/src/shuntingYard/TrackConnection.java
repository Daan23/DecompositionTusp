package shuntingYard;

public class TrackConnection {
	private Side source;
	private Side sink;
	
	public TrackConnection(Side source, Side sink) {
		this.source = source; 
		this.sink = sink; 
	}
	
	public Side getSource() {
		return this.source;
	}
	public Side getSink() {
		return this.sink;
	}
	
	public String toString() {
		return this.source.toString() + " --> " + this.sink.toString(); 
	}
	
	public boolean isConnectedToSameTrack() {
		return this.source.getTrack().equals(this.sink.getTrack()); 
	}
}
