
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class AzureSimulation extends Simulation {

	val httpProtocol = http
		.baseUrl("http://20.151.27.234")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
		.acceptHeader("*/*")
		.acceptEncodingHeader("gzip, deflate")
		.authorizationHeader("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNzQ5MTVhMTAtNGMzYi00ZDg4LThiZDAtZDk5OWYxNDBlZDJhIiwidGltZSI6MTYwNDA0Mzk2MS44ODU5NjZ9.cCsA5iMZG6TQ_DeCP5mAdT_I5b2mSz5mhCh5gp1r1jU")
		.userAgentHeader("PostmanRuntime/7.26.8")

    val user_details = csv("user.csv").random
    val biller_details = csv("biller.csv").random
    val bill_details = csv("bill.csv").random

    val createUser = http("Create New User")
    .post("/api/v1/user/")
    .formParam("user_id", "$(user_id)")
    .formParam("fname", "$(fname)")
    .formParam("lname", "$(lname)")
    .formParam("secanswer", "$(secanswer)")
    .formParam("secquestion", "$(secquestion)")
    .formParam("uname", "$(uname)")

    val createBiller = http("Create New Biller")
    .post("/api/v1/biller/")
    .formParam("biller_id", "$(biller_id)")
    .formParam("biller", "$(biller)")
    .formParam("description", "$(description)")

    val createBill = http("Create New Bill")
    .post("/api/v1/bill/")
    .formParam("bill_id", "$(bill_id)")
    .formParam("bill_amount", "$(bill_amount)")
    .formParam("bill_paid", "$(bill_paid)")
    .formParam("bill_id", "$(bill_id)")
    .formParam("due_date", "$(due_data)")
    .formParam("user_id", "$(user_id)")


    val deleteUser = http("Delete User")
    .delete("/api/v1/user/$(user_id)")

    val deleteBiller = http("Delete Biller")
    .delete("/api/v1/user/$(biller_id)")

    val deleteBill = http("Delete Bill")
    .delete("/api/v1/user/$(bill_id)")


    object Create{

        val cuser = feed(user_details)
        // Delete User if already exists
        .exec(deleteUser)
        .exec(createUser)

        val cbiller = feed(biller_details)
        // Delete Biller if already exists
        .exec(deleteBiller)
        .exec(createBiller)

        val cbill = feed(bill_details)
        // Delete Biller if already exists
        .exec(deleteBill)
        .exec(createBill)


    }

    // Assign roles
    val admins = scenario("Admins").exec(Create.cuser, Create.cbiller)
    val billers = scenario("Billers").exec(Create.cbill)




    // Create User
    // val scn_createUser = scenario("Create User Scenario")
    // // .feed(user_details)
    // // Delete User if already exists
    // .exec(deleteUser)
    // .exec(createUser)

    // // Create Biller
    // val scn_createBiller = scenario("Create Biller Scenario")
    // .feed(biller_details)
    // // Delete Biller if already exists
    // .exec(deleteBiller)
    // .exec(createBiller)

    // // Create Bill
    // val scn_createBill = scenario("Create Bill Scenario")
    // .feed(bill_details)
    // // Delete Biller if already exists
    // .exec(deleteBill)
    // .exec(createBill)




    


	// val scn = scenario("recordedsimulation")
	// 	// Create User
	// 	.exec(http("Create User")
	// 		.post("/api/v1/user/")
	// 		.headers(headers_0)
	// 		.body(RawFileBody("recordedsimulation/0000_request.json")))
	// 	.pause(23)
	// 	// Get User
	// 	.exec(http("Get User")
	// 		.get("/api/v1/user/6a6ac575-f492-422c-b4b8-9058035e8ff0")
	// 		.headers(headers_1)
	// 		.body(RawFileBody("recordedsimulation/0001_request.json")))
	// 	.pause(110)
	// 	// Login User
	// 	.exec(http("Login User")
	// 		.put("/api/v1/user/login")
	// 		.headers(headers_2)
	// 		.body(RawFileBody("recordedsimulation/0002_request.json")))
	// 	.pause(87)
	// 	// Create Biller
	// 	.exec(http("Create Biller")
	// 		.post("/api/v1/biller/")
	// 		.headers(headers_3)
	// 		.body(RawFileBody("recordedsimulation/0003_request.json")))
	// 	.pause(57)
	// 	// Get Biller
	// 	.exec(http("Get Biller")
	// 		.get("/api/v1/biller/f420faf8-2d64-4651-b6d1-7c35ca44823f")
	// 		.headers(headers_4))
	// 	.pause(187)
	// 	// Create Bill
	// 	.exec(http("Create Bill")
	// 		.post("/api/v1/bill/")
	// 		.headers(headers_5)
	// 		.body(RawFileBody("recordedsimulation/0005_request.json")))
	// 	.pause(89)
	// 	// Get Bill
	// 	.exec(http("Get Bill")
	// 		.get("/api/v1/bill/b964a7d3-7048-4de8-9886-360504be6517")
	// 		.headers(headers_6))
	// 	.pause(116)
	// 	// Get User Bills
	// 	.exec(http("Get User Bills")
	// 		.get("/api/v1/bill/users/6a6ac575-f492-422c-b4b8-9058035e8ff0")
	// 		.headers(headers_7))
	// 	.pause(82)
	// 	// .exec(http("request_8")
	// 	// 	.put("/api/v1/bill/pay/b964a7d3-7048-4de8-9886-360504be6517")
	// 	// 	.headers(headers_8))
	// 	// .pause(71)
	// 	// Pay Bill
	// 	// .exec(http("request_9")
	// 	// 	.put("/api/v1/bill/pay/b964a7d3-7048-4de8-9886-360504be6517")
	// 	// 	.headers(headers_9)
	// 	// 	.body(RawFileBody("/recordedsimulationazure/0009_request.json")))
	// 	// .pause(25)
	// 	// Pay Bill
	// 	.exec(http("Pay Bill")
	// 		.put("/api/v1/bill/pay/b964a7d3-7048-4de8-9886-360504be6517")
	// 		.headers(headers_10)
	// 		.body(RawFileBody("recordedsimulation/0010_request.json")))
	// 	.pause(58)
	// 	//Delete Bill
	// 	.exec(http("Delete Bill")
	// 		.delete("/api/v1/bill/b964a7d3-7048-4de8-9886-360504be6517")
	// 		.headers(headers_11))
	// 	.pause(212)
	// 	// Update User
	// 	.exec(http("Update User")
	// 		.put("/api/v1/user/6a6ac575-f492-422c-b4b8-9058035e8ff0")
	// 		.headers(headers_12)
	// 		.body(RawFileBody("recordedsimulation/0012_request.json")))
	// 	.pause(39)
	// 	//Logoff User
	// 	.exec(http("Logoff User")
	// 		.put("/api/v1/user/logoff")
	// 		.headers(headers_16)
	// 		.body(RawFileBody("recordedsimulation/0016_request.json")))
	// 	// Delete User
	// 	.exec(http("Delete User")
	// 		.delete("/api/v1/user/6a6ac575-f492-422c-b4b8-9058035e8ff0")
	// 		.headers(headers_13))
	// 	.pause(70)
	// 	// Delete Biller
	// 	.exec(http("Delete Biller")
	// 		.delete("/api/v1/biller/f420faf8-2d64-4651-b6d1-7c35ca44823f")
	// 		.headers(headers_14))
	// 	.pause(101)
	// 	// Logoff User
	// 	// .exec(http("request_15")
	// 	// 	.put("/api/v1/user/logoff")
	// 	// 	.headers(headers_15))
	// 	// .pause(37)
		

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}