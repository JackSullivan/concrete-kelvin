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
  import KelvinLine._
  def value:String

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
  val TypeRegex = new Regex("""type\t(\w+)""")

  val TypeRegex(typeString) = bodyString

  def kind:Vertex.Kind = typeString match {
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
  val TextValueRegex = new Regex("""[_\w]+\t"(.+)"\t.+""") //todo think more about regex values vis-a-vis inside the quotes

  val TextValueRegex(mentionText) = bodyString

  def toStringAttribute:StringAttribute = StringAttribute.newBuilder
    .setUuid(KelvinLine.genUUID)
    .setMetadata(metadata)
    .setValue(mentionText)
    .build
}

case class MentionRelation(val value:String) extends KelvinLine {
  val ValueRelationRegex = new Regex("""([\w:]+)\t"(.+)"\t.*""") //todo think more about regex values vis-a-vis inside the quotes
  val VertexRelationRegex = new Regex("""([\w:]+)\t(:e\w+)\t.*""")

  def toRelation:Relation = bodyString match { // todo Check we have all we need
    case VertexRelationRegex(relation, id) => Relation.newBuilder
      .setUuid(KelvinLine.genUUID)
      .setKind(Relation.Kind.VERTEX)
      .setName(relation)
      .addVertices(KelvinLine.getMentionUUID(id))
      .build
    case ValueRelationRegex(relation, text) => Relation.newBuilder
      .setUuid(KelvinLine.genUUID)
      .setKind(Relation.Kind.VALUE)
      .setName(relation)
      .addValue(text)
      .build
  }
}

object KelvinLine extends ((String) => Option[KelvinLine]) {

  private val idToUUIDMap:Map[String, ConUUID] = Map[String,ConUUID]()

  val LineRegex = new Regex(""":e_(\w+)_(\d+)\t(.*)\t([\d\.]+)""")
  val IdRegex = new Regex("""(:e\w+)\t.*""")

  def getMentionUUID(mentId:String):ConUUID = idToUUIDMap.getOrElseUpdate(mentId, genUUID)

  def genUUID:ConUUID = {
    val uuid = UUID.randomUUID()
    ConUUID.newBuilder.setHigh(uuid.getMostSignificantBits).setLow(uuid.getLeastSignificantBits).build
  }

  def apply(value:String):Option[KelvinLine] = value match {
    case LineRegex(_, _, bodyString, _) => bodyString.split("\\t").head match {
      case "type" => Option(new MentionType(value))
      case "mention" => Option(new MentionText(value))
      case "canonical_mention" => Option(new MentionText(value))
      case _ => Option(new MentionRelation(value))
    }
    case _ => None
  }

}