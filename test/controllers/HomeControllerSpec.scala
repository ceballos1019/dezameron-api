package controllers

import models.Bed
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with BeforeAndAfter {

  var tokenAuthorization:String = ""

  before{
    tokenAuthorization = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjRmMDYzNDJiNDdjYTQ1Zjg0NjM2MTk0NjE5MjNiMDdjYzQ4OTA1N2UifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vZ29ob3RlbHMtNWE1ODkiLCJuYW1lIjoiQW5kcsOpcyBDZWJhbGxvcyBTw6FuY2hleiIsInBpY3R1cmUiOiJodHRwczovL2xoNi5nb29nbGV1c2VyY29udGVudC5jb20vLU1wOWxzMXhBVDR3L0FBQUFBQUFBQUFJL0FBQUFBQUFBQUtnL3dGNkRNTGRUYWlzL3Bob3RvLmpwZyIsImF1ZCI6ImdvaG90ZWxzLTVhNTg5IiwiYXV0aF90aW1lIjoxNTExNTY4NDU5LCJ1c2VyX2lkIjoic1NiV0dhenRzUE96R1R4anNRUXhaRTlsd3kzMiIsInN1YiI6InNTYldHYXp0c1BPekdUeGpzUVF4WkU5bHd5MzIiLCJpYXQiOjE1MTE1NzgwNzcsImV4cCI6MTUxMTU4MTY3NywiZW1haWwiOiJjZWJhbGxvcy5kaW1AZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImZpcmViYXNlIjp7ImlkZW50aXRpZXMiOnsiZ29vZ2xlLmNvbSI6WyIxMDc0MDI0NTQ2NDA3MjIyODAxOTciXSwiZW1haWwiOlsiY2ViYWxsb3MuZGltQGdtYWlsLmNvbSJdfSwic2lnbl9pbl9wcm92aWRlciI6Imdvb2dsZS5jb20ifX0.V7E6vZZ-6qxkATd96P-j0J1qoBUUwAxFVE1J1n4SjJP42cHkH32hcuxB7kiDKbq-QYAdWUdqPHglMSW20_YrshRnQ8VnKAYKeKDvpa6GMJdMzM_m-rcVYDBbC-gtcezd8TqMJYCDscJET1tx8eu2j89qE3GcEZsZKEUVhcfX3N6xWqiX3R_cBr3d0cl500kIF18Ok2d6VQZ4jqSRM_Y6F4Y6afhxgV5FaF4SvBdqzF7jjy_diWwiN6gzJQEA0fNj5iicCJwc69yoA7Dwvy-9oSuIkq9FuM2UejVLBAZYCnfCfCdL1_9Jd92TGO3Tg5o_OLk3tFIUBlAfAzaq8Rjzmw"
  }

  "HomeController GET" should {

    "render the index page from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents())
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Welcome to Play")
    }

    "render the index page from the application" in {
      val controller = inject[HomeController]
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Welcome to Play")
    }

    "render the index page from the router" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Welcome to Play")
    }
  }
  "Search" should {
    "return a 200 status" in {

      val controller = inject[HomeController]
      val search = controller.search("2017-09-08", "2017-09-09", "11001", 4, "L")
        .apply(FakeRequest(GET, "/v1/rooms?arrive_date=2017-09-08&leave_date=2017-09-09&city=11001&hosts=4&room_type=L"))


      status(search) mustBe OK
    }

    "return a json" in {

      val controller = inject[HomeController]
      val search = controller.search("2017-09-08", "2017-09-09", "11001", 4, "L")
        .apply(FakeRequest(GET, "/v1/rooms?arrive_date=2017-09-08&leave_date=2017-09-09&city=11001&hosts=4&room_type=L"))

      contentType(search) mustBe Some("application/json")
    }

    "return the Medellin hotel id" in {
      val controller = inject[HomeController]
      val medellinHotelId = "1";
      val search = controller.search("2017-09-08", "2017-09-09", "05001", 4, "L")
        .apply(FakeRequest(GET, "/v1/rooms?arrive_date=2017-09-08&leave_date=2017-09-09&city=11001&hosts=4&room_type=L"))

      (contentAsJson(search) \ "hotel_id").as[String] mustBe medellinHotelId
    }

    "return the Bogota hotel id" in {
      val controller = inject[HomeController]
      val bogotaHotelId = "2"
      val search = controller.search("2017-09-08", "2017-09-09", "11001", 4, "L")
        .apply(FakeRequest(GET, "/v1/rooms?arrive_date=2017-09-08&leave_date=2017-09-09&city=11001&hosts=4&room_type=L"))

      (contentAsJson(search) \ "hotel_id").as[String] mustBe bogotaHotelId
    }

    "validate the dates" in {
      val controller = inject[HomeController]
      val dateValidationMessage = "Leave date must be greater than arrive date"
      val search = controller.search("2017-09-08", "2017-09-07", "11001", 4, "L")
        .apply(FakeRequest(GET, "v1/rooms?arrive_date=2017-09-08&leave_date=2017-09-07&city=11001&hosts=4&room_type=L"))

      status(search) mustBe BAD_REQUEST
      contentType(search) mustBe Some("application/json")
      (contentAsJson(search) \ "message").as[String] mustBe dateValidationMessage
    }

    "validate the city code" in {
      val controller = inject[HomeController]
      val cityValidationMessage = "Invalid city code"
      val search = controller.search("2017-09-08", "2017-09-09", "23123", 4, "L")
        .apply(FakeRequest(GET, "v1/rooms?arrive_date=2017-09-08&leave_date=2017-09-09&city=23123&hosts=4&room_type=L"))

      status(search) mustBe BAD_REQUEST
      contentType(search) mustBe Some("application/json")
      (contentAsJson(search) \ "message").as[String] mustBe cityValidationMessage
    }

    "validate that hosts are greater than 0" in {
      val controller = inject[HomeController]
      val hostValidationMessage = "Hosts must be between 1 and 5"
      val search = controller.search("2017-09-08", "2017-09-09", "05001", 0, "L")
        .apply(FakeRequest(GET, "v1/rooms?arrive_date=2017-09-08&leave_date=2017-09-09&city=05001&hosts=0&room_type=L"))

      status(search) mustBe BAD_REQUEST
      contentType(search) mustBe Some("application/json")
      (contentAsJson(search) \ "message").as[String] mustBe hostValidationMessage
    }

    "validate that hosts are less than 6" in {
      val controller = inject[HomeController]
      val hostValidationMessage = "Hosts must be between 1 and 5"
      val search = controller.search("2017-09-08", "2017-09-09", "05001", 7, "L")
        .apply(FakeRequest(GET, "v1/rooms?arrive_date=2017-09-08&leave_date=2017-09-09&city=05001&hosts=7&room_type=L"))

      status(search) mustBe BAD_REQUEST
      contentType(search) mustBe Some("application/json")
      (contentAsJson(search) \ "message").as[String] mustBe hostValidationMessage
    }

    "validate the room type" in {
      val controller = inject[HomeController]
      val roomValidationMessage = "Invalid room type"
      val search = controller.search("2017-09-08", "2017-09-09", "05001", 4, "K")
        .apply(FakeRequest(GET, "v1/rooms?arrive_date=2017-09-08&leave_date=2017-09-09&city=05001&hosts=4&room_type=K"))

      status(search) mustBe BAD_REQUEST
      contentType(search) mustBe Some("application/json")
      (contentAsJson(search) \ "message").as[String] mustBe roomValidationMessage
    }
  }

  "Reserve" should {

    "reserve return a 200 status" in {

      val controller = inject[HomeController]
      val reserve = controller.reserve().apply(
        FakeRequest(POST, "/v1/rooms/reserve").withJsonBody(
          Json.parse("""{
                        "arrive_date": "2017-11-08",
                        "leave_date": "2017-11-09",
                        "room_type": "S",
                        "capacity": 5,
                        "beds": {
                           "simple": 3,
                           "double": 1
                         },
                        "hotel_id": "1",
                         "user": {
                           "doc_type": "CC",
                           "doc_id": "2312312",
                           "email": "test@test.te",
                           "phone_number": "4064543"
                         }
                       }""")).withHeaders(AUTHORIZATION -> tokenAuthorization))

      //status(reserve) mustBe OK
      contentType(reserve) mustBe Some("application/json")


    }

    "reserve return a json" in {

      val controller = inject[HomeController]
      val reserve = controller.reserve().apply(
        FakeRequest(POST, "/v1/rooms/reserve"))

      contentType(reserve) mustBe Some("application/json")

    }

    "validate the dates" in {
      val controller = inject[HomeController]
      val dateValidationMessage = "Leave date must be greater than arrive date"
      val reserve = controller.reserve().apply(
        FakeRequest(POST, "/v1/rooms/reserve").withJsonBody(
          Json.parse("""{
                        "arrive_date": "2017-09-08",
                        "leave_date": "2017-09-07",
                        "room_type": "S",
                        "capacity": 2,
                        "beds": {
                           "simple": 2,
                           "double": 0
                         },
                        "hotel_id": "1",
                         "user": {
                           "doc_type": "CC",
                           "doc_id": "2312312",
                           "email": "test@test.te",
                           "phone_number": "4064543"
                         }
                       }""")).withHeaders(AUTHORIZATION -> tokenAuthorization))

      status(reserve) mustBe BAD_REQUEST
      contentType(reserve) mustBe Some("application/json")
      //status(reserve) mustBe OK
      (contentAsJson(reserve) \ "message").as[String] mustBe dateValidationMessage
    }

    "validate hosts" in {
      val controller = inject[HomeController]
      val dateValidationMessage = "Hosts must be between 1 and 5"
      val reserve = controller.reserve().apply(
        FakeRequest(POST, "/v1/rooms/reserve").withJsonBody(
          Json.parse("""{
                        "arrive_date": "2017-09-08",
                        "leave_date": "2017-09-09",
                        "room_type": "S",
                        "capacity": 6,
                        "beds": {
                           "simple": 2,
                           "double": 0
                         },
                        "hotel_id": "1",
                         "user": {
                           "doc_type": "CC",
                           "doc_id": "2312312",
                           "email": "test@test.te",
                           "phone_number": "4064543"
                         }
                       }""")).withHeaders(AUTHORIZATION -> tokenAuthorization))

      status(reserve) mustBe BAD_REQUEST
      contentType(reserve) mustBe Some("application/json")
      //status(reserve) mustBe OK
      (contentAsJson(reserve) \ "message").as[String] mustBe dateValidationMessage
    }

    "validate room type" in {
      val controller = inject[HomeController]
      val validationMessage = "Invalid room type"
      val reserve = controller.reserve().apply(
        FakeRequest(POST, "/v1/rooms/reserve").withJsonBody(
          Json.parse("""{
                        "arrive_date": "2017-09-08",
                        "leave_date": "2017-09-09",
                        "room_type": "H",
                        "capacity": 2,
                        "beds": {
                           "simple": 2,
                           "double": 0
                         },
                        "hotel_id": "1",
                         "user": {
                           "doc_type": "CC",
                           "doc_id": "2312312",
                           "email": "test@test.te",
                           "phone_number": "4064543"
                         }
                       }""")).withHeaders(AUTHORIZATION -> tokenAuthorization))

      status(reserve) mustBe BAD_REQUEST
      contentType(reserve) mustBe Some("application/json")
      //status(reserve) mustBe OK
      (contentAsJson(reserve) \ "message").as[String] mustBe validationMessage
    }


    "validate arrive date" in {
      val controller = inject[HomeController]
      val validationMessage = "Invalid arrive date"
      val reserve = controller.reserve().apply(
        FakeRequest(POST, "/v1/rooms/reserve").withJsonBody(
          Json.parse("""{
                        "arrive_date": "201-09-08",
                        "leave_date": "2017-09-10",
                        "room_type": "S",
                        "capacity": 2,
                        "beds": {
                           "simple": 2,
                           "double": 0
                         },
                        "hotel_id": "1",
                         "user": {
                           "doc_type": "CC",
                           "doc_id": "2312312",
                           "email": "test@test.te",
                           "phone_number": "4064543"
                         }
                       }""")).withHeaders(AUTHORIZATION -> tokenAuthorization))

      status(reserve) mustBe BAD_REQUEST
      contentType(reserve) mustBe Some("application/json")
      //status(reserve) mustBe OK
      (contentAsJson(reserve) \ "message").as[String] mustBe validationMessage
    }

    "validate leave date" in {
      val controller = inject[HomeController]
      val validationMessage = "Invalid leave date"
      val reserve = controller.reserve().apply(
        FakeRequest(POST, "/v1/rooms/reserve").withJsonBody(
          Json.parse("""{
                        "arrive_date": "2017-09-08",
                        "leave_date": "201709-07",
                        "room_type": "S",
                        "capacity": 2,
                        "beds": {
                           "simple": 2,
                           "double": 0
                         },
                        "hotel_id": "1",
                         "user": {
                           "doc_type": "CC",
                           "doc_id": "2312312",
                           "email": "test@test.te",
                           "phone_number": "4064543"
                         }
                       }""")).withHeaders(AUTHORIZATION -> tokenAuthorization))

      status(reserve) mustBe BAD_REQUEST
      contentType(reserve) mustBe Some("application/json")
      //status(reserve) mustBe OK
      (contentAsJson(reserve) \ "message").as[String] mustBe validationMessage
    }

    "validate room capacity" in {
      val controller = inject[HomeController]
      val validationMessage = "Beds number does not match with room capacity"
      val reserve = controller.reserve().apply(
        FakeRequest(POST, "/v1/rooms/reserve").withJsonBody(
          Json.parse("""{
                        "arrive_date": "2017-09-08",
                        "leave_date": "2017-09-17",
                        "room_type": "S",
                        "capacity": 2,
                        "beds": {
                           "simple": 2,
                           "double": 1
                         },
                        "hotel_id": "1",
                         "user": {
                           "doc_type": "CC",
                           "doc_id": "2312312",
                           "email": "test@test.te",
                           "phone_number": "4064543"
                         }
                       }""")).withHeaders(AUTHORIZATION -> tokenAuthorization))

      status(reserve) mustBe BAD_REQUEST
      contentType(reserve) mustBe Some("application/json")
      (contentAsJson(reserve) \ "message").as[String] mustBe validationMessage
    }

    "validate room availability " in {
      val controller = inject[HomeController]
      val validationMessage = "The room is not available"
      val reserve = controller.reserve().apply(
        FakeRequest(POST, "/v1/rooms/reserve").withJsonBody(
          Json.parse("""{
                        "arrive_date": "2017-10-10",
                        "leave_date": "2017-10-11",
                        "room_type": "L",
                        "capacity": 4,
                        "beds": {
                           "simple": 0,
                           "double": 2
                         },
                        "hotel_id": "2",
                         "user": {
                           "doc_type": "CC",
                           "doc_id": "2312312",
                           "email": "test@test.te",
                           "phone_number": "4064543"
                         }
                       }""")).withHeaders(AUTHORIZATION -> tokenAuthorization))

      status(reserve) mustBe BAD_REQUEST
      contentType(reserve) mustBe Some("application/json")
      (contentAsJson(reserve) \ "message").as[String] mustBe validationMessage
    }


    "validate missing parameter" in {
      val controller = inject[HomeController]
      val validationMessage = "Missing a parameter"
      val reserve = controller.reserve().apply(
        FakeRequest(POST, "/v1/rooms/reserve").withJsonBody(
          Json.parse("""{
                        "arrive_date": "2017-09-05",
                        "leave_date": "2017-09-08",
                        "room_type": "L",
                        "beds": {
                           "simple": 0,
                           "double": 2
                         },
                        "hotel_id": "2",
                         "user": {
                           "doc_type": "CC",
                           "doc_id": "2312312",
                           "email": "test@test.te",
                           "phone_number": "4064543"
                         }
                       }""")).withHeaders(AUTHORIZATION -> tokenAuthorization))

      status(reserve) mustBe BAD_REQUEST
      contentType(reserve) mustBe Some("application/json")
      (contentAsJson(reserve) \ "description").as[String] mustBe validationMessage
    }
  }

  "My Reservations" should {

    "return message when there is no token in the header" in {
      val controller = inject[HomeController]
      val noHeaderMessage = "Authorization header is empty"
      val myReservations = controller.getReservations().apply(FakeRequest(GET, "v1/reservations"))
      (contentAsJson(myReservations) \ "message").as[String] mustBe noHeaderMessage
    }

    "validate the token" in {
      val controller = inject[HomeController]
      val validationTokenMessage = "Token has expired or is not valid"
      val invalidToken = "3sT3_e5_uN_t0kEn_iNv4l1d0"
      val myReservations = controller.getReservations().apply(FakeRequest(GET, "v1/reservations").
        withHeaders(AUTHORIZATION -> invalidToken))
      (contentAsJson(myReservations) \ "message").as[String] mustBe validationTokenMessage
    }

    "return status 200" in {
      val controller = inject[HomeController]
      val myReservations = controller.getReservations().apply(FakeRequest(GET, "v1/reservations").
        withHeaders(AUTHORIZATION -> tokenAuthorization))

      status(myReservations) mustBe OK
    }

    "return json response" in {
      val controller = inject[HomeController]
      val myReservations = controller.getReservations().apply(FakeRequest(GET, "v1/resrvations").
        withHeaders(AUTHORIZATION -> tokenAuthorization))

      contentType(myReservations) mustBe Some("application/json")

    }
  }


  "checkRoom" should{
    "return a true boolean" in{
      val beds= new Bed(0,2)
      val controller = inject[HomeController]
      val room = controller.checkRoom("2","L",beds)
      room mustBe true
    }
    "return a false boolean" in {
      val beds= new Bed (0,1)
      val controller = inject[HomeController]
      val room = controller.checkRoom("1","S",beds)
      room mustBe false
    }
  }
"generateCode" should{
    "return the first part code" in{
      val beds= new Bed(1,0)
      val controller = inject[HomeController]
      val code = controller.generateCode("1","L",beds,"2017-11-16")
      val codemin:String = code.getOrElse("ERROR").substring(0,4)
      codemin mustBe ("RDZM")
    }
    "return a static part code" in {
      val beds= new Bed(1,0)
      val controller = inject[HomeController]
      val code = controller.generateCode("1","L",beds,"2017-11-16")
      val codemin:String = code.getOrElse("ERROR").substring(0,19)
      codemin mustBe ("RDZM1LS1D020171116K")
    }
   /* "there is no reservation code" in{
      val beds= new Bed(0,1)
      val controller = inject[HomeController]
      val code = controller.generateCode("1","S",beds,"2014-11-16")
      val codemin:String = code.getOrElse("ERROR")
      codemin mustBe ("ERROR")
    }*/
  }

}
