package com.viglink.webpageclassification

import java.io.{File, PrintWriter}

import org.jsoup.Jsoup

import scala.collection.JavaConversions._


class SiteCrawler2 {

  var crawledPages = Vector.empty[String]

  def crawlUrl(url: String, depth: Int, maxDepth: Int, domain: String): Unit = {

    if (url.contains(domain) && depth < maxDepth) {

      println(url)
      println(depth)

      try {
        val document = Jsoup.connect(url).get()
        val pageText = document.text()

        val linksOnPage = document.select("a[href]")
        val newDepth = depth + 1
        crawledPages :+= pageText
        linksOnPage.foreach(page => crawlUrl(page.attr("abs:href"), newDepth, maxDepth, domain))
      }
      catch {
        case e: Exception => println(s"An error occurred ${e.getMessage}")
      }
    }
  }

  def writeToFile(rootDomain: String): Unit = {

    var counter = 0

    crawledPages.foreach { cp =>

      val pr = new PrintWriter(new File(s"src/main/resources/trainingdata/15Categories/6/${rootDomain}_${counter}.txt"))
      pr.println(cp)
      pr.close()

      counter += 1
    }

  }

}

object test extends App {

  val sc2 = new SiteCrawler2

  sc2.crawlUrl("https://www.viglink.com/", 0, 2, "viglink")

  sc2.crawledPages.foreach(println)

  sc2.writeToFile("viglink_com")

}