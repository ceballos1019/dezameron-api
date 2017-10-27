package controllers

import java.util.NoSuchElementException
import javax.inject._

import models.{Bed, Hotel, Reservation, Room}
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

  //http://localhost:9000/v1/rooms?arrive_date=2017-02-20&leave_date=2017-03-15&city=11001&hosts=2&room_type=L
  def search(arrive_date: String, leave_date: String, city:String,
             hosts: Int, room_type:String)  =
    Action{
      if(!city.equals("05001") && !city.equals("11001"))
        {
          BadRequest(Json.toJson(
            Map("message" -> "Invalid city code")))
        }
      else if(hosts <= 0 || hosts > 5 )
        {
          BadRequest(Json.toJson(
            Map("message" -> "Hosts must be between 1 and 5")))
        }
      else if(!room_type.equals("L") && !room_type.equals("S"))
        {
          BadRequest(Json.toJson(
            Map("message" -> "Invalid room type")))
        }
      else if(!arrive_date.matches("[0-9]{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[0-1])")){
        BadRequest(Json.toJson(
          Map("message" -> "Invalid arrive date")))
      }
      else if(!leave_date.matches("[0-9]{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[0-1])")){
        BadRequest(Json.toJson(
          Map("message" -> "Invalid leave date")))
      }
      else if(arrive_date.replace("-","").toInt > leave_date.replace("-","").toInt)
      {
        BadRequest(Json.toJson(
          Map("message" -> "Leave date must be greater than arrive date")))
      }
      else {
        val reserved_rooms = checkDates(arrive_date, leave_date, city, room_type)
        val hotel = hotels.find(equal("city", city)).projection(exclude("_id", "city")).headResult();

        var rooms_res = rooms.find(and(equal("city", city), equal("capacity", hosts),
          equal("room_type", room_type))).projection(exclude("room_id", "hotel_id", "city")).results()

        for(reserved <- reserved_rooms)
        {
          rooms_res = rooms_res.filterNot(x => x.beds.simple == reserved.beds.simple
          && x.beds.double == reserved.beds.double)
        }

        var json_res = Hotel(hotel.hotel_id, hotel.hotel_name, hotel.city, hotel.hotel_location, hotel.hotel_thumbnail,
          hotel.check_in, hotel.check_out, hotel.hotel_website, rooms_res)

        Ok(Json.toJson(json_res))
      }
    }


  def reserve() = Action { implicit request =>
      /*Check if the request has body*/
      if(request.hasBody) {
        val bodyAsJson = request.body.asJson.get
        bodyAsJson.validate[Reservation].fold(
          /*Succesful*/
          valid = response => {
            if(response.capacity <= 0 || response.capacity > 5) {
              BadRequest(Json.toJson(
                Map("message" -> "Hosts must be between 1 and 5")))
            } else if(!response.room_type.equals("L") && !response.room_type.equals("S")) {
              BadRequest(Json.toJson(
                Map("message" -> "Invalid room type")))
            } else if(!response.arrive_date.matches("[0-9]{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[0-1])")) {
              BadRequest(Json.toJson(
                Map("message" -> "Invalid arrive date")))
            } else if(!response.leave_date.matches("[0-9]{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[0-1])")) {
              BadRequest(Json.toJson(
                Map("message" -> "Invalid leave date")))
            } else if(response.arrive_date.replace("-","").toInt > response.leave_date.replace("-","").toInt) {
              BadRequest(Json.toJson(
                Map("message" -> "Leave date must be greater than arrive date")))
            } else if(checkRoom(response.hotel_id, response.room_type, response.beds)) {
              response.reservation_id = generateCode(response.hotel_id, response.room_type, response.beds, response.arrive_date)
              reservations.insertOne(response).headResult()
              Ok(Json.toJson(
                Map("reservation" -> response.reservation_id)))
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

  def checkDates(arrive_date:String, leave_date:
  String, city:String, room_type:String):Seq[Reservation] =
  {
    val new_arrive  = arrive_date.replace("-","").toInt
    val new_leave = leave_date.replace("-","").toInt
    val hotel_id = city match{
      case "05001" => "1"
      case "11001" => "2"
    }
    var reservation_list:Seq[Reservation] = reservations.find(and(equal("room_type",room_type),
      equal("hotel_id",hotel_id))).results()

    reservation_list = reservation_list.filter(x =>
      (x.arrive_date.replace("-","").toInt <= new_arrive &&
      x.leave_date.replace("-","").toInt >= new_arrive) ||
      (x.arrive_date.replace("-","").toInt <= new_leave &&
      x.leave_date.replace("-","").toInt >= new_leave))

    reservation_list
  }

}