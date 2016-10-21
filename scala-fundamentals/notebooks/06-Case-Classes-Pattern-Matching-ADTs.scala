// Databricks notebook source exported at Fri, 21 Oct 2016 18:24:46 UTC
// MAGIC %md
// MAGIC <img src="http://www.scala-lang.org/resources/img/smooth-spiral.png" style="float: right; height: 100px; margin-right: 50px; margin-top: 20px"/>
// MAGIC 
// MAGIC # Scala Fundamentals: Data
// MAGIC 
// MAGIC Michael Pilquist  
// MAGIC [CCAD, LLC.](http://ccadllc.com) (Comcast / ARRIS, Inc. Joint Venture)  
// MAGIC [@mpilquist](https://twitter.com/mpilquist) / [Blog](http://mpilquist.github.io)

// COMMAND ----------

// MAGIC %md
// MAGIC # Case Classes
// MAGIC 
// MAGIC The `case` modifier on a class results in:
// MAGIC 
// MAGIC  - `val` for each constructor parameter
// MAGIC  - Structural `equals` and `hashCode`
// MAGIC  - Structural `toString`
// MAGIC  - Ability to create instances without `new` keyword
// MAGIC 
// MAGIC  
// MAGIC  Case classes are still classes though! Can add methods, etc.

// COMMAND ----------

case class PhoneNumber(areaCode: String, number: String)

case class Person(firstName: String, lastName: String, salary: Int, ssn: String, phoneNumber: Option[PhoneNumber]) {
  def fullName: String = firstName + " " + lastName
}

val AllPeople = List(
  Person("Lavern", "Clyatt", 203190, "913-98-6520", Some(PhoneNumber("215", "555-1212"))),
  Person("Dia", "Unverzagt", 57945, "955-66-1239", Some(PhoneNumber("215", "555-1313"))),
  Person("Madison", "Vaszily", 107455, "942-90-6988", Some(PhoneNumber("267", "555-1212"))),
  Person("Elwanda", "Hulse", 207472, "978-38-9209", None),
  Person("Mina", "Suckow", 231743, "943-68-7575", None)
)

val lavern: Person = AllPeople(0)
println(s"Lavern's last name is ${lavern.lastName}")
println(s"Lavern's SSN is ${lavern.ssn}")
println(s"Lavern's full name is ${lavern.fullName}")
println(s"Lavern: $lavern")


val dia: Person = AllPeople(1)

println(s"Lavern == Dia? ${lavern == dia}")
println(s"Lavern == Lavern? ${lavern == lavern}")

// COMMAND ----------

// MAGIC %md
// MAGIC 
// MAGIC ![?](http://i.imgur.com/Guv4TBn.png) **So what? I could have written this myself!**
// MAGIC 
// MAGIC - Lots and lots of boilerplate
// MAGIC - Generally accepted best practice to use a case class any time we want `equals`/`hashCode`/`toString` for a data struture
// MAGIC - Encourages immutability and simple data types

// COMMAND ----------

class Person2(val firstName: String, val lastName: String, val salary: Int, val ssn: String, val phoneNumber: Option[PhoneNumber]) {
  override def toString: String = s"String($firstName,$lastName,$salary,$ssn,$phoneNumber)"
  override def equals(o: Any): Boolean = o match {
    case that: Person2 => 
      firstName == that.firstName && 
      lastName == that.lastName && 
      salary == that.salary && 
      ssn == that.ssn &&
      phoneNumber == that.phoneNumber
    case _ => false
  }
  override def hashCode: Int = ??? // Please help me!
}
object Person2 {
  def apply(firstName: String, lastName: String, salary: Int, ssn: String, phoneNumber: Option[PhoneNumber]): Person2 =
    new Person2(firstName, lastName, salary, ssn, phoneNumber)
}

// COMMAND ----------

// MAGIC %md
// MAGIC ## Copying Case Classes
// MAGIC 
// MAGIC Every case class has a `copy` method which returns a clone of the case class with selective changes.

// COMMAND ----------

val shirley = lavern.copy(firstName = "Shirley", lastName = "Feeney")
val noche = dia.copy(firstName = "Noche", ssn = "123-45-6789")

// COMMAND ----------

// MAGIC %md
// MAGIC 
// MAGIC ![?](http://i.imgur.com/Guv4TBn.png) **How would we implement this without case classes?**
// MAGIC 
// MAGIC - Default parameters initialized to field values
// MAGIC - Named parameters at call site

// COMMAND ----------

