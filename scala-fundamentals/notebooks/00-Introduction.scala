// Databricks notebook source exported at Sat, 22 Oct 2016 10:37:45 UTC
// MAGIC %md
// MAGIC <img src="http://www.scala-lang.org/resources/img/smooth-spiral.png" style="float: right; height: 100px; margin-right: 50px; margin-top: 20px"/>
// MAGIC # Scala Fundamentals
// MAGIC 
// MAGIC Presented by the [Philly Area Scala Enthusiasts (PHASE)](http://scala-phase.org).
// MAGIC 
// MAGIC **NOTE**: You can view this notebook online at <http://tinyurl.com/scala-fundamentals-intro>
// MAGIC 
// MAGIC ## Acknowledgements
// MAGIC 
// MAGIC Thanks to the following people and companies.
// MAGIC 
// MAGIC ### Sponsors
// MAGIC 
// MAGIC **You!** Each of you who paid the $75 registration fee helped us cover the costs of this event.
// MAGIC 
// MAGIC Since the registration fee didn't cover all the costs, the following companies kicked in some extra cash to help:
// MAGIC 
// MAGIC <img src="http://www.wingspan.com/wp-content/uploads/2016/01/Wingspan-Logo-300x100.gif" style="height: 48px"/> <img src="https://s3.amazonaws.com/ardentex-workspace/mini-logo-transparent-small.png" style="height: 48px"/> <img src="http://chariotsolutions.com/wp-content/uploads/event/2015/11/59fc6d9e-cb95-11e0-816a-5fcb627c2ae5-1.jpg" style="height: 48px"/>
// MAGIC 
// MAGIC Finally, a big thank-you to ![Cerner](http://www.cerner.com/include/img/logo-cerner.gif) for providing both the venue and the WiFi access for today's event.
// MAGIC 
// MAGIC ### Instructors
// MAGIC 
// MAGIC Your instructors donated a _lot_ of time and energy preparing for this event. They are:
// MAGIC 
// MAGIC * Michael Pilquist
// MAGIC * Marcus Henry, Jr.
// MAGIC * Sujan Kapadia
// MAGIC * Brad Miller
// MAGIC * Martin Snyder
// MAGIC * Brian Clapper
// MAGIC 
// MAGIC ## Databricks Community Edition
// MAGIC 
// MAGIC <img src="https://databricks.com/wp-content/themes/databricks/assets/images/header_logo.png?v=2.98" style="float: right; margin-left: 20px"/>
// MAGIC 
// MAGIC We're using [Databricks Community Edition](http://databricks.com/ce/) for our class today. Databricks provides [Apache Spark](http://spark.apache.org) as a service, with a notebook-based interface. We're not doing any Spark here, but we're making use of the convenient, cloud-based, and _free_ notebook environment.
// MAGIC 
// MAGIC There's a notebook cheat-sheet in the courseware bundle. It'll help you master this environment.
// MAGIC 
// MAGIC Your Community Edition account is free forever, by the way. Continue to use it after the course, to explore Scala (or even play with Spark) on your own.
// MAGIC 
// MAGIC ## Courseware
// MAGIC 
// MAGIC Let's walk through importing the courseware into Databricks. Once everyone has imported the notebook, we're off to the races.
// MAGIC 
// MAGIC Log into your Databricks Community Edition account. Then:
// MAGIC 
// MAGIC 1. On the left edge of the window, click the "Home" button.  You'll see a file browser appear.  
// MAGIC <img src="https://s3-us-west-2.amazonaws.com/curriculum-release/setup_notebook/Home+Button.png" style="margin-top: 20px">
// MAGIC 
// MAGIC 2. Locate your username, and then click on the triangle icon to the right of your username.  
// MAGIC <img src="https://s3-us-west-2.amazonaws.com/curriculum-release/setup_notebook/User+Name.png" style="margin-top: 20px">
// MAGIC 
// MAGIC 3. In the popup menu, select "Import."  
// MAGIC <img src="https://s3-us-west-2.amazonaws.com/curriculum-release/setup_notebook/Import+Button.png" style="margin-top: 20px"> 
// MAGIC 
// MAGIC 4. Select "Import From URL" and copy paste this URL into the dialog box:  
// MAGIC <http://scala-phase.org/scala-fundamentals/notebooks.dbc>  
// MAGIC 
// MAGIC 5. Click "Import."  It may take a minute or two for all files to successfully import.  (They will progressively appear.)  
// MAGIC <img src="http://i.imgur.com/fMq0sWt.png" style="height: 250px; margin-top: 20px; clip-path: inset(250px 100px))"/>
// MAGIC 
// MAGIC <br/><img src="https://openclipart.org/download/58669/tooltip.svg" alt="Tip" style="height: 90px"/>
// MAGIC > You can later download your lab solutions and share them with others by choosing the "Export" option instead of the import option.  This will download a .dbc archive (databricks cloud archive, a variation of a zip file) that you can then import into any other Databricks account.  Alternatively you can download a folder as a zip of HTML files.  You can even download individual notebooks as source code to run them Scala.
