# Auto Extractor
An intelligent extractor library which learns the structures of the input web pages and then figures out a strategy for scraping the structured content


# Example Usage:
## 1. Structural Similarity Between HTML/XML documents
<pre>
$ mvn clean compile package
$ java -cp target/autoextractor-0.1-SNAPSHOT-jar-with-dependencies.jar edu.usc.cs.autoext.tree.ZSTEDComputer -dir src/test/resources/html/simple/

#Index  File Path
0       /home/tg/work/projects/oss/autoextractor/src/test/resources/html/simple/3.html
1       /home/tg/work/projects/oss/autoextractor/src/test/resources/html/simple/2.html
2       /home/tg/work/projects/oss/autoextractor/src/test/resources/html/simple/1.html

#Similarity Matrix
0.000000        13.000000       10.000000       
13.000000       0.000000        3.000000        
10.000000       3.000000        0.000000 
</pre>

# Developers: 
* [Thamme Gowda, USC](mailto:tgowdan@gmail.com)
* [Chris Mattmann, USC & NASA JPL]()


