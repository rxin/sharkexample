package com.databricks.sharkexample

import shark.{SharkContext, SharkEnv}

object SharkExample {

  def main(args: Array[String]) {
    SharkEnv.initWithSharkContext("shark-example")
    val sc = SharkEnv.sc.asInstanceOf[SharkContext]
    println(sc.runSql("show tables"))

    println(sc.runSql("CREATE TABLE src(key INT, value STRING)"))
    println(sc.runSql("LOAD DATA LOCAL INPATH '${env:HIVE_HOME}/examples/files/kv1.txt' INTO TABLE src"))
    println(sc.runSql("select * from src"))
  }
}
