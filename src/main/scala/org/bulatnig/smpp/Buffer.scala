package org.bulatnig.smpp

import java.nio.charset.StandardCharsets

import akka.util.ByteString

import scala.collection.mutable.ArrayBuffer

/**
  * Mutable. Not thread-safe.
  */
class Buffer(val buffer: ByteString) {

  private var _position = 0

  def toByteBuffer = buffer
  def length = buffer.length
  def position = _position
  def isAvailable = position < length - 1

  def ++(xs: TraversableOnce[Byte]) = {
    val newBuffer = new Buffer(buffer)
    newBuffer ++= xs
    newBuffer
  }

  def ++(xs: Buffer) = {
    val newBuffer = new Buffer(buffer)
    newBuffer ++= xs
    newBuffer
  }

  def ++=(xs: TraversableOnce[Byte]) = {
    buffer ++= xs
    this
  }

  def ++=(xs: Buffer) = {
    buffer ++= xs.toArray
    this
  }

  def appendByte(elem: Int) = {
    buffer += elem.toByte
    this
  }

  def appendShort(elem: Int) = {
    buffer += (elem >> 8).toByte
    buffer += elem.toByte
    this
  }

  def appendInt(elem: Int) = {
    buffer += (elem >> 24).toByte
    buffer += (elem >> 16).toByte
    buffer += (elem >> 8).toByte
    buffer += elem.toByte
    this
  }

  def appendString(elem: String) = {
    if (elem != null) {
      this ++= elem.getBytes(StandardCharsets.US_ASCII)
    }
    buffer += Buffer.NULL
    this
  }

  def read(length: Int) = {
    val bytes = new Array[Byte](length)
    Array.copy(buffer.toArray, _position, bytes, 0, bytes.length)
    _position += length
    bytes
  }

  def readByte(): Int = {
    val result = buffer(_position) & 0xFF
    _position += 1
    result
  }

  def readShort(): Int = {
    val result = ((buffer(_position) & 0xFF) << 8) | (buffer(_position + 1) & 0xFF)
    _position += 2
    result
  }

  def readInt(): Int = {
    val result = (buffer(_position) << 24) |
      ((buffer(_position + 1) & 0xFF) << 16) |
      ((buffer(_position + 2) & 0xFF) << 8) |
      (buffer(_position + 3) & 0xFF)
    _position += 4
    result
  }

  def readString(): String = {
    val idx = buffer.indexOf(Buffer.NULL, _position)
    if (idx == -1) throw new IndexOutOfBoundsException("C-Octet String termination not found")
    var result: String = null
    if (idx > _position) {
      val bytes = new Array[Byte](idx - _position)
      Array.copy(buffer.toArray, _position, bytes, 0, bytes.length)
      result = new String(bytes, StandardCharsets.US_ASCII)
    }
    _position = idx + 1
    result
  }

  def rewind() = {
    _position = 0
    this
  }

  def toHexString = buffer.map("%02X" format _).mkString

}

object Buffer {
  private val NULL: Byte = 0
}
