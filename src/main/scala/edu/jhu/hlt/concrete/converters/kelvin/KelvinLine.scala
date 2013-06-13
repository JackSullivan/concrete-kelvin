package edu.jhu.hlt.concrete.converters.kelvin

import java.util.UUID
import edu.jhu.hlt.concrete.Concrete.{UUID => ConUUID, CommunicationGUID, AttributeMetadata, Vertex, DirectedAttributes, Edge, EdgeId}
import scala.util.matching.Regex
import scala.collection.mutable
import edu.jhu.hlt.concrete.converters.kelvin.CommunicationTraits.{Edgeable, Vertexable}

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

  implicit def metadata:AttributeMetadata = AttributeMetadata.newBuilder
    .setTool("Kelvin")
    .setConfidence(confidence)
    .build

  def myUUID:ConUUID = KelvinLine.getMentionUUID(entId)
}

case class MentionType(value:String) extends KelvinLine with Vertexable {
  import AttributeConversions._
  val TypeRegex = new Regex("""type\t(\w+)""")

  val TypeRegex(typeString) = bodyString

  def kind:Vertex.Kind = typeString match {
    case "ORG" => Vertex.Kind.ORGANIZATION
    case "PER" => Vertex.Kind.PERSON
    case "GPE" => Vertex.Kind.GPE
    case _ => Vertex.Kind.UNKNOWN // We shouldn't get here
  }

  def toBuilder:Vertex.Builder = Vertex.newBuilder
    .setDataSetId(docId)
    .setUuid(myUUID)
    .addKind(kind)

  def id:ConUUID = myUUID
}

case class MentionText(value:String) extends KelvinLine with Vertexable { //todo keep hold of references to text
  import AttributeConversions._
  val TextValueRegex = new Regex("""[_\w]+\t"(.+)"\t[\w\d_\.]+\t(\d+)\t(\d+)""") //todo test new regex

  val TextValueRegex(mentionText, mentionStart, mentionEnd) = bodyString

  def toBuilder:Vertex.Builder = Vertex.newBuilder
    .setDataSetId(docId)
    .setUuid(myUUID)
    .addName(mentionText)

  def id:ConUUID = myUUID
}

case class VertexMentionRelation(value:String, relation:String, otherId:String) extends KelvinLine with Edgeable { // all vertex mentions will be edge builders, some w
  import KelvinLine._
  import AttributeConversions._

  def directedEdge:DirectedAttributes.Builder = relation match { // todo add other cases
    case _ => DirectedAttributes.newBuilder.addOtherAttributes(relation)
  }

  def toBuilder:Edge.Builder = Edge.newBuilder.setEdgeId(id)
    .setV1ToV2(directedEdge)

  def id:EdgeId = myUUID -> getMentionUUID(otherId)

}

case class ValueMentionRelation(value:String, relation:String, text:String) extends KelvinLine with Vertexable {
  import AttributeConversions._
  def toBuilder:Vertex.Builder = relation match { // todo add other cases
   // case "per:age" => Vertex.newBuilder.addAge(text)
    case _ => Vertex.newBuilder.setDataSetId(docId).addOtherAttributes(relation -> text)
  }

  def id:ConUUID = myUUID

}


object KelvinLine extends ((String) => Option[KelvinLine]) {

  private val idToUUIDMap:mutable.Map[String, ConUUID] = mutable.Map[String,ConUUID]()

  val LineRegex = new Regex(""":e_(\w+)_(\d+)\t(.*)\t([\d\.]+)""")
  val IdRegex = new Regex(""":e_(\w+)\t.*""")
  val ValueRelationRegex = new Regex("""([\w:]+)\t"(.+)"\t.*""")
  val VertexRelationRegex = new Regex("""([\w:]+)\t:e_(\w+)\t.*""")

  def getMentionUUID(mentId:String):ConUUID = idToUUIDMap.getOrElseUpdate(mentId, genUUID)

  def genUUID:ConUUID = {
    val uuid = UUID.randomUUID()
    ConUUID.newBuilder.setHigh(uuid.getMostSignificantBits).setLow(uuid.getLeastSignificantBits).build
  }

  def apply(value:String):Option[KelvinLine] = value match {
    case LineRegex(_, _, bodyString, _) => bodyString.split("\\t").head match {
      case "type" => Option(MentionType(value))
      case "mention" => Option(MentionText(value))
      case "canonical_mention" => Option(MentionText(value)) // todo how do we mark mentions as canonical?
      case _ => bodyString match {
        case VertexRelationRegex(relation, id) => Option(VertexMentionRelation(value, relation, id))
        case ValueRelationRegex(relation, text) => Option(ValueMentionRelation(value, relation, text))
      }
    }
    case _ => None
  }

}