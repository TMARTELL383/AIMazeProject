import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;
import java.util.function.IntPredicate;

import javax.swing.*;



public class MazeProject
{
	public static ArrayList<Integer> directions = new ArrayList<>();
	public static int[] pathArray;
	public static final int MWIDTH=30,MHEIGHT=30,BLOCK=20;
	public static boolean robotActive=true;
	public static final int SPEED=100;

	//The walls that the robot encounters will be denoted with integers
	public static final int LEFT=4,RIGHT=8,UP=1,DOWN=2;

	//1=wall above, 2=wall below, 4=wall on left, 8=wall on right, 16=not included in maze yet
	//So, we have a 2D array maze
	static int[][] maze;
	static boolean[][] visited = new boolean[MWIDTH][MHEIGHT];
	static MazeComponent mazecomp;

	//current position our robot
	static int robotX=0,robotY=0;

	//true means that a "crumb" is shown in the room
	//This boolean 2d array will put a "crumb" where the robot has already been, if it = true, then a crumb is placed in that spot in the 2D array
	static boolean[][] crumbs;

	public static void main(String[] args)
	{
		//maze a maze array and a crumb array
		maze=new int[MWIDTH][MHEIGHT]; //establish the maze size with a width and height global final static variable
		crumbs=new boolean[MWIDTH][MHEIGHT]; //crumbs is going to be the same size as maze, however, everything at the start is false, and when the robot steps over a
		//section of the maze, it will be turned to true and place a crumb at that location.
		
		//set each room to be surrounded by walls and not part of the maze
		for (int i=0; i<MWIDTH; i++)
			for (int j=0; j<MHEIGHT; j++)
			{
				//setting each element in the maze to be 31??? and each crumb to be false - this makes sense.
				maze[i][j]=31;
				crumbs[i][j]=false;
			}

		//generate the maze
		makeMaze();

		//knock down up to 100 walls
		for(int i=0; i<100; i++)
		{
			int x=(int)(Math.random()*(MWIDTH-2));
			int y=(int)(Math.random()*MHEIGHT);
			if((maze[x][y]&RIGHT)!=0)
			{
				maze[x][y]^=RIGHT;
				maze[x+1][y]^=LEFT;
			}
		}

		JFrame f = new JFrame();
		f.setSize(MWIDTH*BLOCK+15,MHEIGHT*BLOCK+30);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setTitle("Maze!");
		mazecomp=new MazeComponent();
		f.add(mazecomp);
		f.setVisible(true);


		//have the robot wander around in its own thread
		if(robotActive)
        {
		    new Thread(new Runnable(){
			    public void run() {
			    	//different options for running the maze

			    	AStar();
			    	//depthFirstSearch();
				    //doMazeGuided(new int[] {RIGHT,DOWN,DOWN,LEFT,UP, RIGHT, LEFT});
			    	//doMazeRandomWalk();
			    }
		    }).start();
        }
	}

