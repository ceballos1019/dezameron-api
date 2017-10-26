package controllers

import java.util.NoSuchElementException
import javax.inject._

import models.{Bed, Hotel, Reservation}
import models.Hotel.hotels
import models.Room.rooms
import models.Reservation.reservations
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase, Observer}
import play.api._
import play.api.libs.json.Json
import play.api.mvc._
import models.Helpers._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._

import scala.util.Random
/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc)
{

  var beds = Bed(1,2)
  def test = Action{
    Ok(Json.toJson(reservations.find().results()))
  }

  //http://localhost:9000/v1/rooms?arrive_date=hola&leave_date=chao&city=05001&hosts=3&room_type=L
  def search(arrive_date: String, leave_date: String, city:String,
             hosts: Int, room_type:String)  =
    Action{
      var hotel = hotels.find(equal("city",city)).projection(exclude("_id","city")).headResult();

      var rooms_res = rooms.find(and(equal("city", city), equal("capacity",hosts),
        equal("room_type",room_type))).projection(exclude("room_id","hotel_id","city")).results()

      var json_res = Hotel(hotel.hotel_id,hotel.hotel_name,hotel.city, hotel.hotel_location,hotel.hotel_thumbnail,
        hotel.check_in, hotel.check_out,hotel.hotel_website, rooms_res)

      Ok(Json.toJson(json_res))
    }


  def reserve() = Action { implicit request =>
      /*Check if the request has body*/
      if(request.hasBody) {
        val bodyAsJson = request.body.asJson.get
        bodyAsJson.validate[Reservation].fold(
          /*Succesful*/
          valid = response => {
            if(checkRoom(response.hotel_id, response.room_type, response.beds)) {
              response.reservation_id = generateCode(response.hotel_id, response.room_type, response.beds, response.arrive_date)
              reservations.insertOne(response).headResult()
              Ok(Json.toJson(
                Map("message" -> response.reservation_id)))
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
      } else {
        BadRequest(Json.toJson(Map("error" -> "Bad Request", "description" -> "The request body is missing")))
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

  def checkRoom(hotel_id: Int, room_type: String, beds: Bed): Boolean = {

    var room = rooms.find(and(
                          equal("hotel_id", hotel_id),
                          equal("room_type", room_type),
                          equal("beds", beds))).results()
    room.nonEmpty
  }

  def generateCode(hotel_id: Int, room_type: String, beds: Bed, arrive_date: String): Option[String] = {
    val hotelCode = String.valueOf(hotel_id)
    val roomCode = room_type
    val bedsCode = "S".concat(String.valueOf(beds.simple)).concat("D").concat(String.valueOf(beds.double))
    val dateCode = arrive_date.split("-").mkString("")
    val keyCode = "K".concat(String.valueOf(Math.abs(Random.nextInt())))
    Option("RDZM".concat(hotelCode).concat(roomCode).concat(bedsCode).concat(dateCode).concat(keyCode))
  }
}