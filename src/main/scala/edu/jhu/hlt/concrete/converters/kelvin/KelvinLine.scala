package edu.jhu.hlt.concrete.converters.kelvin;

import java.util.UUID
import edu.jhu.hlt.concrete.Concrete.{UUID => ConUUID, CommunicationGUID, AttributeMetadata, Vertex, VertexKindAttribute, StringAttribute}
import edu.jhu.hlt.concrete.Concrete.Vertex.Relation
import scala.util.matching.Regex
import scala.collection.mutable.Map

/**
 * @author John Sullivan
 */
trait KelvinLine {
  def value:String

  val LineRegex = new Regex(""":e_(\w*)_(\d*)\t(.*)\t([\d\.]+)""")
  val IdRegex = new Regex("""(:e\w+)\t.*""")
  val LineRegex(docId, mentionId, bodyString, confidenceString) = value
  val entId = docId + "_" + mentionId

  def commGUID:CommunicationGUID = CommunicationGUID.newBuilder
    .setCommunicationId(docId)
    .setCorpusName("") //todo populate this
    .build

  def mentionNumber:Int = mentionId.toInt

  def confidence:Float = confidenceString.toFloat

  def metadata:AttributeMetadata = AttributeMetadata.newBuilder
    .setTool("Kelvin")
    .setConfidence(confidence)
    .build

  def vertexUUID:ConUUID = KelvinLine.getMentionUUID(entId)
}

case class MentionType(val value:String) extends KelvinLine {
  def kind:Vertex.Kind = value.split("\\t")(2) match {
    case "ORG" => Vertex.Kind.ORGANIZATION
    case "PER" => Vertex.Kind.PERSON
    case "GPE" => Vertex.Kind.GPE
    case _ => Vertex.Kind.UNKNOWN // We shouldn't get here
  }

  def toVertexKindAttribute:VertexKindAttribute = VertexKindAttribute.newBuilder
    .setUuid(KelvinLine.genUUID)
    .setMetadata(metadata)
    .setValue(kind)
    .build
}

case class MentionText(val value:String) extends KelvinLine {
  def toStringAttribute:StringAttribute = StringAttribute.newBuilder
    .setUuid(KelvinLine.genUUID)
    .setMetadata(metadata)
    .setValue(value)
    .build
}

case class MentionRelation(val value:String) extends KelvinLine {
  def toRelation:Relation = bodyString.split("\\t")(1) match { // todo Check we have all we need
    case IdRegex(id) => Relation.newBuilder
      .setUuid(KelvinLine.genUUID)
      .setKind(Relation.Kind.VERTEX)
      .setName(bodyString.split("\\t").head)
      .addVertices(KelvinLine.getMentionUUID(id))
      .build
    case relationValue => Relation.newBuilder
      .setUuid(KelvinLine.genUUID)
      .setKind(Relation.Kind.VALUE)
      .setName(bodyString.split("\\t").head)
      .addValue(relationValue)
      .build
  }
}

object KelvinLine {

  private val idToUUIDMap:Map[String, ConUUID] = Map[String,ConUUID]()

  def getMentionUUID(mentId:String):ConUUID = idToUUIDMap.getOrElseUpdate(mentId, genUUID)

  def genUUID:ConUUID = {
    val uuid = UUID.randomUUID()
    ConUUID.newBuilder.setHigh(uuid.getMostSignificantBits).setLow(uuid.getLeastSignificantBits).build
  }

  def apply(value:String):KelvinLine = value.split("\\t")(1) match { // This is dangerous
    case "type" => new MentionType(value)
    case "mention" => new MentionText(value)
    case _ => new MentionRelation(value)
  }

}