class Person2(val firstName: String, val lastName: String, val salary: Int, val ssn: String, val phoneNumber: Option[PhoneNumber]) {
  override def toString: String = s"String($firstName,$lastName,$salary,$ssn,$phoneNumber)"
  override def equals(o: Any): Boolean = ???
  override def hashCode: Int = ???
  
  def copy(
    firstName: String = this.firstName, 
    lastName: String = this.lastName,
    salary: Int = this.salary,
    ssn: String = this.ssn,
    phoneNumber: Option[PhoneNumber] = this.phoneNumber
  ): Person2 = new Person2(firstName, lastName, salary, ssn, phoneNumber)
  
}

object Person2 {
  def apply(firstName: String, lastName: String, salary: Int, ssn: String, phoneNumber: Option[PhoneNumber]): Person2 =
    new Person2(firstName, lastName, salary, ssn, phoneNumber)
}

val lavern2 = Person2("Lavern", "Clyatt", 203190, "913-98-6520", Some(PhoneNumber("215", "555-1212")))
val shirley2 = lavern.copy(firstName = "Shirley", lastName = "Feeney")

// COMMAND ----------

// MAGIC %md
// MAGIC ## Exercise 1
// MAGIC Use a case class to model a bank account, which consists of an account holder (Person) and a balance in dollars. Define a function which debits the account by a specified number of dollars, returning a copy of the account with the adjusted balance.

// COMMAND ----------

// TODO
def debit(account: Account, amount: Int): Account = ???
def updatePhoneNumber(account: Account, ph: PhoneNumber): Account = ???

// COMMAND ----------

// MAGIC %md
// MAGIC ### Solution 1

// COMMAND ----------

// ANSWER
case class Account(holder: Person, balance: Int)

def debit(account: Account, amount: Int): Account = {
  account.copy(balance = account.balance - amount)
}

def updatePhoneNumber(account: Account, ph: PhoneNumber): Account = {
  account.copy(holder = account.holder.copy(phoneNumber = Some(ph)))
}

// COMMAND ----------

// MAGIC %md
// MAGIC ### Solution 1a

// COMMAND ----------

// ANSWER
case class Account(holder: Person, balance: Int) {
  
  def debit(amount: Int): Account = {
    copy(balance = balance - amount)
  }

  def updatePhoneNumber(ph: PhoneNumber): Account = {
    copy(holder = holder.copy(phoneNumber = Some(ph)))
  }
}

// COMMAND ----------

// MAGIC %md
// MAGIC ## Validation
// MAGIC 
// MAGIC How can we validate the values in a case class? Two insurmountable issues:
// MAGIC - Generated `apply` method in the companion cannot be overridden / customized
// MAGIC - `copy` method allows insertion of arbitrary values
// MAGIC 
// MAGIC One option is using the `require` method, or otherwise throwing exceptions, from case class constructor:

// COMMAND ----------

case class Phone(areaCode: String, number: String) {
  require(areaCode.size == 3, "area code must be 3 digits")
  require(number.size == 8, "number must be in the format xxx-xxxx")
}

val p = Phone("215", "555-1212")
scala.util.Try(p.copy(areaCode = "21"))

// COMMAND ----------

// MAGIC %md
// MAGIC This leaves a lot to be desired though, as we might want to perform validation that's safe -- i.e., that doesn't throw exceptions.
// MAGIC 
// MAGIC To accomplish this, we can declare the case class *abstract*, which suppresses the creation of the `apply` method in the companion and the `copy` method in the class. We can declare the class *sealed* so that no subtype can be defined outside of the file in which the abstract class is defined. Finally, we can implement our own `apply` method that returns a private subtype.

// COMMAND ----------

sealed abstract case class Phone(areaCode: String, number: String)
object Phone {
  def apply(areaCode: String, number: String): Either[String, Phone] = {
    if (areaCode.size != 3) Left("area code must be 3 digits")
    else if (number.size != 8) Left("number must be in the format xxx-xxxx")
    else Right(new Phone(areaCode, number) {})
  }
}
println(Phone("215", "555-1212"))
println(Phone("21", "555-1212"))
println(Phone("215", "555-1212") == Phone("215", "555-1212"))

// COMMAND ----------

// MAGIC %md
// MAGIC # Pattern Matching
// MAGIC 
// MAGIC The `match` and `case` keywords let us inspect an expression using various patterns or conditions. In its simplest form, this is analogous to `switch`/`case` from C or Java.

// COMMAND ----------

def showDirection(dir: Int): String = {
  dir match {
    case 1 => "East"
    case 2 => "North"
    case 3 => "West"
    case 4 => "South"
  }
}

