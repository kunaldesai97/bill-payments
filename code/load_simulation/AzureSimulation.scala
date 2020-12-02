
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
    
    val path = "/Users/kunal/cmpt756-2/term-project-t4-wednesday/code/Gatling_scripts/Azure"

    val user_details = csv(path.concat("/user.csv")).random
    val biller_details = csv(path.concat("/biller.csv")).random
    val bill_details = csv(path.concat("/bill.csv")).random
    val pay_details = csv(path.concat("/pay.csv")).random

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
    .delete("/api/v1/biller/$(biller_id)")

    val deleteBill = http("Delete Bill")
    .delete("/api/v1/bill/$(bill_id)")

    val getBill = http("Get Bill")
    .get("/api/v1/bill/$(bill_id)")

	val getUser = http("Get User")
	.get("/api/v1/user/$(user_id)")

	val getBiller = http("Get Biller")
	.get("/api/v1/biller/$(biller_id)")

    val payBill = http("Pay Bill")
    .put("/api/v1/bill/pay/$(bill_id)")
    .formParam("cc_number", "$(cc_number)")    
    .formParam("cc_exp_date", "$(cc_exp_date)")
    .formParam("bill_paid", "$(bill_paid)")


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

	object Get{

		val guser = feed(user_details)
		.exec(getUser)

		val gbill = feed(bill_details)
		.exec(getBill)

		val gbiller = feed(biller_details)
		.exec(getBiller)

	}

    
    val pay = feed(bill_details)
    .exec(getBill)
    .feed(pay_details)
    .exec(payBill)


    // Assign roles
    val admins = scenario("Admins")
    .forever(){
		repeat(20){
			exec(Create.cuser, Create.cbiller, Get.guser, Get.gbiller)
		}
    }
    val billers = scenario("Billers")
    .forever(){
		repeat(20){
			 exec(Create.cbill, Get.gbill)
		}
       
    }
    val users = scenario("Users")
    .forever(){
		repeat(20){
			exec(pay)
		}
    }

    setUp(admins.inject(atOnceUsers(20)),
        billers.inject(atOnceUsers(20)),
        users.inject(atOnceUsers(10))).protocols(httpProtocol).maxDuration(30 minutes)


   
		

	
}