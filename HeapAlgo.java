import java.util.*;
class HeapAlgo {

  static List<List<Integer>> finalArr = new ArrayList<List<Integer>>();

  //Prints the array
    List<Integer> printArr(int a[], int n)
    {
      ArrayList<Integer> end = new ArrayList<>();

          if (a[0] == 0) {
            for (int i=0; i<n; i++){
              //end[i] = a[i];
              end.add(a[i]);

              //System.out.print(end.get(i) + " "); //need another for loop here

            }
            finalArr.add(end);
            //System.out.println();

          }
        return end;
    }



    //Generating permutation using Heap Algorithm
    void heapPermutation(int a[], int size, int n)
    {
        // if size becomes 1 then prints the obtained
        // permutation
        if (size == 1)
            printArr(a,n);

        else {
          for (int i=0; i<size - 1; i++)
          {
              heapPermutation(a, size-1, n);

              // if size is odd, swap first and last
              // element
              if (size % 2 == 1)
              {
                  int temp = a[0];
                  a[0] = a[size-1];
                  a[size-1] = temp;
              }

              // If size is even, swap ith and last
              // element
              else
              {
                  int temp = a[i];
                  a[i] = a[size-1];
                  a[size-1] = temp;
              }
          }
          heapPermutation(a, size-1, n);
        }
    }


    // finds best index
    // a is original array
    // finalArry is list of index permutations
    static int bestIndex(List<List<Integer>> x, int[][] a) {
      int bestSum = 0;
      int index = 0;
      for (int i = 0; i < x.size(); i++) {
        int curSum = 0;
        for (int j = 0; j < x.get(i).size(); j++) {
          if (j == x.get(i).size() - 1) {
            curSum += a[x.get(i).get(j)][0];
            //System.out.println("end of list: " + curSum);
          } else {
            curSum += a[x.get(i).get(j)][x.get(i).get(j + 1)];
            //System.out.println("middle of list: " + curSum);
          }
        }
        if (curSum < bestSum || bestSum == 0) {
          bestSum = curSum;
          index = i;
        }
        //System.out.println(bestSum);
      }
      return index;
    }

    public static int[][] takeOut(int a[][]) {
      boolean num = false;

      for (int k = 0; k < a[0].length; k++) {
        if (a[0][k] < 0) {
          num = true;
        }
      }
      if (num) {
        int[][] arr = new int[a.length - 1][a.length - 1];
        int countI = 0;
        int countJ = 0;
        for (int i = 0; i < a.length - 1; i++) {
          for (int j = 0; j < a[0].length - 1; j++) {
            if (a[i][j] > 0) {
              arr[countI][countJ] = a[i][j];
              countI++;
              countJ++;
            }
          }
        }
        return arr;
      }
      return a;
    }

    // Driver code
    public static void main(String args[])
    {
        HeapAlgo obj = new HeapAlgo();
        int a[][] = {{0, 13, 5, 2, 21}, {13, 0, 9, 4, 9}, {5, 9, 0, 1, 21}, {2, 4, 1, 0, 7}, {21, 9, 21, 7, 0}};//{{0, -1, 1, 4}, {-1, -1, -1, -1}, {1, -1, 0, 7}, {4, -1, 7, 0}};
        int arr[][] = takeOut(a);
        int x[] = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
          x[i] = i;
          //System.out.println(x[i]);
        }
        obj.heapPermutation(x, x.length, x.length);

        // for (List<Integer> j : finalArr) {
        //   for (Integer k : j) {
        //     System.out.print(k + " ");
        //   }
        //   System.out.println();
        // }
        int bestPath = bestIndex(finalArr, arr);
        //System.out.println(bestPath);
        System.out.println(finalArr.get(bestPath));
    }
}
