Concrete-Kelvin
===============

Tools to map between Kelvin output and Concrete

To convert a file from Kelvin to Concrete run the following command (replacing the directory file with your directory file):

`mvn scala:run -Dlauncher=kelvin2concrete -DKelvinFile=<my file name>`

The system will write two files (using the HLTCOE Concrete standard protocol buffer reader/writers), named:
<my file name>.ver.pb for vertex protobufs
<my file name>.edg.pb for edge protobufs

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
