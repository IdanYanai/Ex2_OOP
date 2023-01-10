# EX2_A (README only relevent for first half A)
In this exercise we count the total number of lines in n files, using 3 methods. 
# getNumOfLines
is simple counting in for loop one file after another.
# getNumOfLinesThreads
is creating a new thread for each file to count its lines and then sum it all up.
# getNumOfLinesThreadPool
is using the ExecutorService of a fixed thread pool. Which means, it creates a fixed number of
threads, and when a task is submitted to the pool, it assigns a thread to work on it.

# Time Analysis
After running main creating 1000 files and comparing the time it takes for each method to count the lines. 
The results are getNumOfLines is the slowest method, while the threads and threadPool methods
are way faster. The reason for this is because the threads calculate the
number of lines in each file "simultaneously". Which is way faster than going over the files
one by one. Both threads and threadPool calculate it the same way, hence why they approximately 
(because each time it takes isn't fixed) have the same results.