println(showDirection(1))
println(showDirection(2))
println(showDirection(3))
println(showDirection(4))

// COMMAND ----------

// MAGIC %md
// MAGIC If the expression does not match any of the cases, a `scala.MatchError` is thrown:

// COMMAND ----------

try showDirection(5)
catch { case e: Exception => e.printStackTrace }

// COMMAND ----------

// MAGIC %md
// MAGIC We can add a default case by pattern matching on `_`:

// COMMAND ----------

def showDirection(dir: Int): String = {
  dir match {
    case 1 => "East"
    case 2 => "North"
    case 3 => "West"
    case 4 => "South"
    case _ => "Unknown"
  }
}
println(showDirection(5))


// COMMAND ----------

// MAGIC %md
// MAGIC 
// MAGIC ### Destructuring
// MAGIC 
// MAGIC Pattern matching is much more powerful than simple switch constructs. The biggest enabler is *destructuring*, which lets us "take apart" a value based on some pattern and bind names to the constituent parts.
// MAGIC 
// MAGIC We can destructure tuples by pattern matching with their "shape":

// COMMAND ----------

def sumComponents(p: (Int, Int, Int)): Int = {
  p match {
    case (x, y, z) => x + y + z
  }
}

sumComponents((1, 2, 3))

// COMMAND ----------

// MAGIC %md
// MAGIC ### Subtype Matching
// MAGIC We can also pattern match on subtypes.
// MAGIC 
// MAGIC Recall that `Option` is defined with two subtypes: `Some(value)` and `None`. We can use this to pattern match on options:

// COMMAND ----------

def square(x: Option[Int]): Option[Int] = {
  x match {
    case s: Some[Int] => Some(s.get * s.get) // Eek! There's a better way to do this...
    case None => None
  }
}

println(square(Some(5)))
println(square(None))

// COMMAND ----------

// MAGIC %md
// MAGIC We can destructure the `Some` while also doing the subtype pattern match:

// COMMAND ----------

def square(x: Option[Int]): Option[Int] = {
  x match {
    case Some(xx) => Some(xx * xx)
    case None => None
  }
}

println(square(Some(5)))
println(square(None))

// COMMAND ----------

// MAGIC %md
// MAGIC ### Exercise 2: Implement sum of squares using a single match expression
// MAGIC In this exercise, we must implement the function `sumOfSquares`. The implementation should not use any functions on `Option` like `map` or `getOrElse`. The implementation can be done with a single match statement.

// COMMAND ----------

// TODO
// Fill in the following function, replacing None with an implementation that prints true for all 4 test cases.
def sumOfSquares(x: Option[Int], y: Option[Int]): Option[Int] = {
  None
}

println(sumOfSquares(Some(5), Some(4)) == Some(41))
println(sumOfSquares(Some(5), None) == None)
println(sumOfSquares(None, Some(4)) == None)
println(sumOfSquares(None, None) == None)

// COMMAND ----------

// MAGIC %md
// MAGIC ### Solution 2a: Nested Match Expressions

// COMMAND ----------

// ANSWER
def sumOfSquares(x: Option[Int], y: Option[Int]): Option[Int] = {
  x match {
    case Some(xx) =>
      y match {
        case Some(yy) =>
          Some(xx * xx + yy * yy)
        case None =>
          None
      }
    case None =>
      None
  }
}

assert(sumOfSquares(Some(5), Some(4)) == Some(41))
assert(sumOfSquares(Some(5), None) == None)
assert(sumOfSquares(None, Some(4)) == None)
assert(sumOfSquares(None, None) == None)

// COMMAND ----------

// MAGIC %md
// MAGIC ### Solution 2b: Single Match Expression

// COMMAND ----------

// ANSWER
def sumOfSquares(x: Option[Int], y: Option[Int]): Option[Int] = {
  (x, y) match {
    case (Some(xx), Some(yy)) => Some(xx * xx + yy * yy)
    case _ => None
  }
}

assert(sumOfSquares(Some(5), Some(4)) == Some(41))
assert(sumOfSquares(Some(5), None) == None)
assert(sumOfSquares(None, Some(4)) == None)
assert(sumOfSquares(None, None) == None)

// COMMAND ----------

// MAGIC %md
// MAGIC # Pattern Matching on Case Classes
// MAGIC 
// MAGIC - Case classes support destructuring based on their fields
// MAGIC - Each field can either by bound to a variable or further matched

// COMMAND ----------

