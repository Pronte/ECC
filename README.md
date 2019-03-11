# ECC
This repository contains implementation of heuristic algorithms for computing an Edge Clique Cover (ECC) of a graph.

The algorithms are described in the paper

```
Clique covering of large real-world networks - by Alessio Conte, Roberto Grossi and Andrea Marino
```
( available at: https://dl.acm.org/citation.cfm?doid=2851613.2851816 )


and an upcoming journal publication.

The software is written in Java 8. Instructions are displayed by running the jar with no arguments

```
java -jar ECC8.jar
```

As for the code, the key classes are in the package `it.unipi.di.ecc.smallg`

To run, use class `it.unipi.di.ecc.run.ECCrun`

For best performance, provide the graph in `.nde` (nodes-degrees-edges) format:
- one line with the number of nodes
- for each node, one line containing the node's ID (integer) and its degree
- finally, the list of edges (pair of IDs separated by space, one on each line in any order)
