// Databricks notebook source exported at Fri, 21 Oct 2016 18:18:37 UTC
// MAGIC %md 
// MAGIC <img src="http://www.scala-lang.org/resources/img/smooth-spiral.png" style="float: right; height: 100px; margin-right: 50px; margin-top: 20px"/>
// MAGIC 
// MAGIC Brad Miller <br>
// MAGIC Senior Software Engineer at [QuintilesIMS](http://www.quintilesims.com) <br>
// MAGIC [Blog](http://www.bradfordmiller.org)

// COMMAND ----------

// MAGIC %md
// MAGIC ### Functions
// MAGIC This lesson is going to cover Scala functions. Throughout this lesson I will point out where Scala's grammar differs from Java.
// MAGIC 
// MAGIC #### I like this definition
// MAGIC Functions are "self contained" modules of code that accomplish a specific task. Functions usually "take in" data, process it, and "return" a result. 
// MAGIC 
// MAGIC More on function basics here:
// MAGIC [Functions](http://www.cs.utah.edu/~germain/PPS/Topics/functions.html)
// MAGIC 
// MAGIC #### Notes and Terminology
// MAGIC - Functions are <i> invoked </i> with a list of arguments to produce a result
// MAGIC - A Scala function has a parameter list, a body, and a result type.
// MAGIC - Scala Function definitions start with <b> def </b>

// COMMAND ----------

def max(x: Int, y: Int): Int = {
  if(x > y) x
  else y
}

// COMMAND ----------

// MAGIC %md
// MAGIC #### Differences from Java
// MAGIC - No `public` access modifier is needed, `public` is the default accessor
// MAGIC - Each parameter is defined by its name, followed by its data type
// MAGIC - Scala function return type comes at the end of the function definition
// MAGIC - Scala does not require the `return` keyword - The last value computed by the function is always returned
// MAGIC - The `=` sign denotes that the function returns a value

// COMMAND ----------

max(10, 15)

// COMMAND ----------

// MAGIC %md
// MAGIC - We heard a little bit about this in Sujan's talk (Referential Transparency).
// MAGIC - Quick description:  If we have an expression x + y, and we assign it to variable z we get the function z = x + y. 
// MAGIC - <b> For a function to be referentially transparent, anywhere the expression x + y exists could be replaced by variable z without changing the behavior of the program. </b>
// MAGIC - A <b> pure function </b> is a function that is referentially transparent and has no <b> side effects </b>.
// MAGIC - A few examples of program side effects would be: mutating the value of a variable, writing program data out to a file, inserting a row to a database, etc

// COMMAND ----------

// MAGIC %md
// MAGIC Note: Writing purely functional code can seem daunting if you are coming from a purely Object-Oriented background. A good way to practice is to start writing code which prefers immutability, and switch to a mutable approach when dealing with I/O operations like network/database/file interactions.

// COMMAND ----------

//This function is impure because the sum variable is mutated every time it adds a new number to itself
def adder(a: List[Int]): Int = {
  var sum = 0
  a.foreach {i =>
    sum += i
  }
  sum
}

println(adder(List(1,2,3)))

//This function is impure because it prints to standard out, so it is using I/O
def printName(s: String) = println(s)

printName("Brad")

// COMMAND ----------

//These functions are pure because they are referentially transparent and have no side effects
def addMe(x: Int, y: Int): Int = (3 * x) + (4 * y)

val m = Math.PI

// COMMAND ----------

// MAGIC %md
// MAGIC For simple functions, the preferred Scala style is to keep the code concise.  Scala allows you to avoid brackets if all logic can fit on a single line.  

// COMMAND ----------

def max(x: Int, y: Int) = if(x > y) x else y

// COMMAND ----------

// MAGIC %md
// MAGIC - Scala has a feature known as <b> type inference </b> which is means that it can automatically deduce the return type of an expression. In this case, the function returns either x or y, which are both integers.  Scala uses this information to infer the return type of the function to be an integer as well.
// MAGIC - Note that for recursive functions you must provide the function return type or you will receive a compilation error.

// COMMAND ----------

def max(x: Int, y: Int) = {
  if(x > y) "HELLO"
  else y
}

// COMMAND ----------

// MAGIC %md
// MAGIC Note that the return type of our function changed, but still happily compiled.

// COMMAND ----------

def max(x: Int, y: Int): Int = {
  if(x > y) "HELLO"
  else y
}

