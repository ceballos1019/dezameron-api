package utils

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}

class JWTUtil {

  val JwtSecretKey = "secretKey"
  val JwtSecret= "HS256"

  def createToken(payload: String): String = {
    val header = JwtHeader(JwtSecret)
    val claimsSet = JwtClaimsSet(payload)

    JsonWebToken(header, claimsSet, JwtSecretKey)
  }

  def isValidToken(jwtToken: String): Boolean =
    JsonWebToken.validate(jwtToken, JwtSecretKey)

  def decodePayload(jwtToken: String): Option[String] =
    jwtToken match {
      case JsonWebToken(header, claimsSet, signature) => Option(claimsSet.asJsonString)
      case _                                          => None
    }
}
