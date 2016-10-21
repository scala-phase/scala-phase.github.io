// Databricks notebook source exported at Fri, 21 Oct 2016 18:51:02 UTC
// MAGIC %md
// MAGIC <img src="http://www.scala-lang.org/resources/img/smooth-spiral.png" style="float: right; height: 100px; margin-right: 50px; margin-top: 20px"/>
// MAGIC 
// MAGIC # Scala Fundamentals: Classes, Traits and Objects
// MAGIC 
// MAGIC <img src="https://s3.amazonaws.com/ardentex-workspace/mini-logo-transparent-small.png" style="height: 64px"/>&nbsp;<img src="https://databricks.com/wp-content/themes/databricks/assets/images/header_logo.png?v=2.98"/>
// MAGIC 
// MAGIC Brian Clapper, [@brianclapper](https://twitter.com/brianclapper)  
// MAGIC President, [ArdenTex, Inc.](https://www.ardentex.com/)  
// MAGIC Senior Instructor and Application Engineer, [Databricks, Inc.](https://www.databricks.com)   
// MAGIC Founder and Co-organizer, [PHASE](http://scala-phase.org)  
// MAGIC [_bmc@ardentex.com_](mailto:bmc@ardentex.com), [_bmc@databricks.com_](mailto:bmc@databricks.com)

// COMMAND ----------

// MAGIC %md
// MAGIC 
// MAGIC ## Introduction
// MAGIC 
// MAGIC In this lesson, we're going to discusses Scala classes, Scala traits (which are analagous to Java interfaces, but more powerful), and Scala singleton objects.
// MAGIC 
// MAGIC If you know classes in Java, you'll find Scala classes to be refreshingly concise and easy to read.
// MAGIC 
// MAGIC ## Classes
// MAGIC 
// MAGIC Let's do classes first.
// MAGIC 
// MAGIC ### Summary
// MAGIC 
// MAGIC * Scala automatically generates getters and setters. You don't have to code them.
// MAGIC * You can replace a field with a custom gettor or setter, without changing how callers access the 
// MAGIC   field. This is the [Uniform Access Principle](http://docs.scala-lang.org/glossary/#uniform-access-principle).
// MAGIC * You can use `@BeanProperty` if you need JavaBeans setters and getters.
// MAGIC * Every class has a primary constructor that is "interwoven" with the class definition.
// MAGIC * Auxiliary constructors are optional and are named `this`.
// MAGIC 
// MAGIC ### Simple Classes and Parameterless Methods
// MAGIC 
// MAGIC Scala classes look a lot like their counterparts in Java, C#, C++, Python or Ruby:

// COMMAND ----------

class Counter {
  private var value = 0 // You MUST initialize the field.
  
  // Methods are public by default.
  def increment(): Unit = {
    value += 1
  }
  
  def current() = value
}

// COMMAND ----------

// MAGIC %md
// MAGIC 
// MAGIC Methods are just functions inside the class and, like, functions, are created with the `def` keyword.
// MAGIC 
// MAGIC #### Some Differences from Java
// MAGIC 
// MAGIC * `public` is the default access modifier. Classes, fields, and methods are public, by default.
// MAGIC * A source file can contain multiple classes.
// MAGIC * There's no requirement that the class name match the source file name, as there is in Java.
// MAGIC 
// MAGIC #### Using our `Counter` class
// MAGIC 
// MAGIC To use this class, you construct objects and invoke methods the usual way.

// COMMAND ----------

val counter1 = new Counter
val counter2 = new Counter() // equivalent

counter1.increment()

// COMMAND ----------

println(s"counter1=${counter1.current()}")
println(s"counter2=${counter2.current()}")

// COMMAND ----------

// MAGIC %md
// MAGIC You can call parameterless methods, like `current()`, with or without parentheses. The following two calls are equivalent:

// COMMAND ----------

println(counter1.current())
println(counter1.current)

// COMMAND ----------

// MAGIC %md
// MAGIC 
// MAGIC ![?](http://i.imgur.com/Guv4TBn.png) **Which form should you use?**
// MAGIC 
// MAGIC It is considered good style to use `()` for a mutator method (a method that changes object state or performs I/O) and to drop the `()` for an accessor method.
// MAGIC 
// MAGIC That's what we did in our example:

// COMMAND ----------

counter1.increment() // Mutates state: Use ()
counter1.current     // Accesses value without changing state.

// COMMAND ----------

// MAGIC %md
// MAGIC You can enforce the accessor style by declaring `current` without `()`:

// COMMAND ----------

class Counter {
  private var value = 0

  def increment(): Unit = {
    value += 1
  }
  
  def current = value
}

// COMMAND ----------

// MAGIC %md
// MAGIC Now, any attempt to use `()` on `current` will fail. The following cell will not compile.

// COMMAND ----------

(new Counter).current()

// COMMAND ----------

// MAGIC %md
// MAGIC ### Properties (Fields) with getters and setters
// MAGIC 
// MAGIC When writing Java classes, we tend to avoid public fields. For instance, the following is considered to be bad Java code:
// MAGIC 
// MAGIC ```
// MAGIC public class Person { // This is Java, not Scala
// MAGIC     public int age;
// MAGIC     public String name;
// MAGIC }
// MAGIC ```
// MAGIC 
// MAGIC The problem with this approach is its inflexibility. If you publish this class, callers will access `age` as follows:
// MAGIC 
// MAGIC ```
// MAGIC Person fred = new Person();
// MAGIC fred.name = "Fred";
// MAGIC fred.age = 21;
// MAGIC ```
// MAGIC 
// MAGIC But, what if we want to ensure that a caller does not make a person younger? To do that in Java, we have to change the public interface of the class
// MAGIC to add a setter.
// MAGIC 
// MAGIC For this reason, when programming Java, we defensively write setters and getters:
// MAGIC 
// MAGIC ```
// MAGIC public class Person {
// MAGIC     private int age;
// MAGIC     private String name;
// MAGIC     
// MAGIC     public int getAge() { return age; }
// MAGIC     
// MAGIC     public void setAge(int newAge) {
// MAGIC         // Now we can add some checks.
// MAGIC         if (newAge > this.age)
// MAGIC             this.age = newAge;
// MAGIC     }
// MAGIC     
// MAGIC     public String getName() { return name; }
// MAGIC     
// MAGIC     public void setName(String newName) { this.name = newName; }
// MAGIC }
// MAGIC ```
// MAGIC 
// MAGIC A getter/setter pair, such as `getAge()` and `setAge()`, is often called a _property_. So, `Person` has two properties: `age` and `name`.

// COMMAND ----------

// MAGIC %md
// MAGIC 
// MAGIC #### The problem with Java's approach
// MAGIC 
// MAGIC The Java approach is highly verbose. When we write Java classes, we defensively write getters and setters for _every_ field the caller
// MAGIC might need to access. Most of the time, though, the getters don't do anything except return their corresponding property values, and
// MAGIC the setters don't do anything except set their property values.
// MAGIC 
// MAGIC We have to write (and read) a bunch of extra boilerplate code that doesn't actually solve the problem the class is intended to solve.
// MAGIC 
// MAGIC #### Other languages don't do what Java does
// MAGIC 
// MAGIC Python, Ruby, C# and, yes, Scala, all allow you to access a field as if it were public `fred.age`, while providing the ability to
// MAGIC invoke setters and getters implicitly. In Python and C#, you start out with public fields; if you need a setter or getter, you can
// MAGIC later add a property declaration. In Ruby, you can mark a field as an attribute, and it will generate a setter and/or getter for
// MAGIC you, under the covers. Scala automatically generates default getters and setters.
// MAGIC 
// MAGIC In all cases, _you can continue to access the field as if it were public._
// MAGIC 
// MAGIC #### An example

// COMMAND ----------

class Person {
  var age = 0
  var name = ""
}

// COMMAND ----------

