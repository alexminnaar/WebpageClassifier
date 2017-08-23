package com.viglink.webpageclassification

import java.io.IOException

import cc.mallet.classify.Classifier
import org.jsoup.Jsoup

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
  * A simple crawler using the Jsoup parser - each page crawled is also classified
  *
  * @param clf      Mallet text classifier
  * @param maxDepth max crawling depth
  */
class SiteCrawler(clf: Classifier, maxDepth: Int = 2) {

  var links: mutable.HashSet[String] = mutable.HashSet.empty
  var predictionCounts: mutable.HashMap[String, Int] = mutable.HashMap.empty

  /**
    * Recursive function - crawls pages and classifies them
    *
    * @param url   url to crawl
    * @param depth current crawl depth
    */
  def getPageLinks(url: String, depth: Int = 0): Unit = {

    if (!links.contains(url) && (depth < maxDepth)) {

      println(s">> Depth: $depth [$url]")
      try {

        val document = Jsoup.connect(url).get()

        //predict this page using classifier
        val pageText = document.text()
        val pred = WebpageClassifier.predict(pageText, clf)
        val predLabel = pred.getLabeling.getBestLabel.toString
        val predLabelPretty = if (predLabel.contains('/')) predLabel.split('/').last else predLabel

        //update prediction counts
        predictionCounts.contains(predLabelPretty) match {
          case true => predictionCounts(predLabelPretty) += 1
          case false => predictionCounts += (predLabelPretty -> 1)
        }

        println("page prediction: " + predLabelPretty)

        val linksOnPage = document.select("a[href]")

        val newDepth = depth + 1

        linksOnPage.foreach(page => getPageLinks(page.attr("abs:href"), newDepth))
      }
      catch {
        case e: IOException => println(s"Crawling error for $url: ${e.getMessage}")
        case ia: IllegalArgumentException => println(s"Crawling error for $url: ${ia.getMessage}")
        case genericException: Exception => println(s"Crawling error for $url: ${genericException.getMessage}")
      }
    }
  }

}
