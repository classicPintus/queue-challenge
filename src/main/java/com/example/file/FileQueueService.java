package com.example.file;

public class FileQueueService  {
  //
  // Task 3: Implement me if you have time.
  //

    /**
     * I don't have so much time, but basically is the same as in-memory implementation, BUT the I/O operations
     * are often not thread safe, so we need some extra code due to the synchronization issues.
     *
     * Typically I use ReentrantLock for this purpose
     *
     * The implementation should be to save a file for each message and use a folder for each queue.
     * I suppose to have some more constraints related to the max length of the paths and the ones related to
     * the file names, and should be slower than in-memory implementation.
     */
}
