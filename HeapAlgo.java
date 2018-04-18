// https://www.geeksforgeeks.org/heaps-algorithm-for-generating-permutations/
// used to find all permutations

import java.util.*;
class HeapAlgo {

  static List<List<Integer>> finalArr = new ArrayList<List<Integer>>();

  //Prints the array
    List<Integer> printArr(int a[], int n)
    {
      ArrayList<Integer> end = new ArrayList<>();

          if (a[0] == 0) {
            for (int i=0; i<n; i++){
              end.add(a[i]);
            }
            end.add(0);
            finalArr.add(end);
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
          } else {
            curSum += a[x.get(i).get(j)][x.get(i).get(j + 1)];
          }
        }
        if (curSum < bestSum || bestSum == 0) {
          bestSum = curSum;
          index = i;
        }
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
        for (int i = 0; i < a.length; i++) {
          for (int j = 0; j < a[0].length; j++) {
            if (a[i][j] >= 0) {
              arr[countI][countJ] = a[i][j];
              countJ++;
            }
          }
          countJ = 0;
          if (a[i][0] >= 0) {
            countI++;
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
        int a[][] = {{0, -1, 5, 2, 21}, {-1, -1, -1, -1, -1}, {5, -1, 0, 1, 21}, {2, -1, 1, 0, 7}, {21, -1, 21, 7, 0}};
        int arr[][] = takeOut(a);
        int x[] = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
          x[i] = i;
        }
        obj.heapPermutation(x, x.length, x.length);
        int bestPath = bestIndex(finalArr, arr);
        System.out.println(finalArr.get(bestPath));
    }
}