	public static void makeMaze()
	{
		//making two new integer arrays of width size and height size for the maze
		
		int[] blockListX = new int[MWIDTH*MHEIGHT];
		int[] blockListY = new int[MWIDTH*MHEIGHT];
		int blocks=0;
		int x,y;

		//Choose random starting block and add it to maze
		x=(int)(Math.random()*(MWIDTH-2)+1);
		y=(int)(Math.random()*(MHEIGHT-2)+1);
		maze[x][y]^=16;

		//Add all adjacent blocks to blocklist
		if (x>0)
		{
			blockListX[blocks]=x-1;
			blockListY[blocks]=y;
			blocks++;
		}
		if (x<MWIDTH-1)
		{
			blockListX[blocks]=x+1;
			blockListY[blocks]=y;
			blocks++;
		}
		if (y>0)
		{
			blockListX[blocks]=x;
			blockListY[blocks]=y-1;
			blocks++;
		}
		if (y<MHEIGHT-1)
		{
			blockListX[blocks]=x;
			blockListY[blocks]=y+1;
			blocks++;
		}

		//approach:
		// start with a single room in maze and all neighbors of the room in the "blocklist"
		// choose a room that is not yet part of the maze but is adjacent to the maze
		// add it to the maze by breaking a wall
		// put all of its neighbors that aren't in the maze into the "blocklist"
		// repeat until everybody is in the maze
		while (blocks>0)
		{
			//choose a random block from blocklist
			int b = (int)(Math.random()*blocks);

			//find which block in the maze it is adjacent to
			//and remove that wall
			x=blockListX[b];
			y=blockListY[b]; //here we are starting with our 'single room'

			//get a list of all of its neighbors that aren't in the maze
			int[] dir=new int[4];
			int numdir=0;

			//left
			if (x>0 && (maze[x-1][y]&16)==0)
			{
				dir[numdir++]=0;
			}
			//right
			if (x<MWIDTH-1 && (maze[x+1][y]&16)==0)
			{
				dir[numdir++]=1;
			}
			//up
			if (y>0 && (maze[x][y-1]&16)==0)
			{
				dir[numdir++]=2;
			}
			//down
			if (y<MHEIGHT-1 && (maze[x][y+1]&16)==0)
			{
				dir[numdir++]=3;
			}

			//choose one at random
			int d = (int)(Math.random()*numdir);
			d=dir[d];

			//tear down the wall
			//left
			if (d==0)
			{
				maze[x][y]^=LEFT;
				maze[x-1][y]^=RIGHT;
			}
			//right
			else if (d==1)
			{
				maze[x][y]^=RIGHT;
				maze[x+1][y]^=LEFT;
			}
			//up
			else if (d==2)
			{
				maze[x][y]^=UP;
				maze[x][y-1]^=DOWN;
			}
			//down
			else if (d==3)
			{
				maze[x][y]^=DOWN;
				maze[x][y+1]^=UP;
			}

			//set that block as "in the maze"
			maze[x][y]^=16;

			//remove it from the block list
			for (int j=0; j<blocks; j++)
			{
				if ((maze[blockListX[j]][blockListY[j]]&16)==0)
				{
					for (int i=j; i<blocks-1; i++)
					{
						blockListX[i]=blockListX[i+1];
						blockListY[i]=blockListY[i+1];
					}
					blocks--;
					j=0;
				}
			}

			//put all adjacent blocks that aren't in the maze in the block list
			if (x>0 && (maze[x-1][y]&16)>0)
			{
				blockListX[blocks]=x-1;
				blockListY[blocks]=y;
				blocks++;
			}
			if (x<MWIDTH-1 && (maze[x+1][y]&16)>0)
			{
				blockListX[blocks]=x+1;
				blockListY[blocks]=y;
				blocks++;
			}
			if (y>0 && (maze[x][y-1]&16)>0)
			{
				blockListX[blocks]=x;
				blockListY[blocks]=y-1;
				blocks++;
			}
			if (y<MHEIGHT-1 && (maze[x][y+1]&16)>0)
			{
				blockListX[blocks]=x;
				blockListY[blocks]=y+1;
				blocks++;
			}
		}

		//remove top left and bottom right edges
//		maze[0][0]^=LEFT;    //commented out for now so that robot doesn't run out the entrance
		maze[MWIDTH-1][MHEIGHT-1]^=RIGHT;
	}