// MAGIC %md
// MAGIC During compilation, Scala converts `age` and `name` to private fields and generates setters and getters. For instance, here's
// MAGIC the result of running `javap -p` on the compiled `Person` class:
// MAGIC 
// MAGIC ```
// MAGIC public class Person {
// MAGIC   private int age;
// MAGIC   private java.lang.String name;
// MAGIC   public int age();
// MAGIC   public void age_$eq(int);
// MAGIC   public java.lang.String name();
// MAGIC   public void name_$eq(java.lang.String);
// MAGIC   public Person();
// MAGIC }
// MAGIC ```

// COMMAND ----------

// MAGIC %md
// MAGIC What happened?
// MAGIC 
// MAGIC * Scala converted `age` to a private field.
// MAGIC * It generated an `age()` getter, which we can just invoke without the parentheses.
// MAGIC * It generated an `age_=()` setter, which we can just invoke with the assignment operator.
// MAGIC * It did the same for the `name` field.

// COMMAND ----------

val person = new Person
person.name = "Fred" // calls person.name_=()
person.age = 21      // calls person.age_=()

// COMMAND ----------

println(s"${person.name} is ${person.age} years old.")

// COMMAND ----------

// MAGIC %md
// MAGIC Note that assignment to `name` is just _syntactic sugar_ for calling the `name_=` method:

// COMMAND ----------

person.name_=("Joe")
println(s"${person.name} is ${person.age} years old.")

// COMMAND ----------

// MAGIC %md
// MAGIC If we want to add a check to ensure that a caller can't make a person younger, we can do that by defining our own setter.
// MAGIC Doing so prevents Scala from defining a default one.

// COMMAND ----------

class Person {
  private var theAge = 0
  var name = ""
  
  def age_=(newAge: Int): Unit = {
    if (newAge > this.theAge)
      this.theAge = newAge
  }
  
  def age = this.theAge
}

// COMMAND ----------

val person = new Person
person.name = "Maria"
person.age = 43
person.age = 10

// COMMAND ----------

println(s"${person.name} is ${person.age}.")

// COMMAND ----------

// MAGIC %md
// MAGIC #### Note the lack of boilerplate
// MAGIC 
// MAGIC Compare our Scala `Person` class with its Java equivalent. In Scala, we don't have to define every
// MAGIC getter and setter _just in case_. Instead, we allow Scala to generate getters and setters for us,
// MAGIC overriding only the ones we need.
// MAGIC 
// MAGIC This approach leads to much more compact and readable, code. Let's compare one version of
// MAGIC our Scala `Person` class with the corresponding definitions in some other languages. Note how much more
// MAGIC verbose the Java version is. (I've used similar bracket styles, for fairness of comparison, even though
// MAGIC C# and Java conventionally prefer brackets on separate lines.)
// MAGIC 
// MAGIC <table style="width: 100%">
// MAGIC   <thead>
// MAGIC     <tr>
// MAGIC       <th>Scala</th>
// MAGIC       <th>C#</th>
// MAGIC       <th>Ruby</th>
// MAGIC       <th>Python</th>
// MAGIC       <th>Java</th>
// MAGIC     </tr>
// MAGIC   </thead>
// MAGIC   
// MAGIC   <tbody>
// MAGIC     <tr>
// MAGIC       <td>
// MAGIC <pre>
// MAGIC class Person {
// MAGIC   var name: String = ""
// MAGIC   private var theAge: Int = 0
// MAGIC   
// MAGIC   def age\_=(newAge: Int): Unit = {
// MAGIC     if (newAge > this.theAge)
// MAGIC       this.theAge = newAge;
// MAGIC   }
// MAGIC   
// MAGIC   def age = this.theAge
// MAGIC }          
// MAGIC </pre>
// MAGIC       </td>
// MAGIC 
// MAGIC       <td>
// MAGIC <pre>
// MAGIC class Person {
// MAGIC     public string Name = "";
// MAGIC     private int age = 0;
// MAGIC 
// MAGIC     public int Age {
// MAGIC         get { return age; }
// MAGIC         
// MAGIC         set {
// MAGIC             if (value > age)
// MAGIC                 age = value;
// MAGIC         }
// MAGIC     }
// MAGIC }          
// MAGIC </pre>
// MAGIC       </td>
// MAGIC 
// MAGIC       <td>
// MAGIC <pre>
// MAGIC class Person
// MAGIC   attr\_accessor :name
// MAGIC   attr\_reader :age
// MAGIC 
// MAGIC   def initialize()
// MAGIC     @name = ""
// MAGIC     @age  = 0
// MAGIC   end
// MAGIC 
// MAGIC   def age=(new\_age)
// MAGIC     if new\_age > @age
// MAGIC       @age = new\_age
// MAGIC     end
// MAGIC   end
// MAGIC end
// MAGIC </pre>
// MAGIC       </td>
// MAGIC       
// MAGIC       <td>
// MAGIC <pre>
// MAGIC class Person(object):
// MAGIC     def \_\_init\_\_(self):
// MAGIC         self.name = ""
// MAGIC         self.\_age = 0
// MAGIC         
// MAGIC     @property
// MAGIC     def age(self):
// MAGIC         return self.\_age
// MAGIC         
// MAGIC     @age.setter
// MAGIC     def age(self, new\_age):
// MAGIC         if new\_age > self.\_age:
// MAGIC             self.\_age = new\_age
// MAGIC </pre>
// MAGIC       </td>
// MAGIC 
// MAGIC       <td>
// MAGIC <pre>
// MAGIC public class Person {
// MAGIC     private String name = "";
// MAGIC     private int age = 0;
// MAGIC     
// MAGIC     public String getName() {
// MAGIC         return name;
// MAGIC     }
// MAGIC     
// MAGIC     public void setName(String newName) {
// MAGIC         this.name = newName;
// MAGIC     }
// MAGIC     
// MAGIC     public int getAge() {
// MAGIC         return age;
// MAGIC     }
// MAGIC     
// MAGIC     public void setAge(int newAge) {
// MAGIC         if (newAge > this.age)
// MAGIC             this.age = newAge;
// MAGIC     }
// MAGIC }          
// MAGIC </pre>
// MAGIC       </td>
// MAGIC     </tr>
// MAGIC   </tbody>
// MAGIC </table>

// COMMAND ----------

// MAGIC %md
// MAGIC <div style="border: 1px solid #ddd; border-radius: 10px 10px 10px 10px; padding: 10px; background-color: #f7edcd; width: 40%; margin: 20px">
// MAGIC Bertrand Meyer, the inventor of the influential Eiffel language, formulated the <strong>Uniform Access Principle</strong>. It states:
// MAGIC 
// MAGIC <p style="margin-left: 1em; margin-right: 1em">
// MAGIC All services offered by a module should be available through a uniform notation, which does not betray whether they are implemented through storage or through computation.
// MAGIC In Scala, the caller of <tt>fred.age</tt> doesn’t know whether age is implemented through a field or a method. (Of course, in the JVM, the service is always implemented through a method, either synthesized or programmer-supplied.)
// MAGIC </p>
// MAGIC 
// MAGIC </div>

// COMMAND ----------

// MAGIC %md
// MAGIC ### Properties with only getters
// MAGIC 
// MAGIC If you define a field as a `val`, Scala will generate only a getter, since a `val` cannot be changed.

// COMMAND ----------

class Message {
  val timestamp = new java.util.Date
}

// COMMAND ----------

// This works:
val m = new Message

// COMMAND ----------

println(m.timestamp)

// COMMAND ----------

// This doesn't:
m.timestamp = new java.util.Date

// COMMAND ----------

// MAGIC %md
// MAGIC ### Properties with only setters
// MAGIC 
// MAGIC You cannot define a set-only field in Scala. If you try to do, your class will compile, but you'll get a compilation error trying to call the setter.

// COMMAND ----------

class DoesNotWork {
  private var someValue: Int = 0
  
  def value_=(newValue: Int): Unit = {
    someValue = newValue
  }
}

// COMMAND ----------

val dnw = new DoesNotWork
dnw.value = 30

// COMMAND ----------

