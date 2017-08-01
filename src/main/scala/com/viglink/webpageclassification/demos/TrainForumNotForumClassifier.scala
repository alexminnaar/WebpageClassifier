package com.viglink.webpageclassification.demos

import java.io.File

import com.viglink.webpageclassification.WebpageClassifier


object TrainForumNotForumClassifier extends App {

  //specify location of training data
  val trainingPath = new File("src/main/resources/trainingdata/forumornotforum")
  //specify proportion to train on and proportion to test on
  val trainTestSplit = Array(80.0, 20.0)
  //train classifier
  val clf = WebpageClassifier.train(trainingDataDir = trainingPath, trainTestRatio = trainTestSplit)
  //save classifier
  WebpageClassifier.saveClassifier(new File("src/main/resources/models/forum_notforum_clf.bin"), clf)

}
