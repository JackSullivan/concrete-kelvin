package edu.jhu.hlt.concrete.converters.kelvin

import edu.jhu.hlt.concrete.Concrete._
import scala.util.matching.Regex
import scala.collection.mutable
import edu.jhu.hlt.concrete.util.scala._

/**
 * @author John Sullivan
 */
trait Vertexable extends Identified[ValUUID]{def toBuilder:Vertex.Builder = Vertex.newBuilder.setUuid(id.asConcrete)}
trait Edgeable extends Identified[(ValUUID, ValUUID)] {def toBuilder:Edge.Builder = Edge.newBuilder.setEdgeId(id.asConcrete)}
trait EntityMentionable extends Identified[ValUUID] {def toBuilder:EntityMention.Builder = EntityMention.newBuilder.setUuid(id.asConcrete)}

trait KelvinLine {
  import KelvinLine._
  def value:String

  val LineRegex(docId, mentionId, bodyString, confidenceString) = value
  val entId = docId + "_" + mentionId

  def commGUID:CommunicationGUID = CommunicationGUID.newBuilder
    .setCommunicationId(docId)
    .setCorpusName(Settings.Corpus.corpusName)
    .build

  def mentionNumber:Int = mentionId.toInt

  def confidence:Float = confidenceString.toFloat

  def uuid = KelvinLine.getMentionUUID(entId)
}

case class MentionType(value:String) extends KelvinLine with Vertexable {
  val TypeRegex = new Regex("""type\t(\w+)""")

  val TypeRegex(typeString) = bodyString

  def id = uuid

  override def toBuilder:Vertex.Builder = super.toBuilder
    .setDataSetId(docId)
    .addKind((typeString -> confidence).asVertexKindAttribute)

}

case class MentionText(value:String) extends KelvinLine with Vertexable {
  val TextValueRegex = new Regex("""[_\w]+\t"(.+)"\t[\w\d_\.]+\t(\d+)\t(\d+)""")

  val TextValueRegex(mentionText, mentionStart, mentionEnd) = bodyString

  def id = uuid

  override def toBuilder:Vertex.Builder = super.toBuilder
    .setDataSetId(docId)
    .addName((mentionText -> confidence).asStringAttribute)
}

case class VertexMentionRelation(value:String, relation:String, otherId:String) extends KelvinLine with Edgeable { // all vertex mentions will be edge builders, some w
  import KelvinLine._

  def id = uuid -> getMentionUUID(otherId)
  def isForward = uuid < getMentionUUID(otherId)

  def directedEdge:DirectedAttributes.Builder = relation match { // todo add other cases
    case _ => DirectedAttributes.newBuilder.addOtherAttributes((relation -> confidence).asLabeledAttribute)
  }

  override def toBuilder:Edge.Builder = if(isForward) {
    super.toBuilder
      .setV1ToV2(directedEdge)
  } else {
    super.toBuilder
      .setV2ToV1(directedEdge)
  }
}

case class ValueMentionRelation(value:String, relation:String, text:String) extends KelvinLine with Vertexable {

  def id = uuid

  override def toBuilder:Vertex.Builder = relation match { // todo add other cases
   // case "per:age" => Vertex.newBuilder.addAge(text)
    case _ => super.toBuilder.setDataSetId(docId).addOtherAttributes((relation, text, confidence).asLabeledAttribute)
  }
}


object KelvinLine extends ((String) => Option[KelvinLine]) {

  private val idToUUIDMap:mutable.Map[String, ValUUID] = mutable.Map[String, ValUUID]()

  val LineRegex = new Regex(""":e_(\w+)_(\d+)\t(.*)\t([\d\.]+)""")
  val IdRegex = new Regex(""":e_(\w+)\t.*""")
  val ValueRelationRegex = new Regex("""([\w:]+)\t"(.+)"\t.*""")
  val VertexRelationRegex = new Regex("""([\w:]+)\t:e_(\w+)\t.*""")

  def getMentionUUID(mentId:String):ValUUID = idToUUIDMap.getOrElseUpdate(mentId, ValUUID.random)

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