// MAGIC %md
// MAGIC ### Summary: Scala setters and getters
// MAGIC 
// MAGIC To summarize, you have four choices for implementing properties:
// MAGIC 
// MAGIC 1. `var foo`: Scala generates a getter (`foo`) and a setter (`foo_=()`).
// MAGIC 2. `val foo`: Scala generates a getter.
// MAGIC 3. You define your own getter and setter by providing the `foo` and `foo_=()` methods.
// MAGIC 4. You define your own getter by providing just the `foo` method.

// COMMAND ----------

// MAGIC %md
// MAGIC ### Exercise: Implement a simple class
// MAGIC 
// MAGIC #### Time to complete: 5 minutes
// MAGIC 
// MAGIC Implement the following class, and run the cell after it to test that you got it right.
// MAGIC 
// MAGIC **Requirements**
// MAGIC 
// MAGIC 1. The `deposit()` method should not permit a negative deposit. (That would be a withdrawal.)
// MAGIC 2. The `withdrawal()` method should not allow an overdraft. It should return the smaller of the current balance and the desired amount. 
// MAGIC    That is, if someone attempts to withdraw 20.00, and the balance is only 15.00, `withdraw()` should zero the balance and return 15.00.
// MAGIC    It should _not_ throw an exception.
// MAGIC 3. `balance` should return the current balance.
// MAGIC 4. The balance should be initialized to 0.
// MAGIC 
// MAGIC **Notes**: 
// MAGIC 
// MAGIC 1. In Scala, you can use `BigDecimal` the way you use any other numeric type. All the normal operators work fine. You can
// MAGIC    create a BigDecimal from an integer or floating point number easily: `BigDecimal(120)`.
// MAGIC 2. We're using _mutable_ instance data here, which isn't considered good practice in Scala. 
// MAGIC    Don't worry about that for now. At the moment, we're just trying to get our heads around the syntax.
// MAGIC    
// MAGIC **Question**: Why are we using `BigDecimal`, rather than, say, `Double`?

// COMMAND ----------

// TODO
class BankAccount {
  def deposit(amount: BigDecimal): Unit = {
    // implement this
  }
  
  def withdraw(amount: BigDecimal): BigDecimal = {
    // implement this
  }
  
  def balance = // implement this
}

// COMMAND ----------

// ANSWER
class BankAccount {
  private var currentBalance = BigDecimal(0)
  
  def deposit(amount: BigDecimal): Unit = {
    if (amount >= 0) currentBalance += amount
  }
  
  def withdraw(amount: BigDecimal): BigDecimal = {
    val actualAmount = if (amount > currentBalance)
      currentBalance
    else
      amount
    
    currentBalance -= actualAmount
    actualAmount
  }
  
  def balance = currentBalance
}

// COMMAND ----------

// Run this cell to test your solution.

val acct = new BankAccount
assert(acct.balance == 0, "Balance not initialized to 0.")
acct.deposit(-100)
assert(acct.balance == 0, "Balance incorrectly update by negative deposit.")
acct.deposit(100)
assert(acct.balance == 100, "Deposit of 100 didn't update balance properly.")
assert(acct.withdraw(50) == 50, "Withdrawal returned wrong amount with sufficient balance.")
assert(acct.balance == 50, "Balance incorrect after withdrawal with sufficient balance.")
assert(acct.withdraw(100) == 50, "Withdrawal returned wrong amount when balance isn't sufficient.")
assert(acct.balance == 0, s"After attempted overdraft, account balance should be 0, but is ${acct.balance}")

// COMMAND ----------

// MAGIC %md 
// MAGIC ### Object-private fields
// MAGIC 
// MAGIC In Scala, as in Java and other languages, a method can access the private fields of all objects of its class:

// COMMAND ----------

class Foo {
  private var myValue = 0
  
  def examineOther(foo: Foo) = foo.myValue
  
  def value = myValue
  
  def value_=(newValue: Int): Unit = {
    myValue = newValue
  }
}

val foo1 = new Foo
foo1.value = 100
val foo2 = new Foo

// COMMAND ----------

println(s"foo2 says foo1's value is ${foo2.examineOther(foo1)}")

// COMMAND ----------

// MAGIC %md
// MAGIC Scala actually allows an even more severe access restriction, permitting you to make a private field visible _only_
// MAGIC within an instance. To do that, use the `private[this]` access modifier. In the class below, I've changed `myValue`
// MAGIC from `private` to `private[this]`. Note that `examineOther` is now flagged with a compiler error:

// COMMAND ----------

class RestrictedFoo {
  private[this] var myValue = 0
  
  def examineOther(foo: Foo) = foo.myValue
  
  def value = myValue
  
  def value_=(newValue: Int): Unit = {
    myValue = newValue
  }
}

// COMMAND ----------

// MAGIC %md
// MAGIC 
// MAGIC Another side-effect of creating a `private[this]` field is that, unlike a `private` field, Scala will _not_ generate getters or setters for the field.
// MAGIC 
// MAGIC Why do we care about this? Because:
// MAGIC 
// MAGIC * It's kind of cool.
// MAGIC * It's one instance of Scala's far richer access semantics.
// MAGIC * As we'll see shortly, `private[this]` can come into play when we define class constructors.

// COMMAND ----------

// MAGIC %md
// MAGIC ### Bean properties
// MAGIC 
// MAGIC Some third-party libraries you use expect objects you pass to them to adhere to the [JavaBeans Specification](http://www.oracle.com/technetwork/java/javase/documentation/spec-136004.html).
// MAGIC As this [StackOverflow answer](http://stackoverflow.com/a/3295517/53495) notes, a JavaBean must:
// MAGIC 
// MAGIC * have _private_ properties (e.g., property `x` must be set and accessed via `setX()` and `getX()` methods).
// MAGIC * have a public no-argument constructor
// MAGIC * implement `Serializable`.
// MAGIC 
// MAGIC But Scala classes do not adhere to that convention. If you need to pass an instance of a Scala object to a library that wants a JavaBean-compliant
// MAGIC object, what do you do? Do you have to implement all those (annoying) `set` and `get` methods yourself?
// MAGIC 
// MAGIC Well, no. You don't.
// MAGIC 
// MAGIC Instead, you can mark a variable with the `BeanProperty` annotation. For instance:

// COMMAND ----------

import scala.beans.BeanProperty

class Person {
  @BeanProperty var age = 0
  @BeanProperty var name = ""
}

val p = new Person
p.age = 10
assert(p.age == 10)
assert(p.age == p.getAge)
p.setAge(20)
assert(p.age == 20)


// COMMAND ----------

// MAGIC %md
// MAGIC Using `@BeanProperty` on the `var` field `name` told Scala to generate _four_ methods, instead of the usual two:
// MAGIC 
// MAGIC 1. `name`: Scala getter (get the name as a string)
// MAGIC 2. `name_=`: Scala setter
// MAGIC 3. `getName`: JavaBean getter
// MAGIC 4. `setName`: JavaBean setter
// MAGIC 
// MAGIC If you use `@BeanProperty` on a `val` field, it only generates the Scala getter and the Java getter.
// MAGIC 
// MAGIC Using `@BeanProperty`, you can create classes that can be used seamlessly with Java libraries expecting JavaBeans.
// MAGIC 
// MAGIC <div style="border: 1px solid #ddd; border-radius: 10px 10px 10px 10px; padding: 10px; background-color: #f7edcd; width: 40%; margin: 20px">
// MAGIC <h3>What if you need to adapt someone else's Scala class?</h3>
// MAGIC 
// MAGIC <p>Suppose you're using a JavaBean-compliant library, and you want to pass an instance of someone else's Scala class into it, and that
// MAGIC class doesn't use `@BeanProperty`. What can you do?</p>
// MAGIC 
// MAGIC <p>Well, you can certainly write a manual wrapper. But that's tedious, and the wrapper may break with subsequent updates.</p>
// MAGIC 
// MAGIC <p>Another option is to use Brian Clapper's [ClassUtil](http://software.clapper.org/classutil/) library. It has a series
// MAGIC of [methods](http://software.clapper.org/classutil/#generating-java-beans-from-scala-objects) that use the 
// MAGIC [ASM bytecode library](http://asm.ow2.org/) to wrap Scala objects in JavaBeans, on the fly.</p>
// MAGIC 
// MAGIC </div>

