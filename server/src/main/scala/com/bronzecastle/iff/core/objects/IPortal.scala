package com.bronzecastle.iff.core.objects

/**
 * connects two places with custom travel (such as a door)
 */
trait IPortal {
  /**
   * return the destination IPlace given the actor and his origin, or
   *  throw the appropriate UnableToTravelException
   *
   * @param actor whose traveling
   * @param from where he's coming from
   * @return where he should go
   */
  def allowTravel(actor: IActor,from: IPlace): IPlace
}
