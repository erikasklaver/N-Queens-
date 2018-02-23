import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class NQueensClass {
	
	private static final int MAX_STEPS = 500;
	private static final int N = 300; 
	private int[][] board = new int[N][N];
	private int[] queenRows = new int[N];

	//use these booleans to choose an algorithm and turn on/off smart_start and first_better
	private boolean smartStart = false;
	private boolean firstBetter = false;
	private boolean greedy = false;
	private boolean basic = true;
	private boolean rando = false;
	
	Random random = new Random ();
	
	public NQueensClass(){

		long startTime = System.currentTimeMillis();
		if (smartStart){
			smartInitializeBoard();
		}
		else {
			initializeBoard();
		}

		if (greedy) {
			int [][] solvedBoard = minConflictsGreedy(board, MAX_STEPS);
		}
		else if (basic) {
			int [][] solvedBoard = minConflictsBasic(board, MAX_STEPS);
		}
		else if (rando) {
			int [][] solvedBoard = minConflictsRandom(board, MAX_STEPS);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Total execution time in milliseconds: " + (endTime - startTime));
		
	}
	
	//initializes a random board with N queens 
	public void initializeBoard( ) {
		
		//initializes the board with all 0's
		for (int row = 0; row<N; row++){
			for (int col = 0; col<N; col++){
				board[row][col]=0;
			}
		}
		
		//randomly places a one (a queen) in every column
		for (int col = 0; col<N; col++){
			int randomRow = random.nextInt(N);
			board[randomRow][col]=1;
			queenRows[col]=randomRow; 
		}

	}

	//intelligently initializes a board with N queens
	public void smartInitializeBoard(){

		//initializes the board with all 0's
		for (int row = 0; row<N; row++){
			for (int col = 0; col<N; col++){
				board[row][col]=0;
			}
		}

		//places each queen in the row with the fewest number of conflicts
		for (int col = 0; col < N; col++){
			int minRow = findMinConRow(col, board);
			board[minRow][col] = 1;
			queenRows[col] = minRow;
		}

	}
	
	//prints out the board
	public void printBoard(int[][] boardToPrint){
		
		for (int row = 0; row<N; row++){
			for (int col = 0; col<N; col++){
				int n = boardToPrint[row][col];
				if (n == 1){
					System.out.print(n + " ");
				}
				else {
					System.out.print("_ ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	//finds the number of conflicts for a queen in a given row and column
	public int findConflicts(int row, int col, int[][] currBoard){
		int conflicts = 0; 

		//check for other queens in that row
		for (int i=0; i<N; i++){
			if (currBoard[row][i]==1 && i!=col){
				conflicts++;
			}
		}

		//check for other queens in that column
		for (int i=0; i<N; i++){
			if (currBoard[i][col]==1 && i!=row){
				conflicts++;
			}
		}

		//check for other queens in the SE diagonal 
		for (int i=0; (row+i)<N && (col+i)<N; i++){
				if (currBoard[row+i][col+i] == 1 && i!=0){
					conflicts++;
				}
		}
		
		//check for other queens in the SW diagonal
		for (int i=0; (row+i)<N && (col-i) >= 0; i++){
			if (currBoard[row+i][col-i] == 1 && i!=0){
				conflicts++;
			}
		}	
		
		//check for other queens in the NW diagonal
		for (int i=0; (row-i) >= 0 && (col-i) >= 0; i++){
			if (currBoard[row-i][col-i] == 1 && i!=0){
					conflicts++;
				}
			}
	
		//check for other queens in the NE diagonal
		for (int i=0; (row-i) >= 0 && (col+i) < N; i++){
			if (currBoard[row-i][col+i] == 1 && i!=0){
					conflicts++;
				}
			}				

		return conflicts; 
	
	}

    //solves the board by placing random queens in the rows with the fewest number of conflicts
	public int[][] minConflictsBasic(int[][] currBoard, int maxSteps){
		
		//maxes out after a certain number of steps
		for (int i=0; i<maxSteps; i++){
			
			int minConRow = 0;
			//if the board is already solved, stop
			if (validBoard(currBoard)){
				System.out.println("FINISHED AT STEP " + i);
				return currBoard; 
			}

			//ArrayList will hold all conflicted queens
			List<Integer> conflictCols = new ArrayList<Integer>();
			for (int c = 0; c<N; c++){
				int queen = queenRows[c];
				if (findConflicts(queen, c, currBoard) != 0){
					conflictCols.add(c);
				}
			}

			//choose a random queen from the conflicted ones
			int size = conflictCols.size();
			int randIndex = random.nextInt(size);
			int randomQueenCol = conflictCols.get(randIndex);
			int randomQueenRow = queenRows[randomQueenCol];

			//choose a move using first_better if it is turned on
			if (firstBetter){
				minConRow = firstBetter(randomQueenRow, randomQueenCol, currBoard);
			}
			else {
				//find the row with the fewest conflicts and move the queen there
				minConRow = findMinConRow(randomQueenCol, currBoard);
			}
			
			//change the board to reflect the new placement
			currBoard[randomQueenRow][randomQueenCol]=0;
			currBoard[minConRow][randomQueenCol]=1;
			queenRows[randomQueenCol] = minConRow;
			
		}
		return currBoard;
	
	}

    //applies the min conflicts algorithm in a greedy way
    public int[][] minConflictsGreedy(int [][] currBoard, int maxSteps){
        
        int minRow = 0;
        int minCol = 0;
        int minConRow = 0;
        int[] conflictArray = new int[N];

        //maxes out after a certain number of steps
        for (int i=0; i<maxSteps; i++){

        	//if the board is already solved, stop
        	if (validBoard(currBoard)){
        		System.out.println("FINISHED AT STEP " + i);
        		return currBoard;
        	}
     		
     		int minConflicts = Integer.MAX_VALUE;
        	List<Integer> minConflictCols = new ArrayList<Integer>();

     		//find the move that results in the fewest number of conflicts given every conflicted queen
        	for (int c=0; c<N; c++){
        		int queen = queenRows[c];
        		if (findConflicts(queen, c, currBoard) != 0){
        			//use first_better to determine the best move
        			if (firstBetter){
        				minConRow = firstBetter(queen, c, currBoard);
        			}
        			//find the row with the fewest number of conflicts
        			else {
        				minConRow = findMinConRow(c, currBoard);
        			}
        			//save the min number of conflicts for that move and compare it to previous moves
        			int numConflicts = findConflicts(minConRow, c, currBoard);
        			conflictArray[c] = numConflicts;
        			//if it's smaller than the previous min, reassign minConflicts
        			if (numConflicts < minConflicts){
        				minConflicts = numConflicts;
        				minRow = minConRow;
        				minCol = c;
        			}
        		}
        		//if the queen is not conflicted, add a placeholder in the array
        		else {
        			conflictArray[c] = Integer.MAX_VALUE;
        		}
        	}

        	//add all of the moves with the same number of conflicts to an ArrayList
        	for (int j=0; j < N; j++){
				if (conflictArray[j] == minConflicts){
					minConflictCols.add(j);
				}
			}

			int size = minConflictCols.size();

			//choose a queen at random from the ArrayList and place it in the row with the fewest number of conflicts
			if (size != 1){
				int randIndex = random.nextInt(size);
				minCol = minConflictCols.get(randIndex);
				minRow = findMinConRow(minCol, currBoard);
			}
			
			//change the board to reflect the new placement
        	currBoard[queenRows[minCol]][minCol] = 0;
        	currBoard[minRow][minCol] = 1;
        	queenRows[minCol] = minRow;

        }
        return currBoard;
    }
	
	
	//applies the min conflict algorithm in a more random way
	public int[][] minConflictsRandom(int[][] currBoard, int maxSteps){
		
		//maxes out after a certain number of steps
		for (int i=0; i<maxSteps; i++){
			
			int minConRow = 0;
			//if the board is already solved, stop
			if (validBoard(currBoard)){
				System.out.println("FINISHED AT STEP " + i);
				return currBoard; 
			}

			//ArrayList will hold all conflicted queens
			List<Integer> conflictCols = new ArrayList<Integer>();
			for (int c = 0; c<N; c++){
				int queen = queenRows[c];
				if (findConflicts(queen, c, currBoard) != 0){
					conflictCols.add(c);
				}
			}

			//choose a random queen from the conflicted ones
			int size = conflictCols.size();
			int randIndex = random.nextInt(size);
			int randomQueenCol = conflictCols.get(randIndex);
			int randomQueenRow = queenRows[randomQueenCol];

			double rand = Math.random();

			//move the queen to a random row with a probability of 0.3
			if (rand < 0.3){

				int randRow = random.nextInt(N);
				currBoard[randomQueenRow][randomQueenCol] = 0;
				currBoard[randRow][randomQueenCol] = 1;
				queenRows[randomQueenCol] = randRow;
			}

			else {
				//choose a move using first_better if it is turned on
				if (firstBetter){
					minConRow = firstBetter(randomQueenRow, randomQueenCol, currBoard);
				}
				else {
				//find the row with the fewest conflicts and move the queen there
					minConRow = findMinConRow(randomQueenCol, currBoard);
				}
				
				//change the board to reflect this placement
				currBoard[randomQueenRow][randomQueenCol]=0;
				currBoard[minConRow][randomQueenCol]=1;
				queenRows[randomQueenCol] = minConRow;

			}
			
		}
		return currBoard;
	
	}

	//given a queen, choose the first space in the column that gives it fewer conflicts
	public int firstBetter(int row, int col, int[][] currBoard){

		List<Integer> sameConflicts = new ArrayList<Integer>();

		//find the current number of conflicts for that queen
		int numConflicts = findConflicts(row, col, currBoard);
		currBoard[row][col] = 0;

		for (int r = 0; r < N; r++){
			//move through each row and return if that row has fewer conflicts
			if (findConflicts(r, col, currBoard) < numConflicts){
				return r;
			}
			//save the rows with the same number of conflicts as the current placement
			else if (findConflicts(r, col, currBoard) == numConflicts){
				sameConflicts.add(r);
			}
		}
		currBoard[row][col] = 1;

		//if there is nothing below or equal, return the current row
		int size = sameConflicts.size();
		if (size == 1){
			return row;
		}
		//if there are more rows with the same amount of current conflicts, choose from them randomly
		else {
			int randIndex = random.nextInt(size);
			return sameConflicts.get(randIndex);
		}

	}

	//finds the row within the minimum number of conflicts given a column
	public int findMinConRow(int col, int[][] currBoard){
		
		int minConflicts=Integer.MAX_VALUE;
		int minConRow=0; 
		int[] conflictArray = new int[N];
		List<Integer> minConflictRows = new ArrayList<Integer>();

		//sets the queen in the column to zero to not overcount conflicts
		int queenRow = queenRows[col];
		currBoard[queenRow][col] = 0;

		//finds the number of conflicts for each row and saves the mininum 
		for (int r=0; r<N; r++){
			int conflicts = findConflicts(r, col, currBoard);
			conflictArray[r] = conflicts;
			if (conflicts < minConflicts){
				minConflicts = conflicts;
				minConRow = r;
			}
		}

		//adds all of the rows with the same number of conflicts to an ArrayList
		for (int i=0; i < N; i++){
			if (conflictArray[i] == minConflicts){
				minConflictRows.add(i);
			}
		}

		int size = minConflictRows.size();

		//if there is more than one row with the same number of conflicts, choose one randomly
		if (size != 1){
			int randIndex = random.nextInt(size);
			minConRow = minConflictRows.get(randIndex);
		}
		
		if (!smartStart){
			currBoard[queenRow][col] = 1;
		}
		return minConRow; 
	}
	
	//checks whether the board is valid or not, i.e. no queens are in conflict
	public boolean validBoard(int[][] currBoard){
		
		
		for (int row=0; row<N; row++){
			for (int col=0; col<N; col++){
				if (currBoard[row][col]==1){
					if(findConflicts(row, col, currBoard) != 0){
						return false; 
					}
				}
			}
		}
		
		return true;
	}

}