// COMMAND ----------

// MAGIC %md 
// MAGIC ### Constructors
// MAGIC 
// MAGIC Our `Person` class is a bit clunky. Instead of passing values into the class to initialize it, we have to create an
// MAGIC empty instance and then call setters.
// MAGIC 
// MAGIC #### The primary constructor
// MAGIC 
// MAGIC We can fix that easily enough, by creating a _primary constructor_.
// MAGIC 
// MAGIC In most languages, the primary constructor defined as a special method inside the class. For instance, in Java, our `Person` class's primary constructor might look like this:
// MAGIC 
// MAGIC ```
// MAGIC public class Person {
// MAGIC     private String name = "";
// MAGIC     private int age = 0;
// MAGIC     
// MAGIC     public Person(String name, int age) {
// MAGIC         this.name = name;
// MAGIC         this.age = age;
// MAGIC     }
// MAGIC     
// MAGIC     // Getters and setters go here
// MAGIC }
// MAGIC ```
// MAGIC 
// MAGIC In Scala, the primary constructor syntax is _much_ more compact, yet still readable. (Some would argue it's _more_ readable.) The parameters of the primary constructor go right
// MAGIC after the class name. In this example, we'll make the `Person` class immutable:

// COMMAND ----------

class Person(val name: String, val age: Int)

val p = new Person("Fred", 38)

// COMMAND ----------

println(s"${p.name} is ${p.age}")

// COMMAND ----------

// MAGIC %md
// MAGIC 
// MAGIC **A complete class, declared in a single line of code!**
// MAGIC 
// MAGIC Parameters of the primary constructor turn into fields that are initialized with the construction parameters.
// MAGIC 
// MAGIC Compare our 1-line class definition with the equivalent Java class definition:
// MAGIC 
// MAGIC 
// MAGIC <table>
// MAGIC   <thead>
// MAGIC     <tr>
// MAGIC       <th>Scala</th>
// MAGIC       <th>Java</th>
// MAGIC     </tr>
// MAGIC   </thead>
// MAGIC   
// MAGIC   <tbody>
// MAGIC     <tr>
// MAGIC       <td>
// MAGIC <pre>
// MAGIC class Person(val name: String, val age: Int)         
// MAGIC </pre>
// MAGIC       </td>
// MAGIC    
// MAGIC       <td>
// MAGIC <pre>
// MAGIC public class Person {
// MAGIC     private String name = "";
// MAGIC     private int age = 0;
// MAGIC     
// MAGIC     public Person(String name, int age) {
// MAGIC         this.name = name;
// MAGIC         this.age = age;
// MAGIC     }
// MAGIC     
// MAGIC     public String getName() {
// MAGIC         return this.name;
// MAGIC     }
// MAGIC     
// MAGIC     public int getAge() {
// MAGIC         return this.age;
// MAGIC     }
// MAGIC } 
// MAGIC </pre>
// MAGIC       </td>
// MAGIC     </tr>
// MAGIC   </tbody>
// MAGIC </table>  
// MAGIC 
// MAGIC These two classes are equivalent, but the Scala version is so much more compact and readable.
// MAGIC 
// MAGIC * The field assignments are generated automatically.
// MAGIC * So are the getters.

// COMMAND ----------

// MAGIC %md
// MAGIC 
// MAGIC #### Code in the primary constructor
// MAGIC 
// MAGIC What if our primary constructor needs some actual logic? In Java, that's easy: Put it in the primary constructor method. 
// MAGIC 
// MAGIC In Scala, that code just goes inline, in the class. Let's use a variant of our `Person` class to demonstrate:

// COMMAND ----------

class Person(val firstName: String, val lastName: String, val age: Int) {
  val fullName = s"$firstName $lastName" // This assignment runs as part of the primary constructor code.
}

val p = new Person("Melinda", "McPherson", 47)

// COMMAND ----------

println(s"${p.fullName} is ${p.age}")

// COMMAND ----------

// MAGIC %md
// MAGIC **A little more formally:** The primary constructor executes _all_ statements in the class definition.
// MAGIC 
// MAGIC This is also valid:

// COMMAND ----------

class Bar(val value: Int) {
  val derivedValue = value * 2
  
  def somethingRandom = value + scala.util.Random.nextInt
  
  println(s"value is $value, and derived value is $derivedValue")
  
}

// COMMAND ----------

// MAGIC %md
// MAGIC The primary constructor consists of the generated field assignment (for the `value` parameter, passed in on the constructor), plus the following lines of code, gathered from throughout the class definition:
// MAGIC 
// MAGIC ```
// MAGIC val derivedValue = value * 2
// MAGIC private val rng = new scala.util.Random
// MAGIC 
// MAGIC println(s"value is $value, and derived value is $derivedValue")
// MAGIC ```
// MAGIC 
// MAGIC Note that the `println` came _after_ the definition of the `somethingRandom` method. That doesn't matter; it's still part of the primary constructor.

// COMMAND ----------

val b = new Bar(10)
println("----- Constructor is done.")
println(b.somethingRandom)
println(b.somethingRandom)

// COMMAND ----------

// MAGIC %md 
// MAGIC #### Auxiliary Constructors
// MAGIC 
// MAGIC As in Java, a Scala class can have as many constructors as you want. In addition to the primary constructor, you can define _auxiliary_ constructors.
// MAGIC 
// MAGIC Unlike Java, though, these auxiliary instructors are _not_ named after the class. Instead, they have the name `this`. An auxiliary instructor _must_ chain to another auxiliary instructor or to the primary constructor.
// MAGIC 
// MAGIC Consider this example:

// COMMAND ----------

// Primary constructor requires a name and an age.
class Person(val name: String, val age: Int) {
  
  // This auxiliary constructor takes only a name and uses a default value for the age.
  def this(name: String) = this(name, 0)
  
  // This auxiliary constructor takes only an age and uses a default value for the name.
  def this(age: Int) = this("", age)
}

val p1 = new Person("Maria")
assert(p1.name == "Maria", "p1.name isn't Maria")
assert(p1.age == 0, "p1.age isn't 0")

val p2 = new Person(10)
assert(p2.name.isEmpty, "p2.name isn't empty")
assert(p2.age == 10, "p2.age isn't 10")

// COMMAND ----------

// MAGIC %md
// MAGIC In practice, auxiliary constructors are often not needed, as you can frequently get the same behavior with default parameter values. The above class
// MAGIC definition can be rewritten as a single line, and we can use named parameters to pass only the field we want:

// COMMAND ----------

// Primary constructor requires a name and an age.
class Person(val name: String = "", val age: Int = 0)

val p1 = new Person("Maria")
assert(p1.name == "Maria", "p1.name isn't Maria")
assert(p1.age == 0, "p1.age isn't 0")

val p2 = new Person(age=10)
assert(p2.name.isEmpty, "p2.name isn't empty")
assert(p2.age == 10, "p2.age isn't 10")

// COMMAND ----------

// MAGIC %md
// MAGIC #### What about constructor parameters that aren't `val` or `var`?
// MAGIC 
// MAGIC What if you define a class, but you don't put `val` or `var` in front of the constructor parameters?
// MAGIC 
// MAGIC One of two things happens:
// MAGIC 
// MAGIC * If the parameter is _only_ used in the constructor logic, then it's just a parameter. It lives on the constructor method's stack,
// MAGIC   and it disappears after the constructor returns.
// MAGIC * If the parameter is referenced in _any_ method, then it can't be on the stack. It has to hang around for the lifetime of the object.
// MAGIC   In that case, Scala stores its value in a `private[this]` field.
// MAGIC   

// COMMAND ----------

class ParsingPerson(fullName: String, val age: Int) {
  val (firstName, lastName) = fullName.split(":") match {
    case Array(f, l, _ @ _*) => (f, l)
    case Array(f) => (f, "")
    case _ => ("", "")
  }
}

