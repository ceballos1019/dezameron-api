package utils

/**
  * Class to handle the format validations for the services
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
    * @param cityCode - 05001 and 11001 are the valid codes
    * @param arriveDate - date must be in the format YYYY-MM-dd
    * @param leaveDate - date must be in the format YYYY-MM-dd
    * @param capacity - value between 0 and 5
    * @param roomType - "L" or "S"
    * @param simpleBeds - number of simple beds
    * @param doubleBeds - number of double beds
    * @return Message with the error. If there is not an error return NoErrorMessage
    */
  def validate(cityCode: String, arriveDate: String, leaveDate: String, capacity: Integer, roomType: String,
              simpleBeds: Integer, doubleBeds: Integer): String = {
    /*TODO: Check if the params can be null before invoke this method*/

    /*Validate the city code if it is not null*/
    if(cityCode != null && (!cityCode.equals("05001") && !cityCode.equals("11001"))) {
      return CityErrorMessage
    }

    /*Validate the arrive and leave dates format*/
    if(arriveDate != null && (!arriveDate.matches(DatePattern))) {
      return ArriveDateErrorMessage
    }
    if(leaveDate != null && (!leaveDate.matches(DatePattern))) {
      return LeaveDateErrorMessage
    }

    /*Validate the capacity/hosts*/
    if(capacity != null && (capacity <= 0 || capacity > 5)) {
      return CapacityErrorMessage
    }

    /*Validate the room type*/
    if(roomType != null && (!roomType.equals("L") && !roomType.equals("S"))) {
      return RoomTypeErrorMessage
    }

    /*Check that arrive date is before the leave date*/
    if(arriveDate.replace("-","").toInt > leaveDate.replace("-","").toInt) {
      return InvalidDateMessage
    }

    /*Check that capacity of the room matches with the beds distribution*/
    if((simpleBeds != null && doubleBeds != null) && (capacity != (simpleBeds + (doubleBeds * 2)))) {
      return BedsErrorMessage
    }

    /*There are not errors in the validation*/
    NoErrorMessage
  }
}
