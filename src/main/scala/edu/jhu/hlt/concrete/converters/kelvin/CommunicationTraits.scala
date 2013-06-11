package edu.jhu.hlt.concrete.converters.kelvin

import com.google.protobuf.Message
import com.google.protobuf.Message.Builder
import edu.jhu.hlt.concrete.Concrete.{UUID => ConUUID, Vertex, Edge, EdgeId}

/**
 * @author John Sullivan
 */
object CommunicationTraits {
  trait Messageable[A <: Message, B <: Message]{
    def toBuilder:Builder
    def id:B
  }

  trait Vertexable extends Messageable[Vertex, ConUUID]
  trait Edgeable extends Messageable[Edge, EdgeId]
  trait FinalMessage[A <: Message, B <: Message] extends Messageable[A, B] {
    def toMessage:A
  }

  implicit def MessageableSeq2Message[A <: Message,B <: Message](mess:Iterable[Messageable[A,B]]):Message = mess.map(_.toBuilder).reduce[Message.Builder]({(a:Message.Builder, b:Message.Builder) => a.mergeFrom(b.buildPartial)}).buildPartial
}
