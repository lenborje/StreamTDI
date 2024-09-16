# StreamTDI #
This is an implementation of the prime number enumerator TDI, which uses [Wheel factorization](https://en.wikipedia.org/wiki/Wheel_factorization) to check for primality.

The main purpose of TDI is to provide an algorithm that can easily be implemented using various techniques, languages, platforms, etc,
just to give an indication of how well the chosen target supports looping, integer arithmetic, and also how well various languages
support common programming idioms.

And of course it's mostly for fun.

## Building ##
The project is built using Gradle. To build the project, run the following command:

```./gradlew build```

## Running ##

To run the project, run the following command:

```./gradlew run --args "10 100 1000 10000 100000 1000000 10000000" ```

Alternatively, make a local "installation" of the application:
    
```./gradlew installDist```

This will create a directory `build/install/StreamTDI` containing the necessary scripts to run the application, e.g.:

```./build/install/StreamTDI/bin/StreamTDI 10 100 1000 10000 100000 1000000 10000000```

## Native Compilation with GraalVM ##
If you have GraalVM installed, you can compile the project to a native image. Cf. [GraalVM](https://www.graalvm.org/)
for general information about GraalVM and https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html
for information about the GraalVM Gradle plugin.

Make sure your current java is GraalVM; alternatively, you can set the GRAALVM_HOME environment variable to point to
the Java Home directory of GraalVM.

The project can now be compiled to a native image using GraalVM. To do this, run the following command:

```./gradlew nativeCompile```

Run the native code:

```./gradlew nativeRun```

(Note that default arguments are used for the nativeRun task, which can be changed in the build.gradle file.)

### Native Compilation with Profile-Guided Optimization (PGO) ###
If your GraalVM distribution includes the PGO feature (E.g. Oracle GraalVM), you can use it to improve the performance
of the native image. To do this, run the following command:

```./gradlew nativeCompile --pgo-instrument nativeRun```

When the run is finished, copy the resulting profile to the source tree:

```cp build/native/nativeCompile/default.iprof src/pgo-profiles/main```

and re-run the native compilation:

```./gradlew nativeCompile```

The resulting native image should now be optimized based on the profile.

## Benchmarks ##

These are the results of running the program on my machine (Apple M2 Max, 2023):

### Plain Java ###
```
% ./gradlew run --args "10 100 1000 10000 100000 1000000 10000000"               

> Task :run
pa: [2, 3, 5]
product: 30
firstWheel: [7, 11, 13, 17, 19, 23, 29, 31]
increments: [4, 2, 4, 2, 4, 6, 2, 6]
Primes: 4 in 1 ms
Primes: 25 in 1 ms
Primes: 168 in 2 ms
Primes: 1229 in 5 ms
Primes: 9592 in 46 ms
Primes: 78498 in 653 ms
Primes: 664579 in 2742 ms
```

### Native Image, GraalVM Community Edition, without optimisation ###
```
% build/native/nativeCompile/StreamTDI 10 100 1000 10000 100000 1000000 10000000
pa: [2, 3, 5]
product: 30
firstWheel: [7, 11, 13, 17, 19, 23, 29, 31]
increments: [4, 2, 4, 2, 4, 6, 2, 6]
Primes: 4 in 0 ms
Primes: 25 in 0 ms
Primes: 168 in 0 ms
Primes: 1229 in 3 ms
Primes: 9592 in 26 ms
Primes: 78498 in 297 ms
Primes: 664579 in 6690 ms
```

### Native Image, GraalVM Oracle, without optimisation ###
```
 % build/native/nativeCompile/StreamTDI 10 100 1000 10000 100000 1000000 10000000 
pa: [2, 3, 5]
product: 30
firstWheel: [7, 11, 13, 17, 19, 23, 29, 31]
increments: [4, 2, 4, 2, 4, 6, 2, 6]
Primes: 4 in 0 ms
Primes: 25 in 0 ms
Primes: 168 in 0 ms
Primes: 1229 in 2 ms
Primes: 9592 in 21 ms
Primes: 78498 in 258 ms
Primes: 664579 in 5689 ms
```

### Native Image, GraalVM Oracle, first pass collecting profile ###
```
./gradlew nativeCompile --pgo-instrument nativeRun

[...]

> Task :nativeRun
pa: [2, 3, 5]
product: 30
firstWheel: [7, 11, 13, 17, 19, 23, 29, 31]
increments: [4, 2, 4, 2, 4, 6, 2, 6]
Primes: 4 in 0 ms
Primes: 25 in 0 ms
Primes: 168 in 0 ms
Primes: 1229 in 11 ms
Primes: 9592 in 77 ms
Primes: 78498 in 1350 ms
Primes: 664579 in 31194 ms
```

### Native Image, GraalVM Oracle, second pass using profile ###


```
cp build/native/nativeCompile/default.iprof src/pgo-profiles/main
./gradlew nativeCompile

[...]

 % build/native/nativeCompile/StreamTDI 10 100 1000 10000 100000 1000000 10000000
pa: [2, 3, 5]
product: 30
firstWheel: [7, 11, 13, 17, 19, 23, 29, 31]
increments: [4, 2, 4, 2, 4, 6, 2, 6]
Primes: 4 in 0 ms
Primes: 25 in 0 ms
Primes: 168 in 0 ms
Primes: 1229 in 1 ms
Primes: 9592 in 3 ms
Primes: 78498 in 44 ms
Primes: 664579 in 640 ms
```
