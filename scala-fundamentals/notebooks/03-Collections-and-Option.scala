// Databricks notebook source exported at Fri, 21 Oct 2016 18:19:36 UTC
// MAGIC %md
// MAGIC Scala Fundamentals: Collections
// MAGIC ================================
// MAGIC ![Wingspan Logo][logo]  
// MAGIC Martin Snyder  
// MAGIC CTO, [Wingspan Technology, Inc.][wingspan]  
// MAGIC Co-organizer, [PHASE][phase]  
// MAGIC [@MartinSnyder][twitter] / [Blog][blog]
// MAGIC 
// MAGIC [logo]: http://www.wingspan.com/wp-content/uploads/2016/01/Wingspan-Logo-300x100.gif
// MAGIC [wingspan]: http://www.wingspan.com
// MAGIC [phase]: https://www.meetup.com/scala-phase/
// MAGIC [twitter]: https://twitter.com/MartinSnyder
// MAGIC [blog]: http://martinsnyder.net/

// COMMAND ----------

// MAGIC %md
// MAGIC Example Context
// MAGIC ===============

// COMMAND ----------

object Example {
  case class Person(firstName: String, lastName: String, salary: Int, ssn: String) {
    override def toString: String = s"$firstName $lastName"
  }

  val AllPeople = List(
    new Person("Abby",   "Adams",    203190, "913-98-6520"),
    new Person("Brian",  "Bosworth",  57945, "955-66-1239"),
    new Person("Clair",  "Cassidy",  107455, "942-90-6988"),
    new Person("Dan",    "Dirkes",   207472, "978-38-9209"),
    new Person("Elaine", "Edwards",  231743, "943-68-7575")
  )
}

// COMMAND ----------

// MAGIC %md
// MAGIC Example 1 - Overview
// MAGIC ====================
// MAGIC Produce a map of SSN->Person for all of the "high earners" defined as those earning more than $200,000/year. 

// COMMAND ----------

// MAGIC %md
// MAGIC Solution 1a
// MAGIC -----------
// MAGIC Naive solution using mutable collections.

// COMMAND ----------

import scala.collection.mutable.HashMap

def buildMapOfHighEarners(people: List[Example.Person]): HashMap[String, Example.Person] = {
  val highEarners = new HashMap[String, Example.Person]()

  people.foreach(person => {
    if (person.salary > 200000) {
      highEarners.put(person.ssn, person)
    }
  })

  highEarners
}

val highEarners = buildMapOfHighEarners(Example.AllPeople)

// COMMAND ----------

// MAGIC %md
// MAGIC Solution 1b
// MAGIC -----------
// MAGIC Functional solution using immutable collections.

// COMMAND ----------

val highEarners = Example.AllPeople
  .filter(person => person.salary > 200000)
  .map(person => person.ssn -> person)
  .toMap

// COMMAND ----------

// MAGIC %md
// MAGIC Collections Library
// MAGIC =============================
// MAGIC Common Classes
// MAGIC --------------
// MAGIC * [scala.collection.immutable.List][list.class]
// MAGIC * [scala.collection.immutable.Set][set.class]
// MAGIC * [scala.collection.immutable.Map][map.class]
// MAGIC 
// MAGIC Advantages of Immutable Collection
// MAGIC ----------------------------------
// MAGIC * Zero-cost copy operations
// MAGIC * Free "snapshots" of data
// MAGIC * Clearer picture of data transitions
// MAGIC * Related structures can share elements (see [reftree][reftree])
// MAGIC 
// MAGIC [list.class]: http://www.scala-lang.org/api/2.10.3/#scala.collection.immutable.List
// MAGIC [set.class]: http://www.scala-lang.org/api/2.10.3/#scala.collection.immutable.Set
// MAGIC [map.class]: http://www.scala-lang.org/api/2.10.3/#scala.collection.immutable.Map
// MAGIC [reftree]: https://github.com/stanch/reftree

// COMMAND ----------

// MAGIC %md
// MAGIC Example 2 - Adding to a collection
// MAGIC ===================================
// MAGIC Make a copy of 'AllPeople' with a new person

// COMMAND ----------

// MAGIC %md
// MAGIC Solution 2a
// MAGIC -----------
// MAGIC Mutable collection solution

// COMMAND ----------

import scala.collection.mutable.ListBuffer

def addPerson(existingPeople: List[Example.Person], newPerson: Example.Person): List[Example.Person] = {
  val updatedPeople = new ListBuffer[Example.Person]()

  existingPeople.foreach(person => updatedPeople.append(person))
  updatedPeople.append(newPerson)

  updatedPeople.toList
}

val evenMorePeople = addPerson(Example.AllPeople, Example.Person("William", "Gates", 0, "123-45-6789"))

// COMMAND ----------

