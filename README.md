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
import edu.jhu.hlt.concrete.Graph.{Vertex, Edge}
import edu.jhu.hlt.concrete.io.ProtocolBufferReader
import scala.collection.mutable.ArrayBuffer

object App {

  def apply(vertexFile:String, edgeFile: String):(Iterable[Vertex], Iterable[Edge]) = {
    val vertices:ArrayBuffer[Vertex] = new ArrayBuffer[Vertex]()
    val edges:ArrayBuffer[Edge] = new ArrayBuffer[Edge]()

    val vertexReader = new ProtocolBufferReader[Vertex](vertexFile,classOf[Vertex])
    val edgeReader = new ProtocolBufferReader[Edge](vertexFile,classOf[Edge])

    while(vertexReader.hasNext()) {
        vertices += vertexReader.next()
    }
    vertexReader.close()
    while(edgeReader.hasNext()) {
        edges += edgeReader.next()
    }
    edgeReader.close()
    (vertices, edges)
  }

}
```
