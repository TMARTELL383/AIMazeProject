public class State { //my node class
	
	public int xCoordinate;
	public int yCoordinate;
	public State prev;
	public State next;
	public int manhattenX;
	public int manhattenY;
	public int totalManhatten;
	public int heuristic;
	public int latestMove;
	public int moveCount;

	
	public State(int x, int y) {
		xCoordinate = x;
		yCoordinate = y;
		prev = null;
		next = null;
		manhattenX = MazeProject.MWIDTH - x;
		manhattenY = MazeProject.MHEIGHT - y;
		totalManhatten = manhattenX + manhattenY;
		heuristic = totalManhatten + moveCount;
	} 



}
