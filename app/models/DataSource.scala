package models

class DataSource {

  def getUser(email:String, userId:String):Option[User] =
    if(email == "test@example.com" && userId == "12345")
    {
      Some(User("123456", "CC", email, "300000"))
    } else {
      None
    }

}