def lastNameAndAreaCode(p: Person): Option[(String, String)] = {
  p match {
    case Person(firstName, lastName, salary, ssn, Some(PhoneNumber(areaCode, digits))) =>
      Some((lastName, areaCode))
    case _ => None
  }
}

AllPeople.map(lastNameAndAreaCode)

// COMMAND ----------

// MAGIC %md
// MAGIC Note: we didn't use many of the fields of the case class but we still need to match on them -- when we destructure a case class, we need to exactly match the structure. We *don't* need to bind names to the values we don't care about though!

// COMMAND ----------

def lastNameAndAreaCode(p: Person): Option[(String, String)] = {
  p match {
    case Person(_, lastName, _, _, Some(PhoneNumber(areaCode, _))) =>
      Some((lastName, areaCode))
    case _ => None
  }
}

AllPeople.map(lastNameAndAreaCode)

// COMMAND ----------

// MAGIC %md
// MAGIC ## Guards
// MAGIC 
// MAGIC Each case statement can optionally have a _guard_ -- an `if booleanExpression` which further limits the values the case succeeds on.
// MAGIC 
// MAGIC Oddity: we don't *need* parentheses around the boolean expression -- this is a syntax quirk of Scala!

// COMMAND ----------

def migratePhones(p: Person): Person = {
  p match {
    case Person(_, lastName, _, _, Some(PhoneNumber("215", digits))) if digits.startsWith("555-") =>
      p.copy(phoneNumber = Some(PhoneNumber("267", digits)))
    case _ => p
  }
}

AllPeople.map(migratePhones)

// COMMAND ----------

// MAGIC %md
// MAGIC ## Pattern Matching Syntax
// MAGIC 
// MAGIC We've seen a lot of name bindings in patterns. For example, `case Person(_, lastName, _, _, Some(PhoneNumber("215", digits)))` contains two name bindings, `lastName` and `digits`, along with three ignored patterns (the underscores), and one literal pattern (the `"215"`). Every pattern supports name binding though! The [Scala Language Specification (section 8.1)](http://scala-lang.org/files/archive/spec/2.11/08-pattern-matching.html) defines pattern matching syntax in the following way:
// MAGIC 
// MAGIC ```
// MAGIC   Pattern         ::=  Pattern1 { ‘|’ Pattern1 }
// MAGIC   Pattern1        ::=  varid ‘:’ TypePat
// MAGIC                     |  ‘_’ ‘:’ TypePat
// MAGIC                     |  Pattern2
// MAGIC   Pattern2        ::=  varid [‘@’ Pattern3]
// MAGIC                     |  Pattern3
// MAGIC   Pattern3        ::=  SimplePattern
// MAGIC                     |  SimplePattern {id [nl] SimplePattern}
// MAGIC   SimplePattern   ::=  ‘_’
// MAGIC                     |  varid
// MAGIC                     |  Literal
// MAGIC                     |  StableId
// MAGIC                     |  StableId ‘(’ [Patterns] ‘)’
// MAGIC                     |  StableId ‘(’ [Patterns ‘,’] [varid ‘@’] ‘_’ ‘*’ ‘)’
// MAGIC                     |  ‘(’ [Patterns] ‘)’
// MAGIC                     |  XmlPattern
// MAGIC   Patterns        ::=  Pattern {‘,’ Patterns}
// MAGIC ```
// MAGIC 
// MAGIC Aha! This definition is illuminating in a number of ways:
// MAGIC  - Patterns are *recursive*
// MAGIC  - Name bindings are supported in mutiple ways
// MAGIC  - Pattern matching syntax does not say anything about case classes
// MAGIC 
// MAGIC Let's look at each of these in detail.

// COMMAND ----------

// MAGIC %md
// MAGIC ### Patterns are recursive
// MAGIC 
// MAGIC To get a feel for what this means, let's take a pattern we used earlier and project it against the syntax definition from the SLS:
// MAGIC 
// MAGIC ```scala
// MAGIC Person(firstName, lastName, salary, ssn, Some(PhoneNumber(areaCode, digits)))
// MAGIC ```
// MAGIC 
// MAGIC We start with the token `Person` and with the `Pattern` definition. `Person` must be a `Pattern1`. Looking at `Pattern1`, `Person` isn't a `varid : TypePat` nor a `_: TypePat` so it must be a `Pattern2`. `Person` isn't a `varid @ Pattern3` so it must be a `SimplePattern`. It's in the definition of `SimplePattern` where we make progress -- `Person` is a *stable identifier* and it is followed by parentheses enclosing more patterns. This gives us the following traversal of grammar rules:
// MAGIC 
// MAGIC ```
// MAGIC Pattern -> Pattern1 -> Pattern2 -> Pattern3 -> SimplePattern -> StableId '(' [Patterns] ')'
// MAGIC ```
// MAGIC 
// MAGIC The `Patterns` rule expands to a comma separate list of `Pattern`, which means we can parse each argument to `Person` as a pattern. For example, parsing `firstName` is accomplished via the traversal `Pattern -> Pattern1 -> Pattern2 -> varid`.
// MAGIC 
// MAGIC Why does this matter to us? Because we can use full pattern syntax at each "level" of destructuring!

