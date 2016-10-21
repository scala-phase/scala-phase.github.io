// Databricks notebook source exported at Fri, 21 Oct 2016 18:19:04 UTC
// MAGIC %md
// MAGIC <img src="http://www.scala-lang.org/resources/img/smooth-spiral.png" style="float: right; height: 100px; margin-right: 50px; margin-top: 20px"/>
// MAGIC # Scala Fundamentals: For Comprehensions
// MAGIC 
// MAGIC Marcus Henry, Jr. [@dreadedsoftware](https://twitter.com/dreadedsoftware)

// COMMAND ----------

// MAGIC %md
// MAGIC 
// MAGIC The only symbol we need to learn here is `<-` which can be read as the word 'from'.
// MAGIC 
// MAGIC ### Here we will cover for comprehensions.
// MAGIC <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>

// COMMAND ----------

val seq = Seq(0, 1, 2, 3, 4)

// COMMAND ----------

val copy = collection.mutable.Buffer[Int]()
for(index <- (0 until seq.size)){
  val item = seq(index)
  copy += item
}
println("They are equal: " + (seq == copy))

// COMMAND ----------

val double = collection.mutable.Buffer[Int]()
for(index <- (0 until seq.size)){
  val item = seq(index) * 2
  double += item
}

// COMMAND ----------

val biggerSeq = collection.mutable.Buffer[Int]()
for(index <- (0 until seq.size)){
  val item = seq(index)
  val inner = collection.mutable.Buffer[Int]()
  for(int <- (1 to item)){
    inner += int
  }
  biggerSeq ++= inner
}

// COMMAND ----------

val seq1 = Seq(0, 1, 2, 3, 4)
val seq2 = Seq(5, 6, 7, 8, 9)
val seq3 = Seq(10, 11, 12, 13, 14)
val seq4 = Seq(15, 16, 17, 18, 19)

val lots = collection.mutable.Buffer[Int]()
for(item1 <- seq1){//scala has foreach syntax!
  for(item2 <- seq2){
    for(item3 <- seq3){
      for(item4 <- seq4){
        val item = item1 + item2 + item3 + item4
        lots += item
      }
    }
  }
}

// COMMAND ----------

// MAGIC %md
// MAGIC # This is Scala! We can type less
// MAGIC 
// MAGIC Multiple nested for expressions can be written together!
// MAGIC 
// MAGIC <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>

// COMMAND ----------

val seq1 = Seq(0, 1, 2, 3, 4)
val seq2 = Seq(5, 6, 7, 8, 9)
val seq3 = Seq(10, 11, 12, 13, 14)
val seq4 = Seq(15, 16, 17, 18, 19)

val lots = collection.mutable.Buffer[Int]()
for(item1 <- seq1; item2 <- seq2; item3 <- seq3; item4 <- seq4){
  val item = item1 + item2 + item3 + item4
  lots += item
}

// COMMAND ----------

val seq1 = Seq(0, 1, 2, 3, 4)
val seq2 = Seq(5, 6, 7, 8, 9)
val seq3 = Seq(10, 11, 12, 13, 14)
val seq4 = Seq(15, 16, 17, 18, 19)

val lots = collection.mutable.Buffer[Int]()
for{item1 <- seq1
  item2 <- seq2
  item3 <- seq3
  item4 <- seq4
}{
  val item = item1 + item2 + item3 + item4
  lots += item
}

// COMMAND ----------

// MAGIC %md
// MAGIC ####Hey! This doesn't look like a loop!
// MAGIC <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>

// COMMAND ----------

val lots = collection.mutable.Buffer[Int]()
val forExpression = for{item1 <- seq1
  item2 <- seq2
  item3 <- seq3
  item4 <- seq4
}{
  val item = item1 + item2 + item3 + item4
  lots += item
}

// COMMAND ----------

val lots = for{item1 <- seq1
  item2 <- seq2
  item3 <- seq3
  item4 <- seq4
}yield{
  item1 + item2 + item3 + item4
}

// COMMAND ----------

// MAGIC %md
// MAGIC #### So we have
// MAGIC 1. An item from a Seq
// MAGIC 2. Another item from a different Seq
// MAGIC 3. Twice more compounded to compute a new value
// MAGIC 4. accumulate all those nested values into a new Seq
// MAGIC 
// MAGIC ### This seems awfully familiar.
// MAGIC <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>

