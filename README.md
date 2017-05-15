# ee5393-hw5

ee5393-hw5 is a Clojure library for the synthesis of multi-terminal switching
networks. It was written for the University of Minnesota's course, EE 5393,
as homework 5.

## Usage

The representation of a Boolean function closely mirrors the function’s truth
table. Specifically, a function is represented by the set of all input
combinations for which the function is true, together with the set of all input
combinations for which it is false. This method has the advantage of allowing
incompletely specified functions; if an input combination is not given in either
set, then the function’s value at that point is treated as a don’t-care (meaning
that the value of the function is implementation defined). An input combination
is represented by the set of atoms that are asserted. Although constant
functions are clearly representable in this way, the algorithm does not handle
them; constant functions can always be trivially implemented without the aid of
automated design. An example of this representation of Boolean functions
can be found in
[`test/ee5393_hw5/core_test.clj`](test/ee5393_hw5/core_test.clj).

The function `(stoch-beam-slns fs k)` returns a lazy sequence of
circuits implementing the collection of Boolean functions, `fs`. These solutions
are obtained via a stochastic beam search with population size `k`.

Below is an example REPL session showing the usage of `stoch-beam-slns`:
```clojure
ee5393-hw5.core-test=> (map pprint (stoch-beam-slns fs 10000))
; Solutions begin to show up here...
```
Note that the sequence is infinite, so this will not return.

# Future Improvements

First of all, the interface itself is pretty rough around the edges.
It would be nice to:
* Provide a more convenient way to specify Boolean functions
* Add an entry point so that this can be used as a stand-alone program,
  rather than just as a library
* Add documentation

More importantly, the algorithm itself could be improved.
* There may be a fitness function that better captures the fitness of a circuit.
  The current one does not distinguish between two circuits that do not
  implement all of the required functions, even if one of them implements many
  more of them than the other.
* The only mutation operator is the addition of an edge. More mutations
  may lead to better results.
* I suspect that there may be a better way to test circuits for correctness,
  possibly involving binary decision diagrams.

## License

Copyright © 2017 Alex Robbins

Distributed under the MIT License