// COMMAND ----------

def foo(o: Option[Person]) = o match {
  case Some(p @ Person("Lavern", _, _, _, Some(ph))) if ph.number.endsWith("1212") =>
    println("Full name: " + p.fullName)
  case other => "Doesn't match!"
}
foo(Some(lavern))

// COMMAND ----------

// MAGIC %md
// MAGIC ### Name bindings are supported in multiple ways
// MAGIC 
// MAGIC What we've been referring to as a "name binding" is represented in the grammar as productions which use the `varid` rule. These are:
// MAGIC ```
// MAGIC   Pattern1        ::=  varid ‘:’ TypePat | ...
// MAGIC   Pattern2        ::=  varid [‘@’ Pattern3] | Pattern3
// MAGIC   SimplePattern   ::=  ‘_’ | varid | ...
// MAGIC ```
// MAGIC 
// MAGIC A pattern of the form `varid: TypePat` is a *typed pattern*. Like we saw before, it matches when the expression is assignable to the type specified by `TypePat` and it introduces a new value in to scope with the name `varid`.
// MAGIC 
// MAGIC A pattern of the form `varid @ Pattern` is a *pattern binder*. It introduces a new value in to scope with the name `varid` such that the value bound to `varid` matches the pattern after the `@`.
// MAGIC 
// MAGIC ### Pattern matching syntax is not case class aware (or is it?)
// MAGIC 
// MAGIC In the syntax definition a pattern match, case classes were not mentioned. When we match on case classes, we use the grammar rule:
// MAGIC ```
// MAGIC   SimplePattern -> StableId ‘(’ [Patterns] ‘)’
// MAGIC ```
// MAGIC 
// MAGIC Scala uses this grammar rule for two purposes: *constructor patterns* and *extractor patterns*.
// MAGIC 
// MAGIC A constructor pattern is a pattern which matches the primary constructor of a case class -- i.e., a pattern is specified inside the parentheses for each argument to the primary constructor of the case class.
// MAGIC 
// MAGIC An extractor pattern is a pattern where the `StableId` refers to an object that has one or more methods named `unapply`. We'll see these more in a bit.

// COMMAND ----------

// MAGIC %md
// MAGIC ## Stable Identifiers

// COMMAND ----------

def inPhilly(ph: PhoneNumber): Boolean = ph match { case PhoneNumber("215", _) => true; case _ => false }
println(inPhilly(PhoneNumber("215", "555-1212")))
println(inPhilly(PhoneNumber("866", "555-1313")))

// COMMAND ----------

// MAGIC %md
// MAGIC Let's define `"215"` as a constant instead of hard-coding it in our pattern:

// COMMAND ----------

val twoOneFive = "215"
def inPhilly(ph: PhoneNumber): Boolean = ph match { case PhoneNumber(twoOneFive, _) => true; case _ => false }
println(inPhilly(PhoneNumber("215", "555-1212")))
println(inPhilly(PhoneNumber("866", "555-1313")))

// COMMAND ----------

// MAGIC %md
// MAGIC <img alt="Warning" src="http://i.imgur.com/TvgcpDH.png" style="float: left; margin-right: 10px"/>
// MAGIC Eek! What's happening here? The `PhoneNumber(twoOneFive, _)` pattern is getting parsed as a constructor pattern with the two patterns, `twoOneFive` and `_`. The `twoOneFive` pattern is getting parsed as `Pattern -> Pattern1 -> Pattern2 -> varid`. That is, `twoOneFive` is acting as a name binding.
// MAGIC 
// MAGIC Scala defines `varid` as identifiers that start with a lowercase character. We can take advantage of this by naming our constant to start with an uppercase character.

// COMMAND ----------

val TwoOneFive = "215"
def inPhilly(ph: PhoneNumber): Boolean = ph match { case PhoneNumber(TwoOneFive, _) => true; case _ => false }
println(inPhilly(PhoneNumber("215", "555-1212")))
println(inPhilly(PhoneNumber("866", "555-1313")))

// COMMAND ----------

