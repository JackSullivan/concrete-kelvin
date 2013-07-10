Concrete-Kelvin
===============

Tools to map between Kelvin output and Concrete

To convert a file from Kelvin to Concrete run the following command (replacing the directory file with your directory file):

`mvn scala:run -Dlauncher=kelvin2concrete -DKelvinFile=<my file name>`

The system will write a file out to `<my file name>.pb` that will contain:

1. An int representing the number of vertices in the protobuf
2. The appropriate number of vertices
3. An int representing the number of edges in the protobuf
4. The appropriate number of edges

It probably makes the most sense to read in this data like so:

```scala
import java.io.{DataInputStream, FileInputStream}
import edu.jhu.hlt.concrete.Graph.{Vertex, Edge}
import scala.collection.mutable.ArrayBuffer

object App {

  def apply(filename:String):(Iterable[Vertex], Iterable[Edge]) = {
    val stream = new DataInputStream(new FileInputStream(filename))

    val vertices:ArrayBuffer[Vertex] = new ArrayBuffer[Vertex]()
    val edges:ArrayBuffer[Edge] = new ArrayBuffer[Edge]()

    val vertexCount = stream.readInt

    for(index <- 0 until vertexCount) {
      println("Read in vertex " + index + " of " + vertexCount)
      vertices += Vertex.parseDelimitedFrom(stream)
    }
    val edgeCount = stream.readInt
    for(index <- 0 until edgeCount) {
      edges += Edge.parseDelimitedFrom(stream)
    }
    (vertices, edges)
  }

}
```
