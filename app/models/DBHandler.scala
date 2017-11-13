package models

import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}
import scala.reflect.ClassTag

/**
  * Singleton object to handle database connections
  * @author Andrés Ceballos Sánchez - andres.ceballoss@udea.edu.co
  * @version 1.0
  * @since 6/11/2017
  */
object DBHandler {

  /* Database connection properties*/
  private val URL_CONNECTION : String = "mongodb://scaladores:root@ds121945.mlab.com:21945/heroku_8bc7c40l"
  private val DATABASE_NAME : String = "heroku_8bc7c40l"

  private var database : MongoDatabase = _
  private val codecRegistry = fromRegistries(Room.codecRoom, Reservation.codecReservation, User.codecUser,
    Bed.codecBed, Hotel.codecHotel, Location.codecLocation, DEFAULT_CODEC_REGISTRY)

  /**
    * Method that return a connection to the database
    * @return database
    */
  private def getConnection() : MongoDatabase = {
    if(database == null) {
      database = MongoClient(URL_CONNECTION).getDatabase(DATABASE_NAME).withCodecRegistry(codecRegistry)
    }
    database
  }

  /**
    *
    * @tparam T - Type param associated with the model class
    * @return MongoCollection associated with the model class
    */
  def getCollection [T: ClassTag]: MongoCollection[T]= {
    var collectionName : Option[String] = None
    val tClass = implicitly[ClassTag[T]].runtimeClass
    tClass.getSimpleName match {
      case "Reservation" => collectionName = Some(Reservation.COLLECTION_NAME)

      case "Room" => collectionName = Some(Room.COLLECTION_NAME)

      case "Hotel" => collectionName = Some(Hotel.COLLECTION_NAME)
    }
    getConnection()
    database.getCollection(collectionName.get)
  }


}
