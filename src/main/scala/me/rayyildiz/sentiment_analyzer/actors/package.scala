package me.rayyildiz.sentiment_analyzer

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials

package object actors {

  @deprecated
  def getFixedGoogleCredentialProvider: FixedCredentialsProvider = {
    FixedCredentialsProvider.create(getGoogleCredentials)
  }

  @deprecated
  def getGoogleCredentials: GoogleCredentials = {
    GoogleCredentials.fromStream(ClassLoader.getSystemResourceAsStream("./flux-decd0ff29d60.json"))
  }
}