	/*

	-Have the robot traverse the maze using "A-Star" method using Manhatten distance + Heuristic value
	-Manhatten distance will be the total distance (adding both x and y distance) from the end of the maze - a low manhatten distance is good because it means we are closer to the end
	-Heuristic in this case will simply be how many moves we have made (moveCount)
	-Based on these two values, we will see what path is the best, work backwards using the latestMove variable, then actually move the robot to walk that path

	 */
	public static void AStar() {
		
		int x = robotX;
		int y = robotY;
		
		State first = new State(x,y); //getting the first state
		first.moveCount = 0;
		ArrayList<State> heuristicArray = new ArrayList<>();
		heuristicArray.add(first);
		State current = null;

		while(true) {
			int smallest = heuristicArray.get(0).heuristic;
			int removeIndex = 0;
			for(int i=1; i < heuristicArray.size(); i++) {
				if(heuristicArray.get(i).heuristic < smallest) {
					smallest = heuristicArray.get(i).heuristic;
					removeIndex = i;
				}
			}
			
			current = heuristicArray.remove(removeIndex);
			
			if(current.xCoordinate == MWIDTH - 1 && current.yCoordinate == MHEIGHT - 1) {
				break;
			}
			if(!visited[current.xCoordinate][current.yCoordinate]) {
				visited[current.xCoordinate][current.yCoordinate] = true;
			}
			State neighborLEFT, neighborRIGHT, neighborUP, neighborDOWN; //just initializing.
			if((maze[current.xCoordinate][current.yCoordinate] & LEFT) == 0) {
				neighborLEFT = new State(current.xCoordinate - 1, current.yCoordinate);
				if(!visited[current.xCoordinate - 1][current.yCoordinate]) {
					neighborLEFT.prev = current; //this will be used to traverse backwards through the maze once the optimal path has been found
					neighborLEFT.latestMove = LEFT;
					neighborLEFT.moveCount = current.moveCount + 1;
					heuristicArray.add(neighborLEFT);
				}
			}
			if((maze[current.xCoordinate][current.yCoordinate] & RIGHT) == 0) {
				neighborRIGHT = new State(current.xCoordinate + 1, current.yCoordinate);
				if(!visited[current.xCoordinate + 1][current.yCoordinate]) {
					neighborRIGHT.prev = current;
					neighborRIGHT.latestMove = RIGHT;
					neighborRIGHT.moveCount = current.moveCount + 1;
					heuristicArray.add(neighborRIGHT);
				}
				
			}
			if((maze[current.xCoordinate][current.yCoordinate] & UP) == 0) {
				neighborUP = new State(current.xCoordinate, current.yCoordinate - 1);
				if(!visited[current.xCoordinate][current.yCoordinate - 1]) {
					neighborUP.prev = current;
					neighborUP.latestMove = UP;
					neighborUP.moveCount = current.moveCount + 1;
					heuristicArray.add(neighborUP);
				}
			}
			if((maze[current.xCoordinate][current.yCoordinate] & DOWN) == 0) {
				neighborDOWN = new State(current.xCoordinate, current.yCoordinate + 1);
				if(!visited[current.xCoordinate][current.yCoordinate + 1]) {
					neighborDOWN.prev = current;
					neighborDOWN.latestMove = DOWN;
					neighborDOWN.moveCount = current.moveCount + 1;
					heuristicArray.add(neighborDOWN);
				}
			}
		}
		Stack<State> reverse = new Stack<State>();
		while(current != null) {
			reverse.push(current);
			current = current.prev; //move backwards, make current the new parent
		}

		pathArray = new int[reverse.size()];
		
		int poptimes = reverse.size();
		for(int i=0; i < poptimes; i++) {
			pathArray[i] = reverse.pop().latestMove;
		}
		
		robotX = robotY = 0;
		int totalMoves = -1; //accounts for the starting position 0,0 -- I don't count that as a move.
		for(int i=0; i < pathArray.length; i++) {
			x = robotX;
			y = robotY;
			
			if(pathArray[i] == LEFT) {
				robotX--;
			}
			else if(pathArray[i] == RIGHT) {
				robotX++;
			}
			else if(pathArray[i] == UP) {
				robotY--;
			}
			else if(pathArray[i] == DOWN) {
				robotY++;
			}
			totalMoves++;
			crumbs[robotX][robotY] = true;
			
			//repaint and pause momentarily
			mazecomp.repaint();
			//This try, catch pauses the robot momentarily, probably so it doesn't move at blazingly fast speeds.
			try{ Thread.sleep(SPEED); 
			} 
			catch(Exception e) { }
		}
		System.out.println("Total Moves: "+totalMoves);
		System.out.println("Done");
	}

