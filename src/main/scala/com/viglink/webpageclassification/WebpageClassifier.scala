package com.viglink.webpageclassification

import java.io._
import java.util.Random

import cc.mallet.classify.{Classification, Classifier, NaiveBayesTrainer, Trial}
import cc.mallet.pipe._
import cc.mallet.pipe.iterator.FileIterator
import cc.mallet.share.upenn.ner.LengthBins
import cc.mallet.types.{Instance, InstanceList}
import org.jsoup.Jsoup

/**
  * Training, predicting, loading and saving of a webpage classifier
  */
object WebpageClassifier {

  /**
    * Train a Mallet classifier model
    *
    * @param trainingDataDir Path to training data directory
    * @param trainTestRatio  ratio of training data to test data
    * @return A trained Mallet classifier model
    */
  def train(trainingDataDir: File, trainTestRatio: Array[Double]): Classifier = {

    val fileList = trainingDataDir.listFiles().filter(!_.getName.contains(".DS_Store"))

    val trainingDirs = fileList.map(f => new File(trainingDataDir + "/" + f.getName))

    val pipes = Array(
      new Target2Label(),
      new Input2CharSequence("UTF-8"),
      new CharSequence2TokenSequence(),
      new LengthBins("STRINGLENGTH", Array(100, 1000, 5000)),
      //new TokenTextNGrams("PREFIX", Array(0, 1, 2)),
      new TokenSequenceLowercase(),
      new TokenSequenceRemoveStopwords(),
      new TokenSequence2FeatureSequence(),
      new FeatureSequence2FeatureVector()
    )

    val instancePipe = new SerialPipes(pipes)

    //pass training examples through pipes
    val ilist = new InstanceList(instancePipe)
    ilist.addThruPipe(new FileIterator(trainingDirs, FileIterator.STARTING_DIRECTORIES))

    //shuffle the training data
    val r = new Random(System.currentTimeMillis())
    ilist.shuffle(r)

    val ilists = ilist.split(trainTestRatio)

    val training = ilists(0)
    val testing = ilists(1)

    println(s"Training set size: ${training.size()}")
    println(s"Test set size: ${testing.size()}")

    //train a naive bayes classifier
    val naiveBayesTrainer = new NaiveBayesTrainer()
    val classifier = naiveBayesTrainer.train(training)

    val trial = new Trial(classifier, testing)

    val classIter = classifier.getLabelAlphabet.iterator()

    println("\n\nTRAINING RESULTS:")
    //iterate through class labels in test set an get evaluation data
    while (classIter.hasNext) {
      val classLabel = classIter.next().toString

      //pretty print the class label
      val classLabelPretty = if (classLabel.contains('/')) classLabel.split('/').last else classLabel

      println(s"Class Label: $classLabelPretty")
      println(s"Precision: ${trial.getPrecision(classLabel)}, Recall: ${trial.getRecall(classLabel)}, F1: ${trial.getF1(classLabel)}")
    }

    classifier
  }

  /**
    * Predict a document's class for a given classifier
    *
    * @param text Document to classify
    * @param clf  Classifier to apply to document
    * @return a Mallet classification object
    */
  def predict(text: String, clf: Classifier): Classification = {
    clf.classify(clf.getInstancePipe.instanceFrom(new Instance(text, "", "", "")))
  }

  def predictUrl(url:String, clf:Classifier):Classification={
    val html = Jsoup.connect(url).get()
    predict(html.text(),clf)
  }

  /**
    * Remove html tags from an html file
    *
    * @param file html file
    * @return string of page text without html tags
    */
  def removeHtml(file: File): String = {
    val doc = Jsoup.parse(file, "UTF-8")
    doc.text()
  }

  /**
    * Load a classifier from a file
    *
    * @param file file containing serialized classifier
    * @return classifier from file
    */
  def loadClassifier(file: File): Classifier = {

    val ois = new ObjectInputStream(new FileInputStream(file))
    val classifier = ois.readObject().asInstanceOf[Classifier]
    ois.close()

    classifier
  }

  /**
    * Save classifier to file
    *
    * @param file save file
    * @param clf  classifier to save
    */
  def saveClassifier(file: File, clf: Classifier): Unit = {
    val oos = new ObjectOutputStream(new FileOutputStream(file))
    oos.writeObject(clf)
    oos.close()
  }


}
