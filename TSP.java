import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;

public class TSP
{
    private int numberOfNodes;
    private Stack<Integer> stack;

    public TSP()
    {
        stack = new Stack<Integer>();
    }

    public void tsp(int adjacencyMatrix[][])
    {
        numberOfNodes = adjacencyMatrix[1].length - 1;
        int[] visited = new int[numberOfNodes + 1];
        visited[1] = 1;
        stack.push(1);
        int element, dst = 0, i;
        int min = Integer.MAX_VALUE;
        boolean minFlag = false;
        System.out.print(1 + "\t");
        while (!stack.isEmpty())
        {
            element = stack.peek();
            i = 1;
            min = Integer.MAX_VALUE;
            while (i <= numberOfNodes)
            {
                if (adjacencyMatrix[element][i] > 1 && visited[i] == 0)
                {
                    if (min > adjacencyMatrix[element][i])
                    {
                        min = adjacencyMatrix[element][i];
                        dst = i;
                        minFlag = true;
                    }
                }
                i++;
            }
            if (minFlag)
            {
                visited[dst] = 1;
                stack.push(dst);
                System.out.print(dst + "\t");
                minFlag = false;
                continue;
            }
            stack.pop();
        }
    }

    public static void main(String[] args)
    {
        int number_of_nodes;
        int[][] adjMat =  {{0, 13, 5, 2, 21}, {13, 0, 9, 4, 9}, {5, 9, 0, 1, 21}, {2, 4, 1, 0, 7}, {21, 9, 21, 7, 0}};
        try
        {
            number_of_nodes = adjMat.length;
            int adjacency_matrix[][] = new int[number_of_nodes + 1][number_of_nodes + 1];
            for (int i = 1; i <= number_of_nodes; i++)
            {
                for (int j = 1; j <= number_of_nodes; j++)
                {
                    adjacency_matrix[i][j] = adjMat[i - 1][j - 1];
                }
            }
            for (int i = 1; i <= number_of_nodes; i++)
            {
                for (int j = 1; j <= number_of_nodes; j++)
                {
                    if (adjacency_matrix[i][j] == 1
                            && adjacency_matrix[j][i] == 0)
                    {
                        adjacency_matrix[j][i] = 1;
                    }
                }
            }
            System.out.println("The cities are visited as follows: ");
            TSP tspNearestNeighbour = new TSP();
            tspNearestNeighbour.tsp(adjacency_matrix);
            System.out.println("");
        }
        catch (InputMismatchException inputMismatch)
        {
            System.out.println("Wrong Input format");
        }
    }
}