	/*
	-Robot traversing method based on depth first search algorithm
	-The robot starts on the first square, then looks at all its neighbors, then those neighbors look at it's neighbors and so on
	-If we search within a set of neighbors and reach a dead end, we travel back to the "start" of that neighbor tree
	-Eventually, we reach the end of the maze after searching through enough neighbors
	-However, this is not the optimal search algorithm for traversing this maze as quickly as possible because we might end up traversing down a long path that takes us nowhere
	-This can happen multiple times and the robot has the potential to traverse down every path before reaching the end of the maze
	-We want as little "squares" visited as possible
	 */
	public static void depthFirstSearch() {
		
		int x = robotX;
		int y = robotY;
		
		StateStack stack = new StateStack(); //create a new stack
		State first = new State(x,y); //getting the first state
		stack.push(first); //push our FIRST state to the stack
		State current = null;
		while(!stack.isEmpty()) {
			//pop off the states that are visited already
			current = stack.pop();
			
			if(current.xCoordinate == MWIDTH - 1 && current.yCoordinate == MHEIGHT - 1) {
				break;
			}
			
			if(!visited[current.xCoordinate][current.yCoordinate]) {
				visited[current.xCoordinate][current.yCoordinate] = true;
			}

			
			State neighborLEFT = null; //just initializing.
			State neighborRIGHT = null;
			State neighborUP = null;
			State neighborDOWN = null;
			if((maze[current.xCoordinate][current.yCoordinate] & LEFT) == 0) {
				neighborLEFT = new State(current.xCoordinate - 1, current.yCoordinate);
				if(!visited[current.xCoordinate - 1][current.yCoordinate]) {
					neighborLEFT.prev = current; //this will be used to traverse backwards through the maze once the optimal path has been found
					neighborLEFT.latestMove = LEFT;
					stack.push(neighborLEFT);
				}

			}
			if((maze[current.xCoordinate][current.yCoordinate] & RIGHT) == 0) {
				neighborRIGHT = new State(current.xCoordinate + 1, current.yCoordinate);
				if(!visited[current.xCoordinate + 1][current.yCoordinate]) {
					neighborRIGHT.prev = current;
					neighborRIGHT.latestMove = RIGHT;
					stack.push(neighborRIGHT);
				}

			}
			if((maze[current.xCoordinate][current.yCoordinate] & UP) == 0) {
				neighborUP = new State(current.xCoordinate, current.yCoordinate - 1);
				if(!visited[current.xCoordinate][current.yCoordinate - 1]) {
					neighborUP.prev = current;
					neighborUP.latestMove = UP;
					stack.push(neighborUP);
				}

			}
			if((maze[current.xCoordinate][current.yCoordinate] & DOWN) == 0) {
				neighborDOWN = new State(current.xCoordinate, current.yCoordinate + 1);
				if(!visited[current.xCoordinate][current.yCoordinate + 1]) {
					neighborDOWN.prev = current;
					neighborDOWN.latestMove = DOWN;
					stack.push(neighborDOWN);
				}
			}
		}
		
		Stack<State> reverse = new Stack<State>();
		while(current != null) {
			reverse.push(current);
			current = current.prev; //move backwards, make current the new parent
		}
		
		pathArray = new int[reverse.size()];
		
		int poptimes = reverse.size();
		for(int i=0; i < poptimes; i++) {
			pathArray[i] = reverse.pop().latestMove;
		}
		
		robotX = robotY = 0;
		int totalMoves = 0;
		for(int i=0; i < pathArray.length; i++) {
			x = robotX;
			y = robotY;
		
			if(pathArray[i] == LEFT) {
				robotX--;
			}
			else if(pathArray[i] == RIGHT) {
				robotX++;
			}
			else if(pathArray[i] == UP) {
				robotY--;
			}
			else if(pathArray[i] == DOWN) {
				robotY++;
			}
			totalMoves++;
			crumbs[robotX][robotY] = true;
			
			//repaint and pause momentarily
			mazecomp.repaint();
			//This try, catch pauses the robot momentarily, probably so it doesn't move at blazingly fast speeds.
			try{ Thread.sleep(SPEED); 
			} 
			catch(Exception e) { }
		}
		System.out.println("Total Moves: "+totalMoves);
		System.out.println("Done");
	}

