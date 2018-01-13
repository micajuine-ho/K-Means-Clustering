import java.util.*;
import java.io.*;


/** An implementation of Lloyds K-Means Clustering Algorithm 
* 
* @author Micajuine Ho
* @since 11-11-2017
*/
public class KCluster {

/**Requires a file whose first line has 2 numbers: K, the number 
* of clusters and M, the number of dimensions. The rest of the 
* file contains other points in M dimensions. K-Means Clustering 
* initalizes the first K centers as random points, so I just 
* chose the first K points read in as the centers. This is only
* safe if your points are randomly generated, otherwise you should
* use some other initalizatin technique. 
* 
* @param args the filename of the file with K, M, and the points.
* @return Nothing.
* @exception IOException on input error.
* @exception FileNotFoundException on file error.
* @see IOException
* @see FileNotFoundException
*/
    public static void main (String [] args) {

        String fileName = args[0];
        String [] firstLine = new String [2];
        String line = "";
        
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //First line contains how 2 numbers: clusters and dimensions
            firstLine = bufferedReader.readLine().split("\\s+");
            int k = Integer.parseInt(firstLine[0]);
            int m = Integer.parseInt(firstLine[1]);
            
            //Store K clusters mapped in M dimensions in a 2D array.
            double [][] centers = new double [k][m];
            
            //Store the points mapped in M dimensions that will be read in
            //From the rest of the file. 
            HashSet<double []> points = new HashSet<>();
            
            //Find the first K points and use them as the starting centers 
            int kCenterCount = 0;

            //Read the rest of the points into the 2D array
            while((line = bufferedReader.readLine()) != null) {

                double [] temp = new double [m];
                String [] tempLine = line.split("\\s+");
                int count = 0;
                for (String i : tempLine) {
                    temp[count] = Double.parseDouble(i);
                    count++;
                }

                points.add(temp);

                //If the point is one of the first k points, add points to center
                if (kCenterCount < k) {
                    int counter = 0;
                    for (double x : temp) {
                        centers[kCenterCount][counter] = x;
                        counter++;
                    }
                }
                kCenterCount++;
            }
            bufferedReader.close();     
        
            //Find K Clusters
            findKClusters(centers, points);
          
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");                 
        } 
    }


    /** Clustering algorithm of points using the Centers to Clulsters and Clusters to Centers method. 
    * Center to clusters refers to finding all points that are closest to each center. 
    * Clusters to Centers refers to updating the centers based upon the center of gravity of the 
    * points closest to each center. 
    * 
    * @param centers a K by M array that contains the inital centers.
    * @param points a HashSet of arrays of double that represent all of the points.
    * @return nothing.
    */
    public static void findKClusters(double [][] centers, HashSet<double []> points) {

        //Hashmap of centers to their closest points
        HashMap<double [], HashSet<double[]>> centersToPoints = new HashMap<>();
        boolean converge = false; 

        //Loops until the centers stop changing
        while (!converge) {

            //For each center, add them into the HashMap with a newly initalized hashSet
            for (int i = 0; i < centers.length; i++) {
                centersToPoints.put(centers[i], new HashSet<double []>());
            }
          
            //CENTER TO CLUSTER STEP 
            //For each point, place it into the HashMap at the correct center
            for (double [] point : points) {
                double [] closetCenter = findClosestCenter(centers, point);
                centersToPoints.get(closetCenter).add(point);
            }

            //CLUSTER TO CENTER STEP
            //For each cluster find the new center of gravity and add it to the temp centers array
            double [][] tempCenters = new double [centers.length][centers[0].length];
            for (int centerCounter = 0; centerCounter < centers.length ; centerCounter++) {
                double [] tempCenter = findCenterOfGravity(centersToPoints.get(centers[centerCounter]), centers[0].length);
                tempCenters[centerCounter] = tempCenter;
            }

            //Check if the tempCenters are the same as the previous centers.
            //If any of the rows do not match up, then converge will be false
            //and the loop will continue.
            converge = true;
            for (int row = 0; row < tempCenters.length; row++) {
                if (!Arrays.equals(centers[row], tempCenters[row])) {
                    converge = false;
                }
                //Update the previous center to be the tempCenters
                centers[row] = tempCenters[row];
            }

            //Clear the Hashmap
            centersToPoints.clear();
        }

        System.out.println("Final centers are: -----------------");
        centers = roundCenters(centers);
        printArray(centers);

    }

    /** Helper function to round the center's points to 3 decimal places.
    * 
    * @param arr a 2D double array of center points.
    * @return the double array with each point being rounded.
    */
    private static double [][] roundCenters(double [][] arr) {

        for (int row = 0; row < arr.length; row++) {
            for (int col = 0; col < arr[0].length; col++) {
                double temp = Math.round(arr[row][col] * 1000);
                temp = temp/1000;
                arr[row][col] = temp;
            }
        }
        return arr;
    }
    

    /**Helper function to print the 2D Array.
     * 
     * @param arr a 2D double array of points.
     * @return nothing.
     */
    private static void printArray(double [][] arr) {
        System.out.println("Printing 2D array");
        for (int row = 0; row < arr.length; row++) {
            for (int col = 0; col < arr[0].length; col++) {

                System.out.print (arr[row][col] + " ");

            }
            System.out.println();
        }

    }

    /**Helper Function to print an Array.
     * 
     * @param arr a double array.
     * @return nothing.
     */
    private static void printSingleArray(double [] arr) {
        System.out.println("Printing array");
        for (double x: arr) {
            System.out.print(x + " ");
        }
        System.out.println();
    } 


    /**Helper function that finds the 'center of gravity' between all of the points in the 
     * set by finding the average distance for each diimension. 
     * 
     * @param points a HashSet of double arrays representing the points within the cluster.
     * @param dimensions the number of dimensions.
     * @return a double array representing the center in M dimensions.
     */
    private static double [] findCenterOfGravity(HashSet<double []> points, int dimensions) {

        //Initalize all indexes of centerOfGravity to be 0
        double [] centerOfGravity = new double [dimensions];
        for (int i = 0; i < dimensions; i++) {
            centerOfGravity[i] = 0.0;
        }

        //For each point, add each index to the center of gravity index
        for (double [] point : points) {
            for (int dimension = 0; dimension < dimensions; dimension++) {
                centerOfGravity[dimension] += point[dimension];
            }
        }

        //Divide all indexes by how many points there are
        int size = points.size();
        for (int j = 0; j < dimensions; j++) {
            centerOfGravity[j] /= size;
        }

        return centerOfGravity;
    }

    /**Helper function to find a center from the list of centers that is closest 
     * to the point passed in. 
     *
     * @param centers a 2D double array representing a list of centers. 
     * @param point a double array of the point to find the closest center to it.
     * @return a 2D array that is the closest center to the point. 
     */
    private static double[] findClosestCenter(double [][] centers, double [] point) {

        //For each center find distance from it to point and update min value
        double minDistance = findDistance (centers[0], point);
        double [] minCenter = centers[0];

        for (int i = 1; i < centers.length; i++) {
            double tempDistance = findDistance(centers[i], point);
            if (tempDistance < minDistance) {
                minDistance = tempDistance;
                minCenter= centers[i];
            }
        }

        return minCenter; 
    }

    /**Helper function to find the distance between two points in M dimension space.
     * 
     * @param center a double array represneting a center
     * @param point a double array that represents a point
     * @return a double value that is the distance between the two parameters.
     */
    private static double findDistance(double [] center, double [] point) {
        double distance = 0.0;

        for (int i = 0; i < center.length; i++) {
            distance += Math.pow((center[i] - point[i]), 2.0);
        }

        return distance;
    }
 
}
