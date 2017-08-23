package com.viglink.webpageclassification.demos.classifier

import java.io.File

import com.viglink.webpageclassification.WebpageClassifier

object Train15CategoryClassifier extends App{

  //specify location of training data
  val trainingPath = new File("src/main/resources/trainingdata/15Categories")
  //val trainingPath = new File("/users/alexminnaar/15_cat_training_data")
  //specify proportion to train on and proportion to test on
  val trainTestSplit = Array(80.0, 20.0)
  val clf = WebpageClassifier.train(trainingDataDir = trainingPath, trainTestRatio = trainTestSplit)


  WebpageClassifier.saveClassifier(new File("15_cat_classifier.bin"),clf)


}
