package controllers


import java.security.SecureRandom
import javax.inject._


import com.fasterxml.jackson.databind.node.ObjectNode

import com.google.firebase.{FirebaseApp, FirebaseOptions}
import com.google.firebase.auth.{FirebaseAuth, FirebaseToken}
import com.google.firebase.tasks.{OnSuccessListener, Task, Tasks}

import models.Helpers._
import models._
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Updates._
import play.api.libs.json.{JsError, JsResult, JsSuccess, JsValue, Json}
import play.api.mvc._
import utils.{Firebase, ValidationUtils}

import scala.concurrent.Future
import scala.util.Random
import scala.util.control.Breaks

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  /*TODO: Test when insert one document, these variables refresh automatically with that document*/
  lazy val hotels: MongoCollection[Hotel] = DBHandler.getCollection[Hotel]
  lazy val rooms: MongoCollection[Room] = DBHandler.getCollection[Room]
  lazy val reservations: MongoCollection[Reservation] = DBHandler.getCollection[Reservation]

  def test = Action {
    val database: MongoCollection[Reservation] = DBHandler.getCollection[Reservation]
    //k(Json.toJson(reservations.find().results()))
    Ok(Json.toJson(database.find().results()))
  }

  //http://localhost:9000/v1/rooms?arrive_date=2017-02-20&leave_date=2017-03-15&city=11001&hosts=2&room_type=L
  def search(arriveDate: String, leaveDate: String, city: String,
             hosts: Int, roomType: String) =
    Action {
      val messageValidation = ValidationUtils.validate(Some(city), Some(arriveDate), Some(leaveDate), Some(hosts),
        Some(roomType), None, None)
      if (!ValidationUtils.NoErrorMessage.equals(messageValidation)) {
        BadRequest(Json.toJson(
          Map("message" -> messageValidation)
        ))
      } else {
        val reservedRooms = checkDates(arriveDate, leaveDate, city, roomType)
        val hotel = hotels.find(equal("city", city)).projection(exclude("_id", "city")).headResult();

        var roomsRes = rooms.find(and(equal("city", city), equal("capacity", hosts),
          equal("room_type", roomType))).projection(exclude("room_id", "hotel_id", "city")).results()

        for (reserved <- reservedRooms) {
          roomsRes = roomsRes.filterNot(x => x.beds.simple == reserved.beds.simple
            && x.beds.double == reserved.beds.double)
        }

        val jsonResult = Hotel(hotel.hotel_id, hotel.hotel_name, hotel.city, hotel.hotel_location, hotel.hotel_thumbnail,
          hotel.check_in, hotel.check_out, hotel.hotel_website, Some(roomsRes), None)

        Ok(Json.toJson(jsonResult))
      }
    }


  def reserve() = Action { implicit request =>
    val token: Option[String] = request.headers.get("Authorization")
    val res = Firebase.verifyToken(token)
    if(!res.equals(Firebase.TokenErrorMessage) && !res.equals(Firebase.AuthorizationErrorMessage)) {
      /*Check if the request has body*/
      if (request.hasBody) {
        request.body.asJson match {
          case Some(bodyAsJson) =>
            bodyAsJson.validate[Reservation].fold(
              /*Succesful*/
              valid = response => {
                val messageValidation = ValidationUtils.validate(None, Some(response.arrive_date), Some(response.leave_date),
                  Some(response.capacity), Some(response.room_type), Some(response.beds.simple), Some(response.beds.double))
                if (!ValidationUtils.NoErrorMessage.equals(messageValidation)) {
                  BadRequest(Json.toJson(
                    Map("message" -> messageValidation)
                  ))
                } else if (checkRoom(response.hotel_id, response.room_type, response.beds)) {
                  val city = response.hotel_id match {
                    case "1" => "05001"
                    case "2" => "11001"
                  }
                  val reservedRooms = checkDates(response.arrive_date, response.leave_date, city, response.room_type)

                  /*Check if the room is reserved*/
                  val isReserved = if (reservedRooms.exists(room => room.beds.simple == response.beds.simple &&
                    room.beds.double == response.beds.double)) true else false

                  if (isReserved) {
                    BadRequest(Json.toJson(
                      Map("message" -> "The room is not available")
                    ))
                  } else {
                    response.reserve_id = generateCode(response.hotel_id, response.room_type, response.beds, response.arrive_date)
                    response.state = Some("A")
                    response.user_id = Some(res)
                    reservations.insertOne(response).headResult()
                    Ok(Json.toJson(
                      Map("reservation_id" -> response.reserve_id)))
                  }
                } else {
                  BadRequest(Json.toJson(
                    Map("message" -> "The room does not exist")
                  ))
                }
              },

              /*Error*/
              invalid = error => BadRequest(Json.toJson(
                Map("error" -> "Bad Parameters", "description" -> "Missing a parameter")))
            )
          case None => BadRequest(Json.toJson(Map("error" -> "Bad Parameters", "description" -> "JSON is missing")))
        }
      } else {
        BadRequest(Json.toJson(Map("error" -> "Bad Request", "description" -> "The request body is missing")))
      }
    } else {
      BadRequest(Json.toJson(
        Map("message" -> res)
      ))
    }
  }


  //http://localhost:9000/v1/reservation?reserve_id=RDZM1LS1D020171123K1520654374
  def cancelReservation(reserve_id: String) = Action { implicit request =>
    /*Check if the request has body*/
    val token: Option[String]= request.headers.get("Authorization")
    val res = Firebase.verifyToken(token)
    if(!res.equals(Firebase.TokenErrorMessage) && !res.equals(Firebase.AuthorizationErrorMessage)) {
      val ReservationErrorMessage = "Invalid id code"
      if (reserve_id != null && !reserve_id.startsWith("RDZM")) {

        BadRequest(Json.toJson(
          Map("message" -> ReservationErrorMessage)
        ))
      } else {
        val reservation: Reservation = reservations.find(equal("reserve_id", reserve_id)).headResult()
        if (reservation != null) {
          if ((!Some("A").equals(reservation.state)) && (!Some("a").equals(reservation.state))) {

            BadRequest(Json.toJson(
              Map("message" -> "The status of your reserve is not approved")
            ))
          } else {
            val reserve = reservations.find(equal("reserve_id", reserve_id)).headResult()
            if(res.equals(reserve.user_id.getOrElse("NO_TOKEN"))) {
              reservations.updateOne(equal("reserve_id", reserve_id), set("state", "C")).headResult()
              Ok(Json.toJson(
                Map("message" -> "Your reservation was successfully canceled!!")
              ))
            } else {
              BadRequest(Json.toJson(
                Map("message" -> "You do not have authorization to do this action")
              ))
            }
          }
        } else {
          BadRequest(Json.toJson(
            Map("message" -> "This reserve doesn´t exist")
          ))
        }
      }
    } else {
      BadRequest(Json.toJson(
        Map("message" -> res)
      ))
    }
  }


  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def getReservations = Action { implicit request =>
    val token: Option[String]= request.headers.get("Authorization")
    val res = Firebase.verifyToken(token)

    if(!res.equals(Firebase.TokenErrorMessage) && !res.equals(Firebase.AuthorizationErrorMessage)) {
      val hotelsResponse = hotels.find().projection(exclude("_id", "city", "rooms")).results()
      val result = {
        var hotelsJson: Seq[HotelResponse] = Nil
        for (hotel <- hotelsResponse) {
          var reservationsJson: Seq[ReservationResponse] = Nil
          val reservationsResponse = reservations.find(and(equal("hotel_id", hotel.hotel_id), equal("user_id", res))).results()
          for (reservation <- reservationsResponse) {
            val roomResponse = rooms.find(and(equal("room_type", reservation.room_type),
              equal("capacity", reservation.capacity), equal("beds.simple", reservation.beds.simple),
              equal("beds.double", reservation.beds.double))).headResult()
            val roomReserve = RoomResponse(roomResponse.room_type, roomResponse.capacity, roomResponse.price,
              roomResponse.currency, roomResponse.room_thumbnail, roomResponse.description, roomResponse.beds)
            reservationsJson = reservationsJson :+ ReservationResponse(reservation.state, reservation.reserve_id,
              reservation.arrive_date, reservation.leave_date, roomReserve)
          }
          hotelsJson = hotelsJson :+ HotelResponse(hotel.hotel_id, hotel.hotel_name, hotel.hotel_thumbnail,
            hotel.hotel_location, hotel.check_in, hotel.check_out, hotel.hotel_website, reservationsJson)
        }
        hotelsJson
      }
      val jsonResult = Json.obj("reservations" -> result)
      Ok(jsonResult)
    } else {
      BadRequest(Json.toJson(
        Map("message" -> res)
      ))
    }
  }

  def checkRoom(hotelId: String, roomType: String, beds: Bed): Boolean = {

    val room = rooms.find(and(
      equal("hotel_id", hotelId),
      equal("room_type", roomType),
      equal("beds", beds))).results()
    room.nonEmpty
  }


  def generateCode(hotelId: String, roomType: String, beds: Bed, arriveDate: String): Option[String] = {
    val hotelCode = hotelId
    val roomCode = roomType
    val bedsCode = "S".concat(String.valueOf(beds.simple)).concat("D").concat(String.valueOf(beds.double))
    val dateCode = arriveDate.split("-").mkString("")
    val secureRandom = new SecureRandom()
    val keyCode = "K".concat(String.valueOf(Math.abs(secureRandom.nextInt())))
    Option("RDZM".concat(hotelCode).concat(roomCode).concat(bedsCode).concat(dateCode).concat(keyCode))
  }

  def checkDates(arriveDate: String, leaveDate:
  String, city: String, roomType: String): Seq[Reservation] = {
    val newArrive = arriveDate.replace("-", "").toInt
    val newLeave = leaveDate.replace("-", "").toInt
    val hotelId = city match {
      case "05001" => "1"
      case "11001" => "2"
    }

    val reservationList: Seq[Reservation] = reservations.find(and(equal("room_type", roomType),
      equal("hotel_id", hotelId))).results()

    val reservationsFiltered = reservationList.filter(x =>
      (x.arrive_date.replace("-", "").toInt <= newArrive &&
        x.leave_date.replace("-", "").toInt >= newArrive) ||
        (x.arrive_date.replace("-", "").toInt <= newLeave &&
          x.leave_date.replace("-", "").toInt >= newLeave))

    reservationsFiltered
  }

  def verifyFirebaseToken() = Action { implicit request =>
    val token: Option[String] = request.headers.get("Authorization");
    val res = Firebase.verifyToken(token);
    Ok(s"${res}")
  }
}
