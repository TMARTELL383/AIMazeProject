/*
A "state" here is defined to be the position of the robot in the maze (the "square").
There is an x and y coordinate, and previous/next variables to track the state the robot either came from or is going to next
Additionally, there is a latestMove variable - this helps us keep a list of moves the robot makes, which is necessary for search methods like depth-first and A-Star
 */

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
