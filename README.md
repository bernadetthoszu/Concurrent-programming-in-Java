# Concurrent-programming-in-Java
Here are my 2 assignments I did for the Concurrent Programming course at Eötvös Lóránd University Budapest.

## 1st Assignment - Field Race
This is a simulation of a race where more participants need to cross through multiple checkpoints. The contestants share the checkpoints. They get a certain score at each location, which adds to their overall score.

Tools I used from the java.util.concurrent package: AtomicBoolean, AtomicInteger, ConcurrentHashMap, BlockingQueue, ExecutorService

[See task](https://github.com/bernadetthoszu/Concurrent-programming-in-Java/blob/main/1.%20assignment/task.html)

## 2nd Assignment - PigMent
This application simulates the activity of some not so orthodox pigs, in a world in which sunbathing for energy is a thing, but eating a pig is still questionable. Especially if you yourself are a pig. All the credits go out to my lab teacher from ELTE for the, well, funny story of the app.

Tools I used: AtomicInteger, BlockingQueue, ReentrantLock, Condition, ExecutorService

[See task](https://github.com/bernadetthoszu/Concurrent-programming-in-Java/blob/main/2.%20assignment/task.html)