// MAGIC %md
// MAGIC Solution 2b
// MAGIC -----------
// MAGIC Optional immutable list solution

// COMMAND ----------

val evenMorePeople = Example.Person("William", "Gates", 0, "123-45-6789") :: Example.AllPeople

// COMMAND ----------

// MAGIC %md
// MAGIC Solution 2c
// MAGIC -----------
// MAGIC Adding to the end of a list

// COMMAND ----------

val evenMorePeople = Example.AllPeople ::: List(Example.Person("William", "Gates", 0, "123-45-6789"))

// COMMAND ----------

// MAGIC %md
// MAGIC Example 3 - Building Sets and Maps
// MAGIC ==================================

// COMMAND ----------

val billGates = Example.Person("William", "Gates", 0, "123-45-6789")
val tuple: (String, Example.Person) = billGates.ssn -> billGates

val setOfPeople1: Set[Example.Person] = Example.AllPeople.toSet
val setOfPeople2: Set[Example.Person] = Set(billGates)

val mapOfPeople1: Map[String, Example.Person] = Map(tuple)
val mapOfPeople2: Map[String, Example.Person] = List(tuple).toMap

// COMMAND ----------

// MAGIC %md
// MAGIC Common Collection Methods
// MAGIC =========================
// MAGIC * filter
// MAGIC * map
// MAGIC * flatten
// MAGIC * flatMap
// MAGIC 
// MAGIC See documentation for [scala.collection.Traversable][traversable]
// MAGIC 
// MAGIC [traversable]: http://www.scala-lang.org/api/2.10.3/#scala.collection.Traversable

// COMMAND ----------

// MAGIC %md
// MAGIC Example 4 - Filter
// MAGIC ==================
// MAGIC Establish a collection of the people whose first name is one letter shorter than their last name

// COMMAND ----------

val somePeople = Example.AllPeople.filter(person => person.firstName.length == person.lastName.length - 1)

// COMMAND ----------

// MAGIC %md
// MAGIC Example 5 - Map
// MAGIC ===============
// MAGIC Transform a list of 'Person' objects to a collection of SSN (String) values

// COMMAND ----------

val ssns: Seq[String] = Example.AllPeople.map(person => person.ssn)

// COMMAND ----------

// MAGIC %md
// MAGIC Example 6 - Flatten
// MAGIC ===================
// MAGIC Transform a list of integers into a list of the factors of those integers

// COMMAND ----------

def getFactors(number: Int): Seq[Int] = {
  (2 until number)
    .filter(candidate => number % candidate == 0)
}

val numbers = (11 to 20 by 3)
val factors = numbers.map(getFactors)
val flattenedFactors = factors.flatten

val distinctSortedFactors =
  (11 to 20 by 3)
    .map(getFactors)
    .flatten
    .distinct
    .sorted

// COMMAND ----------

// MAGIC %md
// MAGIC Example 7 - FlatMap
// MAGIC ===================
// MAGIC Transform a list of integers into a list of the factors *of the factors* of those integers

// COMMAND ----------

def getFactors(number: Int): Seq[Int] = {
  (2 until number)
    .filter(candidate => number % candidate == 0)
}

val distinctSortedFactorsOfFactors =
  (11 to 20 by 3)
    .flatMap(getFactors)
    .flatMap(getFactors)
    .distinct
    .sorted

// COMMAND ----------

// MAGIC %md
// MAGIC Option
// MAGIC ======
// MAGIC Conceptualize an Option as *either*
// MAGIC * A collection with at *most* one element
// MAGIC * A type-safe null
// MAGIC 
// MAGIC Tony Morris' [Cheat Sheet][option-cheat-sheet]
// MAGIC 
// MAGIC [option-cheat-sheet]: http://blog.tmorris.net/posts/scalaoption-cheat-sheet/

// COMMAND ----------

// MAGIC %md
// MAGIC Example 8 - Handling Unexpected Input
// MAGIC =====================================
// MAGIC Write a function that operates safely on a potentially missing Person

// COMMAND ----------

// MAGIC %md
// MAGIC Solution 8a
// MAGIC -----------
// MAGIC Traditional approach using null

// COMMAND ----------

def getFirstName(person: Example.Person): String = {
  if (person != null) {
    person.firstName
  }
  else {
    null
  }
}

val shouldBeNull = getFirstName(null)
val shouldBeBill = getFirstName(Example.Person("William", "Gates", 0, "123-45-6789"))

// COMMAND ----------

// MAGIC %md
// MAGIC Solution 8b
// MAGIC -----------
// MAGIC Naive approach using Option

// COMMAND ----------

def getFirstName(person: Option[Example.Person]): String = {
  if (person.isDefined) {
    return person.get.firstName
  }
  else {
    return ""
  }
}