// COMMAND ----------

// MAGIC %md
// MAGIC As a general rule, prefer providing an explicit return type for your Scala functions.

// COMMAND ----------

// MAGIC %md
// MAGIC Scala has a type known as Unit, which you can think of an analogous to void in Java or C++. Any function with a return type of Unit effectively returns no value.  

// COMMAND ----------

def printMe(name: String): Unit = {println(name)}

printMe("Brad")

// COMMAND ----------

// MAGIC %md
// MAGIC If a function declaration contains no equal sign, the compiler will infer the result type to be Unit

// COMMAND ----------

def doubleNum(i: Int) {i * 2}

println(doubleNum(2))

// COMMAND ----------

// MAGIC %md
// MAGIC Note: If you have a function with a return type of Unit, you likely have side effects going on in your program

// COMMAND ----------

// MAGIC %md
// MAGIC The triple question mark syntax indicates a method that is not yet implemented.  This can be useful for stubbing out methods that you'll need to implement at a later date.  Think of it like a TODO comment.

// COMMAND ----------

def doSomeTask(i: Int): String = ???

doSomeTask(10)

// COMMAND ----------

//If-else blocks behave like functions, so you can do this:
val x = 10
val isDivByTwo = if(x % 2 == 0) true else false

println(isDivByTwo)

// COMMAND ----------

// MAGIC %md
// MAGIC Scala supports the concept of <b> function overloading</b>.  This is the ability to create multiple functions of the same name with different signatures.

// COMMAND ----------

def addMe(i: Int, j: Int): Int = i + j
def addMe(s: String, t: String): String = s + t

println(addMe(2, 4))
println(addMe("HELLO", "GOODBYE"))

// COMMAND ----------

// MAGIC %md
// MAGIC Scala provides the ability to give function parameters default values so that the caller can omit those parameters when the function is invoked

// COMMAND ----------

def addMe(i: Int = 15, j: Int = 10) = i + j

println(addMe())
println(addMe(10))
println(addMe(5, 5))

// COMMAND ----------

// MAGIC %md
// MAGIC During function invocation, the arguments in the call are matched one by one to the order of the parameters in the called function.  Scala has a feature know as <b> named parameters </b>. This allows the caller of the function to pass in arguments in a different order than that specified in the function's signature.

// COMMAND ----------

def addMe(i: Int, j: Int) = i + j

println(addMe(j = 10, i = 5))

// COMMAND ----------

//Named parameters can be useful to uniquely identify default parameters when there is more than one of the same type
def addThree(i: Int, j: Int = 5, k: Int = 10) = {i + j + k}

addThree(50, k = 70)

// COMMAND ----------

// MAGIC %md
// MAGIC Scala has a feature known as  <b> variable arguments </b> which allows for the last parameter to a function to be repeated.  This allows the client to pass in a <b> variable length </b> argument during the functions invocation.

// COMMAND ----------

def printInts(name: String, nums: Int*) = {
  println("My name is " + name + " and these are my numbers:")
  nums.foreach(println)
}

printInts("Test", 7)
printInts("Brad", 5, 10, 20)

// COMMAND ----------

// MAGIC %md
// MAGIC - Scala has a feature known as a <b> nested function</b>.  This allows the programmer to define a function "inside" another function. In this case, the outer function is known as the <b> enclosing function</b>. 
// MAGIC - The nested function is invisible outside of its immediately enclosing function. It also has access to all local objects of its enclosing function. This is also known as a <b> closure </b> which I will talk about in a few minutes.
// MAGIC - It's generally recommended only to nest your functions a few levels deep.

// COMMAND ----------

def printName(name: String): Unit = {
  //nested function - removes last character in a string
  def cleanString(s: String): String = {
    s.dropRight(1)
  }
  //invocation of inner function by enclosing function
  println(cleanString(name))
}

printName("Brad!")

// COMMAND ----------

// MAGIC %md
// MAGIC - The above example can be made simpler once we understand the concept of a closure
// MAGIC - The `cleanString` function has access to the state of the enclosing function `printName`, hence passing the name to the `cleanString` function is unnecessary
// MAGIC - The `name` parameter can be accessed directly within the the `cleanString` function

// COMMAND ----------

def printName(name: String): Unit = {
  //nested function - removes last character in a string
  def cleanString: String = {
    name.dropRight(1)
  }
  //invocation of inner function by enclosing function
  println(cleanString)
}