// COMMAND ----------

val lots = seq1.flatMap{item1 =>
  seq2.flatMap{item2 =>
    seq3.flatMap{item3 =>
      seq4.map{item4 =>
        item1 + item2 + item3 + item4
      }
    }
  }
}

// COMMAND ----------

// MAGIC %md
// MAGIC So nesting
// MAGIC <pre>
// MAGIC `for(a <- A){
// MAGIC   for(b <- B){
// MAGIC     for(c <- C){func}
// MAGIC   }
// MAGIC }`
// MAGIC </pre>
// MAGIC is equal to curly braced compounding
// MAGIC <pre>
// MAGIC `for{a <- A
// MAGIC   b <- B
// MAGIC   c <- C
// MAGIC }{func}`
// MAGIC </pre>
// MAGIC is equal to nested flatMaps with a map at the end
// MAGIC <pre>
// MAGIC `A.flatMap{a =>
// MAGIC   B.flatMap{b =>
// MAGIC     C.map{func}
// MAGIC   }
// MAGIC }`
// MAGIC </pre>
// MAGIC This is really what the compiler is doing, it converts for into chains of function calls.
// MAGIC <br/><br/><br/><br/>

// COMMAND ----------

val copy1 = collection.mutable.Buffer[Int]()
for(item <- seq){
  copy1 += item
}

val copy2 = collection.mutable.Buffer[Int]()
seq.foreach{item =>
  copy2 += item
}

println("They are equal: " + (copy1 == copy2))

// COMMAND ----------

// MAGIC %md
// MAGIC A for comprehension is just syntactic sugar over the common collections operations. We have seen foreach, map and flatMap. What other common operations can we try inside a for comprehension?
// MAGIC <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>

// COMMAND ----------

//commonly
val seq = Seq(0, 1, 2, 3, 4, 5, 6, 7)
val even = seq.flatMap{item1 =>
  (0 to item1).withFilter(item2 => (0 == (item2 % 2))).map{item2 =>
    item1 * item2
  }
}

val evenSimpler = for{
  item1 <- seq
  item2 <- (0 to item1)
  if(0 == (item2 % 2))
}yield{
  item1 * item2
}

println("They are equal: " + (even.toSeq == evenSimpler.toSeq))//note the toSeq

// COMMAND ----------

// MAGIC %md
// MAGIC ### Excercise
// MAGIC Convert the following to for comprehensions.
// MAGIC 
// MAGIC #### 1.
// MAGIC <pre>
// MAGIC val seq = Seq(0, 1, 2, 3, 4)
// MAGIC val result1 = seq.flatMap{i1 =>
// MAGIC   (0 to i1 by 3).withFilter(i2 => (1 == (i2 %2))).map{i2 =>
// MAGIC     i1 + i2
// MAGIC   }
// MAGIC }
// MAGIC </pre>
// MAGIC 
// MAGIC #### 2.
// MAGIC <pre>
// MAGIC val seq = Seq(0, 1, 2, 3, 4)
// MAGIC val result2 = seq.
// MAGIC   flatMap(i => (i to 5)).
// MAGIC   withFilter(i => 1 != (i % 3)).
// MAGIC   map{i => i * 2}
// MAGIC </pre>

// COMMAND ----------

//Exercise
val seq = Seq(0, 1, 2, 3, 4)
val result1 = for{
  //fill this in
}yield{
  //fill this in
}

val result2 = for{
  //fill this in
}yield{
  //fill this in
}

// COMMAND ----------

//Answer
val seq = Seq(0, 1, 2, 3, 4)
val result1 = for{
  i1 <- seq
  i2 <- (0 to i1 by 3)
  if(1 == (i2 % 2))
}yield{
  i1 + i2
}

val result2 = for{
  i <- seq
  if(1 != (i % 3))
}yield{
  i * 2
}

// COMMAND ----------

// MAGIC %md
// MAGIC - for comprehensions are syntactic sugar for collections operations
// MAGIC - they can equally well handle linear as well as nested calls
// MAGIC 
// MAGIC ### But wait, there's more!
// MAGIC <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>

