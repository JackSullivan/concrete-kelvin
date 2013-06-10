package edu.jhu.hlt.concrete.converters.kelvin

import edu.jhu.hlt.concrete.Concrete.{UUID => ConUUID, AttributeMetadata, FloatAttribute, LabeledStringAttribute, EdgeLabelAttribute, StringAttribute, Vertex, VertexKindAttribute, EdgeId}
import java.util.UUID
import edu.jhu.hlt.concrete.converters.kelvin.CommunicationTraits.Messageable

/**
 * @author John Sullivan
 */
object AttributeConversions {

  def genUUID:ConUUID = {
    val uuid = UUID.randomUUID()
    ConUUID.newBuilder.setHigh(uuid.getMostSignificantBits).setLow(uuid.getLeastSignificantBits).build
  }

  implicit def Con2JavaUUID(cUUID:ConUUID):UUID = new UUID(cUUID.getHigh, cUUID.getLow)

  implicit def Con2JavaUUIDTup(tcuuid:(ConUUID, ConUUID)):(UUID, UUID) = (tcuuid._1, tcuuid._2)

  implicit def String2FloatAttr(fltStr:String)(implicit meta:AttributeMetadata):FloatAttribute = FloatAttribute.newBuilder
    .setUuid(genUUID)
    .setMetadata(meta)
    .setValue(fltStr.toFloat)
    .build

  implicit def RelText2LabelAttr(tup:(String, String))(implicit meta:AttributeMetadata):LabeledStringAttribute = LabeledStringAttribute.newBuilder
    .setUuid(genUUID)
    .setMetadata(meta)
    .setLabel(tup._1)
    .setValue(tup._2)
    .build

  implicit def String2EdgeLabelAttr(label:String)(implicit meta:AttributeMetadata):EdgeLabelAttribute = EdgeLabelAttribute.newBuilder
    .setUuid(genUUID)
    .setMetadata(meta)
    .setLabel(label)
    .build

  implicit def String2Attr(value:String)(implicit meta:AttributeMetadata):StringAttribute = StringAttribute.newBuilder
    .setUuid(genUUID)
    .setMetadata(meta)
    .setValue(value)
    .build

  implicit def Kind2KindAttr(kind:Vertex.Kind)(implicit meta:AttributeMetadata):VertexKindAttribute = VertexKindAttribute.newBuilder
    .setUuid(genUUID)
    .setMetadata(meta)
    .setValue(kind)
    .build

  implicit def UUIDs2EdgeId(tup:(ConUUID, ConUUID))(implicit meta:AttributeMetadata):EdgeId = EdgeId.newBuilder
    .setV1(tup._1)
    .setV2(tup._2)
    .build

}