	/*
	-First AI function written for this project - this just has the robot follow a series of specific pre-defined directions
	-The programmer is telling the A.I. the path to take rather than the A.I. figuring it out itself
	 */
	public static void doMazeGuided(int[] directions) {
		
		while(robotX != MWIDTH - 1 || robotY != MHEIGHT - 1) {

			for(int i=0; i < directions.length; i++) {
				int x = robotX;
				int y = robotY;
				//checks if a wall is there, if we get a 1, there is a wall and we will skip to the next direction.
				if((maze[x][y] & directions[i]) == 0) {
					if(directions[i] == LEFT) {
						robotX--;
					}
					else if(directions[i] == RIGHT) {
						robotX++;
					}
					else if(directions[i] == UP) {
						robotY--;
					}
					else if(directions[i] == DOWN) {
						robotY++;
					}
					
				}
				crumbs[x][y] = true;
				 
			}
			//repaint and pause momentarily
			mazecomp.repaint();
			//This try, catch pauses the robot momentarily, probably so it doesn't move at blazingly fast speeds.
			try{ Thread.sleep(SPEED); 
			} 
			catch(Exception e) { }
				
			
	}
	System.out.println("Done");
	}

	/*
	Has the robot walk in completely random directions
	 */
	public static void doMazeRandomWalk()
	{
		int dir=RIGHT;

		while(robotX!=MWIDTH-1 || robotY!=MHEIGHT-1)
		{
			int x=robotX;
			int y=robotY;

			dir=new int[]{LEFT,RIGHT,UP,DOWN}[(int)(Math.random()*4)];

			if((maze[x][y] & dir) == 0) //performs an and function across every bit in maze
			{
				if(dir==LEFT) robotX--;
				if(dir==RIGHT) robotX++;
				if(dir==UP) robotY--;
				if(dir==DOWN) robotY++;
			}

			//leave a crumb at the maze location you just moved to.
			crumbs[x][y]=true;

			//repaint and pause momentarily
			mazecomp.repaint();
			//This try, catch pauses the robot momentarily, probably so it doesn't move at blazingly fast speeds.
			try{ Thread.sleep(SPEED); } catch(Exception e) { }
		}
		System.out.println("Done");
	} 


	public static class MazeComponent extends JComponent
	{
		public void paintComponent(Graphics g)
		{
			g.setColor(Color.WHITE);
			g.fillRect(0,0,MWIDTH*BLOCK,MHEIGHT*BLOCK);
			g.setColor(new Color(100,0,0));
			for (int x=0; x<MWIDTH; x++)
			{
				for (int y=0; y<MHEIGHT; y++)
				{
					if ((maze[x][y]&1)>0)
						g.drawLine(x*BLOCK,y*BLOCK,x*BLOCK+BLOCK,y*BLOCK);
					if ((maze[x][y]&2)>0)
						g.drawLine(x*BLOCK,y*BLOCK+BLOCK,x*BLOCK+BLOCK,y*BLOCK+BLOCK);
					if ((maze[x][y]&4)>0)
						g.drawLine(x*BLOCK,y*BLOCK,x*BLOCK,y*BLOCK+BLOCK);
					if ((maze[x][y]&8)>0)
						g.drawLine(x*BLOCK+BLOCK,y*BLOCK,x*BLOCK+BLOCK,y*BLOCK+BLOCK);
				}
			}

			if (robotActive)
			{
				g.setColor(Color.BLUE);
				for (int x=0; x<MWIDTH; x++)
				{
					for (int y=0; y<MHEIGHT; y++)
					{
						if (crumbs[x][y])
							g.fillRect(x*BLOCK+BLOCK/2-1,y*BLOCK+BLOCK/2-1,2,2);
					}
				}

				g.setColor(Color.GREEN);
				g.fillOval(robotX*BLOCK+1,robotY*BLOCK+1,BLOCK-2,BLOCK-2);
			}
		}
	}
}