// MAGIC %md
// MAGIC ## Alternative Patterns
// MAGIC 
// MAGIC Scala also supports "OR"-ing patterns using the `|` character to separate patterns.

// COMMAND ----------

def inPhilly(ph: PhoneNumber): Boolean = ph match { 
  case PhoneNumber("215", _) | PhoneNumber("267", _) | PhoneNumber("610", _) => true
  case _ => false
}
println(inPhilly(PhoneNumber("215", "555-1212")))
println(inPhilly(PhoneNumber("267", "555-1313")))
println(inPhilly(PhoneNumber("610", "555-1414")))

def foo(x: Int): String = x match { case -1 | 0 | 1 => "Nearly Zero"; case _ => "Not Nearly Zero" }
println(foo(1))
println(foo(4))

// COMMAND ----------

// MAGIC %md
// MAGIC We cannot use alternatives and name bindings in the same pattern though. For example, this does not comple:
// MAGIC 
// MAGIC ```
// MAGIC > def foo(ph: PhoneNumber) = ph match { case PhoneNumber("215", digits) | PhoneNumber("267", digits) => println(digits) }
// MAGIC 
// MAGIC <console>:31: error: illegal variable in pattern alternative
// MAGIC        def foo(ph: PhoneNumber) = ph match { case PhoneNumber("215", digits) | PhoneNumber("267", digits) => println(digits) }
// MAGIC                                                                      ^
// MAGIC <console>:31: error: illegal variable in pattern alternative
// MAGIC        def foo(ph: PhoneNumber) = ph match { case PhoneNumber("215", digits) | PhoneNumber("267", digits) => println(digits) }
// MAGIC                                                                                                   ^
// MAGIC ```

// COMMAND ----------

// MAGIC %md
// MAGIC We can use name bindings before we get to the alternative pattern though -- for example, by using a pattern binder:

// COMMAND ----------

def foo(ph: PhoneNumber) = ph match { case p @ (PhoneNumber("215", _) | PhoneNumber("267", _)) => println(p.number) }

// COMMAND ----------

// MAGIC %md
// MAGIC ## Extractors
// MAGIC 
// MAGIC When destructuring a case class with a pattern match, we used the grammar production:
// MAGIC ```
// MAGIC SimplePattern -> StableId ‘(’ [Patterns] ‘)’
// MAGIC ```
// MAGIC 
// MAGIC More specifically, we used a constructor pattern, which destructured the case class by matching each argument to an argument in the primary constructor.
// MAGIC 
// MAGIC This same grammar production supports *extractor patterns*. The `StableId` is the name of an object that defines an `unapply` method. The `unapply` method takes apart a value, returning its pieces.

// COMMAND ----------

object SocialSecurityNumber {
  def unapply(str: String): Option[(Int, Int, Int)] = {
    val parts = str.split("-")
    if (parts.size == 3) {
      try {
        val nums = parts.map(_.toInt)
        Some((nums(0), nums(1), nums(2)))
      } catch {
        case _: NumberFormatException => None
      }
    } else {
      None
    }
  }
}

def printSsn(ssn: String): Unit = {
  val description = ssn match { 
    case SocialSecurityNumber(x, y, z) =>
      s"Upper: $x, Mid: $y, Lower: $z"
    case _ => "Not a valid SSN"
  }
  println(description)
}

printSsn("123-45-6789")
printSsn("123-asdf-6789")

// COMMAND ----------

// MAGIC %md
// MAGIC ## Varargs Pattern Matching
// MAGIC 
// MAGIC We can also match a variable number of arguments -- for example, binding a name to the first few values of a `Vector`.

// COMMAND ----------

def foo(xs: Vector[Int]): Boolean = xs match {
  case Vector(1, 2) => true
  case _ => false
}
println(foo(Vector(1, 2)))
println(foo(Vector(1, 2, 3)))
println(foo(Vector(2, 3, 4)))

def bar(xs: Vector[Int]): Boolean = xs match {
  case Vector(1, 2, _*) => true
  case _ => false
}
println(bar(Vector(1, 2)))
println(bar(Vector(1, 2, 3)))
println(bar(Vector(2, 3, 4)))

// COMMAND ----------

def baz(ps: Vector[PhoneNumber]): Boolean = ps match {
  case Vector(PhoneNumber("215", n1), PhoneNumber("267", n2), _*) if n1 == n2 => true
  case _ => false
}

println(baz(Vector(PhoneNumber("215", "555-1212"), PhoneNumber("267", "555-1212"))))
println(baz(Vector(PhoneNumber("215", "555-1212"), PhoneNumber("267", "555-5555"))))

