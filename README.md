# Auto Extractor
An intelligent extractor library which learns the structures of the input web pages and then figures out a strategy for scraping the structured content


NOTE : The project is under active development, as a result the README is out of sync with the codebase.

TODO: update this file with the description of all new features.

# Example Usage:
## 1. Structural Similarity Between HTML/XML documents
<pre>
$ mvn clean compile package
$ java -cp target/autoextractor-0.1-SNAPSHOT-jar-with-dependencies.jar edu.usc.cs.autoext.tree.ZSTEDComputer \
        -dir src/test/resources/html/simple/

#Index  File Path
0       /home/tg/work/projects/oss/autoextractor/src/test/resources/html/simple/3.html
1       /home/tg/work/projects/oss/autoextractor/src/test/resources/html/simple/2.html
2       /home/tg/work/projects/oss/autoextractor/src/test/resources/html/simple/1.html

#Similarity Matrix
0.000000        13.000000       10.000000       
13.000000       0.000000        3.000000        
10.000000       3.000000        0.000000 
</pre>

## 2. Clustering based on style and structure
 
<pre>
$ mvn clean package
$ java -cp target/autoextractor-0.1-SNAPSHOT-jar-with-dependencies.jar edu.usc.cs.autoext.cluster.FileClusterer
    Option "-list" is required
    -list FILE    : path to a file containing paths to html files that requires
                     clustering
     -workdir FILE : Path to directory to create intermediate files and reports

# Creating input list of htmls
$ find src/test/resources/html/simple/ -type f  > list.txt

# Cluster
$ java -cp target/autoextractor-0.1-SNAPSHOT-jar-with-dependencies.jar edu.usc.cs.autoext.cluster.FileClusterer \
        -list list.txt  -workdir out

# Report 
$ cat out/report.txt

# Similarity Matrix
$ cat out/gross-sim.csv

# Clusters
$ cat out/clusters.txt 
    ##Total Clusters:2
    
    #Cluster:0
    src/test/resources/html/simple/3.html
    
    #Cluster:1
    src/test/resources/html/simple/2.html
    src/test/resources/html/simple/1.html

 
</pre>


# Developers: 
* [Thamme Gowda, USC](mailto:tgowdan@gmail.com)
* [Chris Mattmann, USC & NASA JPL]()


# References :
+ K. Zhang and D. Shasha. 1989. "Simple fast algorithms for the editing distance between trees and related problems". SIAM J. Comput. 18, 6 (December 1989), 1245-1262. 
+ Jarvis, R.A.; Patrick, Edward A., "Clustering Using a Similarity Measure Based on Shared Near Neighbors," in Computers, IEEE Transactions on , vol.C-22, no.11, pp.1025-1034, Nov. 1973

