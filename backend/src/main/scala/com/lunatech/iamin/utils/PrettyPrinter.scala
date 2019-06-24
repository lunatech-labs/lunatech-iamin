package com.lunatech.iamin.utils

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements", "org.wartremover.warts.Unit"))
trait PrettyPrinter {

  private val CharReplacements = Map("\n" -> "\\n", "\r" -> "\\r", "\t" -> "\\t", "\"" -> "\\\"")

  val IndentSize: Int = 2
  val MaxWidth: Int = 60

  def prettyPrint(obj: Any): String = pprint0(obj, depth = 0)

  private def pprint0(obj: Any, depth: Int): String = {

    val sb = new StringBuilder

    val indent = " " * depth * IndentSize
    val fieldIndent = indent + (" " * IndentSize)

    obj match {
      case b: Boolean => sb.append(b)
      case b: Byte    => sb.append(b)
      case s: Short   => sb.append(s)
      case i: Int     => sb.append(i)
      case l: Long    => sb.append(l)
      case f: Float   => sb.append(f)
      case d: Double  => sb.append(d)
      case c: Char    => sb.append(c)
      case s: String  => printStr(sb, s)
      case xs: Seq[_] => printSeq(sb, xs, indent, fieldIndent, depth)
      case p: Product => printProduct(sb, p, indent, fieldIndent, depth)
      case other      => sb.append(other.toString)
    }

    sb.toString
  }

  private def printStr(sb: StringBuilder, s: String): Unit = {
    sb.append('"')
    sb.append(CharReplacements.foldLeft(s) { case (acc, (c, r)) => acc.replace(c, r) })
    sb.append('"')
  }

  private def printSeq(sb: StringBuilder, xs: Seq[_], indent: String, fieldIndent: String, depth: Int): Unit =
    if (xs.isEmpty) sb.append(xs.toString)
    else {
      val oneLine = xs.map(pprint0(_, depth + 1)).toString
      if (oneLine.length <= MaxWidth) sb.append(oneLine)
      else {
        val result = xs.map(x => s"\n$fieldIndent${pprint0(x, depth + 1)}").toString
        sb.append(result.dropRight(1))
        sb.append("\n")
        sb.append(indent)
        sb.append(")")
      }
    }

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private def printProduct(sb: StringBuilder, p: Product, indent: String, fieldIndent: String, depth: Int): Unit = {
    val prefix = p.productPrefix
    val cls = p.getClass
    val fields = cls.getDeclaredFields.filterNot(_.isSynthetic).map(_.getName)
    val values = p.productIterator.toSeq

    (fields zip values).toList match {
      case Nil           => sb.append(p.toString)
      case kvs =>
        val pFields = kvs.map { case (k, v) => s"$k = ${pprint0(v, depth + 1)}" }
        val oneLine = s"$prefix(${pFields.mkString(", ")})"

        if (oneLine.length <= MaxWidth) sb.append(oneLine)
        else {
          sb.append(prefix)
          sb.append("(")
          sb.append("\n")
          kvs.foreach { case (k, v) =>
            sb.append(fieldIndent)
            sb.append(k)
            sb.append(" = ")
            sb.append(pprint0(v, depth + 1))
            sb.append("\n")
          }
          sb.append(indent)
          sb.append(")")
        }
    }
  }
}

object PrettyPrinter extends PrettyPrinter