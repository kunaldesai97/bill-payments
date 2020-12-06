package termproject

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class MinikubeSimulation extends Simulation {

	val httpProtocol = http
		.baseUrl("http://10.107.240.234")
		.inferHtmlResources()
		.acceptHeader("*/*")
		.acceptEncodingHeader("gzip, deflate")
		.authorizationHeader("Bearer {{Bearer Token}}")
		.userAgentHeader("PostmanRuntime/7.26.8")

	val headers_0 = Map(
		"Cache-Control" -> "no-cache",
		"Content-Type" -> "application/json",
		"Postman-Token" -> "842fb9ca-ca9c-47d9-9dd4-dc2e1a5bd2b2")

	val headers_1 = Map(
		"Cache-Control" -> "no-cache",
		"Postman-Token" -> "6585b350-e2d4-4f79-b5c6-1699b91a0018")

	val headers_2 = Map(
		"Cache-Control" -> "no-cache",
		"Postman-Token" -> "f5d9c995-ddcd-4425-9b8d-7159298ac74e")

	val headers_3 = Map(
		"Cache-Control" -> "no-cache",
		"Postman-Token" -> "1543bbfd-d9ba-4483-a38d-462a2231ef22")

	val headers_4 = Map(
		"Cache-Control" -> "no-cache",
		"Content-Type" -> "application/json",
		"Postman-Token" -> "733eb7b8-e392-4c17-b032-cf4a0822d219")

	val headers_5 = Map(
		"Cache-Control" -> "no-cache",
		"Content-Type" -> "application/json",
		"Postman-Token" -> "b15a72b8-ea96-4dce-9d74-45500345b0ed")

	val headers_6 = Map(
		"Cache-Control" -> "no-cache",
		"Postman-Token" -> "68cd671c-b350-4269-86c6-d4fcbf3803f0")

	val headers_7 = Map(
		"Cache-Control" -> "no-cache",
		"Postman-Token" -> "0748e1d2-aa1a-4d52-8472-10aff090dbc2")

	val headers_8 = Map(
		"Cache-Control" -> "no-cache",
		"Content-Type" -> "application/json",
		"Postman-Token" -> "83efc10e-fdca-4ca1-a22b-d31000b0b02f")

	val headers_9 = Map(
		"Cache-Control" -> "no-cache",
		"Postman-Token" -> "6d5102d7-0d1b-416e-91dc-7e5668f943e1")

	val headers_10 = Map(
		"Cache-Control" -> "no-cache",
		"Postman-Token" -> "10c0b4c0-58d1-476c-acba-53c661c77ff7")

	val headers_11 = Map(
		"Cache-Control" -> "no-cache",
		"Content-Type" -> "application/json",
		"Postman-Token" -> "62794a19-ab7e-4c1e-8957-2c60512939f4")

	val headers_12 = Map(
		"Cache-Control" -> "no-cache",
		"Content-Type" -> "application/json",
		"Postman-Token" -> "e8c5ad8a-323d-41d7-84b7-ef12b10dc0e1")

	val headers_13 = Map(
		"Cache-Control" -> "no-cache",
		"Content-Type" -> "application/json",
		"Postman-Token" -> "718c6466-9832-458f-bcaa-7bd91a5a5504")



	val scn = scenario("MinikubeSimulation")
		// CREATE BILL
		.exec(http("Create Bill")
			.post("/api/v1/bill/")
			.headers(headers_0)
			.body(RawFileBody("termproject/minikubesimulation/0000_request.json")))
		.pause(3)
		// GET BILL
		.exec(http("Get Bill")
			.get("/api/v1/bill/2e68a8cf-7bb8-42d4-b3dd-d6060a8501d9")
			.headers(headers_1))
		.pause(3)
		// DELETE BILL
		.exec(http("Delete Bill")
			.delete("/api/v1/bill/5fb4f582-decc-41a3-9726-cf0b24c2c141")
			.headers(headers_2))
		.pause(3)
		// GET USER'S BILL
		.exec(http("Get User Bill")
			.get("/api/v1/bill/users/rharode@gmail.com")
			.headers(headers_3))
		.pause(3)
		// PAY BILL
		.exec(http("Pay Bill")
			.put("/api/v1/bill/pay/3da6d705-aafb-4d4e-939d-10f640714d4b")
			.headers(headers_4)
			.body(RawFileBody("termproject/minikubesimulation/0004_request.json")))
		.pause(3)
		// CREATE BILLER
		.exec(http("Create Biller")
			.post("/api/v1/biller/")
			.headers(headers_5)
			.body(RawFileBody("termproject/minikubesimulation/0005_request.json")))
		.pause(3)
		// DELETE BILLER
		.exec(http("Delete Biller")
			.delete("/api/v1/biller/ccb7a2cb-af4a-495b-9242-6ab4ea0b6b68")
			.headers(headers_6))
		.pause(3)
		// GET BILLER
		.exec(http("Get Biller")
			.get("/api/v1/biller/29138d9c-07bf-42b3-adb5-a3401a8c18e6")
			.headers(headers_7))
		.pause(3)
		// CREATE USER
		.exec(http("Create User")
			.post("/api/v1/user/")
			.headers(headers_8)
			.body(RawFileBody("termproject/minikubesimulation/0008_request.json")))
		.pause(3)
		// GET USER
		.exec(http("Get User")
			.get("/api/v1/user/63beb370-5438-43e2-89d1-7b9c4b748f4d")
			.headers(headers_9))
		.pause(3)
		// DELETE USER
		.exec(http("Delete User")
			.delete("/api/v1/user/9a6ac1b2-9676-402b-a28d-5e52425b70a4")
			.headers(headers_10))
		.pause(3)
		// UPDATE USER
		.exec(http("Update User")
			.put("/api/v1/user/276d3867-fdf8-4e50-b449-4b5d5ad50867")
			.headers(headers_11)
			.body(RawFileBody("termproject/minikubesimulation/0011_request.json")))
		.pause(3)
		// USER LOGIN
		.exec(http("User Login")
			.put("/api/v1/user/login")
			.headers(headers_12)
			.body(RawFileBody("termproject/minikubesimulation/0012_request.json")))
		.pause(3)
		// USER LOGOUT
		.exec(http("User Logout")
			.put("/api/v1/user/logoff")
			.headers(headers_13)
			.body(RawFileBody("termproject/minikubesimulation/0013_request.json")))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}