// COMMAND ----------

// MAGIC %md
// MAGIC ### Vararg Extractors
// MAGIC 
// MAGIC We can write extractors that support vararg pattern matching by implementing `unapplySeq` instead of `unapply`.

// COMMAND ----------

object Primes {
  def unapplySeq(n: Int): Some[Seq[Int]] = {
    Some(factorize(n))
  }
  
  // Adapted from http://stackoverflow.com/a/30283151/547212
  def factorize(x: Int): List[Int] = {
    @annotation.tailrec
    def loop(x: Int, a: Int = 2, acc: List[Int] = Nil): List[Int] = {
      a*a > x match {
        case false if x % a == 0 => loop(x / a, a, a :: acc)
        case false => loop(x, a + 1, acc)
        case true => x :: acc
      }
    }
    loop(x).reverse
  }
}

def printDivByTwoAndThree(n: Int): Unit = n match { 
  case Primes(2, 3, _*) => println(s"$n divisible by both 2 and 3") 
  case Primes(_*) => println(s"$n NOT divisible by both 2 and 3")
}
printDivByTwoAndThree(42)
printDivByTwoAndThree(24)

// COMMAND ----------

// MAGIC %md
// MAGIC ## Pattern-ing
// MAGIC 
// MAGIC So far, every use of pattern matching we've seen has involved matching an expression against one or more cases via the `match` keyword. Scala allows pattern matching in a number of other ways though.
// MAGIC 
// MAGIC ### Pattern matching during val assignment

// COMMAND ----------

val SocialSecurityNumber(hi, med, low) = "123-45-6789"
println(s"High: $hi, Medium: $med, Low: $low")

// This will throw a MatchError
// val SocialSecurityNumber(hi, med, low) = "asdf"

val Some(x) = Option(1)
println(s"x: $x")

val y @ Some(z) = Option(42)
println(s"y: $y, z: $z")

// COMMAND ----------

// MAGIC %md
// MAGIC ### Pattern matching in the body of an anonymous function

// COMMAND ----------

val firstNames = AllPeople.map { case Person(firstName, _, _, _, _) => firstName }

// COMMAND ----------

val points: List[(Int, Int)] = List((1 -> 2), (3 -> 4), (5 -> 6))
points.map { case (x, y) => x + y }

// COMMAND ----------

// MAGIC %md
// MAGIC ### Pattern matching in the definition of a PartialFunction

// COMMAND ----------

// MAGIC %md
// MAGIC 
// MAGIC Recall the definition of a `PartialFunction` from `A` to `B`:
// MAGIC 
// MAGIC ```scala
// MAGIC trait PartialFunction[-A,+B] {
// MAGIC   def isDefinedAt(a: A): Boolean
// MAGIC   def apply(a: A): B
// MAGIC }
// MAGIC ```
// MAGIC 
// MAGIC This defines a function which is only safe to call at a given value of type `A` if the `isDefinedAt` method returns true for that value.
// MAGIC 
// MAGIC Consider the `collect` method in the standard collections:
// MAGIC 
// MAGIC ```scala
// MAGIC class List[+A] { 
// MAGIC   /** Filters and maps in a single step. */
// MAGIC   def collect[B](pf: PartialFunction[A, B]): List[B] = {
// MAGIC     // Note: toy implementation - real implementation is significantly more powerful and performant
// MAGIC     this.filter(a => pf.isDefinedAt(a)).map(a => pf(a))
// MAGIC   }
// MAGIC }
// MAGIC ```
// MAGIC 
// MAGIC To use `collect`, we could manually instantiate a subtype of `PartialFunction` though note that this is *more* verbose than simply filtering and then mapping:

// COMMAND ----------

val phoneNumbers = AllPeople.collect(new PartialFunction[Person, PhoneNumber] {
  def isDefinedAt(p: Person): Boolean = p.phoneNumber.isDefined
  def apply(p: Person): PhoneNumber = p.phoneNumber.get
})

// COMMAND ----------

// MAGIC %md
// MAGIC Instead, we can define partial function instances using pattern matching:

// COMMAND ----------

val phoneNumbers = AllPeople.collect { case Person(_, _, _, _, Some(ph)) => ph }

// COMMAND ----------

// MAGIC %md
// MAGIC 
// MAGIC ### Actors
// MAGIC 
// MAGIC The most famous use of defining partial functions via pattern matching is from Akka:

// COMMAND ----------

trait Actor {
  def receive: Actor.Receive
}

object Actor {
  type Receive = PartialFunction[Any, Unit]
}

