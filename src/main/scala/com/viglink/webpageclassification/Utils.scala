package com.viglink.webpageclassification

import java.io.{File, PrintWriter}
import scala.collection.JavaConversions._

import org.jsoup.Jsoup

import scala.io.Source


case class UrlAndContent(url: String, content: String)

object Utils extends App {


  def crawlUrl(url: String): String = {
    val doc = Jsoup.connect(url).get()
    doc.text()
  }

  def validateUrl(url: String): String = {

    if (url.take(4) != "www." && url.count(_ == '.') > 1) {
      "http://" + url
    }
    else if (url.take(4) == "www.") {
      "http://" + url
    }
    else if (url.take(4) != "www.") {
      "http://www." + url
    }
    else {
      url
    }

  }

  def readDomainListFile(file: File): Seq[Option[UrlAndContent]] = {

    Source
      .fromFile(file)
      .getLines()
      .map { line =>
        val url = line.split(",").last.trim
        //put url in format Jsoup is able to handle
        val validatedUrl = validateUrl(url)
        //crawl the url

        try {
          Some(UrlAndContent(url, crawlUrl(validatedUrl)))
        }
        catch {
          case e: Exception => None
        }
      }.toSeq

  }





  def writeStringToFile(text: String, writeLocation: String): Unit = {

    val pr = new PrintWriter(new File(writeLocation))
    pr.println(text)
    pr.close()

  }



  //  val urlContent = readDomainListFile(new File("/users/alexminnaar/15_cat_training_data/15/viglink_impl publishers 2017-08-21T1106.csv"))
  //
  //  var counter = 0
  //  urlContent
  //    .foreach { uc =>
  //
  //      println(counter)
  //      if (uc.isDefined) {
  //        writeStringToFile(uc.get.content, s"/users/alexminnaar/15_cat_training_data/15/${uc.get.url.replace('/', '_').replace('.', '_')}.txt")
  //      }
  //
  //      counter += 1
  //    }
}