val p1 = new ParsingPerson("Moe Howard", 109)

// COMMAND ----------

println(s"${p1.firstName} is ${p1.age}")

// COMMAND ----------

// MAGIC %md
// MAGIC In that case, `fullName` is only referenced in the constructor logic, so it's only a constructor parameter. Its value isn't saved anywhere.
// MAGIC 
// MAGIC Here's a different case:

// COMMAND ----------

class ParsingPerson2(fullName: String, val age: Int) {
  val (firstName, lastName) = fullName.split(":") match {
    case Array(f, l, _ @ _*) => (f, l)
    case Array(f) => (f, "")
    case _ => ("", "")
  }
  
  override def toString = s"ParsingPerson(${fullName}, ${age})"
}

val p2 = new ParsingPerson("Larry Fine", 104)

// COMMAND ----------

println(s"${p2.firstName} is ${p2.age}")

// COMMAND ----------

// MAGIC %md
// MAGIC In this second case, the `fullName` parameter is referenced by the overridden `toString` method, so it must be captured somewhere. The Scala compiler stores it in a `private[this]` field.

// COMMAND ----------

// MAGIC %md
// MAGIC ### Exercise: Use your understanding of constructors to fill in the following class
// MAGIC 
// MAGIC #### Time to complete: 5 minutes
// MAGIC 
// MAGIC 
// MAGIC **Requirements**
// MAGIC 
// MAGIC The `Employee` class must have these properties:
// MAGIC 
// MAGIC * A read-only `firstName` field.
// MAGIC * A read-only `lastName` field.
// MAGIC * A `birthDateString` parameter, which will be a string of the form `yyyy-mm-dd` (e.g., `"1990-10-30"`). Don't worry about
// MAGIC   invalid date strings. Use the `parseDate` method, supplied, to parse it.
// MAGIC * A read-only (derived) `birthDate` field, of type `java.util.Date`.
// MAGIC * A read-only (derived) `age` field.
// MAGIC * A read-only (derived) `fullName` property that puts the first and last name together.
// MAGIC * An optional `ssn` (Social Security Number) field, of type `Option[String]`.
// MAGIC 
// MAGIC Support these constructor patterns:
// MAGIC 
// MAGIC * `new Employee("Melody", "Smythe", "1983-10-03", Some(966-40-9084))` — all parameters specified.
// MAGIC * `new Employee("Ravi", "Kumar", "1956-01-17)` — no SSN specified
// MAGIC 
// MAGIC The class stub contains some private helper methods you might find useful.

// COMMAND ----------

// TODO
import java.util.Date

class Employee(/* fill this in */) {
  /* fill this in */
  
  private def calcAge(birthDate: Date): Int = {
    import org.joda.time.{LocalDate, Years}
    
    val localBirthDate = new LocalDate(birthDate.getTime)
    val now = new LocalDate
    Years.yearsBetween(localBirthDate, now).getYears
  }
  
  private def parseDate(dateString: String): Date = {
    (new java.text.SimpleDateFormat("yyyy-MM-dd")).parse(dateString)
  }
}

// COMMAND ----------

// ANSWER
import java.util.Date

class Employee(val firstName:       String,
               val lastName:        String,
               val birthDateString: String,
               val ssn:             Option[String] = None) {
  import java.util.Date
  
  val birthDate = parseDate(birthDateString)
  val age = calcAge(birthDate)
  val fullName = s"$firstName $lastName"
  
  private def calcAge(birthDate: Date): Int = {
    import org.joda.time.{LocalDate, Years}
    
    val localBirthDate = new LocalDate(birthDate.getTime)
    val now = new LocalDate
    Years.yearsBetween(localBirthDate, now).getYears
  }
  
  private def parseDate(dateString: String): Date = {
    (new java.text.SimpleDateFormat("yyyy-MM-dd")).parse(dateString)
  }
}

// COMMAND ----------

val e1 = new Employee("John", "Smallberries", "1959-10-31", Some("923-37-5027"))
val e2 = new Employee("Anisa", "Gutman", "1989-02-16")

assert(e1.fullName == "John Smallberries", "e1.fullName isn't correct")
assert(e1.lastName == "Smallberries", "e1.lastName isn't correct")
assert(e1.ssn.getOrElse("") == "923-37-5027", "e1.ssn isn't correct")
val ld1 = new org.joda.time.LocalDate(e1.birthDate.getTime)
assert(ld1.getYear == 1959, "e1.birthDate has the wrong year")
assert(ld1.getMonthOfYear == 10, "e1.birthDate has the wrong month")
assert(ld1.getDayOfMonth == 31, "e1.birthDate has the wrong day")

assert(e2.fullName == "Anisa Gutman", "e1.fullName isn't correct")
assert(e2.firstName == "Anisa", "e2.firstName isn't correct")
assert(e2.ssn.isEmpty, "e2.ssn is defined, but shouldn't be")
val ld2 = new org.joda.time.LocalDate(e2.birthDate.getTime)
assert(ld2.getYear == 1989, "e2.birthDate has the wrong year")
assert(ld2.getMonthOfYear == 2, "e2.birthDate has the wrong month")
assert(ld2.getDayOfMonth == 16, "e2.birthDate has the wrong day")


// COMMAND ----------

// MAGIC %md
// MAGIC ### Basic Inheritance 
// MAGIC 
// MAGIC You can inherit from a class, as you would expect. Just like Java, use the `extends` keyword:

// COMMAND ----------

class Foo {
  val i: Int = Math.abs(scala.util.Random.nextInt)
}

class Bar extends Foo

val foo = new Foo
val bar = new Bar

// COMMAND ----------

println(s"foo.i=${foo.i}")
println(s"bar.i=${bar.i}")

// COMMAND ----------

// MAGIC %md 
// MAGIC What if your superclass takes parameters? Easy:

// COMMAND ----------

class Customer(name: String, age: Int, val customerID: String) extends Person(name, age)

val customer = new Customer("John Bigbooté", 55, "X0982349-AL")

// COMMAND ----------

println(s"${customer.name}, ${customer.age}, ${customer.customerID}")

// COMMAND ----------

// MAGIC %md
// MAGIC If you don't want anyone to be able to subclass your class, mark it `final`.

// COMMAND ----------

final class Quux {
  var i: Int = 0
}

// COMMAND ----------

class Fribble extends Quux

// COMMAND ----------

// MAGIC %md 
// MAGIC ### Scala's inheritance hierarchy
// MAGIC 
// MAGIC <img src="http://i.imgur.com/hr7kB5K.png" style="float:right"/>
// MAGIC 
// MAGIC Put this cell in your pocket, for later reference.
// MAGIC 
// MAGIC Scala attempts to model _everything_ as an object, but the JVM doesn't. (The JVM has non-object primitives, like `int`, `float`, and arrays.)
// MAGIC 
// MAGIC Scala's inheritance hierarchy reflects that impedance mismatch.
// MAGIC 
// MAGIC * Classes that correspond to Java primitive types, as well as `Unit`, extend `AnyVal`.
// MAGIC * All other classes extend `AnyRef`, which is a synonym for `java.lang.Object`.
// MAGIC * Both `AnyVal` and `AnyRef` extend `Any`, at the root of the hierarchy.
// MAGIC * All Scala classes implement the marker interface `ScalaObject`.

// COMMAND ----------

// MAGIC %md
// MAGIC ### Abstract classes
// MAGIC 
// MAGIC Scala, like Java, supports abstract classes. An abstract class cannot be instantiated, but it's a good place to stash common logic.
// MAGIC 
// MAGIC * Abstract classes are marked with the keyword `abstract`. 
// MAGIC * They can also have abstract methods (methods without a body) and abstract fields (field definitions without an initializer).
// MAGIC * Their constructors can take parameters.

// COMMAND ----------

abstract class Pet(val age: Int) {
  val name: String // no initializer => abstract field
  
  def speak: String // no body => abstract
}

// COMMAND ----------