printName("Brad!")

// COMMAND ----------

// MAGIC %md
// MAGIC 
// MAGIC > From [Wikipedia](https://en.wikipedia.org/wiki/Anonymous_function):
// MAGIC 
// MAGIC > In computer programming, an anonymous function (function literal, lambda abstraction) is a function definition that is not bound to an identifier. Anonymous functions are often:
// MAGIC <br><br>
// MAGIC > 1. arguments being passed to higher-order functions.
// MAGIC > 2. used for constructing the result of a higher-order function that needs to return a function.
// MAGIC 
// MAGIC Scala has support for <b> first-class functions </b> which means it treats functions as first class citizens. This means that Scala has the ability to pass functions as arguments to other functions, return functions from other functions, assign functions to variables, and store functions in data structures.
// MAGIC 
// MAGIC A <b> higher-order function </b> is a function which takes one or more functions as parameter arguments or returns a function as a result.

// COMMAND ----------

//removeDashes is an anonymous function that accepts a single string as its parameter and has a return type of string
val removeDashes: String => String = (a) => a.replaceAll("-", " ")

//Invocation of the anonymous function
removeDashes("HELLO-WORLD-GOODBYE-WORLD")

// COMMAND ----------

//Note that we now have to provide a type for a because it's not specified in the variable type
val removeDashes = (a: String) => a.replaceAll("-", " ")

//Still works!
removeDashes("HELLO-WORLD-GOODBYE-WORLD")

// COMMAND ----------

//An anonymous function with 0 parameters and a return type of String
val getName = () => "Brad"

println(getName())

//An anonymous function with 2 String parameters and a return type of Unit
val printName = (first: String, last: String) => println("My name is " + first + " " + last)

printName("Brad", "Miller")

//An anonymous function with a String parameter, an Int parameter, and a return type of String
val dropEndChars = (s: String, dropCount: Int) => s.dropRight(dropCount)

println(dropEndChars("Brad", 3))

// COMMAND ----------

//This function takes in a string to be cleaned, and a function for cleaning the string
def cleaner(s: String, f: String => String): String = {
  f(s)
}

val removeDashes = (dashString: String) => dashString.replaceAll("-", " ")
val removeTrailingComma = (commaString: String) => commaString.dropRight(1)

println(cleaner("TEST-STRING", removeDashes))
println(cleaner("Brad,", removeTrailingComma))

// COMMAND ----------

def getTotal(applyTax: Boolean) = {
  if(applyTax)
    (d:Double) => d * 1.06
  else
    (d: Double) => d
}

val calcTax = getTotal(true)
println(calcTax(100.00))

val calcNoTax = getTotal(false)
println(calcNoTax(100.00))

// COMMAND ----------

// MAGIC %md
// MAGIC If there's time, we can briefly explore a few more advanced topics:
// MAGIC - Generic Methods - Scala's type system is a massive topic. This will just give you barely a taste of its capabilities.
// MAGIC - Multiple parameter sets
// MAGIC - Partially applied functions

// COMMAND ----------

//This will return different types of tuples depending upon the input types
def tuplize[T,U](x: T, y: U): (T,U) = (x, y)

tuplize(1, "HELLO")

// COMMAND ----------

tuplize(2, 1.25)

// COMMAND ----------

def addMe(x: Int)(y: Int): Int = x + y

addMe(1)(2)

// COMMAND ----------

// MAGIC %md
// MAGIC More information on where this can be useful is [here](http://daniel-spewaks-scala-style-guide.readthedocs.io/en/latest/declarations/methods/currying.html)

// COMMAND ----------

// MAGIC %md
// MAGIC - When a function is invoked, the function is being <b> applied </b> to the parameters.  If all parameters are passed, then the function is considered to be fully applied. If only a portion of the parameters are provided, a partially applied function is returned.

// COMMAND ----------

//Partially applied function
def addMe(x: Int, y: Int) = x + y

//This actually returns a function of type Int => Int because both parameters are not provided
val f = addMe(1, _ : Int)

//Once the second parameter is provided the function is considered to be fully applied and returns a result of 3
f(2)

// COMMAND ----------

//Also can be expressed with multiple parameter sets
def addMe(x: Int)(y: Int): Int = x + y

//Second parameter set is not provided so a function Int => Int is returned
val f = addMe(1)_

//Again, the second parameter list is populated, so the function is fully applied and returns a result
f(2)
