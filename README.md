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
import edu.jhu.hlt.concrete.{Vertex, Edge}

object App {
  val stream = new DataInputStream(new FileInputStream("kelvinfile.pb"))

  val vertices:mutable.ArrayBuffer[Vertex] = new mutable.ArrayBuffer[Vertex]()
  val edges:mutable:ArrayBuffer[Edge] = new mutable.ArrayBuffer[Edge]()

  for(idex <- 0 until stream.readInt) {
    vertices += Vertex.parseFrom(stream)
  }

  for(idex <- 0 until stream.readInt) {
    edges += Edge.parseFrom(stream)
  }
}
