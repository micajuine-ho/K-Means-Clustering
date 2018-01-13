# KMeansClustering
Implementation of Lloyd's K-means clustering algorithm in Java


Requires a file whose first line has 2 numbers: K, the number 
of clusters and M, the number of dimensions. The rest of the 
file contains other points in M dimensions. K-Means Clustering 
initalizes the first K centers as random points, so I just 
chose the first K points read in as the centers. This is only
safe if your points are randomly generated (due to having hard
to cluster arrangement of points such as a ying-yang design), 
otherwise you should use some other initalization technique.


