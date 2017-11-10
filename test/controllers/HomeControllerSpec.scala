package controllers

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
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

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
                       }""")))

      status(reserve) mustBe OK

      //contentAsString(search) must include ("Welcome to Play")
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
                       }""")))

      status(reserve) mustBe BAD_REQUEST
      contentType(reserve) mustBe Some("application/json")
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
                       }""")))

      status(reserve) mustBe BAD_REQUEST
      contentType(reserve) mustBe Some("application/json")
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
                       }""")))

      status(reserve) mustBe BAD_REQUEST
      contentType(reserve) mustBe Some("application/json")
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
                       }""")))

      status(reserve) mustBe BAD_REQUEST
      contentType(reserve) mustBe Some("application/json")
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
                       }""")))

      status(reserve) mustBe BAD_REQUEST
      contentType(reserve) mustBe Some("application/json")
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
                       }""")))

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
                        "arrive_date": "2017-09-05",
                        "leave_date": "2017-09-08",
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
                       }""")))

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
                       }""")))

      status(reserve) mustBe BAD_REQUEST
      contentType(reserve) mustBe Some("application/json")
      (contentAsJson(reserve) \ "description").as[String] mustBe validationMessage
    }
  }
}