// MAGIC %md
// MAGIC Any subclass of `Pet` _must_ provide a `name` field and a `speak` method, or it will not compile:

// COMMAND ----------

class BadDog(age: Int, val name: String) extends Pet(age)

// COMMAND ----------

class Dog(age: Int, val name: String) extends Pet(age) {
  def speak = "Woof!"
}

// COMMAND ----------

// MAGIC %md
// MAGIC Interestingly, it's possible to override a `def` with a `val`:

// COMMAND ----------

class RefinedDog(age: Int, val name: String) extends Pet(age) {
  val speak = "Woof!"
}

// COMMAND ----------

// MAGIC %md
// MAGIC The rules for overriding are:
// MAGIC 
// MAGIC * A `def` in a subclass can _only_ override a `def` in a superclass. (In other words, a `def` cannot override a `val` or a `var`.)
// MAGIC * A `val` in a subclass can only override another `val` or a parameterless `def`.
// MAGIC * A `var` can only override an abstract `var`.

// COMMAND ----------

// MAGIC %md
// MAGIC <img alt="Warning" src="http://i.imgur.com/TvgcpDH.png" style="float: left; margin-right: 10px"/>
// MAGIC Using a `var` in a non-final class is _not_ a good idea. While you can always hide it later, with your own custom
// MAGIC getter and setter, anyone _extending_ your class doesn't have that option. They _cannot_ override a `var` with a getter/setter pair.
// MAGIC **If you provide a `var` in a superclass, all subclasses are stuck with it.**

// COMMAND ----------

class MyBaseClass {
  var i: Int = 0
}

class MySubClass extends MyBaseClass {
  var _i: Int = 0
  override def i = _i
  override def i_=(newI: Int): Unit = {_i = newI}
}

// COMMAND ----------

// MAGIC %md
// MAGIC ### Overriding concrete members vs overriding abstract members
// MAGIC 
// MAGIC When overriding a concrete member in a superclass, you _must_ use the `override` keyword. This keyword is like the `@Override` annotation in Java, except that (a) it's a keyword, and (b) it's not optional.
// MAGIC 
// MAGIC When overriding an abstract member, you do not need `override`.
// MAGIC 
// MAGIC Some examples will clarify.

// COMMAND ----------

abstract class SomeBase {
  def abstractMethod1: String
  
  def concreteMethod1: String = "foo"
}

// COMMAND ----------

// This will compile just fine.
class GoodSubclass extends SomeBase {
  val abstractMethod1 = "Rickrollin'..."
  override val concreteMethod1 = "...sucks"
}

// COMMAND ----------

// This won't.
class BadSubclass extends SomeBase {
  val abstractMethod1 = "Can we take a break now?"
  val concreteMethod1 = "I could use some caffeine."
}

// COMMAND ----------

// MAGIC %md
// MAGIC ## Traits
// MAGIC 
// MAGIC Conceptually, a trait is similar to a [Java 8 default method implementation](https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html) in a Java interface. However, traits are considerably more powerful.
// MAGIC 
// MAGIC Like Java, Scala only permits you to extend one base class. Also like Java, you can mix in multiple traits (interfaces). Traits can also impose requirements on classes that mix them in.
// MAGIC 
// MAGIC ### Summary
// MAGIC 
// MAGIC * A class can implement any number of traits.
// MAGIC * Traits can require that implementing classes have certain fields, methods, or superclasses.
// MAGIC * A trait can provide default implementations for methods and fields.
// MAGIC * When you layer multiple traits, order matters: The trait whose methods execute _first_ is the last one in the list.

// COMMAND ----------

// MAGIC %md
// MAGIC ### Review: Why is there no multiple inheritance?
// MAGIC 
// MAGIC <img src="http://i.imgur.com/Sniasdo.png" style="float: right"/>
// MAGIC 
// MAGIC Scala does not permit a class to inherit from multiple superclasses. Why? Primarily because _Java_ doesn't permit it.
// MAGIC 
// MAGIC Other languages (such as Python and C++) do permit it. Why did the Java designers decide to avoid it? 
// MAGIC 
// MAGIC One reason was the avoid having to deal with the _diamond inheritance problem_.
// MAGIC 
// MAGIC Consider the diagram to the right, which depicts multiple inheritance in a language that supports it. In the `TeachingAssistant` class, we only want _one_ `name` field, not two. 
// MAGIC 
// MAGIC * Which one do we get, the one from `Student` or the one from `Employee`?
// MAGIC * How is it constructed?
// MAGIC 
// MAGIC Java designers decided to punt on the complexity go for a simpler approach:
// MAGIC 
// MAGIC * A class can only extend one superclass.
// MAGIC * A class can implement any number of interrfaces, but (prior to Java 8), interfaces can only have abstract methods and no fields.
// MAGIC 
// MAGIC Often, you want to provide utility methods that are implemented in terms of other methods. In traditional Java interfaces, you cannot do that. Therefore,
// MAGIC it's common in Java to provide _both_ an interface and an abstract base class. However, that just pushes the problem onto the poor programmer who needs to
// MAGIC use the API. What if you need to extend _two_ such abstract classes?
// MAGIC 
// MAGIC Scala's traits solve this problem, as do Java 8 interfaces. But Scala traits are more elegant and powerful.

// COMMAND ----------

// MAGIC %md
// MAGIC ### Traits as interfaces
// MAGIC 
// MAGIC A Scala trait can work exactly like an interface.

// COMMAND ----------

trait CanLog {
  def log(msg: String): Unit  // abstract method
}

// COMMAND ----------

// MAGIC %md
// MAGIC A subclass can now provide an implementation.
// MAGIC 
// MAGIC **NOTE**: When implementing a single trait, using `extends`1, just as you would when extending a class.

// COMMAND ----------

class ConsoleLogger extends CanLog {
  def log(msg: String): Unit = {
    println(msg)
  }
}

val logger = new ConsoleLogger
logger.log("One of the flayrods has gone out on treadle.")

// COMMAND ----------

// MAGIC %md
// MAGIC #### Extending multiple traits
// MAGIC 
// MAGIC If you need more than one trait, add the others using the `with` keyword. Note that Scala treats Java interfaces as if they were traits.

// COMMAND ----------

class ConsoleLogger extends CanLog with Cloneable with Serializable {
  def log(msg: String): Unit = {
    println(msg)
  }
}

// COMMAND ----------

// MAGIC %md
// MAGIC ![?](http://i.imgur.com/Guv4TBn.png) **Why do we use `extends` on the first one, but `with` for all the others?**
// MAGIC 
// MAGIC Scala actually views the inheritance like this:
// MAGIC 
// MAGIC ```
// MAGIC ConsoleLogger extends [CanLog with Cloneable with Serializable]
// MAGIC ```
// MAGIC 
// MAGIC In other words, `ConsoleLogger` is extending a _composite_ that consists of `CanLog` with `Cloneable` and `Serializable`.

// COMMAND ----------

// MAGIC %md
// MAGIC ### Traits with implementations
// MAGIC 
// MAGIC A trait's methods need not be abstract.

// COMMAND ----------

trait ConsoleLogger {
  def log(msg: String): Unit = println(msg)
}

// COMMAND ----------

// MAGIC %md
// MAGIC We now have a trait with a method, and since it's a trait, it can be mixed into other classes.

// COMMAND ----------

class Customer(name: String, age: Int, val customerID: String) extends Person(name, age) with ConsoleLogger {
  def recordPurchase(product: String, quantity: Int, price: BigDecimal): Unit = {
    log(s"""Customer $customerID just bought "$product" (quantity $quantity) for $price""")
  }
}


// COMMAND ----------

val customer = new Customer("John Yaya", 47, "Y12994310-AL")
customer.recordPurchase("overthruster", 1, 957.99)

// COMMAND ----------

// MAGIC %md
// MAGIC <img alt="Warning" src="http://i.imgur.com/TvgcpDH.png" style="float: left; margin-right: 10px"/>
// MAGIC There's one problem with traits that have concrete implementions: When such a trait changes, _all_ class that mix the trait in _must_ be recompiled.

