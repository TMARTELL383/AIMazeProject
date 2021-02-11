-----MAZE A.I.-----

-----By: Tyler Martell-----

-----README.txt-----

-----What I have done-----

I have completed all the steps.


-----Introduction-----

This project consists of having a green A.I. ball use various algorithms such as Depth First Search to traverse through a maze.


-----How it Works-----

A random maze is generated and the A.I. is placed at the start of the maze at the top left corner of the screen. Certain functions will be called in the main method to guide the A.I. 
Those functions include AStar(), depthFirstSearch(), doMazeGuided(int[] directions), and doMazeRandomWalk(). Each function will have the A.I. traverse differently and some are more
effective than others.

-----Details of each function-----

main(String[] args):

	Calls makeMaze() to generate a random maze, creates a window with that maze, and runs the appropriate A.I. method for traversing the maze (AStar, depthFirstSearch, doMazeGuided, or 
	doMazeRandomWalk).

makeMaze() and paintComponent(Graphics g):

	Given functions that create and display a randomized maze to the JFrame window.

doMazeRandomWalk():

	Has the A.I. move in completely random directions if that particular direction is not a wall. Bit code is used to determine if the location in the maze (maze[x][y]) and the 
	direction the computer wants to move in are compatible with each other (maze[x][y] & direction I want to move in) = 0). In other words, we don't want to run into a wall. If the
	direction is open and free to travel to, the A.I. moves accordingly.

doMazeGuided(int[] directions):

	Taking in an array of directions to travel, this function (much like doMazeRandomWalk()) will determine if each direction in this particular set of directions is viable. If the
	direction is viable, then the robot will make the move. If not, the robot will stay where it is and go to the next move.

depthFirstSearch():

	This method performs a depth first search algorithm. It starts by pushing the first location of the A.I. in the maze (first state) and pushes it to a stack. The first element of the
	stack is then popped and each neighbor of that state (right,left,down,up) is looked at. If this neighbor state is a viable direction the A.I. can move in, the neighbor is added to
	the stack. Once the stack is empty, we should be at the end of the maze. At this point, a new stack is made and states are pushed to this stack, moving backwards to the parent
	state of each current state (current = current.prev). The moves made by the A.I. inside this new stack are then put into a directions array that will be used to help it traverse
	the maze and physically move.

AStar():

	This method follows the same structure as depthFirstSearch, except with a heuristic. The heuristic is the manhatten distance + the amount of moves done so far. The manhatten 
	distance is the distance from the end of the maze to the current position. This was calculated by computing the manhatten distance for the X-coordinate, the manhatten distance 
	for the Y-coordinate, and adding them both together. The manhatten distance for X takes the X coordinate at the end of the maze and subtracts it by the X-coordinate of where the
	A.I. currently is (MWIDTH - x). The same principle applies for Y. AStar() takes the neighbor/direction with the smallest heuristic (closest distance to the end of the maze) and 
	uses that as its next move.

State class:

	This class stores the state of a location in the maze. Each state must store certain information such as x and y coordinates, previous (parent) and next states, latestMove, the
	manhatten distance for x and y, and a moveCount (counts how many moves it took to get to this location). These variables are used when constructing depthFirstSearch() and AStar().
