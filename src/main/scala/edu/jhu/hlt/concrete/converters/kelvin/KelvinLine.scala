package edu.jhu.hlt.concrete.converters.kelvin;

import java.util.UUID
import edu.jhu.hlt.concrete.Concrete.{CommunicationGUID, Vertex, StringAttribute, UUID => ConUUID, AttributeMetadata}
import edu.jhu.hlt.concrete.Concrete.Vertex.{RelationOrBuilder, Relation}
import scala.util.matching.Regex

/**
 * @author John Sullivan
 */
trait KelvinLine {
  def value:String

  val LineRegex = new Regex(""":e_(\w*)_(\d*)\t(.*)\t([\d\.]+)""")
  val IdRegex = new Regex("""(:e\w+)\t.*""")
  val LineRegex(docId, mentionId, bodyString, confidenceString) = value

  def commGUID:CommunicationGUID = CommunicationGUID.newBuilder
    .setCommunicationId(docId)
    .setCorpusName("") //todo populate this
    .build

  def mentionNumber:Int = mentionId.toInt

  def confidence:Float = confidenceString.toFloat
}

case class MentionType(val value:String) extends KelvinLine {
  def kind:Vertex.Kind = value.split("\\t")(2) match {
    case "ORG" => Vertex.Kind.ORGANIZATION
    case "PER" => Vertex.Kind.PERSON
    case "GPE" => Vertex.Kind.GPE
    case _ => Vertex.Kind.UNKNOWN // We shouldn't get here
  }
}
case class MentionText(val value:String) extends KelvinLine {
  def name:StringAttribute = StringAttribute.newBuilder
    .setUuid(KelvinLine.genUUID)
    .setMetadata { AttributeMetadata.newBuilder
      .setTool("Kelvin") //todo enable configuring tool source
      .setConfidence(this.confidence)
      .build
    }
    .setValue(value)
    .build
}
case class MentionRelation(val value:String) extends KelvinLine {
  def relation:RelationOrBuilder = bodyString.split("\\t")(1) match {
    case IdRegex(id) => Relation.newBuilder
      .setUuid(KelvinLine.genUUID)
      .setKind(Relation.Kind.VERTEX)
      .setName(bodyString.split("\\t").head)
      .addVertices(KelvinLine.genUUID) //todo figure out about actually pointing to vertices !!
    case relationValue => Relation.newBuilder
      .setUuid(KelvinLine.genUUID)
      .setKind(Relation.Kind.VALUE)
      .setName(bodyString.split("\\t").head)
      .addValue(relationValue)
  }
}

object KelvinLine {

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