// COMMAND ----------

// MAGIC %md
// MAGIC ### Fields in traits
// MAGIC 
// MAGIC Traits can have fields (values), not just methods.
// MAGIC 
// MAGIC #### Concrete fields
// MAGIC 
// MAGIC Traits can have concrete (non-abstract) fields. Consider:

// COMMAND ----------

// MAGIC %md
// MAGIC Traits can have concrete values, not just default method implementations. Consider:

// COMMAND ----------

trait ShortLogger extends CanLog {
  val maxLength = 15
  
  abstract override def log(msg: String): Unit = {
    super.log(
      if (msg.length < maxLength) msg else msg.take(maxLength - 3).mkString("") + "..."
    )
  }
}

// COMMAND ----------

// MAGIC %md
// MAGIC Any class that mixes `ShortLogger` in acquires a `maxLength` field. The field is added _directly_ to the class; it's not inherited. 

// COMMAND ----------

val log = new ConsoleLogger with ShortLogger

// COMMAND ----------

log.log("This is a message that exceeds 15 characters. It should be truncated before being written to the console.")

// COMMAND ----------

// MAGIC %md
// MAGIC You can think of concrete trait fields as "assembly instructions" for the classes that use the trait. Any such fields become fields of the class.

// COMMAND ----------

// MAGIC %md
// MAGIC <div style="border: 1px solid #ddd; border-radius: 10px 10px 10px 10px; padding: 10px; background-color: #f7edcd; width: 40%; margin: 20px">
// MAGIC <h3>Why do I need <tt>abstract override</tt> when <tt>CanLog.log()</tt> is already abstract?</h3>
// MAGIC 
// MAGIC <p>Scala has no way of knowing which <tt>log()</tt> method <tt>super.log()</tt> is invoking, because it all depends on what traits
// MAGIC are mixed in and in what order. So, the Scala compiler takes the position that <tt>log()</tt> is still abstract, even though we're
// MAGIC providing a default implementation. It still requires that some <i>concrete</i> implementation be mixed in. Thus, you have to take
// MAGIC the <tt>ShortLogger</tt> version of <tt>log()</tt> with <tt>abstract override</tt>.</p>
// MAGIC </div>

// COMMAND ----------

// MAGIC %md 
// MAGIC #### Abstract fields
// MAGIC 
// MAGIC Traits can also have abstract fields. Any concrete class that mixes in the trait must supply an concrete value for the field. Let's demonstrate with a variation of the `ShortLogger` trait that doesn't hard-code the maxLength.

// COMMAND ----------

trait ShortLogger extends CanLog {
  val maxLength: Int  // no initialized value, so abstract
  
  override abstract def log(msg: String): Unit = {
    super.log(
      if (msg.length < maxLength) msg else msg.take(maxLength - 3).mkString("") + "..."
    )
  }
}

// COMMAND ----------

// MAGIC %md
// MAGIC Now, any attempt to instantiate a class that mixes in `ShortLogger` will fail, unless we also provide a `maxLength` concrete field.

// COMMAND ----------

// This fails
val log = new ConsoleLogger with ShortLogger

// COMMAND ----------

// This succeeds
val log = new ConsoleLogger with ShortLogger {
  val maxLength = 20
}

// COMMAND ----------

log.log("This is a message that exceeds 20 characters. It should be truncated before being written to the console.")

// COMMAND ----------

// MAGIC %md 
// MAGIC ### Trait construction order and layered traits

// COMMAND ----------

// MAGIC %md
// MAGIC Just like classes, traits can have primary constructors. For instance:

// COMMAND ----------

trait FileLogger extends CanLog {
  val out = new java.io.PrintWriter("/tmp/phase.log")
  out.println(s"# STARTING NEW LOG AT ${new java.util.Date}")
  
  abstract override def log(msg: String): Unit = {
    out.println(msg)
    out.flush()
    super.log(msg)
  }
}

// COMMAND ----------

val log = new ConsoleLogger with FileLogger

// COMMAND ----------

log.log("This is a message that should go to the file and the console.")

// COMMAND ----------

import scala.sys.process._
import scala.language.postfixOps

println("cat /tmp/phase.log"!!)

// COMMAND ----------

// MAGIC %md
// MAGIC With traits mixed in, here's how object construction works:
// MAGIC 
// MAGIC 1. The superclass constructor is called first.
// MAGIC 2. Trait constructors are executed next, _before_ the class constructor.
// MAGIC 3. Traits are constructed left to right, but:
// MAGIC 4. ...within each trait, _parents_ are constructed first.
// MAGIC 5. If multiple traits share a common parent, and that parent has been constructed already, it is not constructed again.
// MAGIC 6. After all traits are constructed, the subclass is constructed.
// MAGIC 
// MAGIC Confused? Let's clarify with an example.

// COMMAND ----------

abstract class Account(account: String) extends CanLog {
  val accountNumber = account.replace("-", "")
  
  override def log(msg: String): Unit = {}
}

class SavingsAccount(accountNumber: String, val interestRate: BigDecimal) extends Account(accountNumber) with FileLogger with ShortLogger {
  val maxLength = 50
}

// COMMAND ----------

val acct = new SavingsAccount("203-94-87234-1", BigDecimal(0.3))
acct.log(s"Acct number: ${acct.accountNumber}")

// COMMAND ----------

println("cat /tmp/phase.log"!!)

// COMMAND ----------

// MAGIC %md
// MAGIC The `SavingsAccount` object is constructed in this order:
// MAGIC 
// MAGIC 1. The `Account` constructor fires (initializing `accountNumber`).
// MAGIC 2. The `CanLog` trait is constructed, as it's the parent of the first trait, `FileLogger`.
// MAGIC 3. `FileLogger` is constructed next.
// MAGIC 4. Then, `ShortLogger` is constructed—but, since its parent, `CanLog` has already been constructed, it isn't constructed again.
// MAGIC 5. Finally, `SavingsAccount` is constructed.

// COMMAND ----------

// MAGIC %md
// MAGIC This approach lends itself nicely to using traits as an on-the-fly composition mechanism. Since we don't really have time to do another exercise, let's just look at a couple quick examples.

// COMMAND ----------

// Logger that adds a timestamp to a log message and forwards the result along.
trait TimestampLogger extends CanLog {
  private val format = new java.text.SimpleDateFormat("yyyy/mm/dd HH:MM:ss.SSS")
  abstract override def log(msg: String): Unit = {
    val now = new java.util.Date
    super.log(s"${format.format(now)}: $msg")
  }
}

// COMMAND ----------

val log1 = new ConsoleLogger with ShortLogger with TimestampLogger {
  val maxLength = 24
}
val log2 = new ConsoleLogger with TimestampLogger with ShortLogger {
  val maxLength = 24
}

// COMMAND ----------

// MAGIC %md
// MAGIC Okay, watch carefully. I'll want someone to explain what's going on here.

// COMMAND ----------

val msg = "Strange women lying in ponds is no basis for a system of government."

log1.log(msg)
log2.log(msg)

// COMMAND ----------

// MAGIC %md
// MAGIC ## Objects
// MAGIC 
// MAGIC Scala supports the notion of a singleton object, and that's the final concept we'll be covering in this lesson.
// MAGIC 
// MAGIC ### Summary
// MAGIC 
// MAGIC * Objects are for singletons and utility methods.
// MAGIC * A class can have a companion object of the same name.
// MAGIC * Objects can extend classes and traits.
// MAGIC * The `apply()` method of an object is special.
// MAGIC 
// MAGIC ### Where the \*@$! is the "static" keyword?
// MAGIC 
// MAGIC Scala has no `static` keyword. Classes must be instantiated to call any methods on them.
// MAGIC 
// MAGIC Instead of `static` methods and `static` classes, Scala has singleton objects. For example:

// COMMAND ----------

