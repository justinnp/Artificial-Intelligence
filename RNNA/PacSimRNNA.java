import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
//Pacsim stuff
import pacsim.BFSPath;
import pacsim.PacAction;
import pacsim.PacCell;
import pacsim.PacFace;
import pacsim.PacSim;
import pacsim.PacUtils;
import pacsim.PacmanCell;

/*
    University of Central Florida   
    CAP4630 - Spring 2019
    Author: Justin Powell
*/

public class PacSimRNNA implements PacAction {
    long simulation_time;
    List<Point> path;
    int[][] cost_table;
    List<Point> food_on_grid;
    boolean flag;

    public PacSimRNNA( String fname ) {
        PacSim sim = new PacSim( fname );
        sim.init(this);
    }  

    public static void main( String[] args ) { 
        System.out.println("\nTSP using RNNA agent by Justin Powell:");
        System.out.println("\nMaze : " + args[ 0 ] + "\n" );
        new PacSimRNNA( args[ 0 ] );
    }

    @Override
    public void init() {
        simulation_time = 0;
        flag = false;
        path = new LinkedList();
        food_on_grid = new LinkedList();
        solution_moves = new LinkedList();
    }

    @Override
    public PacFace action(Object state) {
        PacCell[][] grid = (PacCell[][]) state;
        PacmanCell pc = PacUtils.findPacman( grid );
        // pacman cant be null, must be on board
        if(pc == null) return null;
        if(!flag){
            //get current loc
            Point pc_location = pc.getLoc();
            //populate all the food on the grid
            food_on_grid = PacUtils.findFood(grid);
            printFood();
            //populate a cost table
            cost_table = getCostTable(pc, grid);
            printCostTable();
            //start the sim time
            long start_time = System.currentTimeMillis();
            List<Path> paths = new LinkedList<Path>();
        }
    }

    //create the 2d array that holds the cost of each path to each food pellet
    //also create cost from pacman to any of the beginning pellets
    //pacman to i + NNA from i
    public int[][] produceCostTable(PacmanCell pc, PacCell[][] grid) {
        int N = food_on_grid.size();
        cost_table = new int[N + 1][N + 1];
        for(int i = 0; i < N; i++){
            for(int j = 0; j < i; j++){
                Point food_i = food_on_grid.get(i);
                Point food_j = food_on_grid.get(j);
                int cost_path = BFSPath.getPath(grid, food_i, food_j);
                int cost = cost_path.size();
                cost_table[i + 1][j + 1] = cost;
                cost_table[j + 1][i + 1] = cost;
            }
            Point food_i = food_on_grid.get(i);
            int cost_path_pacman = BFSPath.getPath(grid, pc.getLoc(), food_i);
            int cost_pacman = cost_path_pacman.size();
            cost_table[0][i + 1] = cost_pacman;
            cost_table[i + 1][0] = cost_pacman;
        }
        printCostTable(cost_table);
        return cost_table;
    }

/////////////////////////////////PRINT METHODS//////////////////////////////////////////////

    //print the cost table - for the output requirement
    public void printCostTable(){
        System.out.println("Cost Table: \n");
        System.out.println("---------------------------------\n");
        for(int i = 0; i < table.length; i++){
            for(int j = 0; j < table.length; j++){
                System.out.print(cost_table[i][j] + "\t");
            }
            System.out.println();
        }
    }

    public void printTime(){
        System.out.println("Simulation Time: \n");
        System.out.println("---------------------------------\n");
        System.out.println(simulation_time);
    }

    //print the food points - for the output requirement
    public void printFood(){
        System.out.println("Food Array: \n");
        System.out.println("---------------------------------\n");
        for(int i = 0; i < food_on_grid.size(); i++){
            Point point = food_on_grid.get(i);
            System.out.println("Index " i + ":\t" + "(" + point.x + "," + point.y + ")");
        }
        System.out.println();
    }

    //print the moves we took
    //not sure if im printing right, come back
    public void printMoves(){
        System.out.println("Solution Moves: \n");
        System.out.println("---------------------------------\n");
        for(int i = 0; i < solution.size(); i++){
            Move move = solution.get(i);
            System.out.println("Move " i + ":\t" + "Cost: " + move.cost);
        }
        System.out.println();
    }

    /////////////////////////////////PRINT METHODS//////////////////////////////////////////////


    ////////////////////////////////////////CLASSES/////////////////////////////////////////////
    class FoodPellet {
        int cost;
        Point location;
    }

    class Moves {
        List<Point> move_list;
        List<Integer> move_costs;
    }
    ////////////////////////////////////////CLASSES/////////////////////////////////////////////


}