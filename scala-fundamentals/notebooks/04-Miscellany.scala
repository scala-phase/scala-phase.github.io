// Databricks notebook source exported at Fri, 21 Oct 2016 18:18:46 UTC
// MAGIC %md
// MAGIC <img src="http://www.scala-lang.org/resources/img/smooth-spiral.png" style="float: right; height: 100px; margin-right: 50px; margin-top: 20px"/>
// MAGIC # Scala Fundamentals: Miscellany
// MAGIC 
// MAGIC Marcus Henry, Jr. [@dreadedsoftware](https://twitter.com/dreadedsoftware)

// COMMAND ----------

// MAGIC %md
// MAGIC ### Here we will go over the basic uses for Packages and Types in Scala.
// MAGIC #### Packages
// MAGIC - How to define a package
// MAGIC - How to access members of a package
// MAGIC - How to pull members of a package into scope
// MAGIC #### Types
// MAGIC - What expressions in Scala are Objects and what are not
// MAGIC - The Scala type hierarchy

// COMMAND ----------

// MAGIC %md
// MAGIC # Packages in the wild

// COMMAND ----------

//make a package with a value
package ds1
object Something
object Something1
object Something2
object Something3
object Something4
object Something5

// COMMAND ----------

//make a second package with a value
package ds2
object Something
object Something1
object Something2
object Something3
object Something4
object Something5

// COMMAND ----------

// MAGIC %md
// MAGIC We have 2 packages, let's try to access the data in them

// COMMAND ----------

//try to access
println(Something)
println(Something1)
println(Something2)

// COMMAND ----------

// MAGIC %md
// MAGIC That didn't quite work  

// COMMAND ----------

//we access using full name
println(ds1.Something)
println(ds2.Something)
println(ds1.Something1)
println(ds2.Something1)
println(ds1.Something2)
println(ds2.Something2)

// COMMAND ----------

// MAGIC %md
// MAGIC Do I need to type the package name every time?!?!?! That's super tedius, Marcus!

// COMMAND ----------

//we can access the package as a shortcut with an import
object first{
  import ds1.Something
  import ds1.Something1
  import ds1.Something2
  def apply() = {
    println(Something)
    println(Something1)
    println(Something2)
  }
}
object second{
  import ds2.Something
  import ds2.Something1
  import ds2.Something2
  def apply() = {
    println(Something)
    println(Something1)
    println(Something2)
  }
}
first()
second()

// COMMAND ----------

object first{
  import ds1.{Something, Something1, Something2}
  def apply() = {
    println(Something)
    println(Something1)
    println(Something2)
  }
}
object second{
  import ds2.{Something, Something1, Something2}
  def apply() = {
    println(Something)
    println(Something1)
    println(Something2)
  }
}
first()
second()

// COMMAND ----------

object first{
  import ds1._
  def apply() = {
    println(Something)
    println(Something1)
    println(Something2)
  }
}
object second{
  import ds2._
  def apply() = {
    println(Something)
    println(Something1)
    println(Something2)
  }
}
first()
second()

// COMMAND ----------

//can we do both?
object mixed{
  import ds1._
  import ds2._
  def apply() = println(Something)
}

// COMMAND ----------

// MAGIC %md
// MAGIC ### So far
// MAGIC - Packages encapsulate data and functions helping to add some organization to the code.
// MAGIC - Imports break this encalsulation allowing one package to access members of another.
// MAGIC     - Imports can be compactified into single liners if necessary
// MAGIC - The type system will not allow identical symbols to be used at the same time.
// MAGIC     - Beware of name clashes (especially when importing with `_`)
// MAGIC - We can mix and match symbols freely, as long as their names in scope are different

// COMMAND ----------

//working mixed
object mixed{
  import ds1.{Something, Something2, Something4}
  import ds2.{Something1, Something3, Something5}
  def apply() = {
    println(Something)
    println(Something1)
    println(Something2)
    println(Something3)
    println(Something4)
    println(Something5)
  }
}
mixed()

// COMMAND ----------

//can we do both?
object mixed{
  import ds1.{Something => Something1}
  import ds2.{Something => Something2}
  def apply() = {
    println(Something1)
    println(Something2)
  }
}
mixed()

// COMMAND ----------

// MAGIC %md
// MAGIC ### Coffee Break
// MAGIC - Imports in Scala are more powerful than imports in Java
// MAGIC - Imports can be multiple on the same line
// MAGIC - Imports can rename classes and members just for the enclosing scope
// MAGIC - Imports can be placed anywhere in the file; even inside of classes

// COMMAND ----------

// MAGIC %md
// MAGIC ###Let's talk about types
// MAGIC - Everything in Scala is an expression
// MAGIC - Expressions have a type
// MAGIC - Types tell the compiler how to interpret your code
// MAGIC - EVERYTHING in Scala is an Object (in contrast to Java)

// COMMAND ----------

1.toString()

// COMMAND ----------

1L.toString()

// COMMAND ----------

"": Object

// COMMAND ----------

mixed: Object

// COMMAND ----------

1: Object

// COMMAND ----------

// MAGIC %md
// MAGIC I thought everything in Scala was an Object, Marcus!!!
// MAGIC 
// MAGIC It is but, the class Object is a mere subclass of the REAL object class, Any.
// MAGIC 
// MAGIC Any is Scala's top type.

// COMMAND ----------

val a = "": Any
val b = mixed3: Any
val c = 1: Any
val d = new Object(): Any

// COMMAND ----------

// MAGIC %md
// MAGIC Scala is split into two object hierarchies
// MAGIC 
// MAGIC We have AnyRef and AnyVal

// COMMAND ----------

val a = "": AnyRef
val b = mixed3: AnyRef
val c = 1: AnyVal
val d = new Object(): AnyRef

// COMMAND ----------

// MAGIC %md
// MAGIC Scala also has a bottom type: Nothing.

// COMMAND ----------

object hierarchy{
  class A
  class B extends A
  class C extends B
  
  
  val listC: List[C] = List(new C, new C, new C)
  val listB: List[B] = List(new B, new B, new B)
  val listA: List[A] = List(new A, new A, new A)
}

// COMMAND ----------

val listAB: List[hierarchy.A] = hierarchy.listB

// COMMAND ----------

// MAGIC %md
// MAGIC In Scala, List follows the same hierarchy as the classes it contains.
// MAGIC 
// MAGIC If B is a subclass of A; List[B] is a subclass of List[A].
// MAGIC 
// MAGIC What type is the empty list?

// COMMAND ----------

val empty: List[Nothing] = Nil

// COMMAND ----------

val a: List[Int] = empty
val b: List[String] = empty
val c: List[Object] = empty
val d: List[(Int, Double)] = empty

// COMMAND ----------

// MAGIC %md
// MAGIC A single value can represent empty for ALL Lists in Scala

// COMMAND ----------

// MAGIC %md
// MAGIC ### In Short
// MAGIC #### Packages
// MAGIC - Packages help organize code
// MAGIC - Packages can encapsulate functionality between modules
// MAGIC - Import statements break Pakage encapsulation
// MAGIC     - One at a time
// MAGIC     - Many in a single statement
// MAGIC     - All at once
// MAGIC     - Can be placed anywhere in a file (scoped imports)
// MAGIC #### Types
// MAGIC - Everything in Scala is an Object
// MAGIC - The top type in Scala is Any
// MAGIC     - All other types are subtypes of Any
// MAGIC     - Defines functionality that all 
// MAGIC - The bottom type in Scala is Nothing
// MAGIC     - Nothing is a subtype of all other types
// MAGIC     - Provides a common way to model the beginning or end of the world

// COMMAND ----------


