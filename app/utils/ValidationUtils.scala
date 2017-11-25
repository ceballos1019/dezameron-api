package utils

/**
  * Class to handle the format validations for the services
  *
  * @author Andrés Ceballos Sánchez - andres.ceballoss@udea.edu.co
  * @version 1.0.0
  * @since 6/11/2017
  */
object ValidationUtils {

  /*Value to return when there is no error*/
  val NoErrorMessage = "NO_ERROR"

  /*Validation Error Messages*/
  private val CityErrorMessage = "Invalid city code"
  private val ArriveDateErrorMessage = "Invalid arrive date"
  private val LeaveDateErrorMessage = "Invalid leave date"
  private val DatePattern = "[0-9]{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[0-1])"
  private val CapacityErrorMessage = "Hosts must be between 1 and 5"
  private val RoomTypeErrorMessage = "Invalid room type"
  private val InvalidDateMessage = "Leave date must be greater than arrive date"
  private val BedsErrorMessage = "Beds number does not match with room capacity"

  /**
    * Validate the params that are not null
    *
    * @param cityCode   - 05001 and 11001 are the valid codes
    * @param arriveDate - date must be in the format YYYY-MM-dd
    * @param leaveDate  - date must be in the format YYYY-MM-dd
    * @param capacity   - value between 0 and 5
    * @param roomType   - "L" or "S"
    * @param simpleBeds - number of simple beds
    * @param doubleBeds - number of double beds
    * @return Message with the error. If there is not an error return NoErrorMessage
    */
  def validate(cityCode: Option[String], arriveDate: Option[String], leaveDate: Option[String], capacity: Option[Integer],
               roomType: Option[String], simpleBeds: Option[Integer], doubleBeds: Option[Integer]): String = {
    /*TODO: Check if the params can be null before invoke this method*/

    /*Validate the city code if it is not null*/
    val city = cityCode.getOrElse("NO_CITY")
    if (!city.equals("NO_CITY") && (!city.equals("05001") && !city.equals("11001"))) {
      return CityErrorMessage
    }

    /*Validate the arrive and leave dates format*/
    val arrive = arriveDate.getOrElse("NO_ARRIVE_DATE")
    if (!arrive.equals("NO_ARRIVE_DATE") && (!arrive.matches(DatePattern))) {
      return ArriveDateErrorMessage
    }

    val leave = leaveDate.getOrElse("NO_LEAVE_DATE")
    if (!leave.equals("NO_LEAVE_DATE") && (!leave.matches(DatePattern))) {
      return LeaveDateErrorMessage
    }

    /*Check that arrive date is before the leave date*/
    if (arrive.replace("-", "").toInt > leave.replace("-", "").toInt) {
      return InvalidDateMessage
    }

    /*Validate the capacity/hosts*/
    val hosts: Integer = capacity.getOrElse(-1)
    if (hosts != -1 && (hosts <= 0 || hosts > 5)) {
      return CapacityErrorMessage
    }

    /*Validate the room type*/
    val room = roomType.getOrElse("NO_ROOM_TYPE")
    if (!room.equals("NO_ROOM_TYPE") && (!room.equals("L") && !room.equals("S"))) {
      return RoomTypeErrorMessage
    }

    /*Check that capacity of the room matches with the beds distribution*/
    val simple: Integer = simpleBeds.getOrElse(-1)
    val double: Integer = doubleBeds.getOrElse(-1)
    if ((simple != -1 && double != -1) && hosts != (simple + (double * 2))) {
      return BedsErrorMessage
    }

    /*There are not errors in the validation*/
    NoErrorMessage
  }
}