// COMMAND ----------

val range = (0 to 5)
val seq = for{
  i1 <- range
  i2 <- range
}yield{(i1, i2)}

val seq2 = for{
  (i1, i2) <- seq//pattern match!!!
  if(0 == (i1 % 2) || 1 == (i2 % 2))
}yield{
  i1 + i2
}

// COMMAND ----------

val range = (0 to 5)
val seq1 = for{
  i1 <- range
  i2 <- range
  if(0 == ((i1 + i2) % 2))//computation is doubled! runtime no no!
}yield{
  i1 + i2//computation is doubled! runtime no no!
}

val seq2 = for{
  i1 <- range
  i2 <- range
  t = i1 + i2//single computation, nice
  if(0 == (t % 2))
}yield{
  t
}

// COMMAND ----------

val range: Seq[Int] = (0 to 5)

//match on literal OK
val seq1 = for{
  2 <- range
}yield{
  2
}

//match on extractor OK
val seq2 = for{
  Seq(0, _, 2, _, _, _) <- range.map(_ => range)
}yield{
  2
}

//match on Upper Case identifier NOT OK
//this desugars to a flatMap with argument bound to Caps, shadowing the outside identifier
val Caps = 2
val seq = for{
  Caps <- range
  if(0 == (Caps % 2))
}yield{
  Caps
}

// COMMAND ----------

// MAGIC %md
// MAGIC #### If for is just syntactic sugar...
// MAGIC <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>

// COMMAND ----------

val option = Option(1)
def next(int: Int): Option[Int] = Option(int * 2)
val anotherOption = for{
  i <- option
  j <- next(i)
  k <- next(j)
}yield{
  i + j + k
}

// COMMAND ----------

val option = Option(1)
def next(int: Int): Option[Int] = Option(int * 2)
val anotherOption = for{
  i <- option
  ahhh <- None
  j <- next(i)
  k <- next(j)
}yield{
  i + j + k
}

// COMMAND ----------

import scala.util.Try
val attempt = Try("1")
def next(str: String): Try[String] = Try(str * 2)
val anotherAttempt = for{
  i <- attempt
  j <- next(i)
  k <- next(j)
}yield{
  i + j + k
}

// COMMAND ----------

import scala.util.Try
val attempt = Try("1")
def next(str: String): Try[String] = Try(str * 2)
val anotherAttempt = for{
  i <- attempt
  ahhh <- Try{throw new Throwable("AHHH!!!")}
  j <- next(i)
  k <- next(j)
}yield{
  i + j + k
}

// COMMAND ----------

object custom{
  class GuessingGame[A](values: Seq[A]){
    def doYouHave(a: A): Boolean = values.contains(a)
    
    def map[B](f: A => B) = new GuessingGame(values.map(f))
    def flatMap[B](f: A => Seq[B]) = new GuessingGame(values.flatMap(f))
    def withFilter(f: A => Boolean) = new GuessingGame(values.filter(f))
  }
  def apply(): Unit = {
    val game = new GuessingGame(Seq(1, 2, 3, 9, 21, 100))
    println("have 21: " + game.doYouHave(21) + "; have 19: " + game.doYouHave(19))
    
    //I want a guessing game with double the values added
    //holding onto only even values but as Strings
    val newGame = for{
      i <- game
      j <- Seq(i, i * 2)
      if(0 == (j % 2))
    }yield{
      j.toString()
    }
    
    println("have 42: " + newGame.doYouHave("42") + "; have 19: " + newGame.doYouHave("19"))
  }
}

custom()

// COMMAND ----------

// MAGIC %md
// MAGIC ### In short
// MAGIC - for comprehensions make operating on collections more readable
// MAGIC - for comprehensions are syntactic sugar for map, flatMap, withFilter, among other things
// MAGIC - Since they desugar to lambda functions, assignments and pattern matching can be used
// MAGIC - Best to stick to pattern matchin on Extractors, its guaranteed to work
// MAGIC - for comprehensions are syntactic sugar for methods on classes
// MAGIC     - we can use any class which defines these operations in a for comprehension
// MAGIC     - we can define custom classes for this

// COMMAND ----------


