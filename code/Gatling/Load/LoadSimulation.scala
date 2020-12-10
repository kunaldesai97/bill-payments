
import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class LoadSimulation extends Simulation {

	val httpProtocol = http
		.baseUrl("http://20.151.27.234")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
		.acceptHeader("*/*")
		.acceptEncodingHeader("gzip, deflate")
		.authorizationHeader("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNzQ5MTVhMTAtNGMzYi00ZDg4LThiZDAtZDk5OWYxNDBlZDJhIiwidGltZSI6MTYwNDA0Mzk2MS44ODU5NjZ9.cCsA5iMZG6TQ_DeCP5mAdT_I5b2mSz5mhCh5gp1r1jU")
		.contentTypeHeader("application/json")
    .userAgentHeader("PostmanRuntime/7.26.8")

  // CSV Feeders
  val user_details = csv("user.csv").random
  val biller_details = csv("biller.csv").random
  val bill_details = csv("bill.csv").random
  val pay_details = csv("pay.csv").random

  // Load testing scenario
	val scn =  scenario("Load Testing").repeat(20){
    forever(){

      // Adding the feeders
      feed(user_details)
      .feed(biller_details)
      .feed(bill_details)
      .feed(pay_details)

      // Create User
		  .exec(http("Create User")
			.post("/api/v1/user/")
      .body(StringBody("""{ 
        "fname": "${fname}", 
        "lname": "${lname}",
        "uname": "${uname}",
        "secquestion": "${secquestion}",
        "secanswer": "${secanswer}"
        }"""))
      .check(status is 200)
      .check(jsonPath("$.user_id").saveAs("user_id")))


      // Get User
      .exec(http("Get User")
      .get("/api/v1/user/${user_id}")
      .check(status is 200))


      // Log in User
      .exec(http("Log in User")
      .put("/api/v1/user/login")
      .body(StringBody("""{
        "uid": "${user_id}"
        }"""
        ))
      .check(bodyString.saveAs("jwt"))
      .check(status is 200))


      // Create Biller
      .exec(http("Create Biller")
			.post("/api/v1/biller/")
			.body(StringBody("""{ 
        "biller": "${biller}", 
        "description": "${description}"
        }"""))
      .check(status is 200)
      .check(jsonPath("$.biller_id").saveAs("biller_id")))


      // Get Biller
      .exec(http("Get Biller")
      .get("/api/v1/biller/${biller_id}")
      .check(status is 200))


      // Create Bill
      .exec(http("Create Bill")
      .post("/api/v1/bill/")
      .body(StringBody("""{
        "user_id": "${user_id}",
        "biller_id": "${biller_id}",
        "bill_amount": "${bill_amount}",
        "due_date": "${due_date}",
        "bill_paid": "false"
        }"""
        ))
      .check(status is 200)
      .check(jsonPath("$.bill_id").saveAs("bill_id")))


      // Get Bill
      .exec(http("Get Bill")
      .get("/api/v1/bill/${bill_id}")
      .check(status is 200))


      // Get Bills for User
      .exec(http("Get Bills for User")
      .get("/api/v1/bill/users/${user_id}")
      .check(status is 200))


      // Pay Bill
      .exec(http("Pay Bill")
      .put("/api/v1/bill/pay/${bill_id}")
      .body(StringBody("""{
        "cc_number": "${cc_number}",
        "cc_exp_date": "${cc_exp_date}",
        "bill_paid": "${bill_paid}"
        }"""
        ))
      .check(status is 200))


      // Delete Bill
      .exec(http("Delete Bill")
      .delete("/api/v1/bill/${bill_id}")
      .check(status is 200))


      // Delete Biller
      .exec(http("Delete Biller")
      .delete("/api/v1/biller/${biller_id}")
      .check(status is 200))


      // Update User
      .exec(http("Update User")
      .put("/api/v1/user/${user_id}")
      .body(StringBody("""{ 
        "fname": "${fname}", 
        "lname": "${lname}",
        "uname": "${uname}",
        "secquestion": "What is your favourite sport?",
        "secanswer": "football"
        }"""))
      .check(status is 200))

      
      // Logoff User
      .exec(http("Logoff User")
      .put("/api/v1/user/logoff")
      .body(StringBody("""{
        "jwt": "${jwt}"
        }"""
        ))
      .check(status is 200))


      // Delete User
      .exec(http("Delete User")
      .delete("/api/v1/user/${user_id}")
      .check(status is 200))
      
    }
  }

    setUp(scn.inject(atOnceUsers(50))).protocols(httpProtocol).maxDuration(30 minutes)

}