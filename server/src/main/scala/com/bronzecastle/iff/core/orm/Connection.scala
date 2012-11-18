/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.orm

import collection.mutable.{ArrayBuffer => MutableArrayBuffer}
import java.sql.{Blob, ResultSet}

/**
 * logical connection to database
 */
class Connection(val directConnection: java.sql.Connection) {
  def commitTransaction() {
    directConnection.commit()
  }

  def rollbackTransaction() {
    directConnection.rollback()
  }

  def close() {
    directConnection.close()
  }

  protected def prepareStatement(stmt: String,args: Seq[Any]) = {
    val ps = directConnection.prepareStatement(stmt)
    for (i <- 0 until args.length) {
      args(i) match {
        case n: Int => ps.setInt(i+1,n)
        case n: Long => ps.setLong(i+1,n)
        case s: String => ps.setString(i+1,s)
        case b: Blob => ps.setBlob(i+1,b)
        case x => throw new RuntimeException("Unimplemented type: "+x.getClass.getName)
      }
    }
    ps
  }

  def executeStatement(stmt: String,args: Any*) = {
    prepareStatement(stmt,args).execute()
  }

  def executeQuery(stmt: String,args: Any*): ResultSet = {
    prepareStatement(stmt,args).executeQuery()
  }

  def executeUpdate(stmt: String,args: Any*) = {
    prepareStatement(stmt,args).executeUpdate()
  }

  def querySingle[T](f: (ResultSet)=>T): (String)=>Option[T] = {
    (stmt: String) => {
      val rs = executeQuery(stmt)
      if (rs.next()) Some(f(rs)) else None
    }
  }

  def querySingleWithArgs[T](f: (ResultSet)=>T): (String,Seq[Any])=>Option[T] = {
    (stmt: String,args: Seq[Any]) => {
      val rs = executeQuery(stmt,args:_*)
      if (rs.next()) Some(f(rs)) else None
    }
  }

  def queryMany[T](f: (ResultSet)=>T): (String)=>Iterator[T] = {
    (stmt: String) => {
      val rs = executeQuery(stmt)
      new Iterator[T] {
        def hasNext = rs.next()
        def next() = f(rs)
      }
    }
  }

  def queryManyWithArgs[T](f: (ResultSet)=>T): (String,Seq[Any])=>Iterator[T] = {
    (stmt: String,args: Seq[Any]) => {
      val rs = executeQuery(stmt,args:_*)
      new Iterator[T] {
        def hasNext = rs.next()
        def next() = f(rs)
      }
    }
  }
}

object Connection {
  protected val tls = new InheritableThreadLocal[Connection]()

  def setCurrent(conn: Connection) { tls.set(conn) }
  def apply() = tls.get()

  object QueryConvertions {
    def asInt(rs: ResultSet): Int = rs.getInt(1)
    def asLong(rs: ResultSet): Long = rs.getLong(1)
    def asString(rs: ResultSet): String = rs.getString(1)
    def asIntStringString(rs: ResultSet): (Int,String,String) = (rs.getInt(1),rs.getString(2),rs.getString(3))
    def asLongBlob(rs: ResultSet): (Long,Blob) = (rs.getLong(1),rs.getBlob(2))
  }
}
