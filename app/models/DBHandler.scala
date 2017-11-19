package models

import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}
import scala.reflect.ClassTag

/**
  * Singleton object to handle database connections
  *
  * @author Andrés Ceballos Sánchez - andres.ceballoss@udea.edu.co
  * @version 1.0
  * @since 6/11/2017
  */
object DBHandler {

  /* Database connection properties*/
  private val URLConnection: String = "mongodb://scaladores:root@ds121945.mlab.com:21945/heroku_8bc7c40l"
  private val DatabaseName: String = "heroku_8bc7c40l"

  private val codecRegistry = fromRegistries(Room.codecRoom, Reservation.codecReservation, User.codecUser,
    Bed.codecBed, Hotel.codecHotel, Location.codecLocation, DEFAULT_CODEC_REGISTRY)

  /**
    * Method that return a connection to the database
    *
    * @return database
    */
  private def getConnection: MongoDatabase = {
    MongoClient(URLConnection).getDatabase(DatabaseName).withCodecRegistry(codecRegistry)
  }

  /**
    *
    * @tparam T - Type param associated with the model class
    * @return MongoCollection associated with the model class
    */
  def getCollection[T: ClassTag]: MongoCollection[T] = {
    val tClass = implicitly[ClassTag[T]].runtimeClass
    val collectionName = tClass.getSimpleName match {
      case "Reservation" => Reservation.CollectionName
      case "Room" => Room.CollectionName
      case "Hotel" => Hotel.CollectionName
    }
    val database = getConnection
    database.getCollection(collectionName)
  }


}
