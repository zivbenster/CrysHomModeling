package alignment;

public interface ScoringScheme {

	double score(Position pos1, Position pos2);
	
}