case class SomeMessage(data: String, reply: Boolean)
case class AnotherMessage(data: String, reply: Boolean, expiry: java.time.Instant)

class MyActor extends Actor {
  def receive = {
    case SomeMessage(data, reply) => ???
    case AnotherMessage(data, reply, expiry) => ???
  }
}

// COMMAND ----------

// MAGIC %md
// MAGIC 
// MAGIC # Data (Algebraic Data Types)
// MAGIC 
// MAGIC Earlier, we defined a case class as one in which a bunch of code was generated for us. This is a practical way to think about case classes, but limits us in certain ways.
// MAGIC 
// MAGIC Instead, let's reconsider a case class as a kind of *data*. More specifically, as a *labelled tuple* -- a tuple with names attached to each value.
// MAGIC 
// MAGIC Contrast `(Int, Int)` with `case class Point(x: Int, y: Int)`:
// MAGIC  - Both types have the same number of elements (`2^64`)
// MAGIC  - There's a natural mapping between these types (`_1 <=> x`, `_2 <=> y`)
// MAGIC  - The case class has a label attached to each field (`x`, `y`)
// MAGIC  - The case class has a label attached to the class itself (`Point`)
// MAGIC 
// MAGIC We can think of a case class as a "product" of some number of types -- in the case of `Point`, an `Int` *and* `an `Int`.
// MAGIC 
// MAGIC For example, consider the product of a `Boolean`, `Boolean`, and `Int`:
// MAGIC 
// MAGIC ```scala
// MAGIC case class Woozle(foo: Boolean, bar: Boolean, count: Int)
// MAGIC ```
// MAGIC 
// MAGIC This is a labelled version of the product of `(Boolean, Boolean, Int)` and it has `2 * 2 * 2^32 = 2^34` possible values.
// MAGIC 
// MAGIC Another way to think of this is that a value of the product type `A x B x C x ...` consists of a value of `A` and a value of `B` and a value of `C` and so on. We can then consider the dual to product types, where instead of having a type that consists of a value of `A` *and* a value of `B` *and* a value of `C` and so on, we replace the ANDs with ORs. We'll call these sum types for a reason that will become clear in a moment.
// MAGIC 
// MAGIC Let's informally write a sum type as `A + B + C + ...`, where a value of this type is a value of `A` *or* a value of `B` *or* a value of `C` and so on. (Technicality: note that just like in the product case, the component types of our sum type do not have to be unique -- our encoding in Scala must be able to handle this without losing track of the "position" of a value).
// MAGIC 
// MAGIC So why do we call this a "sum" type? Consider the number of values of the type `A + B + C + ...` -- there's a value of the sum type for each value of `A`, each value of `B`, etc. So the total number of values of the sum type is the number of values of `A` plus the number of values of `B` plus the number of values of `C`, etc.
// MAGIC 
// MAGIC So how can we encode sum types in Scala? We could use a standard library data type, `scala.util.Either[L, R]`, but that can get pretty inconvenient pretty quickly as we nest `Either`s. Further, it suffers from the same problem as tuples in that the components are *unlabelled*.
// MAGIC 
// MAGIC Instead, let's use subtyping! We've alread seen some very common examples in fact.
// MAGIC 
// MAGIC ```scala
// MAGIC sealed abstract class Option[+A] { ...}
// MAGIC case class Some[+A](get: A) extends Option[A]
// MAGIC case object None extends Option[Nothing]
// MAGIC ```
// MAGIC 
// MAGIC Similarly, `List:`
// MAGIC ```scala
// MAGIC sealed abstract class List[+A] { ... }
// MAGIC case class ::[+A](head: A, tail: List[A]) extends List[A]
// MAGIC case object Nil extends List[Nothing]
// MAGIC ```
// MAGIC 
// MAGIC These are both sum types, though they feel a bit artificial since they both represent a choice between a singleton and non-singleton. Let's consider a domain specific example.

// COMMAND ----------

sealed trait Taxable
case class Individual(firstName: String, lastName: String, ssn: String) extends Taxable
case class Business(name: String, taxIdNumber: String) extends Taxable

def taxId(t: Taxable): String = t match {
  case Individual(_, _, ssn) => ssn
  case Business(_, tin) => tin
}


// COMMAND ----------

// MAGIC %md
// MAGIC 
// MAGIC As you can see, there's nothing new here -- just standard pattern matching that we've used before. The differences are more about philosophy and design mindset. Instead of thinking about defining classes and methods, we instead focus on defining *data*, using product and sum types, and then functions that operate on that data.