object Stat {
  def median[T](items: T*)(implicit n: Numeric[T]): Double = {
    val itemList = items.toList
    val len = itemList.length
    require (len > 0)

    if (len == 1)
      n.toDouble(itemList.head)

    else {
      val sorted = itemList sortWith (n.compare(_, _) < 0)
      val midpoint = sorted.length / 2
      len % 2 match {
        case 0 => mean(n.toDouble(sorted(midpoint)),
                       n.toDouble(sorted(midpoint - 1)))
        case 1 => n.toDouble(sorted(midpoint))
      }
    }
  }
  
  def mean[T](items: T*)(implicit n: Numeric[T]): Double = {
    val itemList = items.toList
    val len = itemList.length
    require (len > 0)

    len match {
      case 1 => n.toDouble(itemList.head)
      case _ => (0.0 /: itemList)((a, b) => a + n.toDouble(b)) / len
    }
  }
}

// COMMAND ----------

val values = (1 to 40).map { _ => scala.util.Random.nextInt(20) }
println(s"Values = ${values.mkString(", ")}")
println(s"mean=${Stat.mean(values: _*)}, median=${Stat.median(values: _*)}")
println("-" * 30)

// COMMAND ----------

// MAGIC %md
// MAGIC **An `object` is _not_ compiled to a static class.**
// MAGIC 
// MAGIC Here's what `javap -p` says about our `Stat` class:
// MAGIC 
// MAGIC ```
// MAGIC public final class Stat$ {
// MAGIC   public static final Stat$ MODULE$;
// MAGIC   public static {};
// MAGIC   public <T> double median(scala.collection.Seq<T>, scala.math.Numeric<T>);
// MAGIC   public <T> double mean(scala.collection.Seq<T>, scala.math.Numeric<T>);
// MAGIC   private Stat$();
// MAGIC }
// MAGIC ```
// MAGIC 
// MAGIC An `object` is constructed once, upon first use. If an `object` is never used, it is never constructed.
// MAGIC 
// MAGIC #### Objects as packages
// MAGIC 
// MAGIC You can also import from an `object`. (In Scala, you can pretty much import from anything, as we saw in Marcus Henry's lesson.) Because of this feature, some people use objects as leaf-level packages.

// COMMAND ----------

import Stat._
val values = (1 to 40).map { _ => scala.util.Random.nextInt(20) }
println(s"Values = ${values.mkString(", ")}")
println(s"mean=${mean(values: _*)}, median=${median(values: _*)}")
println("-" * 30)

// COMMAND ----------

// MAGIC %md 
// MAGIC ### Companion objects
// MAGIC 
// MAGIC A common pattern in Java is the factory pattern, where you construct an object by calling a static method rather than the constructor. Without static methods, how do we do the same thing in Scala?
// MAGIC 
// MAGIC In Scala, you can have a `class` and an `object` that have the same name. As long as they're defined in the same source file, they're called _companion objects_.

// COMMAND ----------

class Account(val accountNumber: String)

object Account {
  def create(accountNumber: String) = new Account(accountNumber)
}

// COMMAND ----------

// MAGIC %md
// MAGIC Now, I can create an `Account` object two ways:

// COMMAND ----------

val a1 = new Account("0927834")
val a2 = Account.create("87654233")

// COMMAND ----------

// MAGIC %md
// MAGIC But there's more to it than that. Companion objects share a special relationship: They can (you'll pardon the expression) _see each other's privates_.
// MAGIC 
// MAGIC Let's make the `Account` constructor private, and try again.

// COMMAND ----------

class Account private(val accountNumber: String)

object Account {
  def create(accountNumber: String) = new Account(accountNumber)
}

// COMMAND ----------

// MAGIC %md
// MAGIC Now, I can't call the constructor:

// COMMAND ----------

val a1 = new Account("0927834")

// COMMAND ----------

// MAGIC %md
// MAGIC I have to use the factory method:

// COMMAND ----------

val a2 = Account.create("0927834")

// COMMAND ----------

// MAGIC %md
// MAGIC ### The `apply()` method
// MAGIC 
// MAGIC There's a special method you can use on _any_ object (whether instantiated from a class or an `object` singleton) called `apply()`. Scala has special syntactic sugar for apply, best described by example. Let's convert our `Account` example so that `create` is called `apply`.

// COMMAND ----------

class Account private(val accountNumber: String)

object Account {
  def apply(accountNumber: String) = new Account(accountNumber)
}

// COMMAND ----------

// MAGIC %md
// MAGIC Naturally, I can now create an account by calling `Account.apply()`. But, Scala has a shorter syntax: If I try to invoke the object as if it were a function, like this:
// MAGIC 
// MAGIC ```
// MAGIC Account()
// MAGIC ```
// MAGIC 
// MAGIC Scala translates the attempted call to
// MAGIC ```
// MAGIC Account.apply()
// MAGIC ```

// COMMAND ----------

val a2 = Account("0923409")

// COMMAND ----------

// MAGIC %md
// MAGIC #### Aside
// MAGIC 
// MAGIC This is why Scala's array subscript "operator" is parentheses, not brackets: There's an `apply()` method on the `Array` _class_. You can call the method on any Array _instance_ to subscript the array.

// COMMAND ----------

val array = (1 to 30).toArray
println(array(3))
println(array.apply(3))

// COMMAND ----------

// MAGIC %md 
// MAGIC ### Objects can extend traits and classes
// MAGIC 
// MAGIC A singleton `object` can extend any class or trait. 

// COMMAND ----------

object MyLogger extends ConsoleLogger with TimestampLogger with ShortLogger {
  val maxLength = 80
}

// COMMAND ----------

MyLogger.log("Here's my fancy log message.")

// COMMAND ----------

// MAGIC %md
// MAGIC ### Objects do _not_ have constructor parameters
// MAGIC 
// MAGIC You can put logic in the object's constructor, exactly as you would do with a class or trait. _But_ objects cannot be passed constructor parameters. (That's what classes are for.)

// COMMAND ----------

object Foo {
  println("This will fire when I first use the object.")
  
  def doSomethingWith(i: Int) = println(i)
}

// COMMAND ----------

Foo.doSomethingWith(10)
Foo.doSomethingWith(20)

// COMMAND ----------

// MAGIC %md 
// MAGIC ### Objects as main programs
// MAGIC 
// MAGIC There are two ways to build a main program in Scala. The most straightforward way is to write an object that contains a `main()` method:

// COMMAND ----------

object MyProgram1 {
  def main(args: Array[String]): Unit = {
    println("Bonjour, tout le monde.")
    args.foreach(println)
  }
}

// COMMAND ----------

// MAGIC %md
// MAGIC I've pre-built a fat jar containing this program and the Scala libraries. Let's download it and run it on the server.

// COMMAND ----------

"""rm MyProgram1.jar"""!!

// COMMAND ----------

"""wget -q -O MyProgram1.jar https://dl.dropboxusercontent.com/u/20993411/MyProgram1.jar"""!!

// COMMAND ----------

println("ls -CF"!!)

// COMMAND ----------

println("java -jar MyProgram1.jar foo bar baz"!!)

// COMMAND ----------

// MAGIC %md
// MAGIC Another way to create a main program in Scala is to use the special `App` trait. Then, everything in the `object` constructor becomes the main program, and the arguments are available as `args`:

// COMMAND ----------

object MyProgram2 extends App {
  println("Hola, mundo.")
  args.foreach(println)
}

// COMMAND ----------

// MAGIC %md
// MAGIC Let's try that one on the server, too.

// COMMAND ----------

"""rm -f MyProgram2.jar"""!!

// COMMAND ----------

"""wget -O MyProgram2.jar -q https://dl.dropboxusercontent.com/u/20993411/MyProgram2.jar"""!!

// COMMAND ----------

println("java -jar MyProgram2.jar foobar quux"!!)

// COMMAND ----------

// MAGIC %md
// MAGIC <img src="http://www.scala-lang.org/resources/img/smooth-spiral.png" style="float: left; height: 100px; margin-right: 20px; margin-top: 0px"/>
// MAGIC # That's it!
// MAGIC 
// MAGIC Thanks for your attention.