val shouldBeBlank = getFirstName(None)
val shouldBeBill = getFirstName(Some(Example.Person("William", "Gates", 0, "123-45-6789")))

// COMMAND ----------

// MAGIC %md
// MAGIC Solution 8c
// MAGIC -----------
// MAGIC Functional approach using Option

// COMMAND ----------

def getFirstName(person: Option[Example.Person]): Option[String] =
  person.map(p => p.firstName)

val shouldBeNone = getFirstName(None)
val shouldBeBill = getFirstName(Some(Example.Person("William", "Gates", 0, "123-45-6789")))

// COMMAND ----------

// MAGIC %md
// MAGIC Exercise 9 - Handling unsafe values from unsafe sources
// MAGIC ========================================================
// MAGIC Use Option to represent handle sentinel values (incuding null)

// COMMAND ----------

// MAGIC %md
// MAGIC Solution 9a
// MAGIC ------------
// MAGIC Naive approach using conditionals

// COMMAND ----------

def cleanup(unsafeString: String): Option[String] = {
  if (unsafeString == null || unsafeString == "")
    None
  else
    Some(unsafeString) // now safe!
}

val ex1 = cleanup(null)
val ex2 = cleanup("")
val ex3 = cleanup("Actual Value")

// COMMAND ----------

// MAGIC %md
// MAGIC Solution 9b
// MAGIC ------------
// MAGIC Functional approach using factory method and filter

// COMMAND ----------

def cleanup(unsafeString: String): Option[String] = {
  Option(unsafeString).filter(s => !s.isEmpty)
}

val ex1 = cleanup(null)
val ex2 = cleanup("")
val ex3 = cleanup("Actual Value")

// COMMAND ----------

// MAGIC %md
// MAGIC Retrieving values from collections
// MAGIC =================================
// MAGIC * Conditionals w/unsafe gets
// MAGIC * Safe gets
// MAGIC * Pattern matching

// COMMAND ----------

// MAGIC %md
// MAGIC Exercise 10 - Unsafe gets
// MAGIC -------------------------

// COMMAND ----------

def addFive(i: Option[Int]): Int = {
  i.get + 5
}

def safeAddFive(maybeInt: Option[Int]): Int = {
  if (maybeInt.isDefined)
    maybeInt.get + 5
  else
    5
}

val ten = addFive(Some(5))
// val five = addFive(None)
val safeTen = safeAddFive(Some(5))
val safeFive = safeAddFive(None)

// COMMAND ----------

val numbers = List(1, 2, 3)
val noNumbers = Nil

val one = numbers.head
// val oops = noNumbers.head

// COMMAND ----------

val numbers: Map[String, Int] = Map(
  "one" -> 1,
  "two" -> 2,
  "three" -> 3
)

val two: Int = numbers.get("two").get

// COMMAND ----------

// MAGIC %md
// MAGIC Exercise 11 - Safe gets
// MAGIC -----------------------

// COMMAND ----------

def addFive(maybeInt: Option[Int]): Int =
  maybeInt.getOrElse(0) + 5

val ten = addFive(Some(5))
val five = addFive(None)

// COMMAND ----------

val numbers = List(1, 2, 3)
val noNumbers = Nil

val one = numbers.headOption.getOrElse(0)
val zero = noNumbers.headOption.getOrElse(0)

// COMMAND ----------

val numbers: Map[String, Int] = Map(
  "one" -> 1,
  "two" -> 2,
  "three" -> 3
)

val trial: Int = numbers.get("two").getOrElse(2)
val two: Int = numbers.getOrElse("two", 2)

// COMMAND ----------

// MAGIC %md
// MAGIC Recap
// MAGIC =====

// COMMAND ----------

// MAGIC %md
// MAGIC Exercise 12
// MAGIC ===========
// MAGIC Extra flatMap example

// COMMAND ----------

/*
  val AllPeople = List(
    new Person("Abby",   "Adams",    203190, "913-98-6520"),
    new Person("Brian",  "Bosworth",  57945, "955-66-1239"),
    new Person("Clair",  "Cassidy",  107455, "942-90-6988"),
    new Person("Dan",    "Dirkes",   207472, "978-38-9209"),
    new Person("Elaine", "Edwards",  231743, "943-68-7575")
  )
*/

val Beneficiaries = Map(
  "913-98-6520" -> "955-66-1239",
  "942-90-6988" -> "978-38-9209",
  "943-68-7575" -> "000-00-0000"
)

def getPersonBySSN(ssn: String) = Example.AllPeople.find(person => person.ssn == ssn)

val beneficiaries: List[Example.Person] = Example.AllPeople
  .flatMap(person => Beneficiaries.get(person.ssn))
  .flatMap(ssn => getPersonBySSN(ssn))
