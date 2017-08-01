package com.viglink.webpageclassification.demos

import java.io.{File, PrintWriter}

import com.viglink.webpageclassification.{SiteCrawler, WebpageClassifier}


object MerchantPublisherCrawler extends App {

  //train a forum/notforum classifier
  val trainingPath = new File("src/main/resources/trainingdata/publisherormerchant")
  //specify proportion to train on and proportion to test on
  val trainTestSplit = Array(100.0, 0.0)
  //train classifier
  val clf = WebpageClassifier.train(trainingDataDir = trainingPath, trainTestRatio = trainTestSplit)


  //create crawler that uses this classifier
  val crawler = new SiteCrawler(maxDepth = 2, clf = clf)
  crawler.getPageLinks("https://www.mmamania.com/")
  //print # pages predicted for each class
  println(crawler.predictionCounts)



//  val f = new File("src/main/resources/trainingdata/publisherormerchant/publisher/sbnation.html")
//  val s = WebpageClassifier.removeHtml(f)
//  val pr = new PrintWriter(new File("src/main/resources/trainingdata/publisherormerchant/publisher/sbnation.html.txt"))
//  pr.close()

}
