
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

    
	val headers_0 = Map("Postman-Token" -> "c6804e01-935b-4367-8b10-60b6f888cfe8")

	val headers_1 = Map("Postman-Token" -> "6c30a572-7c14-4eaa-bf0b-d6b5fcdb09fc")



	val scn =  scenario("RecordedSimulation").repeat(20){
    forever(){
		  exec(http("Create User")
			.post("/api/v1/user/")
			// .body(ElFileBody("recordedsimulation/0000_request.json"))
      .body(StringBody(s"""{ 
        "fname": "Ted", 
        "lname": "Mosby",
        "uname": "tmosby",
        "secquestion": "pet animal",
        "secanswer": "duck"
        }"""))
      .check(status is 200)
      .check(jsonPath("$.user_id").saveAs("user_id")))

      .exec(http("Get User")
      .get("/api/v1/user/${user_id}")
      .check(status is 200))

      .exec(http("Log in User")
      .put("/api/v1/user/login")
      .body(StringBody(s"""{
        "uid": session => session("user_id").as[String]
        }"""
        ))
      .check(bodyString.saveAs("jwt"))
      .check(status is 200))

      .exec(http("Create Biller")
			.post("/api/v1/biller/")
			.body(StringBody(s"""{ 
        "biller": "BC Hydro", 
        "description": "Electricity"
        }"""))
      .check(status is 200)
      .check(jsonPath("$.biller_id").saveAs("biller_id")))

      .exec(http("Get Biller")
      .get("/api/v1/biller/${biller_id}")
      .check(status is 200))

      .exec(http("Create Bill")
      .post("/api/v1/bill/")
      .body(StringBody(s"""{
        "user_id": "0jc337ea-f637-4dbf-b1df-6cfd153b5b5m",
        "biller_id": "9hc337ea-f637-4dbf-b1df-6cfd153b5b4k",
        "bill_amount": "50",
        "due_date": "March 25 2021",
        "bill_paid": "false"
        }"""
        ))
      .check(status is 200)
      .check(jsonPath("$.bill_id").saveAs("bill_id")))

      .exec(http("Get Bill")
      .get("/api/v1/bill/${bill_id}")
      .check(status is 200))

      .exec(http("Get Bills for User")
      .get("/api/v1/bill/users/${user_id}")
      .check(status is 200))

      .exec(http("Pay Bill")
      .put("/api/v1/bill/pay/${bill_id}")
      .body(StringBody(s"""{
        "cc_number": "450556789349966",
        "cc_exp_date": "April 5 2024",
        "bill_paid": "true"
        }"""
        ))
      .check(status is 200))

      .exec(http("Delete Bill")
      .delete("/api/v1/bill/${bill_id}")
      .check(status is 200))

      .exec(http("Delete Biller")
      .delete("/api/v1/biller/${biller_id}")
      .check(status is 200))

      .exec(http("Update User")
      .put("/api/v1/user/${user_id}")
      .body(StringBody(s"""{ 
        "fname": "Ted", 
        "lname": "Mosby",
        "uname": "tmosby",
        "secquestion": "favourite car",
        "secanswer": "ford"
        }"""))
      .check(status is 200))

      .exec(http("Logoff User")
      .put("/api/v1/user/logoff")
      .body(StringBody(s"""{
        "jwt": session => session("jwt").as[String]
        }"""
        ))
      .check(status is 200))

      .exec(http("Delete User")
      .delete("/api/v1/user/${user_id}")
      .check(status is 200))
      }
  }

    setUp(scn.inject(atOnceUsers(50))).protocols(httpProtocol).maxDuration(30 minutes)

}